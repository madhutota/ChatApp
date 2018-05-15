package com.dev.chatapp;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.UsersViewHolder> {

    @NonNull
    @Override
    public UsersAdapter.UsersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull UsersAdapter.UsersViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class UsersViewHolder extends RecyclerView.ViewHolder {
        public UsersViewHolder(View itemView) {
            super(itemView);
        }
    }
}
