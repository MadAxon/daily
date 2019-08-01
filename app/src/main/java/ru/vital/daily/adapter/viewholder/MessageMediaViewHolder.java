package ru.vital.daily.adapter.viewholder;

import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.GridLayout;

import androidx.cardview.widget.CardView;
import androidx.databinding.Observable;
import androidx.databinding.ViewDataBinding;

import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.makeramen.roundedimageview.RoundedImageView;

import ru.vital.daily.BR;
import ru.vital.daily.databinding.ItemMessageEndMediaBinding;
import ru.vital.daily.databinding.ItemMessageStartMediaBinding;
import ru.vital.daily.enums.FileType;
import ru.vital.daily.listener.MessageMediaClickListener;
import ru.vital.daily.listener.SingleLiveEvent;
import ru.vital.daily.repository.data.Media;
import ru.vital.daily.repository.data.Message;
import ru.vital.daily.repository.model.MediaModel;
import ru.vital.daily.util.FileUtil;
import ru.vital.daily.view.model.item.MessageItemViewModel;
import ru.vital.daily.view.model.item.MessageMediaItemViewModel;

public class MessageMediaViewHolder extends MessageViewHolder<MessageMediaItemViewModel> implements Player.EventListener {

    private SingleLiveEvent<Media> videoEvent;

    private SimpleExoPlayer player;

    private final DataSource.Factory dataSourceFactory;

    private PlayerView playerView;

    private RoundedImageView roundedImageView;

    private CardView videoContainer;

    private Media media;

    public MessageMediaViewHolder(ViewDataBinding viewDataBinding) {
        super(viewDataBinding);
        dataSourceFactory = new DefaultDataSourceFactory(viewDataBinding.getRoot().getContext(),
                Util.getUserAgent(viewDataBinding.getRoot().getContext(), "Daily"));

        if (viewDataBinding instanceof ItemMessageEndMediaBinding)
            ((ItemMessageEndMediaBinding) viewDataBinding).setViewHolder(this);
        else if (viewDataBinding instanceof ItemMessageStartMediaBinding)
            ((ItemMessageStartMediaBinding) viewDataBinding).setViewHolder(this);
    }

    @Override
    public MessageMediaItemViewModel onCreateViewModel() {
        return new MessageMediaItemViewModel();
    }

    @Override
    public void unbind() {
        super.unbind();
        if (media != null) {
            media.removeOnPropertyChangedCallback(onPropertyChangedCallback);
            //media.getFiles().get(0).removeOnPropertyChangedCallback(onPropertyChangedCallback);
            stopVideo();
        }
    }

    public synchronized void playVideo() {
        videoEvent.setValue(media);
        player = ExoPlayerFactory.newSimpleInstance(viewDataBinding.getRoot().getContext());
        player.setVolume(0);
        player.setRepeatMode(Player.REPEAT_MODE_ONE);
        this.playerView.setPlayer(player);
        MediaModel mediaModel = media.getFiles().get(0);
        MediaSource videoSource = new ProgressiveMediaSource.Factory(dataSourceFactory)
                .createMediaSource(Uri.parse(mediaModel.getUrl()));
        player.prepare(videoSource);
        player.addListener(this);
        player.setPlayWhenReady(true);
    }

    public void stopVideo() {
        if (player != null) {
            Log.i("my_logs", "stop video");
            player.release();
            player = null;
            media.setPlaying(null);
            videoContainer.setVisibility(View.INVISIBLE);
            roundedImageView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
        Log.i("my_logs", "playbackState " + playbackState);
        switch (playbackState) {
            case 3:
                videoContainer.setVisibility(View.VISIBLE);
                roundedImageView.setVisibility(View.INVISIBLE);
                break;
        }
    }

    @Override
    public void onPlayerError(ExoPlaybackException error) {
        Log.i("my_logs", error.getLocalizedMessage());
    }

    public void setMedia(long mediaId) {
        media = viewModel.item.getMedias().get(mediaId);
        if (media != null) {
            media.addOnPropertyChangedCallback(onPropertyChangedCallback);
            //media.getFiles().get(0).addOnPropertyChangedCallback(onPropertyChangedCallback);
        }
    }

    public void setMediaPlaying(boolean setPropertyCallback) {
        if (media != null) {
            if (setPropertyCallback)
                media.addOnPropertyChangedCallback(onPropertyChangedCallback);
            //media.getFiles().get(0).addOnPropertyChangedCallback(onPropertyChangedCallback);
            media.setPlaying(true);
        }
    }

    public void setMediaReleasing() {
        if (media != null) {
            media.setPlaying(null);
            media.removeOnPropertyChangedCallback(onPropertyChangedCallback);
        }
    }

    public PlayerView getPlayerView() {
        return playerView;
    }

    public void setPlayerView(PlayerView playerView) {
        this.playerView = playerView;
        //this.playerView.setPlayer(player);
    }

    public RoundedImageView getRoundedImageView() {
        return roundedImageView;
    }

    public void setRoundedImageView(RoundedImageView roundedImageView) {
        this.roundedImageView = roundedImageView;
    }

    public CardView getVideoContainer() {
        return videoContainer;
    }

    public void setVideoContainer(CardView videoContainer) {
        this.videoContainer = videoContainer;
    }

    public boolean isReadyToPlay() {
        return media != null && player == null && FileUtil.exists(media.getFiles().get(0).getUrl());
    }

    private Observable.OnPropertyChangedCallback onPropertyChangedCallback = new Observable.OnPropertyChangedCallback() {
        @Override
        public void onPropertyChanged(Observable sender, int propertyId) {
            switch (propertyId) {
                /*case BR.url:
                    media.setPlaying(true);
                    break;*/
                case BR.playing:
                    if (media.getPlaying() != null && media.getPlaying())
                        playVideo();
                    else stopVideo();
                    break;
            }
        }
    };

    public void setVideoEvent(SingleLiveEvent<Media> videoEvent) {
        this.videoEvent = videoEvent;
    }
}
