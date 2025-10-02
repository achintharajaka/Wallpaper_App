package pop.wallpaper.uhd;

import static pop.wallpaper.uhd.Config.BOTH;
import static pop.wallpaper.uhd.Config.HOME_SCREEN;
import static pop.wallpaper.uhd.Config.LOCK_SCREEN;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import pop.wallpaper.uhd.Adapters.AdapterWallpaperDetail;
import pop.wallpaper.uhd.Models.WallpapersModel;
import pop.wallpaper.uhd.Utils.Tools;
import pop.wallpaper.uhd.Utils.WallpaperHelper;
import pop.wallpaper.uhd.databinding.ActivityWallpaperBinding;
import pop.wallpaper.uhd.databse.sqlite.DbFavorite;
import pop.wallpaper.uhd.rests.ApiInterface;
import pop.wallpaper.uhd.rests.RestAdapter;
import com.google.android.material.snackbar.Snackbar;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WallpaperActivity extends AppCompatActivity {

    ActivityWallpaperBinding binding;
    String WallpaperID;
    ActionBar actionBar;
    WallpaperHelper wallpaperHelper;
    AdapterWallpaperDetail wallpaperDetail;
    String wallpaperUrl = "";

    ProgressDialog progressDialog;
    CoordinatorLayout parentView;
    ViewPager2 viewPager2;
    List<WallpapersModel> wallpapers = new ArrayList<>();
    boolean flag = true;
    int position;
    ImageView img_favorite;
    DbFavorite dbFavorite;
    boolean isNightMode;
    private String single_choice_selected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Tools.transparentStatusBarNavigation(this);
        binding = ActivityWallpaperBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        int currentNightMode = AppCompatDelegate.getDefaultNightMode();
        isNightMode = currentNightMode == AppCompatDelegate.MODE_NIGHT_YES;

        dbFavorite = new DbFavorite(this);

        img_favorite = findViewById(R.id.img_favorite);
        parentView = binding.coordinatorLayout;

        progressDialog = new ProgressDialog(this);
        wallpaperHelper = new WallpaperHelper(this);


        WallpaperID = getIntent().getStringExtra("WallpaperID");
        position = getIntent().getIntExtra("POSITION", 0);

        Intent intent = getIntent();
        Bundle bundle = intent.getBundleExtra("key.BUNDLE");
        wallpapers = (List<WallpapersModel>) bundle.getSerializable("key.ARRAY_LIST");


        setupToolbar();
        loadView(wallpapers, position);
        setupViewPager(wallpapers);
        //loadWallpapers();

    }

    //    private void loadWallpapers() {
