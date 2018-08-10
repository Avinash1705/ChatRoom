package com.example.avinash.chatroom;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    private EditText mName;
    private EditText mEmail;
    private EditText mPassword;
    private Button mSignUp;

    private FirebaseAuth mChatAuth;
    private DatabaseReference mChatDatabaseUsers;

    private ProgressDialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mName = findViewById(R.id.name_textfield);
        mEmail = findViewById(R.id.email_textfield);
        mPassword = findViewById(R.id.password_textfield);
        mSignUp = findViewById(R.id.registration_button);

        mChatAuth = FirebaseAuth.getInstance();
        mChatDatabaseUsers = FirebaseDatabase.getInstance().getReference().child("Users");

        mDialog = new ProgressDialog(this);

        mSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signupProcess();
            }
        });

    }

    private void signupProcess() {

        final String name = mName.getText().toString().trim();
        final String email = mEmail.getText().toString().trim();
        String password = mPassword.getText().toString().trim();

        if (checkFields()){

            mDialog.setMessage("Signing Up...");
            mDialog.show();

            mChatAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if (task.isSuccessful()){

                                String user_id = mChatAuth.getCurrentUser().getUid();

                                //DatabaseReference current_user_db = mChatDatabaseUsers.child(user_id);

                                //current_user_db.child("user_name").setValue(name);

                                mDialog.dismiss();

                                Intent mainIntent = new Intent(MainActivity.this, Login.class);
                                mainIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(mainIntent);
                            }
                        }
                    });
        }
    }
    boolean checkFields(){
        if(mName.getText().toString().isEmpty()){
            mName.setError("Required.");
            return false;
        }
        if (mEmail.getText().toString().isEmpty()){
            mEmail.setError("Invalid Email.");
            return false;
        }
        if (mPassword.getText().toString().isEmpty()){
            mPassword.setError("Required.");
            return false;
        }
        return true;
    }
}
