package com.example.letschat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.letschat.Fragment.ProfileFragment;
import com.example.letschat.Model.User;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditProfActivity extends AppCompatActivity {

    private ImageButton back;
    private MaterialEditText username, email, info;
    private CircleImageView img;
    private Button update;

    private FirebaseUser firebaseUser;
    private DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_prof);

        Toolbar toolbar = findViewById(R.id.edit_profile_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(null);

        back = findViewById(R.id.back);
        username = findViewById(R.id.edit_username);
        email = findViewById(R.id.edit_email);
        info = findViewById(R.id.edit_info);
        img = findViewById(R.id.edit_profF_img);
        update = findViewById(R.id.btn_update);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("User").child(firebaseUser.getUid());

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(EditProfActivity.this, MainActivity.class));
                finish();
            }
        });

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                username.setText(user.getUsername());
                email.setText(user.getEmail());
                info.setText(user.getInfo());
                if (user.getImgUrl().equals("default"))
                    img.setImageResource(R.drawable.plane_icon);
                else
                    Glide.with(getApplicationContext()).load(user.getImgUrl()).into(img);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(EditProfActivity.this, "Image can not be change from here.", Toast.LENGTH_SHORT).show();
            }
        });

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ProgressDialog pd = new ProgressDialog(EditProfActivity.this);
                pd.setMessage("Updating");
                pd.show();
                String name = username.getText().toString(),
                        mail = email.getText().toString(),
                        inf = info.getText().toString();
                reference = FirebaseDatabase.getInstance().getReference("User").child(firebaseUser.getUid());
                HashMap<String, Object> map = new HashMap<>();
                map.put("Username", name);
                map.put("Email", mail);
                map.put("Info", inf);
                reference.updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        pd.dismiss();
                        if (task.isSuccessful())
                            Toast.makeText(EditProfActivity.this, "Updated Successfully", Toast.LENGTH_SHORT).show();
                        else
                            Toast.makeText(EditProfActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
}