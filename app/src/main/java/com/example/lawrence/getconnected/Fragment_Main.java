package com.example.lawrence.getconnected;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class Fragment_Main extends Fragment implements View.OnClickListener {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        Button btnadmin=(Button)view.findViewById(R.id.btnadmin);
        btnadmin.setOnClickListener(this::onClick);
        Button btnstf=(Button)view.findViewById(R.id.btnstf);
        btnstf.setOnClickListener(this::onClick);
        Button btnlec=(Button)view.findViewById(R.id.btnlec);
        btnlec.setOnClickListener(this::onClick);
        Button btnstd=(Button)view.findViewById(R.id.btnstd);
        btnstd.setOnClickListener(this::onClick);
        //then return in on create view.
        return view;
    }


    @Override
    public void onClick(View v) {
        Intent intent = new Intent(getActivity(), DashboardActivity.class);
        startActivity(intent);

    }

}