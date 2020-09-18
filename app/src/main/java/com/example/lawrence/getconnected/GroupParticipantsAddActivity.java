 package com.example.lawrence.getconnected;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lawrence.getconnected.Adapters.AdapterParticipantsAdd;
import com.example.lawrence.getconnected.models.ModelUser;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

 public class GroupParticipantsAddActivity extends AppCompatActivity {

    private RecyclerView usersRv;
    private ActionBar actionBar;
    private String groupId,myGroupRole;
    private ArrayList<ModelUser>userList;
    private AdapterParticipantsAdd adapterParticipantsAdd;
    private FirebaseAuth firebaseAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_participants_add);
        actionBar= getSupportActionBar();
        usersRv= findViewById(R.id.usersRv);
        firebaseAuth = FirebaseAuth.getInstance();
        groupId= getIntent().getStringExtra("groupId");
        loadGroupInfo();

    }

     private void getAllUSers() {
        userList = new ArrayList<>();DatabaseReference ref =FirebaseDatabase.getInstance().getReference("Users");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userList.clear();
                for (DataSnapshot ds:dataSnapshot.getChildren()){
                    ModelUser modelUser = ds.getValue(ModelUser.class);
                    if (firebaseAuth.getUid().equals(modelUser.getUid())){
                        userList.add(modelUser);
                    }
                }
                adapterParticipantsAdd = new AdapterParticipantsAdd(GroupParticipantsAddActivity.this,userList,""+groupId,""+myGroupRole);
                usersRv.setAdapter(adapterParticipantsAdd);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
     }

     private void loadGroupInfo() {
         DatabaseReference ref1 = FirebaseDatabase.getInstance().getReference("Groups");
         DatabaseReference ref =FirebaseDatabase.getInstance().getReference("Groups");
         ref.orderByChild("groupId").equalTo(groupId).addValueEventListener(new ValueEventListener() {
             @Override
             public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                 for (DataSnapshot ds: dataSnapshot.getChildren()){
                     String groupId = ""+ds.child("groupId").getValue();
                     String groupTitle =""+ds.child("groupTitle").getValue();
                     String groupDescription =""+ds.child("groupDescription").getValue();
                     String groupIcon =""+ds.child("groupIcon").getValue();
                     String createdBy =""+ds.child("createdBy").getValue();
                     String timestamp =""+ds.child("timestamp").getValue();
                     actionBar.setTitle("Add participants");

                     ref1.child(groupId).child("Participants").child(firebaseAuth.getUid())
                             .addValueEventListener(new ValueEventListener() {
                                 @Override
                                 public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                     if (dataSnapshot.exists()){
                                         myGroupRole =""+dataSnapshot.child("role").getValue();

                                         actionBar.setTitle(groupTitle + "("+myGroupRole+")");
                                         getAllUSers();
                                     }
                                 }

                                 @Override
                                 public void onCancelled(@NonNull DatabaseError databaseError) {

                                 }
                             });
                 }
             }

             @Override
             public void onCancelled(@NonNull DatabaseError databaseError) {

             }
         });

     }

     @Override
     public boolean onSupportNavigateUp() {
        onBackPressed();
         return super.onSupportNavigateUp();
     }
 }
