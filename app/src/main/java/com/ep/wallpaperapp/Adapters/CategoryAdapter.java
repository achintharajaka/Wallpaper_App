package pop.wallpaper.uhd.Adapters;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import pop.wallpaper.uhd.Models.CategoryModel;
import pop.wallpaper.uhd.R;

import java.util.List;
import java.util.Random;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryHolder> {

    List<CategoryModel> categoryModels;
    Context context;

    private OnItemClickListener mOnItemClickListener;

    public CategoryAdapter(List<CategoryModel> categoryModels, Context context) {
        this.categoryModels = categoryModels;
        this.context = context;
    }

    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mOnItemClickListener = mItemClickListener;
    }

    @NonNull
    @Override
    public CategoryHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View menuItemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_category, parent, false);
        return new CategoryHolder(menuItemView);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryHolder holder, int position) {
        CategoryModel c = categoryModels.get(position);
        holder.CateogryTv.setText(c.getName());

        int[] colorArr = {R.color.red, R.color.pink, R.color.purple, R.color.deep_purple, R.color.indigo, R.color.blue, R.color.cyan, R.color.teal, R.color.green, R.color.lime, R.color.orange, R.color.brown, R.color.gray, R.color.blue_gray, R.color.black};
        int rnd = new Random().nextInt(colorArr.length);
        holder.imgAlphabet.setImageResource(colorArr[rnd]);

        if (c.getImage() != null) {
            Glide.with(context)
                    .load(c.getImage().replace(" ", "%20"))
                    .transition(withCrossFade())
                    .thumbnail(0.1f)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(R.drawable.bg_button_transparent)
                    .centerCrop()
                    .into(holder.imgCategory);
            holder.imgCategory.setVisibility(View.VISIBLE);
        } else {
            holder.imgCategory.setVisibility(View.GONE);
        }


        holder.itemView.setOnClickListener(view -> {
            if (mOnItemClickListener != null) {
                mOnItemClickListener.onItemClick(view, c, position);
            }
        });

    }

    @Override
    public int getItemCount() {
        return categoryModels.size();
    }

    public interface OnItemClickListener {
        void onItemClick(View view, CategoryModel obj, int position);
    }

    class CategoryHolder extends RecyclerView.ViewHolder {
        TextView CateogryTv;
        ImageView imgAlphabet;
        ImageView imgCategory;
        CardView cardView;
        LinearLayout lytParent;

        public CategoryHolder(@NonNull View v) {
            super(v);
            CateogryTv = v.findViewById(R.id.txt_label_name);
            imgAlphabet = v.findViewById(R.id.img_alphabet);
            imgCategory = v.findViewById(R.id.img_category);
            cardView = v.findViewById(R.id.card_view);
            lytParent = v.findViewById(R.id.lyt_parent);
        }
    }
}
