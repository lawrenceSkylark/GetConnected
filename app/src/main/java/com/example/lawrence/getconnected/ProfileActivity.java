package com.example.lawrence.getconnected;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class ProfileActivity extends AppCompatActivity {
    // permission reires constants
    private  static final int CAMERA_REQUEST_CODE =100;
    private  static final int STORAGE_REQUEST_CODE =200;
    //IMAGE PICK CONSTANTS
    private static final int IMAGE_PICK_GALLERY_CODE =300;
    private static final int IMAGE_PICK_CAMERA_CODE= 400;
    // PERMISSION TO BE RQUIRED

    //uri of picked image
    private Uri image_uri =null;
    FirebaseAuth firebaseAuth;
    FirebaseUser user;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    StorageReference storageReference;
    String storagePath = "Users_Profi le_Cover_imgs";
    ProgressDialog pd;
    private ProgressDialog progressDialog;

    String profileOrCoverPhoto;
    //  permission constants
    //permision array
    private String[] cameraPermissions;
    private String[] storagePermissions;
    ImageView avatarIV, coverIv;
    TextView phoneTV, emailTV, usernameTv;
    FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Users");
        avatarIV = findViewById(R.id.avatarIV);
        coverIv = findViewById(R.id.coverIv);
        emailTV = findViewById(R.id.emailTV);
        phoneTV = findViewById(R.id.phoneTV);
        fab = findViewById(R.id.fab);
        usernameTv = findViewById(R.id.usernameTV);

        cameraPermissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};
        pd = new ProgressDialog(getApplicationContext());
        Query query = databaseReference.orderByChild("email").equalTo(user.getEmail());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    String username = "" + ds.child("username").getValue();
                    String email = "" + ds.child("email").getValue();
                    String phone = "" + ds.child("phone").getValue();
                    String image = "" + ds.child("image").getValue();
                    String cover = "" + ds.child("cover").getValue();
                    //set data
                    usernameTv.setText(username);
                    phoneTV.setText(phone);
                    emailTV.setText(email);
                    try {
                        Picasso.get().load(image).into(avatarIV);
                    } catch (Exception e) {
                        Picasso.get().load(R.drawable.imagepic).into(avatarIV);

                    }
                    try {
                        Picasso.get().load(cover).into(coverIv);
                    } catch (Exception e) {
                    }
                }
            }
