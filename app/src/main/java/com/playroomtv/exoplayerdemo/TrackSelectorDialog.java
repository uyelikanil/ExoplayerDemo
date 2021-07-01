package com.playroomtv.exoplayerdemo;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;

import com.google.android.material.tabs.TabLayout;
import com.playroomtv.exoplayerdemo.adapter.ViewPagerAdapter;
import com.playroomtv.exoplayerdemo.databinding.TrackSelectorDialogBinding;

public class TrackSelectorDialog extends DialogFragment {
    private final Context context;
    private final Player player;
    private final FragmentManager fragmentManager;
    private final DefaultTrackSelector trackSelector;

    private TrackSelectorDialogBinding binding;
    private ViewPagerAdapter viewPagerAdapter;

    public static final int TAB_AUDIO_INDEX = 0;
    public static final int TAB_SUBTITLE_INDEX = 1;

    public TrackSelectorDialog(Context context, FragmentManager fragmentManager, Player player,
                               DefaultTrackSelector trackSelector) {
        this.context = context;
        this.player = player;
        this.fragmentManager = fragmentManager;
        this.trackSelector = trackSelector;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = TrackSelectorDialogBinding.inflate(inflater, container, false);

        // Set view pager
        setViewPager();
        // Set tabLayout
        setTabLayout();

        return binding.getRoot();
    }

    private void setViewPager() {
        binding.viewPagerLayout.viewPager.setOffscreenPageLimit(2);
        viewPagerAdapter = new ViewPagerAdapter(getChildFragmentManager());
        viewPagerAdapter.addScreen(new AudioFragment(player, trackSelector));
        viewPagerAdapter.addScreen(new SubtitleFragment(player, trackSelector));
        binding.viewPagerLayout.viewPager.setAdapter(viewPagerAdapter);
    }

    private void setTabLayout() {
        binding.tabLayout.setupWithViewPager(binding.viewPagerLayout.viewPager);
        TabLayout.Tab audioTab = binding.tabLayout.getTabAt(TAB_AUDIO_INDEX);
        TabLayout.Tab subtitleTab = binding.tabLayout.getTabAt(TAB_SUBTITLE_INDEX);
        audioTab.setText(R.string.audios);
        subtitleTab.setText(R.string.subtitles);
    }

    @Override
    public void onResume() {
        super.onResume();
        Window window = getDialog().getWindow();
        window.setLayout(500, ViewGroup.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawableResource(R.color.transparentDarkestGray);
    }
}
