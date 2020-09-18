package com.example.lawrence.getconnected.Adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lawrence.getconnected.R;
import com.example.lawrence.getconnected.models.ModelUser;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

public class AdapterParticipantsAdd  extends RecyclerView.Adapter<AdapterParticipantsAdd.HolderparticipantAdd>{
        private Context context;
        private ArrayList<ModelUser> userList;
        private String groupId,myGroupRole;//Creator/Admin/participants


    public AdapterParticipantsAdd(Context context, ArrayList<ModelUser> userList, String groupId, String myGroupRole) {
        this.context = context;
        this.userList = userList;
        this.groupId = groupId;
        this.myGroupRole = myGroupRole;
    }

    public AdapterParticipantsAdd(Context context, ArrayList<ModelUser> userList) {
        this.context = context;
        this.userList = userList;

    }


    @NonNull
    @Override
    public HolderparticipantAdd onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
      // infalte layouts
      View view = LayoutInflater.from(context).inflate(R.layout.row_participants_add,parent,false);
return new HolderparticipantAdd(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderparticipantAdd holder, int position) {
      ModelUser modelUser = userList.get(position);
      String username = modelUser.getUsername();
      String email = modelUser.getEmail();
      String image= modelUser.getImage();
      String udi = modelUser.getUid();
      // set data

        holder.usernameTv.setText(username);
        holder.emailTv.setText(email);
        try{
            Picasso.get().load(image).placeholder(R.drawable.ic_default_face).into(holder.avatarIv);
        }
        catch (Exception e){
            holder.avatarIv.setImageResource(R.drawable.ic_default_face);
        }
        checkIfAlreadyAdded(modelUser,holder);
        // handle click
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*
                * check if user is already added or not
                * if added show:remove /make admin/remove fro admin
                * admin will not change role or creator
                * if not added show add participants*/
                DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Groups");
                reference.child(groupId).child("Participants").child(modelUser.getUid())
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()){
                                    // user exist/ not participant
                                    String hisPreviousRole =""+dataSnapshot.child("role").getValue();
                                    //options to display dialog
                                    String[] options;
                                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                    builder.setTitle("choose");
                                    if (myGroupRole.equals("creator"))
                                    {
                                        if (hisPreviousRole.equals("admin")){
                                            // im creator he is admin
                                            options = new String[]{"Remove admin", "Remove User"};
                                            builder.setItems(options, new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    if (which==0){
                                                        // remove admin
                                                        removeAdmin(modelUser);
                                                    }
                                                    else

                                                    {
                                                        //remove user clicked
                                                        removeParticipant(modelUser);
                                                    }
                                                }
                                            }).show();
                                        }
                                        else if (hisPreviousRole.equals("partipant"))
                                        {
                                            // im a creator his  a participant
                                            options = new String[]{"Make Admin", "Remove User"};
                                            builder.setItems(options, new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    if (which==0){
                                                        // Make Admin
                                                        makeAdmin(modelUser);
                                                    }
                                                    else

                                                    {
                                                        //Remove User
                                                        removeParticipant(modelUser);
                                                    }
                                                }
                                            }).show();
                                        }
                                    }
                                    else if (myGroupRole.equals("admin")){ }
                                    if (hisPreviousRole.equals("creator")){
                                        // im admin ,hes crator
                                        Toast.makeText(context, "creator of a group", Toast.LENGTH_SHORT).show();
                                    }
                                    else if (hisPreviousRole.equals("admin")){
                                        //  am admin ,he is admin too
                                        options = new String[]{"remove Admin", "Remove User"};
                                        builder.setItems(options, new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                if (which==0){
                                                    // Make Admin
                                                    removeAdmin(modelUser);
                                                }
                                                else

                                                {
                                                    //Remove User
                                                    removeParticipant(modelUser);
                                                }
                                            }
                                        }).show();
                                    }
                                    else if (hisPreviousRole.equals("participant")){
                                        options = new String[]{"Make Admin","Remove User"};
                                        builder.setItems(options, new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                if (which==0){
                                                    // Make Admin
                                                    makeAdmin(modelUser);
                                                }
                                                else

                                                {
                                                    //Remove User
                                                    removeParticipant(modelUser);
                                                }
                                            }
                                        }).show();
                                    }
                                }
                                else {
                                    // user doenot exist
                                    AlertDialog.Builder builder =new AlertDialog.Builder(context);
                                    builder.setTitle("Add Participant")
                                            .setMessage("Add user in this channel?")
                                            .setPositiveButton("ADD", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                              // add user
                                                    addParticipant(modelUser);
                                                }
                                            })
                                            .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                               dialog.dismiss();
                                                }
                                            }).show();

                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
            }
        });
    }
    private void addParticipant(ModelUser modelUser) {
        // set up data
        String timestamp = ""+System.currentTimeMillis();
        HashMap<String,String> hashMap = new HashMap<>();
        hashMap.put("uid",modelUser.getUid());
        hashMap.put("rol","participant");
        hashMap.put("timestamp",""+timestamp);
        // add that user in groups > group id particint

     DatabaseReference ref =FirebaseDatabase.getInstance().getReference("Groups");
     ref.child(groupId).child("Participants").child(modelUser.getUid()).setValue(hashMap)
             .addOnSuccessListener(new OnSuccessListener<Void>() {
                 @Override
                 public void onSuccess(Void aVoid) {
                     // added successfully
                     Toast.makeText(context, "added successfully", Toast.LENGTH_SHORT).show();

                 }
             }).addOnFailureListener(new OnFailureListener() {
         @Override
         public void onFailure(@NonNull Exception e) {
             Toast.makeText(context, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
         }
     });
    }  

    private void makeAdmin(ModelUser modelUser) {
        HashMap<String,Object>hashMap = new HashMap<>();
        hashMap.put("role","participant");// role are participants/ admin /creator
        //update role in db
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Groups");
        reference.child(groupId).child("Participants").child(modelUser.getUid()).updateChildren(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(context, "The User is no longer admin....", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }


    private void removeParticipant(ModelUser modelUser) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Groups");
       reference.child(groupId).child("Participants").child(modelUser.getUid()).removeValue()
               .addOnSuccessListener(new OnSuccessListener<Void>() {
                   @Override
                   public void onSuccess(Void aVoid) {
                       // removed successfully
                   }
               }).addOnFailureListener(new OnFailureListener() {
           @Override
           public void onFailure(@NonNull Exception e) {


           }
       });
    }


    private void removeAdmin(ModelUser modelUser) {
        HashMap<String,Object>hashMap = new HashMap<>();
        hashMap.put("role","admin");// role are participants/ admin /creator
        //update role in db
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Groups");
        reference.child(groupId).child("Participants").child(modelUser.getUid()).updateChildren(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(context, "The User is Now Admin", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void checkIfAlreadyAdded(ModelUser modelUser, HolderparticipantAdd holder) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Groups");
        ref.child(groupId).child("Participants").child(modelUser.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()){

                            String hisRole = ""+dataSnapshot.child("role").getValue();
                            holder.statusTv.setText(hisRole);
                        }

                        else {
                            holder.statusTv.setText("");
                            // doesnot exit
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    class HolderparticipantAdd extends RecyclerView.ViewHolder{
          private ImageView avatarIv;
          private TextView usernameTv,emailTv,statusTv;
        public HolderparticipantAdd(@NonNull View itemView) {
            super(itemView);
            avatarIv = itemView.findViewById(R.id.avatarIv);
            usernameTv=itemView.findViewById(R.id.usernameTv);
            emailTv = itemView.findViewById(R.id.emailTv);
            statusTv=itemView.findViewById(R.id.statusTv);
        }
    }

}
