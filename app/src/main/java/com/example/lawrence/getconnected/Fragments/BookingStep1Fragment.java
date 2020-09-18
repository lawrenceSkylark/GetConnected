package com.example.lawrence.getconnected.Fragments;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lawrence.getconnected.Adapters.MyHallAdapter;
import com.example.lawrence.getconnected.Common.Common;
import com.example.lawrence.getconnected.Common.SpacesItemDecoration;
import com.example.lawrence.getconnected.Interface.IAllHallLoadListener;
import com.example.lawrence.getconnected.Interface.IBranchLoadListener;
import com.example.lawrence.getconnected.R;
import com.example.lawrence.getconnected.models.Hall;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.jaredrummler.materialspinner.MaterialSpinner;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import dmax.dialog.SpotsDialog;

public class BookingStep1Fragment extends Fragment implements IAllHallLoadListener, IBranchLoadListener {
    //varibels
    CollectionReference allHallsTRef;
    CollectionReference branchRef;

    IAllHallLoadListener iAllHallLoadListener;
    IBranchLoadListener iBranchLoadListener;

    @BindView(R.id.spinner)
    MaterialSpinner spinner;
    @BindView(R.id.recycle_hall)
    RecyclerView recyclerView_hall;


    Unbinder unbinder;
    AlertDialog waiting_dialog;

    static BookingStep1Fragment instance;
    public static BookingStep1Fragment getInstance() {
        if (instance==null);
        instance=new BookingStep1Fragment();
        return instance;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        allHallsTRef = FirebaseFirestore.getInstance().collection("AllHall");
        iAllHallLoadListener = this;
        iBranchLoadListener = this;
        waiting_dialog = new SpotsDialog.Builder().setContext(getActivity())
                .setMessage("please wait ..")
                .setCancelable(false)
                .build();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View itemView = inflater.inflate(R.layout.fragment_booking_step_one,container,false);
        unbinder = ButterKnife.bind(this,itemView);
        initView();
        loadAllHalls();
        return itemView;

    }

    private void initView() {
        recyclerView_hall.setHasFixedSize(true);
        recyclerView_hall.setLayoutManager(new GridLayoutManager(getActivity(),2));
        recyclerView_hall.addItemDecoration(new SpacesItemDecoration(4));
    }

    private void loadAllHalls() {
        allHallsTRef.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){
                            List <String> list = new ArrayList<>();
                            list.add("please choose branch");
                            for (QueryDocumentSnapshot documentSnapshot:task.getResult())
                                list.add(documentSnapshot.getId());
                            iAllHallLoadListener.OnAllHallLoadSuccess(list);
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                iAllHallLoadListener.OnAllHallLoadFailed(e.getMessage());
            }
        });
    }

    @Override
    public void OnAllHallLoadSuccess(List<String> AreaNameList) {
        spinner.setItems(AreaNameList);
        spinner.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, Object item) {
                if (position > 0)
                {
                    loadHallOfCity(item.toString());
                }
                else recyclerView_hall.setVisibility(View.GONE);
            }
        });

    }


    private void loadHallOfCity(String cityName) {
        waiting_dialog.show();
        Common.city=cityName;
        branchRef = FirebaseFirestore.getInstance()
                .collection("AllHall")
                .document(cityName)
                .collection("Branch");
        branchRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                List <Hall> list = new ArrayList<>();
                if (task.isSuccessful())
                {
                    for (QueryDocumentSnapshot documentSnapshot:task.getResult())
                    {
                        Hall hall = documentSnapshot.toObject(Hall.class);
                        hall.setHallId(documentSnapshot.getId());
                        list.add(hall);
                    }
                    iBranchLoadListener.OnBranchLoadSuccess(list);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                iBranchLoadListener.OnBranchLoadFailed(e.getMessage());
            }
        });
    }

    @Override
    public void OnAllHallLoadFailed(String message) {
        Toast.makeText(getActivity(), message , Toast.LENGTH_SHORT).show();
    }

    @Override
    public void OnBranchLoadSuccess(List<Hall> HallList) {
        MyHallAdapter adapter = new MyHallAdapter(getActivity(),HallList);
        recyclerView_hall.setAdapter(adapter);
        recyclerView_hall.setVisibility(View.VISIBLE);
        waiting_dialog.dismiss();

    }

    @Override
    public void OnBranchLoadFailed(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
        waiting_dialog.dismiss();

    }
}
