package com.dev.chatapp.activities;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.dev.chatapp.R;
import com.dev.chatapp.UserModel;
import com.dev.chatapp.adapters.ChatFirebaseAdapter;
import com.dev.chatapp.interfaces.ClickListenerChatFirebase;
import com.dev.chatapp.model.ChatModel;
import com.dev.chatapp.model.FileModel;
import com.dev.chatapp.model.MapModel;
import com.dev.chatapp.utils.Utility;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static com.dev.chatapp.utils.Constants.IMAGE_CAMERA_REQUEST;
import static com.dev.chatapp.utils.Constants.IMAGE_GALLERY_REQUEST;
import static com.dev.chatapp.utils.Constants.PLACE_PICKER_REQ_CODE;

public class ChatActivity extends AppCompatActivity implements View.OnClickListener,ClickListenerChatFirebase {
    public static final String TAG = ChatActivity.class.getSimpleName();
    private ImageButton camera, gallery, video, location, btn_add, btn_send;
    boolean isClicked = false;
    private EditText et_chat;
    private LinearLayout ll_add;
    FirebaseStorage storage = FirebaseStorage.getInstance();

    static final String CHAT_REFERENCE = "Chat";
    private UserModel userModel;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    private DatabaseReference mFirebaseDatabaseReference;
    private SwipeRefreshLayout message_swipe_layout;


