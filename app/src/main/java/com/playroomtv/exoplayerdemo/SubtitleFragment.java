package com.playroomtv.exoplayerdemo;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.source.TrackGroup;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.MappingTrackSelector;
import com.playroomtv.exoplayerdemo.adapter.TrackSelectorRecyclerViewAdapter;
import com.playroomtv.exoplayerdemo.databinding.FragmentSubtitleBinding;

import java.util.ArrayList;

public class SubtitleFragment extends Fragment {
    private FragmentSubtitleBinding binding;
    private Player player;
    private DefaultTrackSelector trackSelector;
    private TrackSelectorRecyclerViewAdapter recyclerViewAdapter;
    private ArrayList<Format> recyclerViewItems;

    public SubtitleFragment(Player player, DefaultTrackSelector trackSelector) {
        this.player = player;
        this.trackSelector = trackSelector;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentSubtitleBinding.inflate(inflater, container, false);

        attachUI();

        return binding.getRoot();
    }

    private void attachUI() {
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerView.setHasFixedSize(true);
        binding.recyclerView.setNestedScrollingEnabled(true);
        recyclerViewItems = new ArrayList<>();
        recyclerViewAdapter = new TrackSelectorRecyclerViewAdapter(
                new TrackSelectorRecyclerViewAdapter.TrackDiff(), getActivity(), player,
                trackSelector, C.TRACK_TYPE_TEXT);
        binding.recyclerView.setAdapter(recyclerViewAdapter);
    }

    private void list() {
        MappingTrackSelector.MappedTrackInfo mappedInfoTrack = trackSelector.getCurrentMappedTrackInfo();
        TrackGroupArray trackGroups = mappedInfoTrack.getTrackGroups(C.TRACK_TYPE_TEXT);

        for (int groupIndex = 0; groupIndex < trackGroups.length; groupIndex++)
        {
            TrackGroup group = trackGroups.get(groupIndex);
            for (int trackIndex = 0; trackIndex < group.length; trackIndex++)
            {
                Format trackFormat = group.getFormat(trackIndex);
                recyclerViewItems.add(trackFormat);
            }
        }
        recyclerViewAdapter.submitList(recyclerViewItems);

        setLayoutVisibility();
    }

    private void setLayoutVisibility() {
        binding.recyclerView.setVisibility(recyclerViewItems.isEmpty()
                ? View.GONE : View.VISIBLE);
        binding.nullItemLayout.setVisibility(recyclerViewItems.isEmpty()
                ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onStart() {
        super.onStart();

        // List
        list();
    }
}