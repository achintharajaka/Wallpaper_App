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
import androidx.recyclerview.widget.GridLayoutManager;

import com.bdtopcoder.quickadmob.Admob;
import com.bdtopcoder.quickadmob.AdsUnit;
import com.bdtopcoder.quickadmob.onDismiss;
import pop.wallpaper.uhd.Adapters.CategoryAdapter;
import pop.wallpaper.uhd.CategoryDetailActivity;
import pop.wallpaper.uhd.Models.CategoryModel;
import pop.wallpaper.uhd.R;
import pop.wallpaper.uhd.Utils.Tools;
import pop.wallpaper.uhd.databinding.FragmentCategoryBinding;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class CategoryFragment extends Fragment {


    FragmentCategoryBinding binding;
    CategoryAdapter categoryAdapter;
    List<CategoryModel> categoryModels;
    boolean fs = true;
    private ShimmerFrameLayout lytShimmer;

    public CategoryFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentCategoryBinding.inflate(getLayoutInflater());

        AdsUnit.INTERSTITIAL = getString(R.string.interstital1);
        Admob.loadInterstitialAds(getActivity());

        // on swipe list
        binding.swipeRefreshLayout.setOnRefreshListener(() -> {
            //categoryAdapter.resetListData();
            swipeProgress(true);
            new Handler().postDelayed(this::loadCategories, 1000);
        });

//        if (fs) {
//            initShimmerLayout();
//            fs = false;
//        }
        //initShimmerLayout();
        loadCategories();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                loadCategories();
            }
        }, 1000);



        return binding.getRoot();
    }


    private void loadCategories() {
        if (Tools.isConnect(getActivity())) {
            swipeProgress(true);
            categoryModels = new ArrayList<>();

            DatabaseReference categorylist = FirebaseDatabase.getInstance().getReference("Category");
            categorylist.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    swipeProgress(false);
                    List<CategoryModel> newCategoryModels = new ArrayList<>();
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        Object value = ds.getValue();
                        if (value instanceof CategoryModel) {
                            newCategoryModels.add((CategoryModel) value);
                        } else if (value instanceof Map) {
                            Map<String, Object> map = (Map<String, Object>) value;
                            String id = (String) map.get("id");
                            String name = (String) map.get("name");
                            String imageUrl = (String) map.get("image");
                            CategoryModel category = new CategoryModel(id, name, imageUrl);
                            newCategoryModels.add(category);
                        }
                    }

                    if (!newCategoryModels.isEmpty()) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                showNoItemView(false);
                                swipeProgress(false);
                                // There are items in the list, so set up the RecyclerView
                                binding.recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
                                categoryAdapter = new CategoryAdapter(newCategoryModels, getActivity());
                                binding.recyclerView.setAdapter(categoryAdapter);

                                categoryAdapter.setOnItemClickListener((view, obj, position) -> {
                                    new Admob(new onDismiss() {
                                        @Override
                                        public void onDismiss() {}}).ShowInterstitial(getActivity(), true);

                                    Intent intent = new Intent(getActivity(), CategoryDetailActivity.class);
                                    intent.putExtra("CategoryID", obj.getId());
                                    startActivity(intent);
                                });
                            }
                        });
                    } else {
                        showNoItemView(true);
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    // Handle database error
                }
            });
        } else {
            showFailedView(true, getString(R.string.failed_text));
        }
    }


    // Your existing code for loadCategories method is the same
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
    private void swipeProgress(final boolean show) {
        if (show) {
            binding.swipeRefreshLayout.setRefreshing(true);
            binding.shimmerViewContainer.setVisibility(View.VISIBLE);
            binding.shimmerViewContainer.startShimmer();

        } else {
            binding.swipeRefreshLayout.setRefreshing(false);
            binding.shimmerViewContainer.setVisibility(View.GONE);
            binding.shimmerViewContainer.stopShimmer();
        }

    }


    private void requestAction() {
        showFailedView(false, "");
        swipeProgress(true);
        showNoItemView(false);
        new Handler(Looper.getMainLooper()).postDelayed(this::loadCategories, 0);
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

    private void initShimmerLayout() {
        ViewStub stub = binding.getRoot().findViewById(R.id.lytShimmerView);
        stub.setLayoutResource(R.layout.shimmer_category_grid);
        stub.inflate();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        //lytShimmer.stopShimmer();
    }

}