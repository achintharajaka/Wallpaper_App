package pop.wallpaper.uhd;

import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.viewpager2.widget.ViewPager2;

import pop.wallpaper.uhd.Adapters.AdapterWallpaperDetail;
import pop.wallpaper.uhd.Models.WallpapersModel;
import pop.wallpaper.uhd.Utils.Tools;
import pop.wallpaper.uhd.Utils.WallpaperHelper;
import pop.wallpaper.uhd.databinding.ActivityWallpaperViewBinding;

import java.util.ArrayList;
import java.util.List;

public class WallpaperViewActivity extends AppCompatActivity {

    ActivityWallpaperViewBinding binding;
    String WallpaperID;
    ActionBar actionBar;

    AdapterWallpaperDetail wallpaperDetail;
    String wallpaperUrl = "";

    WallpaperHelper wallpaperHelper;
    ProgressDialog progressDialog;
    CoordinatorLayout parentView;
    ViewPager2 viewPager2;
    List<WallpapersModel> wallpapers = new ArrayList<>();
    int position;
    private String single_choice_selected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Tools.transparentStatusBarNavigation(this);
        binding = ActivityWallpaperViewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        progressDialog = new ProgressDialog(this);
        wallpaperHelper = new WallpaperHelper(this);


        WallpaperID = getIntent().getStringExtra("WallpaperID");
        position = getIntent().getIntExtra("POSITION", 0);

        Bundle bundle = getIntent().getBundleExtra("key.BUNDLE");
        if (bundle != null) {
            wallpapers = (List<WallpapersModel>) bundle.getSerializable("key.ARRAY_LIST");
        }

        //setupToolbar();
        setupViewPager(wallpapers);

    }

    private void setupViewPager(final List<WallpapersModel> wallpapers) {
        viewPager2 = binding.getRoot().findViewById(R.id.viewPager2);
        wallpaperDetail = new AdapterWallpaperDetail(wallpapers, this);
        viewPager2.setAdapter(wallpaperDetail);
        viewPager2.setOffscreenPageLimit(wallpapers.size());

        int initialPosition = position;
        if (initialPosition < 0 || initialPosition >= wallpapers.size()) {
            initialPosition = 0;
        }

        viewPager2.setCurrentItem(initialPosition, false);

        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                loadView(wallpapers, position);
            }
        });
    }

    private void loadView(final List<WallpapersModel> wallpapers, int position) {
        // Your code for loading and displaying wallpaper details
    }
}