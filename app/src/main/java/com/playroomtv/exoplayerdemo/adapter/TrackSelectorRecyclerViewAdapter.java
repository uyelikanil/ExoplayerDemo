package com.playroomtv.exoplayerdemo.adapter;

import android.content.Context;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;

import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;

import java.util.Locale;

public class TrackSelectorRecyclerViewAdapter extends ListAdapter<Format,
        TrackSelectorRecyclerViewHolder> {
    private Context context;
    public Player player;
    public DefaultTrackSelector defaultTrackSelector;
    public Format selectedFormat;
    public int trackType;

    public TrackSelectorRecyclerViewAdapter(@NonNull DiffUtil.ItemCallback<Format> diffCallback,
                                            Context context, Player player,
                                            DefaultTrackSelector defaultTrackSelector, int trackType)
    {
        super(diffCallback);

        this.context = context;
        this.player = player;
        this.defaultTrackSelector = defaultTrackSelector;
        this.trackType = trackType;

        setSelectedFormat();
    }

    // Sets selected format
    private void setSelectedFormat(){
        TrackSelectionArray currentTrackGroups = player.getCurrentTrackSelections();
        TrackSelection currentTrackSelection = currentTrackGroups.get(trackType);

        if(currentTrackSelection != null)
            selectedFormat = currentTrackSelection.getFormat(0);
        else {
            defaultTrackSelector.setParameters(defaultTrackSelector.buildUponParameters()
                    .setPreferredAudioLanguage(Locale.getDefault().getDisplayLanguage()));
        }
    }

    @Override
    public TrackSelectorRecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                                              int viewType) {
        return TrackSelectorRecyclerViewHolder.create(context, parent, this,
                player, trackType);
    }

    @Override
    public void onBindViewHolder(@NonNull TrackSelectorRecyclerViewHolder holder, int position) {
        Format format = getCurrentList().get(position);
        holder.bind(format, position);
    }

    @Override
    public int getItemCount() {
       if (getCurrentList() != null)
           return getCurrentList().size();
       else
           return 0;
    }

    public static class TrackDiff extends DiffUtil.ItemCallback<Format> {
        @Override
        public boolean areItemsTheSame(@NonNull Format oldItem, @NonNull Format newItem) {
            return oldItem == newItem;
        }

        @Override
        public boolean areContentsTheSame(@NonNull Format oldItem, @NonNull Format newItem) {
            return oldItem.equals(newItem);
        }
    }
}
