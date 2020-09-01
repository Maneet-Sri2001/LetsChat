package com.example.letschat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.rengwuxian.materialedittext.MaterialEditText;

public class PasswordActivity extends AppCompatActivity {

    private MaterialEditText email;
    private Button reset;
    private ImageButton back;

    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password);

        Toolbar toolbar = findViewById(R.id.reset_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(null);

        email = findViewById(R.id.pass_email);
        reset = findViewById(R.id.btn_pass_reset);
        firebaseAuth = FirebaseAuth.getInstance();
        back = findViewById(R.id.pass_back);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mail = email.getText().toString();

                if (mail.equals("") )
                    Toast.makeText(PasswordActivity.this, "Invalid Details", Toast.LENGTH_SHORT).show();
                else
                    firebaseAuth.sendPasswordResetEmail(mail).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful())
                                Toast.makeText(PasswordActivity.this, "Please Follow Instruction given in link on Email Id.", Toast.LENGTH_SHORT).show();
                            else
                                Toast.makeText(PasswordActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
            }
        });
    }
}