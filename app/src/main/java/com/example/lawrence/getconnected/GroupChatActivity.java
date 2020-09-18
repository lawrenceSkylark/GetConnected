package com.example.lawrence.getconnected;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lawrence.getconnected.Adapters.AdapterGroupChat;
import com.example.lawrence.getconnected.models.Message;
import com.example.lawrence.getconnected.models.ModelGroupChat;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GroupChatActivity extends AppCompatActivity {
    FirebaseDatabase database;
    private ArrayList<ModelGroupChat> groupChatList;
    private AdapterGroupChat adapterGroupChat;

    // permission reires constants
    private  static final int CAMERA_REQUEST_CODE =200;
    private  static final int STORAGE_REQUEST_CODE =400;
    //IMAGE PICK CONSTANTS
    private static final int IMAGE_PICK_GALLERY_CODE =1000;
    private static final int IMAGE_PICK_CAMERA_CODE= 4000;
    // PERMISSION TO BE RQUIRED
    private String[]cameraPermission;
    private String[]storagePermission;

    //uri of picked image
    private Uri image_uri =null;

    private Toolbar toolbar;
    com.example.lawrence.getconnected.models.ModelUser User;
    List<Message> messages;
    RecyclerView chatRv;
    TextView groupTitleTv;
    //0768973130
    EditText etmessage;
    ImageView groupIconIv,add_participant;
    private String groupId,myGroupRole="";
    ImageButton btnsend,attachBtn;
    FirebaseAuth firebaseauth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_chat);
      toolbar=findViewById(R.id.toolbar);
      setSupportActionBar(toolbar);
      add_participant= findViewById(R.id.addmem);
        groupIconIv=findViewById(R.id.groupIconIv);
        groupTitleTv=findViewById(R.id.groupTitleTv);
        attachBtn= findViewById(R.id.attachBtn);

        // get id of the group
      Intent intent =getIntent();
      groupId=intent.getStringExtra("groupId");


      //init required permission
        cameraPermission = new String[]{
                Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        };
        storagePermission = new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        };

        firebaseauth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        etmessage = (EditText) findViewById(R.id.etMessage);
        chatRv = (RecyclerView) findViewById(R.id.chatRv);
        btnsend = (ImageButton) findViewById(R.id.btnsend);
        messages = new ArrayList<>();
        
        
        
            loadGroupInfo();
            loadGroupMessages();
            myGroupRole();
            add_participant.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(getApplicationContext(), GroupInfoActivity.class));
                }
            });
          btnsend.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View v) {
                  // input data
                  String message = etmessage.getText().toString().trim();
                  //valiadte
                  if (TextUtils.isEmpty(message))
                  {
                      Toast.makeText(GroupChatActivity.this, "Can't sent empty message ", Toast.LENGTH_SHORT).show();

                  }
                  else {
                      // sent message
                      sendMessage(message);
                  }

              }
          });
          attachBtn.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View v) {
          //pick from gallery
          showImageImportDialog();
              }
          });
    }

    private void showImageImportDialog() {
        // display options
        String [] options={"Camera","Gallery"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("pick Image")
                .setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // handle clicks
                        if (which==0){
                            //camera
                            if (!checkCameraPermission()){
                                requestCameraPermission();

                            }
                            else
                            {
                                pickCamera();

                            }
                        }
                        else
                        {
                            // gallery
                            if (!checkStoragePermission()){
                                requestStoragePermission();
                            }
                            else
                            {
                                pickGallery();
                            }
                        }
                    }
                }).show();
    }
    private void pickGallery(){
        //
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent,IMAGE_PICK_GALLERY_CODE);

    }
    private  void pickCamera(){
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.Images.Media.TITLE,"GroupImagesTitle");
        contentValues.put(MediaStore.Images.Media.DESCRIPTION,"GroupImageDescriptions");
        image_uri=getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,contentValues);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT,image_uri);
        startActivityForResult(intent,IMAGE_PICK_CAMERA_CODE);


    }
    private void requestStoragePermission(){
        ActivityCompat.requestPermissions(this,storagePermission,STORAGE_REQUEST_CODE);
    }
    private boolean checkStoragePermission(){
        boolean result = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)==(PackageManager.PERMISSION_GRANTED);
        return result;
    }

    private void requestCameraPermission(){
        ActivityCompat.requestPermissions(this,cameraPermission,CAMERA_REQUEST_CODE);
    }
     private boolean checkCameraPermission(){
        boolean result =ContextCompat.checkSelfPermission(this,Manifest.permission.CAMERA)==(PackageManager.PERMISSION_GRANTED);
         boolean result1 = ContextCompat.checkSelfPermission(this,
                 Manifest.permission.WRITE_EXTERNAL_STORAGE)==(PackageManager.PERMISSION_GRANTED);
         return result && result1;
     }
     private void sendImageMessage() {
         ProgressDialog pd = new ProgressDialog(this);
         pd.setMessage("please Wait");
         pd.setTitle("sending image ....");
         pd.setCanceledOnTouchOutside(false);
         pd.show();
         String filenamePath="ChatImages/"+""+System.currentTimeMillis();
         StorageReference storageReference = FirebaseStorage.getInstance().getReference(filenamePath);

         // upload image
        storageReference.putFile(image_uri)
                 .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                     @Override
                     public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                         Task<Uri>p_uriTask = taskSnapshot.getStorage().getDownloadUrl();
                         while(!p_uriTask.isSuccessful());
                         Uri p_downloadUri =p_uriTask.getResult();
                         if (p_uriTask.isSuccessful()){
                                 //timeStamp
                                 String timestamp = ""+System.currentTimeMillis();
                                 // setup message data
                                 HashMap<String,Object> hashMap = new HashMap<>();
                                 hashMap.put("sender", ""+firebaseauth.getUid());
                                 hashMap.put("message", ""+p_downloadUri);
                                 hashMap.put("timestamp", ""+timestamp);
                                 hashMap.put("type", ""+"image");// text/image
                                 DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Groups");
                                 ref.child(groupId).child("Messages").child(timestamp)
                                         .setValue(hashMap)
                                         .addOnSuccessListener(new OnSuccessListener<Void>() {
                                             @Override
                                             public void onSuccess(Void aVoid) {
                                                 // message sent
                                                 etmessage.setText("");
                                                 pd.dismiss();
                                             }
                                         }).addOnFailureListener(new OnFailureListener() {
                                     @Override
                                     public void onFailure(@NonNull Exception e) {
                                         pd.dismiss();
                                         // message failed to sent
                                         Toast.makeText(GroupChatActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                                     }
                                 });


                         }
                     }
                 }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(GroupChatActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
               pd.dismiss();
            }
        });



    }

    private void myGroupRole() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Groups");
        ref.child(groupId).child("Participants")
                .orderByChild("uid").equalTo(firebaseauth.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot ds:dataSnapshot.getChildren()){
                            myGroupRole=""+ds.child("role").getValue();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private void loadGroupMessages() {
         //init List
        groupChatList = new ArrayList<>();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Groups");
        ref.child(groupId).child("Messages")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                     groupChatList.clear();
                     for (DataSnapshot ds:dataSnapshot.getChildren())
                     {
                         ModelGroupChat model = ds.getValue(ModelGroupChat.class);
                         groupChatList.add(model);
                     }
                     // adapter
                        adapterGroupChat = new AdapterGroupChat(GroupChatActivity.this
                        ,groupChatList);
                     // set to recycle view
                        chatRv.setAdapter(adapterGroupChat);


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private void sendMessage(String message) {
        //timeStamp
        String timestamp = ""+System.currentTimeMillis();
    // setup message data
        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put("sender", ""+firebaseauth.getUid());
        hashMap.put("message", ""+message);
        hashMap.put("timestamp", ""+timestamp);
        hashMap.put("type", ""+"text");// text/image
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Groups");
        ref.child(groupId).child("Messages").child(timestamp)
                .setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                    // message sent
                        etmessage.setText("");
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // message failed to sent
                Toast.makeText(GroupChatActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadGroupInfo() {
        DatabaseReference ref =FirebaseDatabase.getInstance().getReference("Groups");
        ref.orderByChild(groupId).equalTo("groupId")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot ds:dataSnapshot.getChildren()){
                            String groupTitle =""+ds.child("").getValue();
                            String groupDescriptions =""+ds.child("groupDescriptions").getValue();
                            String groupIcon =""+ds.child("groupIcon").getValue();
                            String timestamp =""+ds.child("timestamp").getValue();
                            String CreatedBy =""+ds.child("CreatedBy").getValue();
                            groupTitleTv.setText(groupTitle);
                            try {
                                Picasso.get().load(groupIcon).placeholder(R.drawable.ic_group_white).into(groupIconIv);

                            }
                             catch (Exception e){
                                 groupIconIv.setImageResource(R.drawable.ic_group_white);
                             }
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }


    @Override
    public boolean onOptionsItemSelected(
            @NonNull MenuItem item) {
        int id  = item.getItemId();
        if (id==R.id.action_add_partcipant)
        {
            Intent intent = new Intent(this,GroupInfoActivity.class);
            intent.putExtra("groupId",groupId);
            startActivity(intent);

        }
        else if (id==R.id.menuInfo)
        {
            Intent intent = new Intent(this,GroupInfoActivity.class);
            intent.putExtra("groupId",groupId);
            startActivity(intent);

        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode==RESULT_OK){
            if (requestCode==IMAGE_PICK_GALLERY_CODE){
                image_uri=data.getData();
                sendImageMessage();

            }
            if (requestCode==IMAGE_PICK_CAMERA_CODE){
                sendImageMessage();
            }
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case CAMERA_REQUEST_CODE:
                if (grantResults.length>0){
                    boolean cameraAccepted = grantResults[0]== PackageManager.PERMISSION_GRANTED;
                    boolean writeStorageAccepted =grantResults[1]==PackageManager.PERMISSION_GRANTED;
                    if (cameraAccepted&&writeStorageAccepted){
                        pickCamera();
                    }
                    else {
                        Toast.makeText(this, "Camera and storage Permission are required", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            case STORAGE_REQUEST_CODE:
                if (grantResults.length>0){
                    boolean writeStorageAccepted =grantResults[0]==PackageManager.PERMISSION_GRANTED;
                    if (writeStorageAccepted){
                        pickGallery();
                    }
                    else {
                        Toast.makeText(this, "Storage Permission are required", Toast.LENGTH_SHORT).show();

                    }

                }
                break;
        }
    }
}

