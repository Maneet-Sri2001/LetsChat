package com.example.letschat.Fragment;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.letschat.EditProfActivity;
import com.example.letschat.Model.User;
import com.example.letschat.PasswordActivity;
import com.example.letschat.R;
import com.example.letschat.StartActivity;
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

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;

public class ProfileFragment extends Fragment {

    private CircleImageView img;
    private TextView name;
    private TextView email;
    private TextView resetPass;
    private TextView logOut;
    private TextView editProf;
    private TextView changeImg;
    private TextView info;

    private FirebaseUser firebaseUser;
    private DatabaseReference reference;

    private StorageReference storageReference;
    private static final int IMG_REQ = 1;
    private Uri imgUri;
    private String pic;
    private StorageTask uploadTask;

    private Context context;

    @Override

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_profile, container, false);

        context = getActivity();

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("User").child(firebaseUser.getUid());
        storageReference = FirebaseStorage.getInstance().getReference("Upload");

        img = view.findViewById(R.id.profF_img);
        name = view.findViewById(R.id.profF_name);
        email = view.findViewById(R.id.profF_email);
        resetPass = view.findViewById(R.id.profF_reset_pass);
        logOut = view.findViewById(R.id.profF_logout);
        editProf = view.findViewById(R.id.profF_edit_prof);
        changeImg = view.findViewById(R.id.profF_change_prof);
        info = view.findViewById(R.id.profF_info);


        changeImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImg();
            }
        });

        final Dialog mDia = new Dialog(context);
        mDia.setContentView(R.layout.display_img);
        mDia.getWindow();

        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImageView diaImg = mDia.findViewById(R.id.dis_img);
                Glide.with(context).load(pic).into(diaImg);
                mDia.show();
            }
        });

        editProf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), EditProfActivity.class));
            }
        });

        logOut.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(context, StartActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            }
        });

        resetPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), PasswordActivity.class));
            }
        });

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                final User user = snapshot.getValue(User.class);
                name.setText(user.getUsername());
                email.setText(user.getEmail());
                info.setText(user.getInfo());
                if (user.getImgUrl().equals("default"))
                    img.setImageResource(R.drawable.user);
                else {
                    Glide.with(context.getApplicationContext()).load(user.getImgUrl()).into(img);
                    pic = user.getImgUrl();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        /*img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImg();
            }
        });*/
        return view;
    }

    private void openImg() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, IMG_REQ);
    }

    private String getExt(Uri uri) {
        ContentResolver resolver = getContext().getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(resolver.getType(uri));
    }

    private void uploadImg() {
        final ProgressDialog pd = new ProgressDialog(getContext());
        pd.setMessage("Uploading");
        pd.show();

        if (imgUri != null) {
            final StorageReference storageReference1 = storageReference.child(System.currentTimeMillis()
                    + "." + getExt(imgUri));

            uploadTask = storageReference1.putFile(imgUri);
            uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }
                    return storageReference1.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri dUri = task.getResult();
                        String uri = dUri.toString();

                        reference = FirebaseDatabase.getInstance().getReference("User").child(firebaseUser.getUid());
                        HashMap<String, Object> map = new HashMap<>();
                        map.put("ImgUrl", uri);
                        reference.updateChildren(map);

                        pd.dismiss();

                    } else {
                        pd.dismiss();
                        Toast.makeText(getContext(), "Try Again", Toast.LENGTH_SHORT).show();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            pd.dismiss();
            Toast.makeText(getContext(), "No Image Selected", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == IMG_REQ && resultCode == RESULT_OK &&
                data != null && data.getData() != null)
            imgUri = data.getData();
        if (uploadTask != null && uploadTask.isInProgress())
            Toast.makeText(getContext(), "Upload in Progress", Toast.LENGTH_SHORT).show();
        else
            uploadImg();
    }
}