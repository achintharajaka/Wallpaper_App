package pop.wallpaper.uhd;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import pop.wallpaper.uhd.Adapters.WallpapersAdapter;
import pop.wallpaper.uhd.Models.WallpapersModel;
import pop.wallpaper.uhd.Utils.Tools;
import pop.wallpaper.uhd.databinding.ActivitySearchBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class SearchActivity extends AppCompatActivity {

    ActivitySearchBinding binding;
    private List<WallpapersModel> wallpapersModel;
    private WallpapersAdapter wallpapersAdapter;
    private DatabaseReference wallpaperslist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySearchBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        loadWallpapers();

        binding.searchEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String serchtext = binding.searchEt.getText().toString();
                if (serchtext.isEmpty()) {
                    loadWallpapers();
                } else {
                    loadSearchWallpapers(serchtext);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void loadSearchWallpapers(String serchtext) {
        if (Tools.isConnect(SearchActivity.this)) {
            wallpapersModel = new ArrayList<>();
            wallpaperslist = FirebaseDatabase.getInstance().getReference("Wallpapers");
            wallpaperslist.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    wallpapersModel.clear();
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        Object value = ds.getValue();
                        if (value instanceof WallpapersModel) {
                            WallpapersModel wallpaperModel = (WallpapersModel) value;
                            wallpapersModel.add(wallpaperModel);
                        } else if (value instanceof Map) {
                            Map<String, Object> map = (Map<String, Object>) value;
                            String id = (String) map.get("id");
                            String title = (String) map.get("title");
                            String imageUrl = (String) map.get("image");
                            String categoryId = (String) map.get("categoryid");
                            WallpapersModel wallpaperModel = new WallpapersModel(id, categoryId, title, imageUrl);
                            wallpapersModel.add(wallpaperModel);
                        }
                    }

                    if (wallpapersModel.size() > 0) {
                        List<WallpapersModel> filteredWallpapers = new ArrayList<>();
                        String searchTextLower = serchtext.toLowerCase();

                        for (WallpapersModel wallpaper : wallpapersModel) {
                            if (wallpaper.getTitle().toLowerCase().contains(searchTextLower) || wallpaper.getTitle().toLowerCase().contains(searchTextLower)) {
                                filteredWallpapers.add(wallpaper);
                            }
                        }

                        if (!filteredWallpapers.isEmpty()) {
                            showNoItemView(false);
                            Collections.shuffle(filteredWallpapers);
                            binding.recyclerView.setLayoutManager(new StaggeredGridLayoutManager(Config.WALLPAPER_GRID_STYLE, StaggeredGridLayoutManager.VERTICAL));
                            wallpapersAdapter = new WallpapersAdapter(filteredWallpapers, SearchActivity.this, binding.recyclerView);
                            binding.recyclerView.setAdapter(wallpapersAdapter);

                            wallpapersAdapter.setOnItemClickListener((view, obj, position) -> {
                                Intent intent = new Intent(SearchActivity.this, WallpaperActivity.class);
                                intent.putExtra("POSITION", position);
                                Bundle bundle = new Bundle();
                                bundle.putSerializable("key.ARRAY_LIST", (Serializable) filteredWallpapers);
                                intent.putExtra("key.BUNDLE", bundle);
                                startActivity(intent);
                            });
                        } else {
                            showNoItemView(true);
                        }
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


    private void loadWallpapers() {
        if (Tools.isConnect(SearchActivity.this)) {
            wallpapersModel = new ArrayList<>();
            wallpaperslist = FirebaseDatabase.getInstance().getReference("Wallpapers");
            wallpaperslist.addValueEventListener(new ValueEventListener() {
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
                        wallpapersAdapter = new WallpapersAdapter(wallpapersModel, SearchActivity.this, binding.recyclerView);
                        binding.recyclerView.setAdapter(wallpapersAdapter);

                        wallpapersAdapter.setOnItemClickListener((view, obj, position) -> {
                            Intent intent = new Intent(SearchActivity.this, WallpaperActivity.class);
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
        binding.getRoot().findViewById(R.id.failed_retry).setOnClickListener(view -> loadWallpapers());
    }


}