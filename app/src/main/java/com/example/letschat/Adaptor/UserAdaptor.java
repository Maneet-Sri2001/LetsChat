package com.example.letschat.Adaptor;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.letschat.MessageActivity;
import com.example.letschat.Model.Chat;
import com.example.letschat.Model.User;
import com.example.letschat.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserAdaptor extends RecyclerView.Adapter<UserAdaptor.ViewHolder> {

    private Context context;
    private List<User> userList;
    private boolean isChat;

    String msg;

    public UserAdaptor(Context context, List<User> userList, boolean isChat) {
        this.context = context;
        this.userList = userList;
        this.isChat = isChat;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.user_item, parent, false);
        return new UserAdaptor.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final User user = userList.get(position);
        holder.name.setText(user.getUsername());
        if (user.getImgUrl().equals("default"))
            holder.img.setImageResource(R.drawable.user);
        else
            Glide.with(context).load(user.getImgUrl()).into(holder.img);

        if (isChat)
            if (user.getStatus().equals("online"))
                holder.on.setVisibility(View.VISIBLE);
            else
                holder.off.setVisibility(View.VISIBLE);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, MessageActivity.class);
                intent.putExtra("UserId", user.getID());
                context.startActivity(intent);
            }
        });

        if (isChat)
            lastmsg(user.getID(), holder.lastMsg);
        else
            holder.lastMsg.setVisibility(View.GONE);

        final Dialog mDia = new Dialog(context);
        mDia.setContentView(R.layout.dialog);

        holder.img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView diaName = mDia.findViewById(R.id.dia_name),
                        diamail = mDia.findViewById(R.id.dia_email),
                        diaInfo = mDia.findViewById(R.id.dia_info);
                ImageView diaImg = mDia.findViewById(R.id.diaImg);

                Glide.with(context).load(user.getImgUrl()).into(diaImg);
                diaName.setText(user.getUsername());
                diamail.setText(user.getEmail());
                diaInfo.setText(user.getInfo());
                mDia.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private CircleImageView img;
        private TextView name, on, off, lastMsg;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.user_name);
            lastMsg = itemView.findViewById(R.id.last_msg);
            img = itemView.findViewById(R.id.user_img);
            on = itemView.findViewById(R.id.user_status_on);
            off = itemView.findViewById(R.id.user_status_off);
        }
    }

    private void lastmsg(final String userId, final TextView tv) {
        msg = "none";
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Chat");
        try {
            ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        Chat chat = dataSnapshot.getValue(Chat.class);
                        assert chat != null;
                        assert firebaseUser != null;
                        if (chat.getReceiver().equals(firebaseUser.getUid()) && chat.getSender().equals(userId)
                                || chat.getReceiver().equals(userId) && chat.getSender().equals(firebaseUser.getUid()))
                            msg = chat.getMessage();

                    }
                    if (msg.equals("none"))
                        tv.setText("");
                    else
                        tv.setText(msg);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        } catch (Exception e) {
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}
