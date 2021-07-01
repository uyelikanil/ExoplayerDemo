package com.playroomtv.exoplayerdemo.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import androidx.recyclerview.widget.RecyclerView;

import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.ui.DefaultTrackNameProvider;
import com.google.android.exoplayer2.ui.TrackNameProvider;
import com.playroomtv.exoplayerdemo.utilities.TrackConst;
import com.playroomtv.exoplayerdemo.databinding.TrackCardviewBinding;

public class TrackSelectorRecyclerViewHolder extends RecyclerView.ViewHolder {
    private Context context;
    private TrackSelectorRecyclerViewAdapter trackSelectorRecyclerViewAdapter;
    private int trackType;
    private TrackCardviewBinding dataBinding;

    private TrackSelectorRecyclerViewHolder(Context context, TrackCardviewBinding dataBinding,
                                            TrackSelectorRecyclerViewAdapter trackSelectorRecyclerViewAdapter,
                                            Player player, int trackType) {
        super(dataBinding.getRoot());

        this.context = context;
        this.trackSelectorRecyclerViewAdapter = trackSelectorRecyclerViewAdapter;
        this.trackType = trackType;
        this.dataBinding = dataBinding;
    }

    public void bind(Format format, int position) {
        TrackNameProvider trackNameProvider = new DefaultTrackNameProvider(context.getResources());
        String trackName = format.label != null
                ? trackNameProvider.getTrackName(format) + "(" + format.label + ")"
                : trackNameProvider.getTrackName(format);
        dataBinding.textView.setText(trackName);
        dataBinding.textView.setEllipsize(TextUtils.TruncateAt.MARQUEE);

        dataBinding.radioButton.setOnCheckedChangeListener(null);
        dataBinding.radioButton.setChecked(format == trackSelectorRecyclerViewAdapter.selectedFormat);
        dataBinding.radioButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    if(trackType == TrackConst.TRACK_AUDIO) {
                        trackSelectorRecyclerViewAdapter.defaultTrackSelector.setParameters(
                                trackSelectorRecyclerViewAdapter.defaultTrackSelector
                                        .buildUponParameters()
                                        .setPreferredAudioLanguage(format.language));
                    } else if(trackType == TrackConst.TRACK_SUBTITLE) {
                        trackSelectorRecyclerViewAdapter.defaultTrackSelector.setParameters(
                                trackSelectorRecyclerViewAdapter.defaultTrackSelector
                                        .buildUponParameters()
                                        .setPreferredTextLanguage(format.language));
                    }

                    trackSelectorRecyclerViewAdapter.selectedFormat = format;
                    trackSelectorRecyclerViewAdapter.notifyDataSetChanged();
                }
            }
        });

        dataBinding.cardView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus)
                    dataBinding.radioButton.requestFocus();
            }
        });

        dataBinding.radioButton.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                dataBinding.textView.setSelected(hasFocus);
            }
        });
    }

    static TrackSelectorRecyclerViewHolder create(Context context, ViewGroup parent,
                                                  TrackSelectorRecyclerViewAdapter trackSelectorRecyclerViewAdapter,
                                                  Player player, int trackType) {
        TrackCardviewBinding binding = TrackCardviewBinding.inflate(LayoutInflater.from(
                parent.getContext()), parent, false);

        return new TrackSelectorRecyclerViewHolder(context, binding,
                trackSelectorRecyclerViewAdapter, player, trackType);
    }
}