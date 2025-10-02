package pop.wallpaper.uhd.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import pop.wallpaper.uhd.Adapters.WallpapersAdapter;
import pop.wallpaper.uhd.Models.WallpapersModel;
import pop.wallpaper.uhd.R;
import pop.wallpaper.uhd.WallpaperActivity;
import pop.wallpaper.uhd.databinding.FragmentFavoriteBinding;
import pop.wallpaper.uhd.databse.sqlite.DbFavorite;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class FavoriteFragment extends Fragment {

    FragmentFavoriteBinding binding;
    DbFavorite dbFavorite;
    private List<WallpapersModel> wallpapersModels = new ArrayList<>();
    private WallpapersAdapter wallpapersAdapter;

    public FavoriteFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentFavoriteBinding.inflate(getLayoutInflater());
        binding.recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        loadDataFromDatabase();

        binding.swipeRefreshLayout.setOnRefreshListener(() -> {
            wallpapersAdapter.resetListData();

            new Handler().postDelayed(this::loadDataFromDatabase, 1000);
            binding.swipeRefreshLayout.setRefreshing(false);
        });


        return binding.getRoot();

    }

    public void loadDataFromDatabase() {
        dbFavorite = new DbFavorite(getActivity());
        wallpapersModels = dbFavorite.getAllData();

        //set data and list adapter
        wallpapersAdapter = new WallpapersAdapter(wallpapersModels, getActivity(), binding.recyclerView);
        binding.recyclerView.setAdapter(wallpapersAdapter);

        if (wallpapersAdapter.getItemCount() == 0) {
            showNoItemView(true);
        } else {
            showNoItemView(false);
        }

        // on item list clicked
        wallpapersAdapter.setOnItemClickListener((v, obj, position) -> {
            Intent intent = new Intent(getActivity(), WallpaperActivity.class);
            intent.putExtra("POSITION", position);
            Bundle bundle = new Bundle();
            bundle.putSerializable("key.ARRAY_LIST", (Serializable) wallpapersModels);
            intent.putExtra("key.BUNDLE", bundle);
            startActivity(intent);
            //if (getActivity() != null)
            //((MainActivity) getActivity()).showInterstitialAd();
        });


    }

    private void showNoItemView(boolean show) {
        ((TextView) binding.getRoot().findViewById(R.id.no_item_message)).setText(R.string.no_favorite_found);
        if (show) {
            binding.recyclerView.setVisibility(View.GONE);
            binding.getRoot().findViewById(R.id.lyt_no_favorite).setVisibility(View.VISIBLE);
        } else {
            binding.recyclerView.setVisibility(View.VISIBLE);
            binding.getRoot().findViewById(R.id.lyt_no_favorite).setVisibility(View.GONE);
        }
    }
}