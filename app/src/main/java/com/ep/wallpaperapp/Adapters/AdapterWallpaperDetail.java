package pop.wallpaper.uhd.Adapters;


import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import pop.wallpaper.uhd.Models.WallpapersModel;
import pop.wallpaper.uhd.R;
import pop.wallpaper.uhd.WallpaperActivity;
import com.github.chrisbanes.photoview.PhotoView;

import java.util.List;

public class AdapterWallpaperDetail extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final int VIEW_ITEM = 1;
    private final int VIEW_AD = 2;
    private List<WallpapersModel> items;
    private Context context;
    private OnItemClickListener mOnItemClickListener;
    private boolean loading;


    public AdapterWallpaperDetail(List<WallpapersModel> items, Context context) {
        this.items = items;
        this.context = context;
    }

    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mOnItemClickListener = mItemClickListener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_wallpaper_detail, parent, false);
        return new OriginalViewHolder(v);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof OriginalViewHolder) {
            final WallpapersModel p = items.get(position);
            final OriginalViewHolder vItem = (OriginalViewHolder) holder;

            vItem.wallpaperImage.setOnClickListener(v -> ((WallpaperActivity) context).showFullScreen());

            vItem.wallpaperImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
            Glide.with(context)
                    .load(p.getImage().replace(" ", "%20"))
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(R.drawable.bg_button_transparent)
                    .thumbnail(0.3f)
                    .addListener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            vItem.progressBar.setVisibility(View.GONE);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            vItem.progressBar.setVisibility(View.GONE);
                            return false;
                        }
                    })
                    .into(vItem.wallpaperImage);

        }
    }

    public void insertData(List<WallpapersModel> items) {
        setLoaded();
        int positionStart = getItemCount();
        int itemCount = items.size();
        this.items.addAll(items);
        notifyItemRangeInserted(positionStart, itemCount);
    }

    @SuppressWarnings("SuspiciousListRemoveInLoop")
    public void setLoaded() {
        loading = false;
        for (int i = 0; i < getItemCount(); i++) {
            if (items.get(i) == null) {
                items.remove(i);
                notifyItemRemoved(i);
            }
        }
    }

    public void resetListData() {
        this.items.clear();
        notifyDataSetChanged();
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public int getItemViewType(int position) {
        WallpapersModel post = items.get(position);
        if (post != null) {
            if (post.getTitle() == null) {
                return VIEW_AD;
            }
            return VIEW_ITEM;
        } else {
            return VIEW_ITEM;
        }
    }

    public interface OnItemClickListener {
        void onItemClick(View view, WallpapersModel obj, int position);
    }

    public class OriginalViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public PhotoView wallpaperImage;
        public ProgressBar progressBar;

        public OriginalViewHolder(View v) {
            super(v);
            wallpaperImage = v.findViewById(R.id.wallpaperImage);
            progressBar = v.findViewById(R.id.progressBar);
        }
    }

}