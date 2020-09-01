package com.example.letschat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    private DatabaseReference ref;
    private FirebaseAuth auth;

    private MaterialEditText username, email, password;
    private Button btnRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        FirebaseApp.initializeApp(RegisterActivity.this);

        Toolbar toolbar = findViewById(R.id.register_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Register User");

        btnRegister = findViewById(R.id.btn_reg_register);
        username = findViewById(R.id.reg_username);
        email = findViewById(R.id.reg_email);
        password = findViewById(R.id.reg_password);

        ref = FirebaseDatabase.getInstance().getReference("User");
        auth = FirebaseAuth.getInstance();

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String user = username.getText().toString(),
                        mail = email.getText().toString(),
                        pass = password.getText().toString();
                if (TextUtils.isEmpty(user) | TextUtils.isEmpty(pass) | TextUtils.isEmpty(mail))
                    Toast.makeText(RegisterActivity.this, "Email and Password must be entered", Toast.LENGTH_SHORT).show();
                else if (pass.length() < 6)
                    Toast.makeText(RegisterActivity.this, "Password can't less than 6 character", Toast.LENGTH_SHORT).show();
                else
                    registerUser(user, mail, pass);
            }
        });

    }

    private void registerUser(final String user, final String mail, String pass) {
        final ProgressDialog pd = new ProgressDialog(this);
        pd.setMessage("Registering User");
        pd.show();
        auth.createUserWithEmailAndPassword(mail, pass).addOnSuccessListener(RegisterActivity.this, new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                HashMap<String, Object> map = new HashMap<>();
                map.put("ID", auth.getCurrentUser().getUid());
                map.put("Username", user);
                map.put("Email", mail);
                map.put("ImgUrl", "default");
                map.put("Search", user.toLowerCase());

                ref.child(auth.getCurrentUser().getUid()).setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            pd.dismiss();
                            Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish();
                        }
                    }
                });
            }
        }).addOnFailureListener(RegisterActivity.this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                pd.dismiss();
                Toast.makeText(RegisterActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}