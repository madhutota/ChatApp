package com.dev.chatapp;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.dev.chatapp.activities.ChatActivity;
import com.dev.chatapp.activities.SplashActivity;
import com.dev.chatapp.utils.Constants;
import com.dev.chatapp.utils.Preference;
import com.dev.chatapp.utils.Utility;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {

    private RecyclerView mUsersList;

    private DatabaseReference mFriendsDatabase;
    private DatabaseReference mUsersDatabase;
    private DatabaseReference mUserDatabaseReference;

    private FirebaseAuth mAuth;

    private String mCurrent_user_id;
    private DatabaseReference mUserRef;
    FirebaseUser currentUser;
    String LoginUSer = "";
    String mCurrentUserProfileImage = "";



    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        inITUI();
    }



    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void inITUI() {
        mAuth = FirebaseAuth.getInstance();
        mCurrent_user_id = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
        mUsersList = findViewById(R.id.recycler_view);

        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        mUsersDatabase.keepSynced(true);

        mUserDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(mCurrent_user_id);
        mUserDatabaseReference.keepSynced(true);


        mUserRef = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid());

        mUsersList.setHasFixedSize(true);
        mUsersList.setLayoutManager(new LinearLayoutManager(this));

        mUserDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                LoginUSer  = dataSnapshot.child("name").getValue().toString();
                mCurrentUserProfileImage = dataSnapshot.child("image").getValue().toString();
                // String status = dataSnapshot.child("status").getValue().toString();
                String thumb_image = dataSnapshot.child("thumb_image").getValue().toString();
                if (!mCurrentUserProfileImage.equals("default")) {


                }
                Preference.setPrefStringData(MainActivity.this, Constants.LOGIN_USER,LoginUSer);
                Preference.setPrefStringData(MainActivity.this, Constants.LOGIN_USER_PROFILE,mCurrentUserProfileImage);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });





    }

    @Override
    protected void onStart() {
        super.onStart();
        currentUser = mAuth.getCurrentUser();

        if (currentUser == null) {
            startActivity(new Intent(MainActivity.this, SplashActivity.class));

        } else {

            mUserRef.child("online").setValue("true");

        }
        FirebaseRecyclerAdapter<UserModel, UsersViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<UserModel, UsersViewHolder>(

                UserModel.class,
                R.layout.users_single_layout,
                UsersViewHolder.class,
                mUsersDatabase

        ) {
            @Override
            protected void populateViewHolder(final UsersViewHolder usersViewHolder, final UserModel users, int position) {


                final String user_id = getRef(position).getKey();

                mUsersDatabase.child(user_id).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        final String userName = dataSnapshot.child("name").getValue().toString();

                        if (!user_id.equalsIgnoreCase(mCurrent_user_id)) {
                        }


                        usersViewHolder.setDisplayName(users.getName());
                        usersViewHolder.setUserImage(users.getThumb_image(), getApplicationContext());
                        if (dataSnapshot.hasChild("online")) {

                            String userOnline = dataSnapshot.child("online").getValue().toString();
                            usersViewHolder.setUserStatus(userOnline);

                        }
                        usersViewHolder.mView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                               // Toast.makeText(MainActivity.this, "" + user_id, Toast.LENGTH_SHORT).show();

                        Intent profileIntent = new Intent(MainActivity.this, ChatActivity.class);
                        profileIntent.putExtra("user_id", user_id);
                        profileIntent.putExtra("user_name", userName);
                        startActivity(profileIntent);

                            }
                        });
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


            }
        };


        mUsersList.setAdapter(firebaseRecyclerAdapter);

    }

    public static class UsersViewHolder extends RecyclerView.ViewHolder {

        View mView;

        public UsersViewHolder(View itemView) {
            super(itemView);

            mView = itemView;

        }

        public void setDisplayName(String name) {

            TextView userNameView = (TextView) mView.findViewById(R.id.user_single_name);
            userNameView.setText(name);

        }

        public void setUserStatus(String status) {

            TextView userStatusView = (TextView) mView.findViewById(R.id.user_single_status);
            userStatusView.setText(status);


        }

        public void setUserImage(String thumb_image, Context ctx) {

            CircleImageView userImageView = (CircleImageView) mView.findViewById(R.id.user_single_image);

            Picasso.with(ctx).load(thumb_image).placeholder(R.drawable.default_avatar).into(userImageView);

        }


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser == null) {
            startActivity(new Intent(MainActivity.this, SplashActivity.class));
        } else {
            mUserRef.child("online").setValue("false");
        }
    }


}
