package com.example.lawrence.getconnected;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lawrence.getconnected.Adapters.AdapterParticipantsAdd;
import com.example.lawrence.getconnected.models.ModelUser;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class GroupInfoActivity extends AppCompatActivity {
 private String groupId;

 private String myGroupRole="";

 private FirebaseAuth firebaseAuth;

 private ActionBar actionBar;
 private ImageView groupIconIv;
 private TextView descriptionTv,createdByTv,addParticipantsTv,leaveGroupTv,participantsTv,editGroupTv;

  private RecyclerView participantsRv;

  private ArrayList<ModelUser> userList;
  private AdapterParticipantsAdd adapterParticipantsAdd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_info);
        actionBar=getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);


        groupIconIv=findViewById(R.id.groupIconIv);
        createdByTv= findViewById(R.id.createdByTv);
        addParticipantsTv=findViewById(R.id.addParticipantsTv);
        leaveGroupTv= findViewById(R.id.leaveGroupTv);
        editGroupTv=findViewById(R.id.editGroupTv);
        participantsRv=findViewById(R.id.participantsRv);
        participantsTv= findViewById(R.id.participantsTv);

        descriptionTv= findViewById(R.id.descriptionTv);


        groupId=getIntent().getStringExtra("groupId");

        firebaseAuth=FirebaseAuth.getInstance();

        loadGroupInfo();
        loadMyGroupRole();

        addParticipantsTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GroupInfoActivity.this,GroupParticipantsAddActivity.class);
                intent.putExtra("groupId",groupId);
                startActivity(intent);
            }
        });
        editGroupTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GroupInfoActivity.this,GroupEditActivity.class);
                intent.putExtra("groupId",groupId);
                startActivity(intent);
            }
        });
        leaveGroupTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String dialogTitle ="";
                String dialogDescription="";
                String positiveButtonTitle= "";
                if (myGroupRole.equals("creator")){
                    dialogTitle= "Delete Group";
                    dialogDescription= "Are you sure You want to delete group permanently?";
                    positiveButtonTitle= "Delete";
                }
                else{
                    dialogTitle= "leave Group";
                    dialogDescription= "Are you sure You want to leave  this group permanently?";
                    positiveButtonTitle= "leave";
                }
                AlertDialog.Builder builder = new AlertDialog.Builder(GroupInfoActivity.this);
                builder.setTitle(dialogTitle)
                        .setMessage(dialogDescription)
                        .setPositiveButton(positiveButtonTitle, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                             if (myGroupRole.equals("creator")){
                                 deleteGroup();

                             }
                             else {
                                 leaveGroup();
                             }
                            }
                        }).setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                     dialog.dismiss();
                    }
                })
                .show();

            }
        });

    }

    private void leaveGroup() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Groups");
        ref.child(groupId).child("Participants").child(firebaseAuth.getUid())
                .removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(GroupInfoActivity.this, "Group left Successfully", Toast.LENGTH_SHORT).show();
                     startActivity(new Intent(GroupInfoActivity.this,DashboardActivity.class));
                     finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(GroupInfoActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void deleteGroup() {
    DatabaseReference ref = FirebaseDatabase.getInstance().getReference("groups");
    ref.child(groupId)
            .removeValue()
            .addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Toast.makeText(GroupInfoActivity.this, "Group Successfully deleted", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(GroupInfoActivity.this,DashboardActivity.class));
                    finish();
                }
            }).addOnFailureListener(new OnFailureListener() {
        @Override
        public void onFailure(@NonNull Exception e) {
            Toast.makeText(GroupInfoActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    });
    }


    private void loadGroupInfo() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Groups");
        ref.orderByChild("groupId").equalTo(groupId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    String groupId = ""+ds.child("groupId").getValue();
                    String groupTitle = ""+ds.child("groupTitle").getValue();
                    String groupDescription = ""+ds.child("groupDescription").getValue();
                    String groupIcon = ""+ds.child("groupIcon").getValue();
                    String createdBy = ""+ds.child("createdBy").getValue();
                    String timeStamp = ""+ds.child("timeStamp").getValue();

                    Calendar cal  = Calendar.getInstance(Locale.ENGLISH);
                    cal.setTimeInMillis(Long.parseLong(timeStamp));
                    String dateTime= (String) DateFormat.format("dd/MM/yyyy hh:mm aa",cal).toString();

                    loadCreatorInfo(dateTime , createdBy);

                    actionBar.setTitle(groupTitle);
                    descriptionTv.setText(groupDescription);

                    try {
                        Picasso.get().load(groupIcon).placeholder(R.drawable.ic_group_primary).into(groupIconIv);
                    }
                    catch (Exception e)
                    {
                      groupIconIv.setImageResource(R.drawable.ic_group_primary);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void loadCreatorInfo(String dateTime, String createdBy) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.orderByChild("uid").equalTo(createdBy).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds:dataSnapshot.getChildren()){
                    String name = ""+ds.child("name").getValue();
                    createdByTv.setText("Created By "+name +" on "+dateTime);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void loadMyGroupRole() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Groups");
        ref.child(groupId).child("Participants").orderByChild("uid")
                .equalTo(firebaseAuth.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot ds: dataSnapshot.getChildren()){
                            myGroupRole=""+ds.child("role").getValue();
                            actionBar.setSubtitle(firebaseAuth.getCurrentUser().getEmail()+"("+myGroupRole+")");
                            if (myGroupRole.equals("Participants")){
                                editGroupTv.setVisibility(View.GONE);
                                addParticipantsTv.setVisibility(View.GONE);
                                leaveGroupTv.setText(" leave Group");

                            }
                            else if (myGroupRole.equals("admin")){
                                editGroupTv.setVisibility(View.GONE);
                                addParticipantsTv.setVisibility(View.VISIBLE);
                                leaveGroupTv.setText("leave Group");
                            }
                            else if (myGroupRole.equals("creator")){
                                editGroupTv.setVisibility(View.VISIBLE);
                                addParticipantsTv.setVisibility(View.VISIBLE);
                                leaveGroupTv.setText("Delete Group");
                            }
                        }
                        loadParticipants();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }

    private void loadParticipants() {
        userList = new ArrayList<>();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Groups");
      ref.child(groupId).child("Participants").addValueEventListener(new ValueEventListener() {
          @Override
          public void onDataChange(@NonNull DataSnapshot snapshot) {
              userList.clear();
              for (DataSnapshot ds: snapshot.getChildren()){
                  String uid = ""+ds.child("uid").getValue();
                  DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
                  ref.orderByChild("uid").equalTo(uid).addValueEventListener(new ValueEventListener() {
                      @Override
                      public void onDataChange(@NonNull DataSnapshot snapshot) {
                          for (DataSnapshot ds: snapshot.getChildren()){
                              ModelUser modelUser = ds.getValue(ModelUser.class);
                              userList.add(modelUser);

                          }
                          adapterParticipantsAdd = new AdapterParticipantsAdd(GroupInfoActivity.this,userList,groupId,myGroupRole);
                          participantsRv.setAdapter(adapterParticipantsAdd);
                          participantsTv.setText("Participants("+userList.size()+")");
                      }

                      @Override
                      public void onCancelled(@NonNull DatabaseError error) {

                      }
                  });

              }
          }

          @Override
          public void onCancelled(@NonNull DatabaseError error) {

          }
      });
       }

}