//        DatabaseReference wallpapersRef = FirebaseDatabase.getInstance().getReference("Wallpapers");
//        wallpapersRef.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                for (DataSnapshot ds : snapshot.getChildren()) {
//                    WallpapersModel wallpaper;
//
//                    Object value = ds.getValue();
//
//                    if (value instanceof WallpapersModel) {
//                        wallpaper = (WallpapersModel) value;
//                    } else if (value instanceof Map) {
//                        Map<String, Object> map = (Map<String, Object>) value;
//                        String id = (String) map.get("id");
//                        String title = (String) map.get("title");
//                        String imageUrl = (String) map.get("image");
//                        String categoryid = (String) map.get("categoryid");
//                        wallpaper = new WallpapersModel(id, categoryid, title, imageUrl);
//                    } else {
//                        continue; // Skip invalid data
//                    }
//
//                    wallpapers.add(wallpaper);
//                }
//
//                // Initialize and set up the ViewPager2
//                viewPager2 = binding.getRoot().findViewById(R.id.viewPager2);
//                AdapterWallpaperDetail wallpaperDetail = new AdapterWallpaperDetail(wallpapers, WallpaperActivity.this);
//                viewPager2.setAdapter(wallpaperDetail);
//                viewPager2.setOffscreenPageLimit(wallpapers.size());
//
//                // Ensure the 'position' variable is set correctly before using it
//                int initialPosition = position;
//                if (initialPosition < 0 || initialPosition >= wallpapers.size()) {
//                    initialPosition = 0; // Handle invalid position gracefully
//                }
//
//                viewPager2.setCurrentItem(initialPosition, false);
//
//                TextView title_toolbar = findViewById(R.id.title_toolbar);
//
//                title_toolbar.setText(wallpapers.get(position).getTitle());
//
//                // Register a onPageChangeCallback
//                viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
//                    @Override
//                    public void onPageSelected(int position) {
//                        super.onPageSelected(position);
//                        loadView(wallpapers, position);
//
//                    }
//                });
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//                // Handle onCancelled event if needed
//            }
//        });
//    }
    public void setupViewPager(final List<WallpapersModel> wallpapers) {
        viewPager2 = findViewById(R.id.viewPager2);
        wallpaperDetail = new AdapterWallpaperDetail(wallpapers, this);
        viewPager2.setAdapter(wallpaperDetail);
        viewPager2.setOffscreenPageLimit(wallpapers.size());
        viewPager2.setCurrentItem(position, false);
        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
            }

            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                loadView(wallpapers, position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                super.onPageScrollStateChanged(state);
            }
        });
    }


    public void loadView(final List<WallpapersModel> wallpapers, int position) {

        wallpaperUrl = wallpapers.get(position).getImage().replace(" ", "%20");

        TextView title_toolbar = findViewById(R.id.title_toolbar);

        title_toolbar.setText(wallpapers.get(position).getTitle());

        findViewById(R.id.btn_save).setOnClickListener(view -> {
            if (!verifyPermissions()) {
                return;
            }
            String wallpaperName = wallpapers.get(position).getTitle().toLowerCase().replace(" ", "_") + "_" + wallpapers.get(position).getId();
            wallpaperHelper.downloadWallpaper(progressDialog, wallpaperName, wallpaperUrl);
        });

        findViewById(R.id.btn_share).setOnClickListener(view -> {
            if (!verifyPermissions()) {
                return;
            }
            String wallpaperName = wallpapers.get(position).getTitle().toLowerCase().replace(" ", "_") + "_" + wallpapers.get(position).getId();
            wallpaperHelper.shareWallpaper(progressDialog, wallpaperName, wallpaperUrl);
        });

        findViewById(R.id.btn_set_wallpaper).setOnClickListener(view -> {
            if (!verifyPermissions()) {
                return;
            }
            loadFile(wallpapers, progressDialog, position, wallpaperUrl);
        });

        findViewById(R.id.btn_favorite).setOnClickListener(view -> {
            List<WallpapersModel> data = dbFavorite.getFavRow(wallpapers.get(position).getId());
            if (data.size() == 0) {
                dbFavorite.AddToFavorite(new WallpapersModel(wallpapers.get(position).getId(), wallpapers.get(position).getCategoryid(), wallpapers.get(position).getTitle(), wallpapers.get(position).getImage()));
                Snackbar.make(view, "Added To Favorites", Snackbar.LENGTH_SHORT).show();
                img_favorite.setImageResource(R.drawable.ic_menu_favorite);
            } else {
                if (data.get(0).getId().equals(wallpapers.get(position).getId())) {
                    dbFavorite.RemoveFav(new WallpapersModel(wallpapers.get(position).getId(), wallpapers.get(position).getCategoryid(), wallpapers.get(position).getTitle(), wallpapers.get(position).getImage()));
                    Snackbar.make(view, "Removed From Favorites", Snackbar.LENGTH_SHORT).show();
                    img_favorite.setImageResource(R.drawable.ic_menu_favorite_outline);
                }
            }
        });
        favToggle(wallpapers, position);


        binding.lytBottom.setVisibility(View.VISIBLE);
        binding.toolbar.setVisibility(View.VISIBLE);
        fullScreenMode(false);
        showShadow(true);

    }


    public void favToggle(final List<WallpapersModel> wallpapers, int position) {
        List<WallpapersModel> data = dbFavorite.getFavRow(wallpapers.get(position).getId());
        if (!data.isEmpty()) {
            if (data.get(0).getId().equals(wallpapers.get(position).getId())) {
                img_favorite.setImageResource(R.drawable.ic_menu_favorite);
            }
        } else {
            img_favorite.setImageResource(R.drawable.ic_menu_favorite_outline);
        }
    }


    public void setupToolbar() {
        setSupportActionBar(binding.toolbar);
        actionBar = getSupportActionBar();
        if (actionBar != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }
    }

