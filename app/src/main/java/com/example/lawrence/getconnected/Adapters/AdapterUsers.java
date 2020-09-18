package com.example.lawrence.getconnected.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lawrence.getconnected.PrivateChatActivity;
import com.example.lawrence.getconnected.R;
import com.example.lawrence.getconnected.models.ModelUser;
import com.squareup.picasso.Picasso;

import java.util.List;

public class AdapterUsers extends RecyclerView.Adapter<AdapterUsers.MyHolder>{
    Context context;
    List<ModelUser> userList;

    public AdapterUsers(Context context, List<ModelUser> userList) {
        this.context = context;
        this.userList = userList;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup ViewGroup, int i) {
        View view = LayoutInflater .from(context).inflate(R.layout.row_users, ViewGroup,false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder myholder, int i) {
        String hisUID =userList.get(i).getUid();
        String UseruserImage = userList.get(i).getImage();
     String Userusername =userList.get(i).getUsername();
     String UseruserEmail =userList.get(i).getEmail();
     // set data
     myholder.mNameTv.setText(Userusername);
     myholder.mEmailTv.setText(UseruserEmail);
     try {
         Picasso.get().load(UseruserImage)
                 .placeholder(R.drawable.ic_default)
                 .into(myholder.mAvatar);
     }
     catch (Exception e){

     }
     myholder.itemView.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             Intent intent = new Intent(context,PrivateChatActivity.class);
            intent.putExtra("hisUid",hisUID);
            context.startActivity(intent);
         }
     });
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    class MyHolder extends RecyclerView.ViewHolder{
         ImageView mAvatar;
         TextView mNameTv,mEmailTv;
        public MyHolder(@NonNull View itemView) {

            super(itemView);
            mAvatar=itemView.findViewById(R.id.avatarIV);
            mNameTv=itemView.findViewById(R.id.NameTV);
            mEmailTv=itemView.findViewById(R.id.EmailTv);
        }
    }
}

