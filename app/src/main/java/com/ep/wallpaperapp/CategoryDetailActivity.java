package pop.wallpaper.uhd;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import pop.wallpaper.uhd.Adapters.WallpapersAdapter;
import pop.wallpaper.uhd.Models.WallpapersModel;
import pop.wallpaper.uhd.Utils.Tools;
import pop.wallpaper.uhd.databinding.ActivityCategoryDetailBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class CategoryDetailActivity extends AppCompatActivity {

    ActivityCategoryDetailBinding binding;
    String CategroyID;
    private List<WallpapersModel> wallpapersModel;
    private WallpapersAdapter wallpapersAdapter;
    private DatabaseReference wallpaperslist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCategoryDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        CategroyID = getIntent().getStringExtra("CategoryID");

        loadCategory(CategroyID);
    }

    private void loadCategory(String categroyID) {
        if (Tools.isConnect(CategoryDetailActivity.this)) {
            wallpapersModel = new ArrayList<>();
            wallpaperslist = FirebaseDatabase.getInstance().getReference("Wallpapers");
            // Apply the query to filter by categoryid
            Query query = wallpaperslist.orderByChild("categoryid").equalTo(categroyID);
            query.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    wallpapersModel.clear();
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        Object value = ds.getValue();
                        if (value instanceof WallpapersModel) {
                            WallpapersModel wallpaperrM = (WallpapersModel) value;
                            wallpapersModel.add(wallpaperrM);
                        } else if (value instanceof Map) {
                            Map<String, Object> map = (Map<String, Object>) value;
                            String id = (String) map.get("id");
                            String title = (String) map.get("title");
                            String imageUrl = (String) map.get("image");
                            String categoryid = (String) map.get("categoryid");
                            WallpapersModel wallpaperrM = new WallpapersModel(id, categoryid, title, imageUrl);
                            wallpapersModel.add(wallpaperrM);
                        }
                    }

                    if (wallpapersModel.size() > 0) {
                        showNoItemView(false);
                        Collections.shuffle(wallpapersModel);
                        binding.recyclerView.setLayoutManager(new StaggeredGridLayoutManager(Config.WALLPAPER_GRID_STYLE, StaggeredGridLayoutManager.VERTICAL));
                        wallpapersAdapter = new WallpapersAdapter(wallpapersModel, CategoryDetailActivity.this, binding.recyclerView);
                        binding.recyclerView.setAdapter(wallpapersAdapter);

                        wallpapersAdapter.setOnItemClickListener((view, obj, position) -> {
                            Intent intent = new Intent(CategoryDetailActivity.this, WallpaperActivity.class);
                            intent.putExtra("POSITION", position);
                            Bundle bundle = new Bundle();
                            bundle.putSerializable("key.ARRAY_LIST", (Serializable) wallpapersModel);
                            intent.putExtra("key.BUNDLE", bundle);
                            startActivity(intent);
//                            if (SearchActivity.this != null) {
//                                //((MainActivity) getActivity()).showInterstitialAd();
//                            }
                        });

                    } else {
                        showNoItemView(true);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
        } else {
            showFailedView(true, getString(R.string.failed_text));
        }
    }


    private void showNoItemView(boolean show) {
        View lytNoItem = binding.getRoot().findViewById(R.id.lyt_no_item);
        ((TextView) binding.getRoot().findViewById(R.id.no_item_message)).setText(R.string.no_category_found);
        if (show) {
            binding.recyclerView.setVisibility(View.GONE);
            lytNoItem.setVisibility(View.VISIBLE);
        } else {
            binding.recyclerView.setVisibility(View.VISIBLE);
            lytNoItem.setVisibility(View.GONE);
        }
    }

    private void showFailedView(boolean flag, String message) {
        View lytFailed = binding.getRoot().findViewById(R.id.lyt_failed);
        ((TextView) binding.getRoot().findViewById(R.id.failed_message)).setText(message);
        if (flag) {
            binding.recyclerView.setVisibility(View.GONE);
            lytFailed.setVisibility(View.VISIBLE);
        } else {
            binding.recyclerView.setVisibility(View.VISIBLE);
            lytFailed.setVisibility(View.GONE);
        }
        binding.getRoot().findViewById(R.id.failed_retry).setOnClickListener(view -> loadCategory(CategroyID));
    }
}