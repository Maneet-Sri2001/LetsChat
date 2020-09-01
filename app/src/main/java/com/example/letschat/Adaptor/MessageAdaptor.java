package com.example.letschat.Adaptor;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.letschat.Model.Chat;
import com.example.letschat.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

public class MessageAdaptor extends RecyclerView.Adapter<MessageAdaptor.ViewHolder> {

    private Context context;
    private List<Chat> chatList;
    private String imgUrl;

    private FirebaseUser firebaseUser;

    public static final int MSG_TYPE_REC = 0;
    public static final int MSG_TYPE_SEND = 1;

    public MessageAdaptor(Context context, List<Chat> chatList, String imgUrl) {
        this.context = context;
        this.chatList = chatList;
        this.imgUrl = imgUrl;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if (viewType == MSG_TYPE_SEND) {
            View view = LayoutInflater.from(context).inflate(R.layout.chat_send, parent, false);
            return new MessageAdaptor.ViewHolder(view);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.chat_rec, parent, false);
            return new MessageAdaptor.ViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final Chat chat = chatList.get(position);
        holder.msg.setText(chat.getMessage());
        if (position == (chatList.size() - 1)) {
            if (chat.isStatus())
                holder.status.setText("Seen");
            else holder.status.setText("Delivered");
        } else
            holder.status.setVisibility(View.GONE);
    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView msg;
        private TextView status;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            status = itemView.findViewById(R.id.delv_status);
            msg = itemView.findViewById(R.id.msg);
        }
    }

    @Override
    public int getItemViewType(int position) {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (chatList.get(position).getSender().equals(firebaseUser.getUid()))
            return MSG_TYPE_SEND;
        else
            return MSG_TYPE_REC;
    }
}

