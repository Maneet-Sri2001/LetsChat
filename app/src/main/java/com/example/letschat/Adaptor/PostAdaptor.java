package com.example.letschat.Adaptor;

import android.app.ProgressDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.letschat.Model.PostModel;
import com.example.letschat.Model.User;
import com.example.letschat.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class PostAdaptor extends RecyclerView.Adapter<PostAdaptor.ViewHolder> {

    private Context context;
    private List<PostModel> postModelList;

    private FirebaseUser firebaseUser;

    public PostAdaptor(Context context, List<PostModel> postModelList) {
        this.context = context;
        this.postModelList = postModelList;
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.post_item, parent, false);
        return new PostAdaptor.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        final PostModel post = postModelList.get(position);

        Glide.with(context).load(post.getImgURL()).into(holder.postImg);

        FirebaseDatabase.getInstance().getReference().child("User").child(post.getPublisher()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                try {
                    Glide.with(context).load(user.getImgUrl()).into(holder.profImg);
                } catch (Exception e) {
                    holder.profImg.setImageResource(R.drawable.user);
                }
                holder.profId.setText(user.getUsername());
                holder.user.setText(user.getUsername().toUpperCase() + ",  " + post.getDescription());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        isLiked(post.getPostId(), holder.like);
        noLikes(post.getPostId(), holder.noLike);

        if (post.getPublisher().equals(firebaseUser.getUid()))
            holder.del.setVisibility(View.VISIBLE);

        holder.del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ProgressDialog pd = new ProgressDialog(context);
                pd.setMessage("Deleting");
                pd.show();
                FirebaseDatabase.getInstance().getReference("Post").child(post.getPostId()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful())
                            pd.dismiss();
                    }
                });
            }
        });

        holder.like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.like.getTag().equals("Like")) {
                    FirebaseDatabase.getInstance().getReference().child("Like").child(post.getPostId())
                            .child(firebaseUser.getUid()).setValue(true);
                } else
                    FirebaseDatabase.getInstance().getReference().child("Like").child(post.getPostId())
                            .child(firebaseUser.getUid()).removeValue();
            }
        });

    }

    @Override
    public int getItemCount() {
        return postModelList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView like, del;
        public ImageView postImg;
        public CircleImageView profImg;

        public TextView profId, noLike, user;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            like = itemView.findViewById(R.id.post_like);
            del = itemView.findViewById(R.id.del_post);

            postImg = itemView.findViewById(R.id.post_img);
            profImg = itemView.findViewById(R.id.post_profile_img);

            profId = itemView.findViewById(R.id.post_profile_id);
            noLike = itemView.findViewById(R.id.no_like);
            user = itemView.findViewById(R.id.post_user);

        }
    }

    private void isLiked(String postId, final ImageView img) {
        FirebaseDatabase.getInstance().getReference().child("Like")
                .child(postId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child(firebaseUser.getUid()).exists()) {
                    img.setImageResource(R.drawable.liked);
                    img.setTag("Liked");
                } else {
                    img.setImageResource(R.drawable.like);
                    img.setTag("Like");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void noLikes(String postId, final TextView txt) {
        FirebaseDatabase.getInstance().getReference().child("Like")
                .child(postId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                txt.setText(snapshot.getChildrenCount() + " Likes");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
