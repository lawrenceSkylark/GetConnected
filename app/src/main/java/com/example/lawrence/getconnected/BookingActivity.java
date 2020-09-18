package com.example.lawrence.getconnected;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.viewpager.widget.ViewPager;

import com.example.lawrence.getconnected.Adapters.MyViewPagerAdapter;
import com.example.lawrence.getconnected.Common.Common;
import com.example.lawrence.getconnected.Common.NonSwipeViewPager;
import com.example.lawrence.getconnected.models.Lecturer;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.shuhart.stepview.StepView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import dmax.dialog.SpotsDialog;

public class BookingActivity extends AppCompatActivity {
    LocalBroadcastManager localBroadcastManager;
    AlertDialog alertDialog;
    CollectionReference lecturerRef;

    @BindView(R.id.step_view)
    StepView stepView;
    @BindView(R.id.viewPager)
    NonSwipeViewPager viewPager;
    @BindView(R.id.previous)
    Button previous;
    @BindView(R.id.Next)
    Button next;

    @OnClick(R.id.previous)
    void previousStep(){
        if (Common.step==3|| Common.step> 0){
            Common.step--;
            viewPager.setCurrentItem(Common.step);
        }

    }

    @OnClick(R.id.Next)
    void nextClick(){

if (Common.step< 3|| Common.step ==0)
{
    Common.step++;
    if (Common.step==1)
    {
        if (Common.currentHall!=null)
            loadLecturerByHall(Common.currentHall.getHallId());
    }
    viewPager.setCurrentItem(Common.step);
}
    }

    private void loadLecturerByHall(String hallId) {

alertDialog.show();

if(!TextUtils.isEmpty(Common.city))
{
    lecturerRef = FirebaseFirestore.getInstance()
            .collection("AllLectureHalls")
            .document(Common.city)
            .collection("Branch")
            .document(hallId)
            .collection("Lecturer");

    lecturerRef.get()
            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                     ArrayList<Lecturer> lecturers = new ArrayList<>();
                     for (QueryDocumentSnapshot lecturerSnapShot:task.getResult())
                     {
                         Lecturer lecturer = lecturerSnapShot.toObject(Lecturer.class);
                     lecturer.setPassword("");
                     lecturer.setLecturerId(lecturerSnapShot.getId());
                     lecturers.add(lecturer);
                     }
                      Intent intent = new Intent(Common.KEY_LECTURER_LOAD_DONE);
                     intent.putParcelableArrayListExtra(Common.KEY_LECTURER_LOAD_DONE,lecturers);
                     localBroadcastManager.sendBroadcast(intent);
                     alertDialog.dismiss();
                }

            }).addOnFailureListener(new OnFailureListener() {
        @Override
        public void onFailure(@NonNull Exception e) {
            alertDialog.dismiss();
        }
    });
}


    }

    private BroadcastReceiver buttonNextReceiver = new BroadcastReceiver() {
       @Override
       public void onReceive(Context context, Intent intent) {
           Common.currentHall = intent.getParcelableExtra(Common.KEY_HALL_STORE);
           next.setEnabled(true);
           setColorButton();
       }
   };

    @Override
    protected void onDestroy() {
        localBroadcastManager.unregisterReceiver(buttonNextReceiver);
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);
        ButterKnife.bind(BookingActivity.this);

        alertDialog = new SpotsDialog.Builder().setContext(this).build();

        localBroadcastManager = LocalBroadcastManager.getInstance(this);
        localBroadcastManager.registerReceiver(buttonNextReceiver,new IntentFilter(Common.KEY_ENABLE_BUTTON_NEXT));


        setUpStepView();
        setColorButton();
        viewPager.setAdapter(new MyViewPagerAdapter(getSupportFragmentManager()));
        viewPager.setOffscreenPageLimit(4);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                stepView.go(i,true);
                if (i==0){
                    previous.setEnabled(false);
                }
                else previous.setEnabled(true);
                setColorButton();

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void setColorButton() {
        if (next.isEnabled()){
            next.setBackgroundResource( R.color.dark_background);

        }
        else {next.setBackgroundResource(R.color.colorBtn);
    }
        if (previous.isEnabled()){
            previous.setBackgroundResource(
                    R.color.dark_background);

        }
        else {previous.setBackgroundResource(R.color.colorBtn);
        }
    }



    private void setUpStepView() {
        List<String>StepList = new ArrayList<>();
        StepList.add("Lecture Hall");
        StepList.add("Lecturer");
        StepList.add("Time");
        StepList.add("confirm");
        stepView.setSteps(StepList);

    }
}




