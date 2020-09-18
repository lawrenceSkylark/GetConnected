package com.example.lawrence.getconnected;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.navigation.NavigationView;

public class homeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout drawer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        FragmentTransaction begin =getSupportFragmentManager().beginTransaction();
        begin.replace(R.id.fragment_container,new Fragment_Main());
        begin.commit();
        Toolbar toolbar= findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawer = findViewById(R.id.Drawer_layout);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        TextView editprof = (TextView)navigationView.getHeaderView(0).findViewById(R.id.editProfile);
        editprof.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(homeActivity.this, "hello world", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
                startActivity(intent);
            }
        });

        navigationView.setNavigationItemSelectedListener(this);
        ActionBarDrawerToggle toggle= new ActionBarDrawerToggle( this ,drawer,toolbar,R.string.navigation_drawer_open,R.string.navigation_drawer_closed);
        drawer.addDrawerListener(toggle);
        toggle.syncState();    }


    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)){
            drawer.closeDrawer(GravityCompat.START);
        } else{
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.Home:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new Fragment_Main()).commit();
                break;
            case R.id.share:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new Fragment_share()).commit();
                break;
            case R.id.channels:
                GroupChannelsFragment channel = new GroupChannelsFragment();
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,channel,"")
                        .commit();

                break;
            case R.id.miradi:
                        Intent intent2 = new Intent(homeActivity.this,BookingActivity.class);

                        startActivity(intent2);
                        break;
            case R.id.mail:
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_APP_EMAIL);
                getApplication().startActivity(intent);
                break;
            case R.id.create:
                Intent i = new Intent(homeActivity.this,CreateChannelActivity.class);
                startActivity(i);
                finish();
                break;
            case R.id.Help:

                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new Fragment_Help()).commit();
                break;
            case R.id.Developer:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new Fragment_Developer()).commit();
                break;


        }
        drawer.closeDrawer(GravityCompat.START);

        return true;
    }


}
