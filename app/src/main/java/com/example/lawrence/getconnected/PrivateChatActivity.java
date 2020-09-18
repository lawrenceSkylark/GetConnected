package com.example.lawrence.getconnected;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lawrence.getconnected.Adapters.AdapterChat;
import com.example.lawrence.getconnected.models.ModelChat;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class PrivateChatActivity extends AppCompatActivity {
       Toolbar toolbar;
       RecyclerView recyclerView;
       ImageView profileIv;
       TextView usernameTv,userStatusTv;
       EditText messageEt;
       ImageButton sendBtn;

    ValueEventListener seenListener;
    DatabaseReference userRefForSeen;
    List<ModelChat> chatList;
    AdapterChat adapterChat;
    FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference userDbRef;
    String hisUid;
    String myUid;
    String hisImage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_private_chat);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        recyclerView=findViewById(R.id.chat_recycleViewer);
        profileIv=findViewById(R.id.profileIv);
        usernameTv=findViewById(R.id.usernameTv);
        userStatusTv=findViewById(R.id.userStatusTv);
        messageEt=findViewById(R.id.messageEt);
        sendBtn=findViewById(R.id.sendBtn);


        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager );
        Intent intent = getIntent();

        hisUid=intent.getStringExtra("hisUid");
        // firebase auth instnace
         firebaseAuth = FirebaseAuth.getInstance();

         firebaseDatabase=FirebaseDatabase.getInstance();
         userDbRef =firebaseDatabase.getReference("Users");

         // search users to get that user's info
        Query userQuery = userDbRef.orderByChild("Uid").equalTo(hisUid);
        userQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            // check until required info is received
            for (DataSnapshot ds:dataSnapshot.getChildren()){
                // get data
                String username =""+  ds.child("username").getValue();
               String typingStatus =""+  ds.child("typingTo").getValue();

               //check typing stattus
                if (typingStatus.equals(myUid)){
                    userStatusTv.setText("typing...");
                }
                else
                {
                    // convert proper timestamp to proper time date
                    Calendar cal = Calendar.getInstance(Locale.ENGLISH);
                    cal.setTimeInMillis(Long.parseLong(typingStatus));
                    String datetime = DateFormat.format("dd/MM/yyyy hh:mm aa",cal).toString();
                    userStatusTv.setText("Last Seen At:"+datetime);

                }
                String onlineStatus = ""+ds.child("onlineStatus").getValue();
                checkOnlineStatus("online");
                hisImage =""+  ds.child("image").getValue();
                if (onlineStatus.equals("online")){
                    userStatusTv.setText(onlineStatus);
                }
                else {
                    // convert proper timestamp to proper time date
                    Calendar cal = Calendar.getInstance(Locale.ENGLISH);
                    cal.setTimeInMillis(Long.parseLong(onlineStatus));
                    String datetime = DateFormat.format("dd/MM/yyyy hh:mm aa",cal).toString();
                   userStatusTv.setText("Last Seen At:"+datetime);
                   // addd any time stamp to registerd users in  firebase manually
                }

                // set data
                usernameTv.setText(username);
                try {
                    // image received set image to image view
                    Picasso.get().load(hisImage).placeholder(R.drawable.ic_default_face).into(profileIv);
                }
                catch (Exception e){
                    // there is no exception getting picture set default picture
                    Picasso.get().load(R.drawable.ic_default_face).into(profileIv);
                }
            }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //
                String message = messageEt.getText().toString().trim();
                // check if text is empty
                if (TextUtils.isEmpty(message)){

                    Toast.makeText(PrivateChatActivity.this, " say something please", Toast.LENGTH_SHORT).show();
                }
                else
                {
                     sendMessage(message);
                }
            }
        });
        messageEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
           if (s.toString().trim().length()==0){
               checkTypingStatus("noOne");

           }
           else
           {
               checkTypingStatus(hisUid);// uid of the receiver
               
           }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        readMessages();
    }


    private void readMessages() {
        chatList = new ArrayList<>();
        DatabaseReference dbRf = FirebaseDatabase.getInstance().getReference("Chats");
        dbRf.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                chatList.clear();
                for (DataSnapshot ds:dataSnapshot.getChildren())
                {
                    ModelChat chat=ds.getValue(ModelChat.class);
                    if (chat.getReceiver().equals (myUid) && chat.getSender().equals (hisUid) ||
                            chat.getReceiver().equals(hisUid) && chat.getSender().equals(myUid)){
                        chatList.add(chat);
                    }

                    adapterChat = new AdapterChat(PrivateChatActivity.this,chatList,hisImage);
                     adapterChat.notifyDataSetChanged();

                     recyclerView.setAdapter(adapterChat);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void sendMessage(String message) {
        String timestamp = String.valueOf(System.currentTimeMillis());
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put("sender", myUid);
        hashMap.put("receiver", hisUid);
        hashMap.put("message", message);
        hashMap.put("timestamp", timestamp);
        hashMap.put("isSeen", false);
       databaseReference.child("Chats").push().setValue(hashMap);

       // reset editet after  sending message

        messageEt.setText("");

    }

    // get current user
    private void checkUserStatus() {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user!=null) {
    // user is sign in stay here
            myUid=user.getUid();//currently signed in user
        } else
        {
            // user not sign in go to main activity
            startActivity(new Intent(this,MainActivity.class));
           finish();
        }
    }
    private void checkOnlineStatus(String status){

        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("Users").child(myUid);
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("onlineStatus", status);
        dbRef.updateChildren(hashMap);
    }
    private void checkTypingStatus(String typing ){

        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("Users").child(myUid);
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("typingTo", typing);
        dbRef.updateChildren(hashMap);
    }
    @Override
    protected void onStart() {
        checkUserStatus();
        // setOnlineStatus
        checkOnlineStatus("online");

        super.onStart();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //get timestamp

        String timestamp = String.valueOf(System.currentTimeMillis());
        checkOnlineStatus(timestamp);
        checkTypingStatus("noOne");

        userRefForSeen.removeEventListener(seenListener);
    }

    @Override
    protected void onResume() {
        // setOnlineStatus
        checkOnlineStatus("online");
        super.onResume();
    }
}
