package pop.wallpaper.uhd.Utils;


import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import pop.wallpaper.uhd.Fragment.CategoryFragment;
import pop.wallpaper.uhd.Fragment.FavoriteFragment;
import pop.wallpaper.uhd.Fragment.WallpaperFragment;

@SuppressWarnings("ALL")
public class NavigationAdapter {

    public static class BottomNavigationAdapter extends FragmentPagerAdapter {

        public BottomNavigationAdapter(FragmentManager fm) {
            super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new WallpaperFragment();
                case 1:
                    return new CategoryFragment();
                case 2:
                    return new FavoriteFragment();
            }
            return null;
        }

        @Override
        public int getCount() {
            return 3;
        }

    }

}
