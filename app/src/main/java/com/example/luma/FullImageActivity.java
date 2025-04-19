package com.example.luma;
import android.util.Log;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import android.Manifest; // استيراد الحزمة التي تحتوي على READ_EXTERNAL_STORAGE
import android.content.pm.PackageManager; // استيراد الكلاس PackageManager
import androidx.annotation.NonNull; // استيراد الحزمة التي تحتوي على @NonNull
import android.widget.Toast; // استيراد الكلاس Toast
import android.os.Build; //// استيراد الكلاس Build
/*public class FullImageActivity extends AppCompatActivity {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_image);

        ImageView fullImageView = findViewById(R.id.fullImageView);
        String imageUriString = getIntent().getStringExtra("IMAGE_URI");

        if (imageUriString != null) {
            Uri imageUri = Uri.parse(imageUriString);
            try {
                fullImageView.setImageURI(imageUri); // عرض الصورة
            } catch (Exception e) {
                Log.e("FullImageActivity", "Failed to load image: " + e.getMessage());
            }
        } else {
            Log.e("FullImageActivity", "Image URI is null");
        }
    }

}*/



















public class FullImageActivity extends AppCompatActivity {

@Override
protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_full_image);

    ImageView fullImageView = findViewById(R.id.fullImageView);
    String imageUriString = getIntent().getStringExtra("IMAGE_URI");

    if (imageUriString != null) {
        Uri imageUri = Uri.parse(imageUriString);

        // تسجيل URI للتحقق
        Log.d("FullImageActivity", "Received Image URI: " + imageUriString);

        try {
            fullImageView.setImageURI(imageUri); // عرض الصورة
        } catch (Exception e) {
            Log.e("FullImageActivity", "Failed to load image: " + e.getMessage());
            Toast.makeText(this, "Failed to load image", Toast.LENGTH_SHORT).show();
        }
    } else {
        Log.e("FullImageActivity", "Image URI is null");
        Toast.makeText(this, "Image URI is null", Toast.LENGTH_SHORT).show();
    }
}}