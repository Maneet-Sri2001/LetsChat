package com.example.letschat;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.letschat.Adaptor.MessageAdaptor;
import com.example.letschat.Model.Chat;
import com.example.letschat.Model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


public class MessageActivity extends AppCompatActivity {

    private CircleImageView img;
    private ImageButton back;
    private TextView name, delvStattus;

    private FirebaseUser firebaseUser;
    private DatabaseReference reference;

    MessageAdaptor messageAdaptor;
    List<Chat> chatList;

    private RecyclerView recyclerView;
    private ImageView btnSend;
    private EditText message;
    private String userId;

    private ValueEventListener listener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        Intent intent = getIntent();
        userId = intent.getStringExtra("UserId");
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();


        Toolbar toolbar = findViewById(R.id.msg_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(null);

        back = findViewById(R.id.back);
        img = findViewById(R.id.msg_img);
        name = findViewById(R.id.msg_username);
        btnSend = findViewById(R.id.send);
        message = findViewById(R.id.message);
        delvStattus = findViewById(R.id.delv_status);

        recyclerView = findViewById(R.id.msg_recycler);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        reference = FirebaseDatabase.getInstance().getReference("User").child(userId);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                name.setText(user.getUsername());
                if (user.getImgUrl().equals("default"))
                    img.setImageResource(R.drawable.user);
                else
                    Glide.with(getApplicationContext()).load(user.getImgUrl()).into(img);

                readMessage(firebaseUser.getUid(), userId, user.getImgUrl());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        status();

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = message.getText().toString();
                if (!msg.equals(""))
                    addMessage(firebaseUser.getUid(), userId, msg);
                else
                    Toast.makeText(MessageActivity.this, "Enter Message", Toast.LENGTH_SHORT).show();
                message.setText("");
            }
        });

        final ImageView smiley = findViewById(R.id.emoji);
        smiley.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
            }
        });
    }

    private void status() {
        reference = FirebaseDatabase.getInstance().getReference().child("Chat");
        listener = reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Chat chat = dataSnapshot.getValue(Chat.class);
                    if (chat.getReceiver().equals(firebaseUser.getUid()) && chat.getSender().equals(userId)) {
                        HashMap<String, Object> map = new HashMap<>();
                        map.put("Status", true);
                        dataSnapshot.getRef().updateChildren(map);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void addMessage(String uid, final String userId, String msg) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

        HashMap<String, Object> map = new HashMap<>();
        map.put("Sender", uid);
        map.put("Receiver", userId);
        map.put("Message", msg);
        map.put("Status", false);

        reference.child("Chat").push().setValue(map);
    }

    private void readMessage(final String myId, final String userId, final String imgUrl) {
        chatList = new ArrayList<>();
        reference = FirebaseDatabase.getInstance().getReference("Chat");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                chatList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Chat chat = dataSnapshot.getValue(Chat.class);
                    if (chat.getReceiver().equals(myId) && chat.getSender().equals(userId) ||
                            chat.getReceiver().equals(userId) && chat.getSender().equals(myId))
                        chatList.add(chat);
                }
                messageAdaptor = new MessageAdaptor(MessageActivity.this, chatList, imgUrl);
                recyclerView.setAdapter(messageAdaptor);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private void status(String status) {
        reference = FirebaseDatabase.getInstance().getReference("User").child(firebaseUser.getUid());
        HashMap<String, Object> map = new HashMap<>();
        map.put("Status", status);

        reference.updateChildren(map);
    }

    @Override
    protected void onResume() {
        super.onResume();
        status("online");
    }

    @Override
    protected void onPause() {
        super.onPause();
        reference.removeEventListener(listener);
        status("offline");
    }
}