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

import com.example.lawrence.getconnected.R;
import com.example.lawrence.getconnected.models.ModelChat;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class AdapterChat extends RecyclerView.Adapter<AdapterChat.MyHolder>{
    private static  final int MSG_TYPE_LEFT =0;
    private static  final int MSG_TYPE_RIGHT =1;
    private Context context;
    List<ModelChat> ChatList;
    String imageUr1;

    public AdapterChat(Context context, List<ModelChat> chatList, String imageUr1) {
        this.context = context;
        ChatList = chatList;
        this.imageUr1 = imageUr1;
    }

    private FirebaseUser fUser;

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        if (i==MSG_TYPE_RIGHT){

        View view =LayoutInflater.from(context).inflate(R.layout.row_chat_right, viewGroup, false);
        return new MyHolder(view);
        }
        else {
            View view = LayoutInflater.from(context).inflate(R.layout.row_chat_left,viewGroup,false);
            return new MyHolder(view);

        }
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder myHolder, int i) {
    String message = ChatList.get(i).getMessage();
    String timestamp = ChatList.get(i).getTimestamp();
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(Long.parseLong(timestamp));
        String datetime = DateFormat.format("dd/MM/yyyy hh:mm aa",cal).toString();
          myHolder.messageTv.setText(message);
          myHolder.timeTv.setText(datetime);
          try
          {
              Picasso.get().load(imageUr1).into(myHolder.profileIv);
          }
          catch (Exception e)
          {
            // set seen/ delivered status of message
            if (i==ChatList.size()-1)
            {
                if (ChatList.get(i).isSeen())
                {
                    myHolder.isSeenTv.setText("Seen");
                }
                else
                {
                    myHolder.isSeenTv.setText("Delivered");
                }
            }
            else myHolder.isSeenTv.setVisibility(View.GONE);
          }

    }

    @Override
    public int getItemCount() {
        return ChatList.size();
    }

    @Override
    public int getItemViewType(int position) {
        fUser = FirebaseAuth.getInstance().getCurrentUser();
        if (ChatList.get(position).getSender().equals(fUser.getUid()))
       return MSG_TYPE_RIGHT;
        else {
            return MSG_TYPE_LEFT;
        }
    }

    // view  class
    class MyHolder extends RecyclerView.ViewHolder{
       ImageView profileIv;
        TextView messageTv,timeTv,isSeenTv;
        public MyHolder(@NonNull View itemView) {
            super(itemView);

            // int views
            profileIv = itemView.findViewById(R.id.profileIv);
            messageTv= itemView.findViewById(R.id.messageTv);
            timeTv= itemView.findViewById(R.id.timeTv);
            isSeenTv = itemView.findViewById(R.id.isSeenTv);
        }
    }
}
