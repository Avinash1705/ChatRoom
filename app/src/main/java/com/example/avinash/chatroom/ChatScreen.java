package com.example.avinash.chatroom;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ChatScreen extends AppCompatActivity {
    private static final String TAG = "rawat";
    public EditText Forward;
    public Button send;
    private RecyclerView recyclerView;
    private FirebaseAuth.AuthStateListener mChatAuthListener;
    private DatabaseReference mChatDatabase;
    private FirebaseAuth mChatAuth;
    private FirebaseRecyclerAdapter<Chat, ChatViewHolder> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_screen);
        Forward = findViewById(R.id.forward_edittext);
        send = findViewById(R.id.send_button);
        recyclerView = findViewById(R.id.recycler);

        mChatAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                if (firebaseAuth.getCurrentUser() == null) {

                    Intent loginIntent = new Intent(ChatScreen.this, Login.class);
                    loginIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(loginIntent);
                    finish();
                }
            }
        };
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMsg();
            }
        });

        LinearLayoutManager mLinearLayoutManager;
        mLinearLayoutManager = new LinearLayoutManager(this);
        // mLinearLayoutManager.setReverseLayout(true);
        recyclerView.setLayoutManager(mLinearLayoutManager);
        mChatDatabase = FirebaseDatabase.getInstance().getReference().child("Chats");
        mChatAuth = FirebaseAuth.getInstance();

        //to scroll recycler view when keyboard pops up or goes down
        recyclerView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View view, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                if (bottom != oldBottom) {
                    recyclerView.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            recyclerView.scrollToPosition(adapter.getItemCount() - 1);
                        }
                    }, 100);
                }
            }
        });
    }

    private void sendMsg() {
        DatabaseReference newPost = mChatDatabase.push();
        newPost.child("message").setValue(Forward.getText().toString().trim());
        newPost.child("user_id").setValue(mChatAuth.getCurrentUser().getUid());
        Forward.setText("");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart: ");
        mChatAuth.addAuthStateListener(mChatAuthListener);
        adapter = new FirebaseRecyclerAdapter<Chat, ChatViewHolder>(
                Chat.class,
                R.layout.chat_row,
                ChatViewHolder.class,
                mChatDatabase) {
            @Override
            protected void populateViewHolder(ChatViewHolder viewHolder, Chat model, int position) {
                Log.d(TAG, "populateViewHolder: "+model.getMessage());
                    viewHolder.setMsg(model.getMessage());
            }
        };
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    public static class ChatViewHolder extends RecyclerView.ViewHolder {
        View mView;
        TextView msgv;
        public ChatViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            Log.d(TAG, "ChatViewHolder: ");
        }

        public void setMsg(String msg) {
            msgv = mView.findViewById(R.id.message_view);
            msgv.setText(msg);
        }
    }

}