// ok
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEditProfileDialog();

            }
        });
    }

    private void showEditProfileDialog() {
        // display options
        String [] options={"Edit Profile Picture","Edit Cover Photo","Edit Phone Number","Edit Email"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose Action")
                .setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // handle clicks
                          if (which==0){
                            //edit profile click
                    pd.setMessage("updating profile picture");
                    profileOrCoverPhoto= "image";
                            showImagePicDialog();
                        }
                        else if (which==1) {
                            // edit cover clicked`
                              final ProgressDialog pd = new ProgressDialog(getApplicationContext());
                            pd.setMessage("updating cover photo");
                            profileOrCoverPhoto= "cover";
                            showImagePicDialog();
                        }
                        else if (which==2) {
                            // edit email  clicked
                            showNamePhoneUpdateDialog("Phone");
                            if (
                            progressDialog !=null && progressDialog.isShowing()){
                                progressDialog.dismiss();
                            }
                            progressDialog.setTitle("please wait");

                        }
                        else if (which==3) {
                            // edit phone clicked
                            pd.setMessage("updating username");
                            showNamePhoneUpdateDialog("username");
                        }
                    }
                }).show();
    }
    private void showNamePhoneUpdateDialog(String key) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());
        builder.setTitle("update" + key);
        LinearLayout linearLayout = new LinearLayout(getApplicationContext());
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setPadding(10, 10, 10, 10);
        EditText editText = new EditText(getApplicationContext());
        editText.setHint("Enter" + key);
        linearLayout.addView(editText);
        builder.setView(linearLayout);
        builder.setPositiveButton("update", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String value = editText.getText().toString().trim();
                if (!TextUtils.isEmpty(value)) {
                    pd.show();
                    HashMap<String, Object> result = new HashMap<>();
                    result.put(key, value);
                    databaseReference.child(user.getUid()).updateChildren(result)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    pd.dismiss();
                                    Toast.makeText(ProfileActivity.this, "updated", Toast.LENGTH_SHORT).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(ProfileActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Toast.makeText(ProfileActivity.this, " please enter" + key, Toast.LENGTH_SHORT).show();

                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
    }

    private boolean checkStoragePermissions() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void requestStoragePermissions() {
        requestPermissions(storagePermissions, STORAGE_REQUEST_CODE);
    }

    private boolean checkCameraPermissions() {
        boolean result = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == (PackageManager.PERMISSION_GRANTED);
        boolean result1 = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
        return result1 && result;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void requestCameraPermission() {
        requestPermissions(cameraPermissions, CAMERA_REQUEST_CODE);
    }

    private void showImagePicDialog() {

        String [] options={"Camera","Gallery"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("pick Image From")
                .setItems(options, new DialogInterface.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.M)
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // handle clicks
                        if (which==0){
                            //camera
                            if (!checkCameraPermissions()){
                                requestCameraPermission();

                            }
                            else
                            {
                                pickFromCamera();

                            }
                        }
                        else
                        {
                            // gallery
                            if (!checkStoragePermissions()){
                                requestStoragePermissions();
                            }
                            else
                            {
                                pickFromGallery();
                            }
                        }
                    }
                }).show();
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case CAMERA_REQUEST_CODE: {
                if (grantResults.length > 0) {
                    boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean storageAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    if (cameraAccepted && storageAccepted) {
                        pickFromCamera();
                    } else {
                        //both or one is denied
                        Toast.makeText(this, "camera and storage required", Toast.LENGTH_SHORT).show();
                    }
                }

            }
            break;
            case STORAGE_REQUEST_CODE: {
                if (grantResults.length > 0) {
                    boolean storageAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if (storageAccepted) {
                        //persmission allowed
                        pickFromGallery();
                    } else {
                        // permissions  denied
                        Toast.makeText(this, " storage permissions required ", Toast.LENGTH_SHORT).show();
                    }

                }

            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void pickFromGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, IMAGE_PICK_GALLERY_CODE);
    }

    private void pickFromCamera() {
        ContentValues cv = new ContentValues();
        cv.put(MediaStore.Images.Media.TITLE, "Temp pic");
        cv.put(MediaStore.Images.Media.DESCRIPTION, "Temp description");
        image_uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, cv);
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri);
        startActivityForResult(cameraIntent, IMAGE_PICK_CAMERA_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == IMAGE_PICK_GALLERY_CODE) {
                //was picked from galle
                image_uri = data.getData();
                //set image to imageview
                uploadProfileCoverPhoto(image_uri);
            } else if (requestCode == IMAGE_PICK_CAMERA_CODE) {
                //was picked from camera
                //set image to imageview
                uploadProfileCoverPhoto(image_uri);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void uploadProfileCoverPhoto(Uri uri) {
        pd.show();
        String filePathAndName = storagePath + "" + profileOrCoverPhoto +"_"+ user.getUid();
        StorageReference storageReference2nd = storageReference.child(filePathAndName);
        storageReference2nd.putFile(uri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                        while (!uriTask.isSuccessful()) ;
                        Uri downloadUri = uriTask.getResult();
                        if (uriTask.isSuccessful()) {
                            //check if image is upload or not and uri is received
                            HashMap<String, Object> results = new HashMap<>();
                            assert downloadUri != null;
                            results.put(profileOrCoverPhoto, downloadUri.toString());
                            databaseReference.child(user.getUid()).updateChildren(results)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            pd.dismiss();
                                            Toast.makeText(ProfileActivity.this, "image updated successfully", Toast.LENGTH_SHORT).show();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    pd.dismiss();
                                    Toast.makeText(ProfileActivity.this, "error updating", Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else {
                            pd.dismiss();
                            Toast.makeText(ProfileActivity.this, "some error occurred", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                pd.show();
                Toast.makeText(ProfileActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
       getMenuInflater().inflate(R.menu.logout,menu);
        // search item
        return super.onCreateOptionsMenu(menu);
    }
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id==R.id.Action_logout){
            firebaseAuth.signOut();
            checkUserStatus ();
        }
        return super.onOptionsItemSelected(item);
    }

    private void checkUserStatus() {
        FirebaseUser user= firebaseAuth.getCurrentUser();
        if (user !=null){

        }
        else
        {
            startActivity(new Intent(ProfileActivity.this,MainActivity.class));
            finish();
        }
    }
}
