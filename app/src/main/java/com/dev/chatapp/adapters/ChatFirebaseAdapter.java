package com.dev.chatapp.adapters;

import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.dev.chatapp.R;
import com.dev.chatapp.activities.ChatActivity;
import com.dev.chatapp.interfaces.ClickListenerChatFirebase;
import com.dev.chatapp.model.ChatModel;
import com.dev.chatapp.model.MapModel;
import com.dev.chatapp.utils.Utility;
import com.dev.chatapp.views.CircleTransform;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.squareup.picasso.Picasso;

import hani.momanii.supernova_emoji_library.Helper.EmojiconTextView;

public class ChatFirebaseAdapter extends FirebaseRecyclerAdapter<ChatModel, ChatFirebaseAdapter.ChatViewHolder> {

    private ClickListenerChatFirebase mClickListenerChatFirebase;

    private static final int RIGHT_MSG = 0;
    private static final int LEFT_MSG = 1;
    private static final int RIGHT_MSG_IMG = 2;
    private static final int LEFT_MSG_IMG = 3;
    private String nameUser;
    ChatActivity chatActivity;

    public ChatFirebaseAdapter(DatabaseReference ref, String nameUser, ClickListenerChatFirebase mClickListenerChatFirebase, ChatActivity chatActivity) {
        super(ChatModel.class, R.layout.item_message_left, ChatFirebaseAdapter.ChatViewHolder.class, ref);
        this.nameUser = nameUser;
        this.mClickListenerChatFirebase = mClickListenerChatFirebase;
        this.chatActivity = chatActivity;
    }


    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == RIGHT_MSG) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_right, parent, false);
            return new ChatViewHolder(view);
        } else if (viewType == LEFT_MSG) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_left, parent, false);
            return new ChatViewHolder(view);
        } else if (viewType == RIGHT_MSG_IMG) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_right_img, parent, false);
            return new ChatViewHolder(view);
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_left_img, parent, false);
            return new ChatViewHolder(view);
        }
    }

    @Override
    public int getItemViewType(int position) {
        ChatModel model = getItem(position);
        if (model.getMapModel() != null) {
            if (model.getUserModel().getName().equals(nameUser)) {
                return RIGHT_MSG_IMG;
            } else {
                return LEFT_MSG_IMG;
            }
        } else if (model.getFile() != null) {
            if (model.getFile().getType().equals("img") && model.getUserModel().getName().equals(nameUser)) {
                return RIGHT_MSG_IMG;
            } else {
                return LEFT_MSG_IMG;
            }
        } else if (model.getUserModel().getName().equals(nameUser)) {
            return RIGHT_MSG;
        } else {
            return LEFT_MSG;
        }
    }


    @Override
    protected void populateViewHolder(ChatViewHolder viewHolder, ChatModel model, int position) {
        viewHolder.setIvUser(model.getUserModel().getProfile_image());
        viewHolder.setTxtMessage(model.getMessage());
        viewHolder.setTvTimestamp(model.getTimeStamp());
        viewHolder.tvIsLocation(View.GONE, null);
        if (model.getFile() != null) {
            viewHolder.tvIsLocation(View.GONE, null);
            viewHolder.setIvChatPhoto(model.getFile().getUrl_file());
        } else if (model.getMapModel() != null) {
            viewHolder.setIvChatPhoto(Utility.local(model.getMapModel().getLatitude(), model.getMapModel().getLongitude()));
            viewHolder.tvIsLocation(View.VISIBLE, model.getMapModel());
        }
    }

    public class ChatViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView tvTimestamp, tvLocation;
        EmojiconTextView txtMessage;
        ImageView ivUser, ivChatPhoto;


        public ChatViewHolder(View itemView) {
            super(itemView);
            tvTimestamp = (TextView) itemView.findViewById(R.id.timestamp);
            txtMessage = (EmojiconTextView) itemView.findViewById(R.id.txtMessage);
            tvLocation = (TextView) itemView.findViewById(R.id.tvLocation);
            ivChatPhoto = (ImageView) itemView.findViewById(R.id.img_chat);
            ivUser = (ImageView) itemView.findViewById(R.id.ivUserChat);
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            ChatModel model = getItem(position);
            if (model.getMapModel() != null) {
                mClickListenerChatFirebase.clickImageMapChat(view, position, model.getMapModel().getLatitude(), model.getMapModel().getLongitude());
            } else {
                mClickListenerChatFirebase.clickImageChat(view, position, model.getUserModel().getName(), model.getUserModel().getProfile_image(), model.getFile().getUrl_file());
            }
        }

        public void setTxtMessage(String message) {
            if (txtMessage == null) return;
            txtMessage.setText(message);
        }

        public void setIvUser(String urlPhotoUser) {
            if (ivUser == null) return;
            if (urlPhotoUser != null) {
                Picasso.with(ivUser.getContext()).
                        load(urlPhotoUser).centerCrop().resize(40, 40)
                        .placeholder(R.drawable.default_avatar).into(ivUser);
            }





            /*Glide.with(ivUser.getContext()).load(urlPhotoUser).
                    transform(new CircleTransform(ivUser.getContext())).override(40, 40).into(ivUser);*/
        }

        public void setTvTimestamp(String timestamp) {
            if (tvTimestamp == null) return;
            tvTimestamp.setText(converteTimestamp(timestamp));
        }

        public void setIvChatPhoto(String url) {
            if (ivChatPhoto == null) return;


            Picasso.with(ivChatPhoto.getContext()).load(url).centerCrop().resize(100, 100)
                    .placeholder(R.drawable.default_avatar).into(ivChatPhoto);

            ivChatPhoto.setOnClickListener(this);
        }

        public void tvIsLocation(int visible, MapModel mapModel) {
            if (tvLocation == null) return;
            tvLocation.setVisibility(visible);
            if (mapModel != null) {
                tvLocation.setText(chatActivity.getResources().getString(R.string.address_text, mapModel.getPlaceName(), mapModel.getAddress(),
                        System.currentTimeMillis()));
            }
        }
    }

    private CharSequence converteTimestamp(String mileSegundos) {
        return DateUtils.getRelativeTimeSpanString(Long.parseLong(mileSegundos), System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS);
    }
}
