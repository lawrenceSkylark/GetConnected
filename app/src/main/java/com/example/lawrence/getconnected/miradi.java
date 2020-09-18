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

public class miradi extends Fragment implements View.OnClickListener{
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.miradi,container,false);
        Button btnshare=(Button)view.findViewById(R.id.btnshare);
        btnshare.setOnClickListener(this);
        //then return in on create view.
        return view;
   }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(getActivity(), GroupChatActivity.class);
        startActivity(intent);
    }
}
