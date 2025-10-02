package pop.wallpaper.uhd;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import pop.wallpaper.uhd.Adapters.StorageWallpapers;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class PihersActivity extends AppCompatActivity {

    // Define a constant for the permission request code
    private static final int REQUEST_STORAGE_PERMISSION = 1;
    SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private StorageWallpapers adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pihers);

        recyclerView = findViewById(R.id.picherrv);
        swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));

        // Check if the app has permission to read external storage
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted, request it from the user
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_STORAGE_PERMISSION);
        } else {
            // Permission is already granted, proceed to load and display images
            loadImages();
        }


        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Refresh action triggered, reload images
                loadImages();
            }
        });


    }

    private void loadImages() {
        List<File> imageFiles = getImageFilesFromDirectory();
        adapter = new StorageWallpapers(this, imageFiles);
        recyclerView.setAdapter(adapter);
        swipeRefreshLayout.setRefreshing(false);
    }

    // Add onRequestPermissionsResult to handle the user's response to the permission request
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_STORAGE_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, load and display images
                loadImages();
            } else {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    // Permission is not granted, request it from the user
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_STORAGE_PERMISSION);
                } else {
                    // Permission is already granted, proceed to load and display images
                    loadImages();
                }
            }
        }
    }

    @NonNull
    private List<File> getImageFilesFromDirectory() {
        List<File> imageFiles = new ArrayList<>();
        try {
            File dir;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), getApplication().getString(R.string.app_name));
            } else {
                dir = new File(Environment.getExternalStorageDirectory(), getApplication().getString(R.string.app_name));
            }

            if (!dir.exists() && !dir.mkdirs()) {
                // Directory creation failed
                return imageFiles; // Return an empty list
            }

            // Iterate through the files in the directory and add image files to the list
            File[] files = dir.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isFile() && isImageFile(file.getName())) {
                        imageFiles.add(file);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return imageFiles;
    }

    private boolean isImageFile(String fileName) {
        // You can check the file extension to determine if it's an image file
        return fileName.endsWith(".jpg") || fileName.endsWith(".png") || fileName.endsWith(".jpeg");
    }

}