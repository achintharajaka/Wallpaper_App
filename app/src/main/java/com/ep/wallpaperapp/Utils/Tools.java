package pop.wallpaper.uhd.Utils;

import static pop.wallpaper.uhd.Config.DOWNLOAD;
import static pop.wallpaper.uhd.Config.SET_WITH;
import static pop.wallpaper.uhd.Config.SHARE;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.StrictMode;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import pop.wallpaper.uhd.BuildConfig;
import pop.wallpaper.uhd.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class Tools {

    MenuItem prevMenuItem;
    private Activity activity;

    public Tools(Activity activity) {
        this.activity = activity;
    }

    public static void setupToolbar(AppCompatActivity activity, Toolbar toolbar, String title, boolean backButton) {
        activity.setSupportActionBar(toolbar);
        final ActionBar actionBar = activity.getSupportActionBar();
        if (actionBar != null) {
            activity.getSupportActionBar().setDisplayHomeAsUpEnabled(backButton);
            activity.getSupportActionBar().setHomeButtonEnabled(backButton);
            activity.getSupportActionBar().setTitle(title);
        }
    }

    public static boolean isConnect(Context context) {
        try {
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            if (activeNetworkInfo != null) {
                return activeNetworkInfo.isConnected() || activeNetworkInfo.isConnectedOrConnecting();
            } else {
                return false;
            }
        } catch (Exception e) {
            return false;
        }
    }

    public static void transparentStatusBarNavigation(Activity activity) {
        activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        setWindowFlag(activity, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION, false);
        activity.getWindow().setStatusBarColor(Color.TRANSPARENT);
        activity.getWindow().setNavigationBarColor(Color.TRANSPARENT);
    }

    public static void setWindowFlag(Activity activity, final int bits, boolean on) {
        Window win = activity.getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }

    public static void setAction(Context context, byte[] bytes, String imgName, String action) {
        try {
            File dir;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/" + context.getString(R.string.app_name));
            } else {
                dir = new File(Environment.getExternalStorageDirectory() + "/" + context.getString(R.string.app_name));
            }
            boolean success = true;
            if (!dir.exists()) {
                success = dir.mkdirs();
            }
            if (success) {
                File imageFile = new File(dir, imgName);
                FileOutputStream fileWriter = new FileOutputStream(imageFile);
                fileWriter.write(bytes);
                fileWriter.flush();
                fileWriter.close();

                Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                File file = new File(imageFile.getAbsolutePath());
                Uri contentUri = Uri.fromFile(file);
                mediaScanIntent.setData(contentUri);
                context.sendBroadcast(mediaScanIntent);

                StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
                StrictMode.setVmPolicy(builder.build());

                switch (action) {
                    case DOWNLOAD:
                        //do nothing
                        break;

                    case SHARE:
                        Intent share = new Intent(Intent.ACTION_SEND);
                        share.setType("image/*");
                        share.putExtra(Intent.EXTRA_TEXT, context.getResources().getString(R.string.share_text) + "\n" + "https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID);
                        share.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://" + imageFile.getAbsolutePath()));
                        context.startActivity(Intent.createChooser(share, "Share Image"));
                        break;

                    case SET_WITH:
                        Intent setWith = new Intent(Intent.ACTION_ATTACH_DATA);
                        setWith.addCategory(Intent.CATEGORY_DEFAULT);
                        setWith.setDataAndType(Uri.parse("file://" + file.getAbsolutePath()), "image/*");
                        setWith.putExtra("mimeType", "image/*");
                        context.startActivity(Intent.createChooser(setWith, "Set as:"));
                        break;

//                    case SET_GIF:
//                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
//                            Config.GIF_PATH = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/" + context.getString(R.string.app_name);
//                        } else {
//                            Config.GIF_PATH = Environment.getExternalStorageDirectory() + "/" + context.getString(R.string.app_name);
//                        }
//                        Config.GIF_NAME = file.getName();
//
////                        SharedPref sharedPref = new SharedPref(context);
////                        sharedPref.saveGif(Constant.GIF_PATH, Constant.GIF_NAME);
//
//                        try {
//                            WallpaperManager.getInstance(context).clear();
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
//                        Intent setGif = new Intent(WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER);
//                        setGif.putExtra(WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT, new ComponentName(context, SetGIFAsWallpaperService.class));
//                        context.startActivity(setGif);
//
////                        Log.d("GIF_PATH", Constant.GIF_PATH);
////                        Log.d("GIF_NAME", Constant.GIF_NAME);
//                        break;
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static byte[] getBytesFromFile(File file) throws IOException {
        long length = file.length();
        if (length > Integer.MAX_VALUE) {
            throw new IOException("File is too large!");
        }
        byte[] bytes = new byte[(int) length];
        int offset = 0;
        int numRead = 0;
        InputStream is = new FileInputStream(file);
        try {
            while (offset < bytes.length
                    && (numRead = is.read(bytes, offset, bytes.length - offset)) >= 0) {
                offset += numRead;
            }
        } finally {
            is.close();
        }
        if (offset < bytes.length) {
            throw new IOException("Could not completely read file " + file.getName());
        }
        return bytes;
    }

    public static String createName(String url) {
        return url.substring(url.lastIndexOf('/') + 1);
    }

    public void setupViewPager(AppCompatActivity activity, ViewPager viewPager, BottomNavigationView navigation, Toolbar toolbar) {
        viewPager.setVisibility(View.VISIBLE);
        viewPager.setAdapter(new NavigationAdapter.BottomNavigationAdapter(activity.getSupportFragmentManager()));
        viewPager.setOffscreenPageLimit(3);
        navigation.setOnNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.navigation_recent) {
                viewPager.setCurrentItem(0);
            } else if (itemId == R.id.navigation_category) {
                viewPager.setCurrentItem(1);
            } else if (itemId == R.id.navigation_favorite) {
                viewPager.setCurrentItem(2);
            } else {
                viewPager.setCurrentItem(0);
            }
            return false;
        });
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (prevMenuItem != null) {
                    prevMenuItem.setChecked(false);
                } else {
                    navigation.getMenu().getItem(0).setChecked(false);
                }
                navigation.getMenu().getItem(position).setChecked(true);
                prevMenuItem = navigation.getMenu().getItem(position);

                int currentItem = viewPager.getCurrentItem();
                if (currentItem == 0) {
                    toolbar.setTitle(activity.getResources().getString(R.string.app_name));
                } else if (currentItem == 1) {
                    toolbar.setTitle(activity.getResources().getString(R.string.title_nav_category));
                } else if (currentItem == 2) {
                    toolbar.setTitle(activity.getResources().getString(R.string.title_nav_favorite));
                } else {
                    toolbar.setTitle(activity.getResources().getString(R.string.app_name));
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }
}
