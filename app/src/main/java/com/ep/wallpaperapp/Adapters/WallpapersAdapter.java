package pop.wallpaper.uhd.Adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import pop.wallpaper.uhd.Config;
import pop.wallpaper.uhd.Models.WallpapersModel;
import pop.wallpaper.uhd.R;

import java.util.List;

public class WallpapersAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<WallpapersModel> items;
    private Context context;
    private OnItemClickListener onItemClickListener;
    private boolean loading;

    public WallpapersAdapter(List<WallpapersModel> items, Context context, RecyclerView view) {
        this.items = items;
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_wallpaper_rectangle, parent, false);
        return new WallpapersViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof WallpapersViewHolder) {
            final WallpapersModel p = items.get(position);
            final WallpapersViewHolder vItem = (WallpapersViewHolder) holder;

            vItem.wallpaperName.setText(p.getTitle());

            if (Config.WALLPAPER_GRID_STYLE == 2) {
                Glide.with(context)
                        .load(p.getImage().replace(" ", "%20"))
                        .thumbnail(0.3f)
                        .apply(new RequestOptions().override(Config.THUMBNAIL_WIDTH, Config.THUMBNAIL_HEIGHT))
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .placeholder(R.drawable.bg_button_transparent)
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
            } else {
                Glide.with(context)
                        .load(p.getImage().replace(" ", "%20"))
                        .thumbnail(0.3f)
                        .apply(new RequestOptions().override(Config.THUMBNAIL_WIDTH, Config.THUMBNAIL_HEIGHT))
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .placeholder(R.drawable.bg_button_transparent)
                        .centerCrop()
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


            vItem.lytParent.setOnClickListener(view -> {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(view, p, position);
                }
            });

        } else {
            ((ProgressViewHolder) holder).progressBar.setIndeterminate(true);
        }
    }

    @Override
    public int getItemCount() {
        if (items != null) {
            return items.size();
        } else {
            return 0;
        }
    }

    public void setLoading() {
        if (getItemCount() != 0) {
            this.items.add(null);
            notifyItemInserted(getItemCount() - 1);
            loading = true;
        }
    }

    public void resetListData() {
        if (this.items != null) {
            this.items.clear();
            notifyDataSetChanged();
        }
    }

    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.onItemClickListener = mItemClickListener;
    }

    public interface OnItemClickListener {
        void onItemClick(View view, WallpapersModel obj, int position);
    }

    public static class WallpapersViewHolder extends RecyclerView.ViewHolder {
        public TextView wallpaperName;
        public ImageView wallpaperImage;
        public ProgressBar progressBar;
        public FrameLayout lytParent;

        public WallpapersViewHolder(View v) {
            super(v);
            wallpaperName = v.findViewById(R.id.wallpaperName);
            wallpaperImage = v.findViewById(R.id.wallpaperImage);
            progressBar = v.findViewById(R.id.progressBar);
            lytParent = v.findViewById(R.id.lytParent);
        }
    }

    public static class ProgressViewHolder extends RecyclerView.ViewHolder {
        public ProgressBar progressBar;

        public ProgressViewHolder(View v) {
            super(v);
            progressBar = v.findViewById(R.id.progressBar);
        }
    }


}
