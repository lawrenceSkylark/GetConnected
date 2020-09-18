package com.example.lawrence.getconnected;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lawrence.getconnected.Adapters.AdapterGroupChatList;
import com.example.lawrence.getconnected.models.ModelGroupChatlist;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

public class GroupChannelsFragment extends Fragment {
    private RecyclerView groupsRs;
    private FirebaseAuth firebaseAuth;

    private ArrayList<ModelGroupChatlist>groupChatlists;
    private AdapterGroupChatList adapterGroupChatList;
    public GroupChannelsFragment() {
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        android.view.View v = inflater.inflate(R.layout.fragment_channels, container, false);
        groupsRs =v.findViewById(R.id.groupsRs);
        firebaseAuth = FirebaseAuth.getInstance();
        loadGRoupChatList();

        return v;
    }

    private void loadGRoupChatList() {
        groupChatlists =new ArrayList<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Groups");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                groupChatlists.size();
                for(DataSnapshot ds:dataSnapshot.getChildren()){
                    // if current user"s uid xist in participants list of groups thenshow that grp
                    if (ds.child("participants").child(Objects.requireNonNull(firebaseAuth.getUid())).exists()){
                        ModelGroupChatlist model = ds.getValue(ModelGroupChatlist.class);
                        groupChatlists.add(model);
                    }
                }
                adapterGroupChatList = new AdapterGroupChatList(getActivity(),groupChatlists);
                groupsRs.setAdapter(adapterGroupChatList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}