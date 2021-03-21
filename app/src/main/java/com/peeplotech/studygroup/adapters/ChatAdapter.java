package com.peeplotech.studygroup.adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import java.util.List;

import com.peeplotech.studygroup.R;
import com.peeplotech.studygroup.databases.Database;
import com.peeplotech.studygroup.models.Chat;
import com.peeplotech.studygroup.models.User;
import com.peeplotech.studygroup.util.Common;

import io.paperdb.Paper;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder>{

    private Activity activity;
    private Context ctx;
    private List<Chat> chatList;
    private String chatTable;

    public ChatAdapter(Activity activity, Context context, List<Chat> chatList, String chatTable) {
        this.activity = activity;
        this.ctx = context;
        this.chatList = chatList;
        this.chatTable = chatTable;
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(ctx).inflate(R.layout.chat_item, parent, false);

        return new ChatViewHolder(itemView);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {

        //get current module
        Chat currentMsg = chatList.get(position);
        User currentUser = new Database(ctx).getUserDetails(currentMsg.getSender());
        User currentUserLogged = Paper.book().read(Common.CURRENT_USER);

        //avatar
        if (!currentUser.getUser_avatar().isEmpty()){
            Picasso.get()
                    .load(Uri.parse(currentUser.getUser_avatar()))
                    .into(holder.senderImage);
        }

        //name and type
        holder.userName.setText("@" + currentUser.getUser_id());
        holder.userType.setText(currentUser.getUser_type());

        //message
        holder.theMessage.setText(currentMsg.getMessage());

        //type
        if (currentUserLogged.getUser_type().equals(Common.USER_TYPE_LECTURER)){
            holder.messageOption.setVisibility(View.VISIBLE);
        } else {
            holder.messageOption.setVisibility(View.INVISIBLE);
            holder.messageOption.setEnabled(false);
        }

        //validation
        if (currentMsg.getIs_approved().equals(Common.IS_APPROVED)){

            holder.verificationProcess.setVisibility(View.VISIBLE);

            holder.messageOption.setOnClickListener(v -> {

                PopupMenu popupInvalidate = new PopupMenu(ctx, holder.messageOption);
                popupInvalidate.inflate(R.menu.invalidate);
                popupInvalidate.setOnMenuItemClickListener(item -> {

                    switch (item.getItemId()) {

                        case R.id.action_validate:

                            new Database(ctx).changeMessageStatus(chatTable, currentMsg.getId(), Common.NOT_APPROVED);

                            holder.verificationProcess.setVisibility(View.GONE);
                            notifyItemChanged(position);
                            notifyDataSetChanged();
                            return true;

                        default:
                            return false;
                    }
                });

                popupInvalidate.show();

            });

        } else {

            holder.messageOption.setOnClickListener(v -> {

                PopupMenu popupInvalidate = new PopupMenu(ctx, holder.messageOption);
                popupInvalidate.inflate(R.menu.validate);
                popupInvalidate.setOnMenuItemClickListener(item -> {

                    switch (item.getItemId()) {

                        case R.id.action_validate:

                            new Database(ctx).changeMessageStatus(chatTable, currentMsg.getId(), Common.IS_APPROVED);

                            holder.verificationProcess.setVisibility(View.VISIBLE);
                            notifyItemChanged(position);
                            notifyDataSetChanged();
                            return true;

                        default:
                            return false;
                    }
                });

                popupInvalidate.show();

            });

            holder.verificationProcess.setVisibility(View.GONE);

        }

    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }

    public class ChatViewHolder extends RecyclerView.ViewHolder{

        //widgets
        public RoundedImageView senderImage;
        public TextView userName, userType, theMessage;
        public ImageView messageOption, verificationProcess;

        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);

            //init widgets
            senderImage = itemView.findViewById(R.id.senderImage);
            userName = itemView.findViewById(R.id.userName);
            userType = itemView.findViewById(R.id.userType);
            theMessage = itemView.findViewById(R.id.theMessage);
            verificationProcess = itemView.findViewById(R.id.verificationProcess);
            messageOption = itemView.findViewById(R.id.messageOption);

        }

    }

}