//    private void loadFile(final List<WallpapersModel> wallpapers, ProgressDialog progressDialog, int position, String fileUrl) {
//        progressDialog.setMessage(getString(R.string.msg_loading_wallpaper));
//        progressDialog.setCancelable(false);
//        progressDialog.show();
//
//        ApiInterface apiInterface = RestAdapter.createDownloadApi();
//        Call<ResponseBody> call = apiInterface.downloadFileWithDynamicUrl(fileUrl);
//        call.enqueue(new Callback<ResponseBody>() {
//            @Override
//            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
//                if (response.isSuccessful()) {
//                    //Log.d(TAG, "Got the body for the file");
//                    if (response.body() != null) {
//                        try {
//                            InputStream inputStream = new BufferedInputStream(response.body().byteStream());
//                            String mimeType = URLConnection.guessContentTypeFromStream(inputStream);
//
//                            if (mimeType.equals("image/gif") || mimeType.equals("image/GIF")) {
//                                String imageName = wallpapers.get(position).getTitle().toLowerCase().replace(" ", "_") + "_" + wallpapers.get(position).getId();
//                                wallpaperHelper.setGif(parentView, progressDialog, imageName, wallpaperUrl);
//                            } else {
//                                if (Build.VERSION.SDK_INT >= 24) {
//                                    dialogOptionSetWallpaper(wallpaperUrl);
//
//                                } else {
//                                    wallpaperHelper.setWallpaper(parentView, progressDialog, wallpaperUrl);
//                                }
//                            }
//                            //Log.d(TAG, "mime type : " + mimeType);
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                            progressDialog.dismiss();
//                        }
//                    } else {
//                        progressDialog.dismiss();
//                    }
//                } else {
//                    //Log.d(TAG, "Connection failed " + response.errorBody());
//                    progressDialog.dismiss();
//                }
//            }
//
//            @Override
//            public void onFailure(Call<ResponseBody> call, Throwable t) {
//                t.printStackTrace();
//                //Log.e(TAG, t.getMessage());
//                progressDialog.dismiss();
//            }
//        });
//    }


    private void loadFile(final List<WallpapersModel> wallpapers, ProgressDialog progressDialog, int position, String fileUrl) {

        progressDialog.setMessage(getString(R.string.msg_loading_wallpaper));
        progressDialog.setCancelable(false);
        progressDialog.show();

        ApiInterface apiInterface = RestAdapter.createDownloadApi();
        Call<ResponseBody> call = apiInterface.downloadFileWithDynamicUrl(fileUrl);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        InputStream inputStream = new BufferedInputStream(response.body().byteStream());
                        String mimeType = URLConnection.guessContentTypeFromStream(inputStream);

                        if (mimeType != null && (mimeType.equals("image/gif") || mimeType.equals("image/GIF"))) {
                            String imageName = wallpapers.get(position).getTitle().toLowerCase().replace(" ", "_") + "_" + wallpapers.get(position).getId();
                            wallpaperHelper.setGif(parentView, progressDialog, imageName, wallpaperUrl);
                        } else {
                            if (Build.VERSION.SDK_INT >= 24) {
                                dialogOptionSetWallpaper(wallpaperUrl);
                            } else {
                                wallpaperHelper.setWallpaper(parentView, progressDialog, wallpaperUrl);
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        progressDialog.dismiss();
                    }
                } else {
                    Toast.makeText(WallpaperActivity.this, "Connection failed " + response.errorBody(), Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                t.printStackTrace();
                // Handle the failure
                progressDialog.dismiss();
            }
        });
    }


    @TargetApi(Build.VERSION_CODES.N)
    public void dialogOptionSetWallpaper(String imageURL) {
        String[] items = getResources().getStringArray(R.array.dialog_set_wallpaper);
        single_choice_selected = items[0];
        int itemSelected = 0;

        // Check if it's day mode or night mode and set the appropriate theme
        int themeResId = isNightMode ? R.style.AlertDialogNightTheme : R.style.AlertDialogDayTheme;

        AlertDialog alertDialog = new AlertDialog.Builder(new ContextThemeWrapper(this, themeResId))
                .setTitle(R.string.dialog_set_title)
                .setSingleChoiceItems(items, itemSelected, (dialogInterface, i) -> single_choice_selected = items[i])
                .setPositiveButton(R.string.dialog_option_ok, (dialogInterface, i) -> {

                    progressDialog.setMessage(getString(R.string.msg_preparing_wallpaper));
                    progressDialog.setCancelable(false);
                    progressDialog.show();

                    new Handler(Looper.getMainLooper()).postDelayed(()
                            -> Glide.with(WallpaperActivity.this)
                            .load(imageURL.replace(" ", "%20"))
                            .into(new CustomTarget<Drawable>() {
                                @Override
                                public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                                    Bitmap bitmap = ((BitmapDrawable) resource).getBitmap();

                                    if (single_choice_selected.equals(getResources().getString(R.string.set_home_screen))) {
                                        wallpaperHelper.setWallpaper(binding.coordinatorLayout, progressDialog, bitmap, HOME_SCREEN);
                                        progressDialog.setMessage(getString(R.string.msg_apply_wallpaper));
                                    } else if (single_choice_selected.equals(getResources().getString(R.string.set_lock_screen))) {
                                        wallpaperHelper.setWallpaper(binding.coordinatorLayout, progressDialog, bitmap, LOCK_SCREEN);
                                        progressDialog.setMessage(getString(R.string.msg_apply_wallpaper));
                                    } else if (single_choice_selected.equals(getResources().getString(R.string.set_both))) {
                                        wallpaperHelper.setWallpaper(binding.coordinatorLayout, progressDialog, bitmap, BOTH);
                                        progressDialog.setMessage(getString(R.string.msg_apply_wallpaper));
                                    } else if (single_choice_selected.equals(getResources().getString(R.string.set_with))) {
                                        wallpaperHelper.setWallpaperFromOtherApp(wallpaperUrl);
                                        progressDialog.dismiss();
                                    }
                                }

                                @Override
                                public void onLoadCleared(@Nullable Drawable placeholder) {
                                    progressDialog.dismiss();
                                }

                                @Override
                                public void onLoadFailed(@Nullable Drawable errorDrawable) {
                                    super.onLoadFailed(errorDrawable);
                                    Snackbar.make(binding.coordinatorLayout, getString(R.string.snack_bar_failed), Snackbar.LENGTH_SHORT).show();
                                    progressDialog.dismiss();
                                }
                            }), Config.DELAY_SET);
                })
                .setNegativeButton(R.string.dialog_option_cancel, (dialog, which) -> progressDialog.dismiss())
                .setCancelable(false)
                .show();
        // Set the custom style for the positive button text color in light mode
        if (!isNightMode) {
            alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.black));
            alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.black));
        }
    }


    public boolean verifyPermissions() {
        String storagePermission;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            storagePermission = Manifest.permission.READ_MEDIA_IMAGES;
        } else {
            storagePermission = Manifest.permission.WRITE_EXTERNAL_STORAGE;
        }

        int permissionResult = ContextCompat.checkSelfPermission(this, storagePermission);

        if (permissionResult == PackageManager.PERMISSION_GRANTED) {
            // Permission granted
            return true;
        } else {
            // Request permission here
            String[] storagePermissions = {storagePermission};
            ActivityCompat.requestPermissions(this, storagePermissions, 1);
            return false;
        }
    }


    private void showShadow(boolean show) {
        if (show) {
            binding.bgShadowTop.setVisibility(View.VISIBLE);
            binding.bgShadowBottom.setVisibility(View.VISIBLE);
        } else {
            binding.bgShadowTop.setVisibility(View.GONE);
            binding.bgShadowBottom.setVisibility(View.GONE);
        }
    }


    public void showFullScreen() {
        if (flag) {
            fullScreenMode(true);
            flag = false;
        } else {
            fullScreenMode(false);
            flag = true;
        }
    }

    public void fullScreenMode(boolean on) {
        if (on) {
            binding.toolbar.setVisibility(View.GONE);
            binding.toolbar.animate().translationY(-112);
            binding.lytBottom.setVisibility(View.GONE);
            binding.lytBottom.animate().translationY(binding.lytBottom.getHeight());

            binding.bgShadowTop.setVisibility(View.GONE);
            binding.bgShadowTop.animate().translationY(-112);

            binding.bgShadowBottom.setVisibility(View.GONE);
            binding.bgShadowBottom.animate().translationY(binding.bgShadowBottom.getHeight());

            Tools.transparentStatusBarNavigation(this);

            hideSystemUI();

        } else {
            binding.toolbar.setVisibility(View.VISIBLE);
            binding.toolbar.animate().translationY(0);
            binding.lytBottom.setVisibility(View.VISIBLE);
            binding.lytBottom.animate().translationY(0);

            binding.bgShadowTop.setVisibility(View.VISIBLE);
            binding.bgShadowTop.animate().translationY(0);

            binding.bgShadowBottom.setVisibility(View.VISIBLE);
            binding.bgShadowBottom.animate().translationY(0);

            Tools.transparentStatusBarNavigation(this);

        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(menuItem);
    }

    private void hideSystemUI() {
        // Enables regular immersive mode.
        // For "lean back" mode, remove SYSTEM_UI_FLAG_IMMERSIVE.
        // Or for "sticky immersive," replace it with SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE
                        // Set the content to appear under the system bars so that the
                        // content doesn't resize when the system bars hide and show.
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        // Hide the nav bar and status bar
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN);
    }

}
//    @TargetApi(Build.VERSION_CODES.N)
//    public void dialogOptionSetWallpaper(String imageURL) {
//        String[] items = getResources().getStringArray(R.array.dialog_set_wallpaper);
//        single_choice_selected = items[0];
//        int itemSelected = 0;
//        new AlertDialog.Builder(WallpaperActivity.this)
//                .setTitle(R.string.dialog_set_title)
//                .setSingleChoiceItems(items, itemSelected, (dialogInterface, i) -> single_choice_selected = items[i])
//                .setPositiveButton(R.string.dialog_option_ok, (dialogInterface, i) -> {
//
//                    progressDialog.setMessage(getString(R.string.msg_preparing_wallpaper));
//                    progressDialog.setCancelable(false);
//                    progressDialog.show();
//
//                    new Handler(Looper.getMainLooper()).postDelayed(() -> Glide.with(this)
//                            .load(imageURL.replace(" ", "%20"))
//                            .into(new CustomTarget<Drawable>() {
//                                @Override
//                                public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
//                                    Bitmap bitmap = ((BitmapDrawable) resource).getBitmap();
//                                    if (single_choice_selected.equals(getResources().getString(R.string.set_home_screen))) {
//                                        wallpaperHelper.setWallpaper(parentView, progressDialog, bitmap, HOME_SCREEN);
//                                        progressDialog.setMessage(getString(R.string.msg_apply_wallpaper));
//                                    } else if (single_choice_selected.equals(getResources().getString(R.string.set_lock_screen))) {
//                                        wallpaperHelper.setWallpaper(parentView, progressDialog, bitmap, LOCK_SCREEN);
//                                        progressDialog.setMessage(getString(R.string.msg_apply_wallpaper));
//                                    } else if (single_choice_selected.equals(getResources().getString(R.string.set_both))) {
//                                        wallpaperHelper.setWallpaper(parentView, progressDialog, bitmap, BOTH);
//                                        progressDialog.setMessage(getString(R.string.msg_apply_wallpaper));
//
//                                    }  else if (single_choice_selected.equals(getResources().getString(R.string.set_with))) {
//                                        wallpaperHelper.setWallpaperFromOtherApp(wallpaperUrl);
//                                        progressDialog.dismiss();
//                                    }
//                                    //Glide.with(getApplicationContext()).clear(this);
//                                }
//
//                                @Override
//                                public void onLoadCleared(@Nullable Drawable placeholder) {
//                                    progressDialog.dismiss();
//                                }
//
//                                @Override
//                                public void onLoadFailed(@Nullable Drawable errorDrawable) {
//                                    super.onLoadFailed(errorDrawable);
//                                    Snackbar.make(parentView, getString(R.string.snack_bar_failed), Snackbar.LENGTH_SHORT).show();
//                                    progressDialog.dismiss();
//                                }
//                            }), Config.DELAY_SET);
//
//                })
//                .setNegativeButton(R.string.dialog_option_cancel, (dialog, which) -> progressDialog.dismiss())
//                .setCancelable(false)
//                .show();
//
//    }

//    public Boolean verifyPermissions() {
//        int permissionExternalMemory = ActivityCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
//        if (permissionExternalMemory != PackageManager.PERMISSION_GRANTED) {
//            String[] STORAGE_PERMISSIONS = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
//            ActivityCompat.requestPermissions(this, STORAGE_PERMISSIONS, 1);
//            return false;
//        }
//        return true;
//    }