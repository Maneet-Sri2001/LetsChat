package com.example.letschat;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.example.letschat.Adaptor.PagerAdaptor;
import com.example.letschat.Fragment.ChatFragment;
import com.example.letschat.Fragment.ProfileFragment;
import com.example.letschat.Fragment.StoryFragment;
import com.example.letschat.Fragment.UserFragment;
import com.example.letschat.Model.User;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {

    private CircleImageView profImg;
    private TextView username;
    private FirebaseUser firebaseUser;
    private DatabaseReference reference;

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private PagerAdaptor adaptor;

    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = MainActivity.this;

        Toolbar toolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");

        profImg = findViewById(R.id.profile_img);
        username = findViewById(R.id.username);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("User").child(firebaseUser.getUid());

        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);
        adaptor = new PagerAdaptor(getSupportFragmentManager());

        // Add Fragment
        adaptor.addFrag(new ChatFragment(), "");
        adaptor.addFrag(new StoryFragment(), "");
        adaptor.addFrag(new UserFragment(), "");
        adaptor.addFrag(new ProfileFragment(), "");

        viewPager.setAdapter(adaptor);
        tabLayout.setupWithViewPager(viewPager);

        //for icon
        tabLayout.getTabAt(0).setIcon(R.drawable.chat);
        tabLayout.getTabAt(1).setIcon(R.drawable.story);
        tabLayout.getTabAt(2).setIcon(R.drawable.user_add);
        tabLayout.getTabAt(3).setIcon(R.drawable.profile);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                username.setText(user.getUsername());
                if (user.getImgUrl().equals("default"))
                    profImg.setImageResource(R.mipmap.ic_launcher);
                else
                    Glide.with(getApplicationContext()).load(user.getImgUrl()).into(profImg);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @SuppressLint("ResourceType")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logout:
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(MainActivity.this, LoginActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                finish();
                return true;
            case R.id.edit_profile:
                startActivity(new Intent(MainActivity.this, EditProfActivity.class));
                return true;
            case R.id.dark:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                startActivity(new Intent(context, MainActivity.class));
                finish();
                return true;
            case R.id.light:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                startActivity(new Intent(context, MainActivity.class));
                finish();
                getApplication().setTheme(R.style.AppTheme);
                return true;
        }
        return true;
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
        status("offline");
    }
}