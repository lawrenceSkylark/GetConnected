package com.example.lawrence.getconnected.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lawrence.getconnected.R;
import com.example.lawrence.getconnected.models.Lecturer;

import java.util.List;

public class MyLecturerAdapter extends RecyclerView.Adapter<MyLecturerAdapter.MyViewHolder> {


    Context context;
    List<Lecturer>lecturerList;

    public MyLecturerAdapter(Context context, List<Lecturer> lecturerList) {
        this.context = context;
        this.lecturerList = lecturerList;
    }



    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View itemView = LayoutInflater.from(context)
                .inflate(R.layout.layout_lecturer,viewGroup,false);

        return new MyLecturerAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {
   myViewHolder.txt_lecturer_name.setText(lecturerList.get(i).getName());
   myViewHolder.ratingBar.setRating((float)lecturerList.get(i).getRating());
    }

    @Override
    public int getItemCount() {
        return lecturerList.size();
    }

    public class MyViewHolder extends  RecyclerView.ViewHolder{

        TextView txt_lecturer_name;
        RatingBar ratingBar;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            txt_lecturer_name = (TextView)itemView.findViewById(R.id.txt_lecturer_name);
            ratingBar= (RatingBar)itemView.findViewById(R.id.rtb_lecturer);
        }
    }
}
