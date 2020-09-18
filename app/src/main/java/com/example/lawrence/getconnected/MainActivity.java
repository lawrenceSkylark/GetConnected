package com.example.lawrence.getconnected;

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
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;


public class MainActivity extends AppCompatActivity {

    private EditText inputEmail, inputPassword;
    private FirebaseAuth mAuth;
    private ProgressBar progressBarlogin;
    private Button  btnLogin, btnReset;
    TextView newUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Get Firebase auth instance
        mAuth = FirebaseAuth.getInstance();

        if (mAuth.getCurrentUser() != null) {
            startActivity(new Intent(MainActivity.this, SplashScreenActivity.class));
            finish();
        }

        // set the view now
        setContentView(R.layout.activity_main);

        // Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        // setSupportActionBar(toolbar);

        inputEmail = (EditText) findViewById(R.id.email);
        inputPassword = (EditText) findViewById(R.id.password);
        progressBarlogin = (ProgressBar) findViewById(R.id.progressBarlogin);
        newUser = (TextView) findViewById(R.id.newUser);
        btnLogin = (Button) findViewById(R.id.loginaccount);
        btnReset = (Button) findViewById(R.id.btn_reset_password);

        //Get Firebase auth instance
        mAuth = FirebaseAuth.getInstance();

        newUser.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, SignUpActivity.class)));

        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, ResetPasswordActivity.class));
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = inputEmail.getText().toString();
                final String password = inputPassword.getText().toString();

                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                    inputEmail.setError("invalid email");
                    inputEmail.setFocusable(true);

                }

                if (password.length()<6){
                    inputPassword.setError("password too short");
                    inputPassword.setFocusable(true);

                }

                progressBarlogin.setVisibility(View.VISIBLE);

                //authenticate user
                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                // If sign in fails, display a message to the user. If sign in succeeds
                                // the auth state listener will be notified and logic to handle the
                                // signed in user can be handled in the listener.
                                progressBarlogin.setVisibility(View.GONE);
                                if (!task.isSuccessful()) {
                                    // there was an error
                                    if (password.length() < 6) {
                                        inputPassword.setError(getString(R.string.minimum_password));
                                    } else {
                                        Toast.makeText(MainActivity.this, getString(R.string.auth_failed), Toast.LENGTH_LONG).show();
                                    }
                                } else {
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    String email = user.getEmail();
                                    String uid = user.getUid();
                                    HashMap<Object,String> hashMap = new HashMap<>();
                                    hashMap.put("email", email);
                                    hashMap.put("uid", uid);
                                    hashMap.put("onlineStatus", "online");
                                    hashMap.put("typingTo","noOne");
                                    hashMap.put("username", "");
                                    hashMap.put("phone", "");
                                    hashMap.put("image","");
                                    hashMap.put("cover","");
                                    // path to store data
                                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                                    DatabaseReference reference = database.getReference("Users");
                                    reference.child(uid).setValue(hashMap);
                                    Intent intent = new Intent(MainActivity.this, MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                            }
                        });
            }
        });
    }
}