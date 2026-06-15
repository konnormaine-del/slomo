package com.example.videoproofpoc;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
    private static final int REQUEST_CAMERA_PERMISSION = 10;
    private static final int REQUEST_VIDEO_CAPTURE = 20;

    private TextView statusText;
    private Uri lastVideoUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        buildUi();
    }

    private void buildUi() {
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setGravity(Gravity.CENTER);
        root.setPadding(48, 48, 48, 48);

        TextView title = new TextView(this);
        title.setText("Video Proof POC");
        title.setTextSize(26);
        title.setGravity(Gravity.CENTER);
        root.addView(title);

        statusText = new TextView(this);
        statusText.setText("Tap the button to open the camera and record a video.");
        statusText.setGravity(Gravity.CENTER);
        statusText.setPadding(0, 32, 0, 32);
        root.addView(statusText);

        Button recordButton = new Button(this);
        recordButton.setText("Record Video");
        recordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startVideoFlow();
            }
        });
        root.addView(recordButton);

        Button shareButton = new Button(this);
        shareButton.setText("Share Last Video");
        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareLastVideo();
            }
        });
        root.addView(shareButton);

        setContentView(root);
    }

    private void startVideoFlow() {
        if (android.os.Build.VERSION.SDK_INT >= 23 && checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
            return;
        }
        launchCamera();
    }

    private void launchCamera() {
        Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        takeVideoIntent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 60);
        takeVideoIntent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
        if (takeVideoIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takeVideoIntent, REQUEST_VIDEO_CAPTURE);
        } else {
            Toast.makeText(this, "No camera app found.", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CAMERA_PERMISSION && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            launchCamera();
        } else {
            Toast.makeText(this, "Camera permission is needed to record video.", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_VIDEO_CAPTURE) {
            if (resultCode == RESULT_OK && data != null) {
                lastVideoUri = data.getData();
                statusText.setText("Video saved/returned:\n" + lastVideoUri);
            } else {
                statusText.setText("No video captured.");
            }
        }
    }

    private void shareLastVideo() {
        if (lastVideoUri == null) {
            Toast.makeText(this, "Record a video first.", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("video/*");
        shareIntent.putExtra(Intent.EXTRA_STREAM, lastVideoUri);
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(Intent.createChooser(shareIntent, "Share proof video"));
    }
}
