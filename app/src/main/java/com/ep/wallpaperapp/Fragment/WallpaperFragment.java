package pop.wallpaper.uhd.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.bdtopcoder.quickadmob.Admob;
import com.bdtopcoder.quickadmob.AdsUnit;
import com.bdtopcoder.quickadmob.onDismiss;
import pop.wallpaper.uhd.Adapters.WallpapersAdapter;
import pop.wallpaper.uhd.Config;
import pop.wallpaper.uhd.Models.WallpapersModel;
import pop.wallpaper.uhd.R;
import pop.wallpaper.uhd.Utils.Tools;
import pop.wallpaper.uhd.WallpaperActivity;
import pop.wallpaper.uhd.databinding.FragmentWallpaperBinding;
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


public class WallpaperFragment extends Fragment {

    FragmentWallpaperBinding binding;
    private List<WallpapersModel> wallpapersModel;
    private WallpapersAdapter wallpapersAdapter;

    private DatabaseReference wallpaperslist;
    private ValueEventListener valueEventListener;


    public WallpaperFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentWallpaperBinding.inflate(getLayoutInflater());

        binding.swipeRefreshLayout.setColorSchemeResources(R.color.primary);
        wallpapersAdapter = new WallpapersAdapter(wallpapersModel, getActivity(), binding.recyclerView);

        AdsUnit.INTERSTITIAL = getString(R.string.interstital1);
        Admob.loadInterstitialAds(getActivity());

        FirebaseDatabase.getInstance().setPersistenceEnabled(true);

        initSwipeRefreshLayout();
        initShimmerLayout();
        loadWallpapers();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                loadWallpapers();
            }
        }, 1000);

        return binding.getRoot();
    }


    private void initSwipeRefreshLayout() {
        binding.swipeRefreshLayout.setOnRefreshListener(() -> {
            wallpapersAdapter.resetListData();
            swipeProgress(true);
            new Handler().postDelayed(this::loadWallpapers, 1000);
        });
    }

    private void initShimmerLayout() {
        ViewStub stub = binding.getRoot().findViewById(R.id.lytShimmerView);
        if (3 == Config.WALLPAPER_GRID_STYLE) {
            stub.setLayoutResource(R.layout.shimmer_wallpaper_3_columns_rectangle);
        } else {
            stub.setLayoutResource(R.layout.shimmer_wallpaper_2_columns_rectangle);
        }
        stub.inflate();
    }

    private void loadWallpapers() {
        if (Tools.isConnect(getActivity())) {
            swipeProgress(false);
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
                        Collections.shuffle(wallpapersModel);
                        showNoItemView(false);
                        binding.recyclerView.setLayoutManager(new StaggeredGridLayoutManager(Config.WALLPAPER_GRID_STYLE, StaggeredGridLayoutManager.VERTICAL));
                        wallpapersAdapter = new WallpapersAdapter(wallpapersModel, getActivity(), binding.recyclerView);
                        binding.recyclerView.setAdapter(wallpapersAdapter);

                        wallpapersAdapter.setOnItemClickListener((view, obj, position) -> {
                            new Admob(new onDismiss() {
                                @Override
                                public void onDismiss() {}}).ShowInterstitial(getActivity(), true);

                            Intent intent = new Intent(getActivity(), WallpaperActivity.class);
                            intent.putExtra("POSITION", position);
                            Bundle bundle = new Bundle();
                            bundle.putSerializable("key.ARRAY_LIST", (Serializable) wallpapersModel);
                            intent.putExtra("key.BUNDLE", bundle);
                            startActivity(intent);

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


    private void swipeProgress(final boolean show) {
        if (!show) {
            binding.swipeRefreshLayout.setRefreshing(show);
            binding.shimmerViewContainer.setVisibility(View.GONE);
            binding.shimmerViewContainer.stopShimmer();
            return;
        }
        binding.swipeRefreshLayout.post(() -> {
            binding.swipeRefreshLayout.setRefreshing(show);
            binding.shimmerViewContainer.setVisibility(View.VISIBLE);
            binding.shimmerViewContainer.startShimmer();
        });
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
        binding.getRoot().findViewById(R.id.failed_retry).setOnClickListener(view -> requestAction());
    }

    private void requestAction() {
        showFailedView(false, "");
        swipeProgress(true);
        showNoItemView(false);
        wallpapersAdapter.setLoading();
        new Handler(Looper.getMainLooper()).postDelayed(this::loadWallpapers, 0);

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


    @Override
    public void onDestroy() {
        super.onDestroy();
//        if (wallpaperslist != null) {
//            wallpaperslist.removeEventListener(valueEventListener);
//        }
        // Stop the Shimmer animation if it's running
        if (binding != null && binding.shimmerViewContainer != null) {
            binding.shimmerViewContainer.stopShimmer();
        }
    }

}