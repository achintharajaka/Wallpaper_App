package pop.wallpaper.uhd.Adapters;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import pop.wallpaper.uhd.R;

import java.io.File;
import java.util.List;

public class StorageWallpapers extends RecyclerView.Adapter<StorageWallpapers.ImageViewHolder> {

    private final Context context;
    private final List<File> imageFiles;

    public StorageWallpapers(Context context, List<File> imageFiles) {
        this.context = context;
        this.imageFiles = imageFiles;
    }

    @Override
    public ImageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_wallpaper_rectangle, parent, false);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ImageViewHolder holder, int position) {
        File imageFile = imageFiles.get(position);
        // Load and display the image using a library like Glide or Picasso
        // Replace 'imageView' with the ID of your ImageView in the item layout
        Glide.with(context)
                .load(imageFile)
                .into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return imageFiles.size();
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        public ImageViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.wallpaperImage);
        }
    }
}