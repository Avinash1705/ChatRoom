package com.example.avinash.chatroom;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


    public class Login extends AppCompatActivity {

        private EditText mEmail, mPassword;
        private Button loginBtn;
        private TextView creatAccountBtn;


        private FirebaseAuth mChatAuth;
        private DatabaseReference mChatDatabaseUsers;

        private ProgressDialog mDialog;
        private static int RC_SIGN_IN = 1;
        private static final String TAG = "kant";
        private GoogleApiClient mGoogleApiClient;


        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_login);

            mEmail = findViewById(R.id.email_edittext);
            mPassword = findViewById(R.id.password_edittext);
            loginBtn = findViewById(R.id.login_button);
            creatAccountBtn = findViewById(R.id.signup_textview);

            mDialog = new ProgressDialog(this);

            mChatAuth = FirebaseAuth.getInstance();
            mChatDatabaseUsers = FirebaseDatabase.getInstance().getReference().child("Users");
            // mChatDatabaseUsers.keepSynced(true);

            loginBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    checklogin();
                }
            });

            creatAccountBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(Login.this, MainActivity.class));
                }
            });
        }

        private void checklogin() {

            String email = mEmail.getText().toString().trim();
            String password = mPassword.getText().toString().trim();

            if (checkFields()) {

                mDialog.setMessage("Checking Login");
                mDialog.show();

                mChatAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {

                            mDialog.dismiss();

                        } else {
                            mDialog.dismiss();

                            Toast.makeText(Login.this, "Authentication Failed.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }

        boolean checkFields() {
            if (mEmail.getText().toString().isEmpty()){
                mEmail.setError("Required..");
                return false;
            }
            if (mPassword.getText().toString().isEmpty()) {
                mPassword.setError("Required.");
                return false;
            }
            return true;
        }





}