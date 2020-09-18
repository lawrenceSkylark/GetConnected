package com.example.lawrence.getconnected;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class SignUpActivity extends AppCompatActivity {

    private EditText inputEmail, inputPassword,inputPhone,inputUsername;
    private Button register ;
    private TextView Loginuser;
    private ProgressBar progressBar;
    private ProgressDialog progressDialog;
    private FirebaseAuth mAuth;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        //Get Firebase auth instance
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        Loginuser = (TextView) findViewById(R.id.Loginuser);
        register = (Button) findViewById(R.id.register);
        inputUsername = (EditText) findViewById(R.id.username);
        inputEmail = (EditText) findViewById(R.id.email);
        inputPhone = (EditText) findViewById(R.id.phone);
        inputPassword = (EditText) findViewById(R.id.password);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        progressDialog= new ProgressDialog(this);
        progressDialog.setMessage("registering ....");
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                  String email = inputEmail.getText().toString().trim();
                  String password= inputPassword.getText().toString().trim();
                  String phone=inputPhone.getText().toString().trim();
                  String username=inputUsername.getText().toString().trim();
                  if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                      inputEmail.setError("invalid email");
                      inputEmail.setFocusable(true);

                  }
                  else if (password.length()<6){
                      inputPassword.setError("pasword too short");
                      inputPassword.setFocusable(true);

                  }
                  else registerUser(email,password,username,phone);
            }
        });
        Loginuser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(SignUpActivity.this,MainActivity.class));
            }
        });

        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users");

    }

    private void registerUser(String email, String password,String username,String phone) {
        //email and password patterns is succeful register user and show progges dialog
      progressDialog.show();
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success,dismiss dialog and strt register activity
                            progressDialog.dismiss();
                            FirebaseUser user = mAuth.getCurrentUser();
                            String email = user.getEmail();
                            String uid = user.getUid();
                            HashMap<Object,String> hashMap = new HashMap<>();
                            hashMap.put("email",email);
                            hashMap.put("onlineStatus","online");
                            hashMap.put("typingTo","noOne");
                            hashMap.put("uid",uid);
                            hashMap.put("phone",phone);
                            hashMap.put("username",username);
                            hashMap.put("image","");
                            hashMap.put("cover","");
                            // path to store data
                            FirebaseDatabase database = FirebaseDatabase.getInstance();
                            DatabaseReference reference = database.getReference("Users");
                            reference.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(hashMap);
                            Toast.makeText(SignUpActivity.this, "registered successfully"+user.getEmail(), Toast.LENGTH_SHORT).show();
                       startActivity(new Intent(SignUpActivity.this,MainActivity.class));
                       finish();
                        } else {
                            // If sign in fails, display a message to the user.
                            progressDialog.dismiss();

                            Toast.makeText(SignUpActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }

                        // ...
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(SignUpActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}