    private LinearLayoutManager mLinearLayoutManager;
    private RecyclerView messages_recycler;



    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);


        requestPermission();

        mLinearLayoutManager = new LinearLayoutManager(this);
        mLinearLayoutManager.setStackFromEnd(true);
        messages_recycler = findViewById(R.id.messages_recycler);



        message_swipe_layout = findViewById(R.id.message_swipe_layout);
        et_chat = findViewById(R.id.et_chat);
        camera = findViewById(R.id.camera);
        gallery = findViewById(R.id.gallery);
        video = findViewById(R.id.video);
        location = findViewById(R.id.location);
        btn_add = findViewById(R.id.btn_add);
        btn_send = findViewById(R.id.btn_send);
        ll_add = findViewById(R.id.ll_add);
        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isClicked) {
                    ll_add.setVisibility(View.VISIBLE);
                    isClicked = true;
                } else {
                    ll_add.setVisibility(View.GONE);
                    isClicked = false;
                }
            }
        });
        camera.setOnClickListener(this);
        gallery.setOnClickListener(this);
        video.setOnClickListener(this);
        location.setOnClickListener(this);
        btn_send.setOnClickListener(this);

        verifyUserAvailableOrNot();

    }

    private void verifyUserAvailableOrNot(){

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser == null){
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }else{
            userModel = new UserModel(firebaseUser.getUid(), firebaseUser.getDisplayName() );
            lerMessagensFirebase();
        }
    }
    private void lerMessagensFirebase(){
        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();
        final ChatFirebaseAdapter firebaseAdapter = new ChatFirebaseAdapter(mFirebaseDatabaseReference.child(CHAT_REFERENCE),userModel.getName(),this);
        firebaseAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                int friendlyMessageCount = firebaseAdapter.getItemCount();
                int lastVisiblePosition = mLinearLayoutManager.findLastCompletelyVisibleItemPosition();
                if (lastVisiblePosition == -1 ||
                        (positionStart >= (friendlyMessageCount - 1) &&
                                lastVisiblePosition == (positionStart - 1))) {
                    messages_recycler.scrollToPosition(positionStart);
                }
            }
        });
        messages_recycler.setLayoutManager(mLinearLayoutManager);
        messages_recycler.setAdapter(firebaseAdapter);
    }



    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void requestPermission() {

        Dexter.withActivity(this)
                .withPermissions(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.CAMERA,
                        Manifest.permission.ACCESS_FINE_LOCATION)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        // check if all permissions are granted
                        if (report.areAllPermissionsGranted()) {
                            // do you work now
                        }

                        // check for permanent denial of any permission
                        if (report.isAnyPermissionPermanentlyDenied()) {
                            Utility.showSettingsDialog(ChatActivity.this);
                            // permission is denied permenantly, navigate user to app settings
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                })
                .onSameThread()
                .check();

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        StorageReference storageRef = storage.getReferenceFromUrl(Utility.URL_STORAGE_REFERENCE).child(Utility.FOLDER_STORAGE_IMG);

        if (requestCode == IMAGE_GALLERY_REQUEST) {
            if (resultCode == RESULT_OK) {
                Uri selectedImageUri = data.getData();
                if (selectedImageUri != null) {
                    sendFileFirebase(storageRef, selectedImageUri);
                } else {
                    //URI IS NULL
                }
            }
        } else if (requestCode == IMAGE_CAMERA_REQUEST) {
            if (resultCode == RESULT_OK) {
              /*  if (filePathImageCamera != null && filePathImageCamera.exists()) {
                    StorageReference imageCameraRef = storageRef.child(filePathImageCamera.getName() + "_camera");
                    sendFileFirebase(imageCameraRef, filePathImageCamera);
                } else {
                    //IS NULL
                }*/
            }
        } else if (requestCode == PLACE_PICKER_REQ_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(this, data);
                if (place != null) {
                    LatLng latLng = place.getLatLng();
                    MapModel mapModel = new MapModel(latLng.latitude + "", latLng.longitude + "");
                   /* ChatModel chatModel = new ChatModel(userModel, Calendar.getInstance().getTime().getTime()+"",mapModel);
                    mFirebaseDatabaseReference.child(CHAT_REFERENCE).push().setValue(chatModel);*/
                } else {
                    //PLACE IS NULL
                }
            }
        }

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.camera:
                openCamera();
                break;
            case R.id.location:
                openLocation();
                break;
            case R.id.gallery:
                openGallery();
                break;
            case R.id.video:
                openVideo();
                break;
            case R.id.btn_send:
               sendMessage();
                break;
            default:
                break;
        }

    }

    private void sendMessage() {
        ChatModel model = new ChatModel(userModel,et_chat.getText().toString(), Calendar.getInstance().getTime().getTime()+"",null);
        mFirebaseDatabaseReference.child(CHAT_REFERENCE).push().setValue(model);
        et_chat.setText(null);
    }

    private void openVideo() {
    }

    private void openGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, getString(R.string.select_picture_title)), IMAGE_GALLERY_REQUEST);

    }

    private void openLocation() {
        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
        try {
            startActivityForResult(builder.build(this), PLACE_PICKER_REQ_CODE);
        } catch (Exception e) {
            Log.e(TAG, e.getStackTrace().toString());
        }
    }

    private void openCamera() {
    }

    @Override
    public void clickImageChat(View view, int position, String nameUser, String urlPhotoUser, String urlPhotoClick) {

    }

    @Override
    public void clickImageMapChat(View view, int position, String latitude, String longitude) {

    }

    private void sendFileFirebase(StorageReference storageReference, final Uri file){
        if (storageReference != null){
            final String name = DateFormat.format("yyyy-MM-dd_hhmmss", new Date()).toString();
            StorageReference imageGalleryRef = storageReference.child(name+"_gallery");
            UploadTask uploadTask = imageGalleryRef.putFile(file);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.e(TAG,"onFailure sendFileFirebase "+e.getMessage());
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Log.i(TAG,"onSuccess sendFileFirebase");
                    Uri downloadUrl = taskSnapshot.getDownloadUrl();
                    FileModel fileModel = new FileModel("img",downloadUrl.toString(),name,"");
                    ChatModel chatModel = new ChatModel(userModel,"",Calendar.getInstance().getTime().getTime()+"",fileModel);
                    mFirebaseDatabaseReference.child(CHAT_REFERENCE).push().setValue(chatModel);
                }
            });
        }else{
            //IS NULL
        }}
}
