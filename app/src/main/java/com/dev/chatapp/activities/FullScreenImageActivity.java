package com.dev.chatapp.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.dev.chatapp.R;
import com.dev.chatapp.views.TouchImageView;

public class FullScreenImageActivity extends AppCompatActivity {
    private TouchImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen_image);
        imageView = findViewById(R.id.imageView);
    }
}
