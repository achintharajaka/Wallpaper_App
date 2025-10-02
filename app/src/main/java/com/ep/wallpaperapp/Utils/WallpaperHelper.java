package pop.wallpaper.uhd.Utils;


import static pop.wallpaper.uhd.Config.BOTH;
import static pop.wallpaper.uhd.Config.DELAY_SET;
import static pop.wallpaper.uhd.Config.DOWNLOAD;
import static pop.wallpaper.uhd.Config.HOME_SCREEN;
import static pop.wallpaper.uhd.Config.LOCK_SCREEN;
import static pop.wallpaper.uhd.Config.SET_GIF;
import static pop.wallpaper.uhd.Config.SET_WITH;
import static pop.wallpaper.uhd.Config.SHARE;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.app.WallpaperManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import pop.wallpaper.uhd.R;
import com.google.android.material.snackbar.Snackbar;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;

public class WallpaperHelper {

    private static final String TAG = "WallpaperHelper";
    Activity activity;

    public WallpaperHelper(Activity activity) {
        this.activity = activity;
    }

    @TargetApi(Build.VERSION_CODES.N)
    public void setWallpaper(View view, ProgressDialog progressDialog, Bitmap bitmap, String setAs) {
        switch (setAs) {
            case HOME_SCREEN:
                try {
                    WallpaperManager wallpaperManager = WallpaperManager.getInstance(activity);
                    wallpaperManager.setBitmap(bitmap, null, true, WallpaperManager.FLAG_SYSTEM);
                    onWallpaperApplied(progressDialog, activity.getString(R.string.msg_success_applied));
                } catch (IOException e) {
                    e.printStackTrace();
                    Snackbar.make(view, activity.getString(R.string.snack_bar_failed), Snackbar.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
                break;

            case LOCK_SCREEN:
                try {
                    WallpaperManager wallpaperManager = WallpaperManager.getInstance(activity);
                    wallpaperManager.setBitmap(bitmap, null, true, WallpaperManager.FLAG_LOCK);
                    onWallpaperApplied(progressDialog, activity.getString(R.string.msg_success_applied));
                } catch (IOException e) {
                    e.printStackTrace();
                    Snackbar.make(view, activity.getString(R.string.snack_bar_failed), Snackbar.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
                break;

            case BOTH:
                try {
                    WallpaperManager wallpaperManager = WallpaperManager.getInstance(activity);
                    wallpaperManager.setBitmap(bitmap);
                    onWallpaperApplied(progressDialog, activity.getString(R.string.msg_success_applied));
                } catch (IOException e) {
                    e.printStackTrace();
                    Snackbar.make(view, activity.getString(R.string.snack_bar_failed), Snackbar.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
                break;
        }
    }

    public void setWallpaper(View view, ProgressDialog progressDialog, String imageURL) {

        progressDialog.setMessage(activity.getString(R.string.msg_preparing_wallpaper));
        progressDialog.setCancelable(false);
        progressDialog.show();
        new Handler().postDelayed(() -> Glide.with(activity)
                .load(imageURL.replace(" ", "%20"))
                .into(new CustomTarget<Drawable>() {
                    @Override
                    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                        Bitmap bitmap = ((BitmapDrawable) resource).getBitmap();
                        try {
                            WallpaperManager wallpaperManager = WallpaperManager.getInstance(activity);
                            wallpaperManager.setBitmap(bitmap);
                            progressDialog.setMessage(activity.getString(R.string.msg_apply_wallpaper));
                            onWallpaperApplied(progressDialog, activity.getString(R.string.msg_success_applied));
                        } catch (IOException e) {
                            e.printStackTrace();
                            Snackbar.make(view, activity.getString(R.string.snack_bar_failed), Snackbar.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        }
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {

                    }

                    @Override
                    public void onLoadFailed(@Nullable Drawable errorDrawable) {
                        super.onLoadFailed(errorDrawable);
                        Snackbar.make(view, activity.getString(R.string.snack_bar_error), Snackbar.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                }), DELAY_SET);
    }

    public void onWallpaperApplied(ProgressDialog progressDialog, String message) {
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            showSuccessDialog(message);
            progressDialog.dismiss();
            //new Handler(Looper.getMainLooper()).postDelayed(adsManager::showInterstitialAd, 1500);
        }, DELAY_SET);
    }

    //    public void showSuccessDialog(String message) {
//        AlertDialog.Builder dialog = new AlertDialog.Builder(activity);
//        LayoutInflater mInflater = LayoutInflater.from(activity);
//        final View view = mInflater.inflate(R.layout.dialog_success, null);
//
//        TextView msgSuccess = view.findViewById(R.id.msgSuccess);
//        msgSuccess.setText(message);
//
//        dialog.setView(view);
//        dialog.setCancelable(false);
//
//        final AlertDialog alertDialog = dialog.create();
//        Button btn_done = view.findViewById(R.id.btn_done);
//        btn_done.setOnClickListener(v -> new Handler().postDelayed(() -> {
//            alertDialog.dismiss();
//            activity.finish();
//        }, 250));
//
//        alertDialog.show();
//
//    }
    public void showSuccessDialog(String message) {

        if (activity != null && !activity.isFinishing()) {

            AlertDialog.Builder dialog = new AlertDialog.Builder(activity);
            LayoutInflater mInflater = LayoutInflater.from(activity);
            final View view = mInflater.inflate(R.layout.dialog_success, null);

            TextView msgSuccess = view.findViewById(R.id.msgSuccess);
            msgSuccess.setText(message);

            dialog.setView(view);
            dialog.setCancelable(false);

            final AlertDialog alertDialog = dialog.create();

            Button btn_done = view.findViewById(R.id.btn_done);
            btn_done.setOnClickListener(v -> new Handler().postDelayed(() -> {
                alertDialog.dismiss();
                activity.finish();
            }, 250));

            alertDialog.show();
        }
    }

    public void setGif(View view, ProgressDialog progressDialog, String imageName, String imageURL) {

        progressDialog.setMessage(activity.getString(R.string.msg_preparing_wallpaper));
        progressDialog.setCancelable(false);
        progressDialog.show();

        new Handler().postDelayed(() -> Glide.with(activity)
                .download(imageURL.replace(" ", "%20"))
                .listener(new RequestListener<File>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<File> target, boolean isFirstResource) {
                        progressDialog.dismiss();
                        Snackbar.make(view, activity.getString(R.string.snack_bar_failed), Snackbar.LENGTH_SHORT).show();
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(File resource, Object model, Target<File> target, DataSource dataSource, boolean isFirstResource) {
                        try {
                            Tools.setAction(activity, Tools.getBytesFromFile(resource), Tools.createName(imageName + ".gif"), SET_GIF);
                            progressDialog.dismiss();
                        } catch (IOException e) {
                            e.printStackTrace();
                            progressDialog.dismiss();
                            Snackbar.make(view, activity.getString(R.string.snack_bar_failed), Snackbar.LENGTH_SHORT).show();
                        }
                        return true;
                    }
                })
                .submit(), DELAY_SET);
    }

    public void setWallpaperFromOtherApp(String imageURL) {

        new Handler().postDelayed(() -> Glide.with(activity)
                .download(imageURL.replace(" ", "%20"))
                .listener(new RequestListener<File>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<File> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(File resource, Object model, Target<File> target, DataSource dataSource, boolean isFirstResource) {
                        try {
                            Tools.setAction(activity, Tools.getBytesFromFile(resource), Tools.createName(imageURL), SET_WITH);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        return true;
                    }
                })
                .submit(), DELAY_SET);

    }

    public void downloadWallpaper(ProgressDialog progressDialog, String imageName, String imageURL) {

        progressDialog.setMessage(activity.getString(R.string.snack_bar_saving));
        progressDialog.setCancelable(false);
        progressDialog.show();

        new Handler().postDelayed(() -> Glide.with(activity)
                .download(imageURL.replace(" ", "%20"))
                .listener(new RequestListener<File>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<File> target, boolean isFirstResource) {
                        progressDialog.dismiss();
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(File resource, Object model, Target<File> target, DataSource dataSource, boolean isFirstResource) {
                        try {
                            InputStream is = new BufferedInputStream(new FileInputStream(resource));
                            String mimeType = URLConnection.guessContentTypeFromStream(is);

                            if (mimeType.equals("image/gif")) {
                                Tools.setAction(activity, Tools.getBytesFromFile(resource), Tools.createName(imageName + ".gif"), DOWNLOAD);
                            } else if (mimeType.equals("image/png")) {
                                Tools.setAction(activity, Tools.getBytesFromFile(resource), Tools.createName(imageName + ".png"), DOWNLOAD);
                            } else {
                                Tools.setAction(activity, Tools.getBytesFromFile(resource), Tools.createName(imageName + ".jpg"), DOWNLOAD);
                            }

                            onWallpaperApplied(progressDialog, activity.getString(R.string.msg_success_saved));

                        } catch (IOException e) {
                            e.printStackTrace();
                            progressDialog.dismiss();
                        }
                        return true;
                    }
                })
                .submit(), DELAY_SET);

    }

    public void shareWallpaper(ProgressDialog progressDialog, String imageName, String imageURL) {
        progressDialog.setMessage(activity.getString(R.string.msg_preparing_wallpaper));
        progressDialog.setCancelable(false);
        progressDialog.show();

        new Handler().postDelayed(() -> Glide.with(activity)
                .download(imageURL.replace(" ", "%20"))
                .listener(new RequestListener<File>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<File> target, boolean isFirstResource) {
                        progressDialog.dismiss();
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(File resource, Object model, Target<File> target, DataSource dataSource, boolean isFirstResource) {
                        try {

                            InputStream is = new BufferedInputStream(new FileInputStream(resource));
                            String mimeType = URLConnection.guessContentTypeFromStream(is);

                            if (mimeType.equals("image/gif")) {
                                Tools.setAction(activity, Tools.getBytesFromFile(resource), Tools.createName(imageName + ".gif"), SHARE);
                            } else if (mimeType.equals("image/png")) {
                                Tools.setAction(activity, Tools.getBytesFromFile(resource), Tools.createName(imageName + ".png"), SHARE);
                            } else {
                                Tools.setAction(activity, Tools.getBytesFromFile(resource), Tools.createName(imageName + ".jpg"), SHARE);
                            }

                            progressDialog.dismiss();
                        } catch (IOException e) {
                            e.printStackTrace();
                            progressDialog.dismiss();
                        }
                        return true;
                    }
                })
                .submit(), DELAY_SET);

    }

//    public void downloadWallpaperManager(View view, String filename, String image_url, String extension, String mime) {
//        try {
//            DownloadManager dm = (DownloadManager) activity.getSystemService(Context.DOWNLOAD_SERVICE);
//            Uri downloadUri = Uri.parse(image_url);
//            DownloadManager.Request request = new DownloadManager.Request(downloadUri);
//            request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE)
//                    .setAllowedOverRoaming(false)
//                    .setTitle(filename)
//                    .setMimeType(mime) // Your file type. You can use this code to download other file types also.
//                    //.setMimeType("image/jpeg") // Your file type. You can use this code to download other file types also.
//                    .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
//                    .setDestinationInExternalPublicDir(Environment.DIRECTORY_PICTURES, File.separator + filename + "." + extension);
//            dm.enqueue(request);
//            //Toast.makeText(activity, "Image download started.", Toast.LENGTH_SHORT).show();
//            Snackbar.make(view, activity.getString(R.string.start_download), Snackbar.LENGTH_SHORT).show();
//        } catch (Exception e) {
//            //Toast.makeText(activity, "Image download failed.", Toast.LENGTH_SHORT).show();
//            Snackbar.make(view, activity.getString(R.string.failed_download), Snackbar.LENGTH_SHORT).show();
//        }
//    }

}