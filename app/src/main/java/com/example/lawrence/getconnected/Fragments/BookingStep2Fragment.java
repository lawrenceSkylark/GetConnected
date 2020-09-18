package com.example.lawrence.getconnected.Fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lawrence.getconnected.Adapters.MyLecturerAdapter;
import com.example.lawrence.getconnected.Common.Common;
import com.example.lawrence.getconnected.Common.SpacesItemDecoration;
import com.example.lawrence.getconnected.R;
import com.example.lawrence.getconnected.models.Lecturer;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class BookingStep2Fragment extends Fragment {

     Unbinder unbinder;
     LocalBroadcastManager localBroadcastManager;

     @BindView(R.id.recycler_lecturer)
    RecyclerView recyclerView_lecturer;
     private BroadcastReceiver lecturerDoneReceiver = new BroadcastReceiver() {
         @Override
         public void onReceive(Context context, Intent intent) {
             ArrayList<Lecturer> lecturerArrayList = intent.getParcelableArrayListExtra(Common.KEY_LECTURER_LOAD_DONE);
             MyLecturerAdapter adapter= new MyLecturerAdapter(getContext(),lecturerArrayList);
             recyclerView_lecturer.setAdapter(adapter);
         }
     };

    static BookingStep2Fragment instance;
    public static BookingStep2Fragment getInstance() {
        if (instance==null);
        instance=new BookingStep2Fragment();
        return instance;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        localBroadcastManager= LocalBroadcastManager.getInstance(getContext());
        localBroadcastManager.registerReceiver(lecturerDoneReceiver,new IntentFilter(Common.KEY_LECTURER_LOAD_DONE));
    }

    @Override
    public void onDestroy() {
        localBroadcastManager.unregisterReceiver(lecturerDoneReceiver);
        super.onDestroy();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View itemView = inflater.inflate(R.layout.fragment_booking_step_two,container,false);
       unbinder = ButterKnife.bind(this,itemView);
       initView();
        return itemView;
        
    }

    private void initView() {
        recyclerView_lecturer.setHasFixedSize(true);
        recyclerView_lecturer.setLayoutManager(new GridLayoutManager(getActivity(),2));
        recyclerView_lecturer.addItemDecoration(new SpacesItemDecoration(4));
    }
}
