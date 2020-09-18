package com.example.lawrence.getconnected;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class DashboardActivity extends AppCompatActivity {
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        firebaseAuth = FirebaseAuth.getInstance();
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNav);
        bottomNavigationView.setOnNavigationItemSelectedListener(navListerner);
        FragmentTransaction begin =getSupportFragmentManager().beginTransaction();
        begin.replace(R.id.fragmentNavcontainer,new GroupChannelsFragment());
        begin.commit();    }
private BottomNavigationView.OnNavigationItemSelectedListener navListerner =new BottomNavigationView.OnNavigationItemSelectedListener() {
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Fragment fragmentSelected = null;
        switch (item.getItemId()) {
            case R.id.action_home:
                setTitle("My channels");
                GroupChannelsFragment fragmen1 = new GroupChannelsFragment();
                FragmentTransaction f1 = getSupportFragmentManager().beginTransaction();
                f1.replace(R.id.fragmentNavcontainer, fragmen1, "");
                f1.commit();
                return true;
            case R.id.contacts:setTitle("Users");
                UserFragment fragmen2 = new UserFragment();
                FragmentTransaction f2 = getSupportFragmentManager().beginTransaction();
                f2.replace(R.id.fragmentNavcontainer, fragmen2, "");
                f2.commit();
                return true;
            case R.id.ChatList:setTitle("Users");
            ChatListFragment fragmen3 = new ChatListFragment();
                FragmentTransaction f3 = getSupportFragmentManager().beginTransaction();
                f3.replace(R.id.fragmentNavcontainer, fragmen3, "");
                f3.commit();
                return true;
            case R.id.more:
                setTitle("More");
                Intent intent2 = new Intent(DashboardActivity.this, AccountActivity.class);
                startActivity(intent2);
                break;


        }
        return false;
    }
};

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_profile:
                Intent acc=new Intent(this, GroupParticipantsAddActivity.class);
                startActivity(acc);
                return true;
                    case R.id.action_setting:
                        return true;

            default:return super.onOptionsItemSelected(item);
        }
    }
}
