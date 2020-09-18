package com.example.lawrence.getconnected.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lawrence.getconnected.Common.Common;
import com.example.lawrence.getconnected.Interface.IRecyclerItemSelectedListener;
import com.example.lawrence.getconnected.R;
import com.example.lawrence.getconnected.models.Hall;

import java.util.ArrayList;
import java.util.List;

public class MyHallAdapter extends RecyclerView.Adapter<MyHallAdapter.MyViewHolder> {
    Context context;
    List <Hall> hallList;
    List<CardView> cardViewList;
    LocalBroadcastManager localBroadcastManager;

    public MyHallAdapter(Context context, List<Hall> hallList) {
        this.context = context;
        this.hallList = hallList;
        cardViewList= new ArrayList<>();
        localBroadcastManager = LocalBroadcastManager.getInstance(context);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(context)
                .inflate(R.layout.layout_lecture_hall,viewGroup,false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {
        myViewHolder.txt_hall_name.setText(hallList.get(i).getName());
        myViewHolder.txt_hall_address.setText(hallList.get(i).getAddress());
        if (!cardViewList.contains(myViewHolder.card_hall))
            cardViewList.add(myViewHolder.card_hall);
        myViewHolder.setiRecyclerItemSelectedListener(new IRecyclerItemSelectedListener() {
            @Override
            public void onItemSelectedListener(View view, int pos) {
                for (CardView cardView:cardViewList)
                    cardView.setCardBackgroundColor(context.getResources().getColor(android.R.color.white));
                myViewHolder.card_hall.setCardBackgroundColor(context.getResources().getColor(android.R.color.holo_orange_dark));

                Intent intent = new Intent(Common.KEY_ENABLE_BUTTON_NEXT);
                intent.putExtra(Common.KEY_HALL_STORE,hallList.get(pos));
                localBroadcastManager.sendBroadcast(intent);
            }
        });


    }

    @Override
    public int getItemCount() {
        return hallList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView txt_hall_name,txt_hall_address;
        CardView card_hall;

        IRecyclerItemSelectedListener iRecyclerItemSelectedListener;

        public void setiRecyclerItemSelectedListener(IRecyclerItemSelectedListener iRecyclerItemSelectedListener) {
            this.iRecyclerItemSelectedListener = iRecyclerItemSelectedListener;
        }

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            txt_hall_name= (TextView)itemView.findViewById(R.id.text_hall_name);
            txt_hall_address= (TextView)itemView.findViewById(R.id.text_hall_address);
            card_hall= (CardView)itemView.findViewById(R.id.card_hall);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            iRecyclerItemSelectedListener.onItemSelectedListener(view,getAdapterPosition());
        }
    }
}
