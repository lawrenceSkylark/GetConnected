package com.example.lawrence.getconnected.Adapters;

import android.content.Context;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lawrence.getconnected.models.ModelGroupChat;
import com.example.lawrence.getconnected.R;
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

public class AdapterGroupChat extends RecyclerView.Adapter<AdapterGroupChat.HolderGroupChat> {
    private static  final int MSG_TYPE_LEFT =0;
    private static  final int MSG_TYPE_RIGHT =1;
    private Context context;
    private ArrayList<ModelGroupChat> modelGroupChatList;
    private FirebaseAuth firebaseAuth;

    public AdapterGroupChat(Context context, ArrayList<ModelGroupChat> modelGroupChatList) {
        this.context = context;
        this.modelGroupChatList = modelGroupChatList;
        firebaseAuth =FirebaseAuth.getInstance();
    }

    @NonNull
    @Override
    public HolderGroupChat onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       // inflate layouts
        if (viewType==MSG_TYPE_RIGHT){
            View view = LayoutInflater.from(context).inflate(R.layout.row_groupchat_right,parent,false);
            return new HolderGroupChat(view);
        }else
        {
            View view = LayoutInflater.from(context).inflate(R.layout.row_groupchat_left,parent,false);
            return new HolderGroupChat(view);

        }
    }

    @Override
    public void onBindViewHolder(@NonNull HolderGroupChat holder, int position) {
ModelGroupChat model = modelGroupChatList.get(position);
// get data
String message = model.getMessage();
String timestamp = model.getTimestamp();
String SenderUid = model.getSender();
String messageType =model.getType();
//  convert time to
        Calendar cal  = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(Long.parseLong(timestamp));
        String dateTime= (String) DateFormat.format("dd/MM/yyyy hh:mm aa",cal).toString();
        // set data
        if(messageType.equals("text")){
            //text message
            holder.timeTv.setVisibility(View.VISIBLE);
            holder.messageTv.setText(message);
        }
        else {
            //image message,hide imageview,show message tv
            holder.messageTv.setVisibility(View.GONE);
            holder.timeTv.setVisibility(View.VISIBLE);
            holder.messageIv.setVisibility(View.VISIBLE);
            try{
                Picasso.get().load(message).placeholder(R.drawable.imagepic).into(holder.messageIv);
            }catch (Exception e){
                holder.messageIv.setImageResource(R.drawable.imagepic);
            }
        }


        holder.timeTv.setText(dateTime);
        holder.usernameTv.setText(SenderUid);
        setUserName(model, holder);


    }

    private void setUserName(ModelGroupChat model, HolderGroupChat holder) {
        // get uid of the sender from the model
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.orderByChild("uid").equalTo(model.getSender())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                      for (DataSnapshot ds:dataSnapshot.getChildren()){
                          String username = ""+ds.child("username").getValue();
                          holder.usernameTv.setText(username);
                      }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    @Override
    public int getItemCount() {
        return modelGroupChatList.size() ;
    }

    @Override
    public int getItemViewType(int position) {
if (modelGroupChatList.get(position).getSender().equals(firebaseAuth.getUid()))
{
    return MSG_TYPE_RIGHT;
}
else {
    return MSG_TYPE_LEFT;
}
    }

    class HolderGroupChat extends RecyclerView.ViewHolder {
      private TextView usernameTv,messageTv,timeTv;
      private ImageView messageIv;
        public HolderGroupChat(@NonNull View itemView) {
            super(itemView);
        usernameTv =itemView.findViewById(R.id.usernameTv);
            messageTv =itemView.findViewById(R.id.messageTv);
            timeTv =itemView.findViewById(R.id.timeTv);
            messageIv =itemView.findViewById(R.id.messageIv);
        }
    }
}
