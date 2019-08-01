package ru.vital.daily.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.ViewModelProviders;
import androidx.viewpager.widget.ViewPager;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.bluelinelabs.logansquare.LoganSquare;
import com.github.chrisbanes.photoview.PhotoView;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.google.android.exoplayer2.video.VideoListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import ru.vital.daily.BR;
import ru.vital.daily.R;
import ru.vital.daily.adapter.GalleryPagerAdapter;
import ru.vital.daily.databinding.ActivityMediaEditorBinding;
import ru.vital.daily.enums.FileType;
import ru.vital.daily.repository.data.Media;
import ru.vital.daily.repository.model.MediaModel;
import ru.vital.daily.view.model.MediaEditorViewModel;

public class MediaEditorActivity extends BaseActivity<MediaEditorViewModel, ActivityMediaEditorBinding> {

    public static final String
                    MEDIA_CURRENT_EXTRA = "MEDIA_CURRENT_EXTRA",
                    MEDIA_LIST_EXTRA= "MEDIA_LIST_EXTRA";

    private SimpleExoPlayer player;

    private PlayerView playerView;

    private DataSource.Factory dataSourceFactory;

    private GalleryPagerAdapter pagerAdapter;

    private PhotoView photoView;

    @Override
    protected MediaEditorViewModel onCreateViewModel() {
        return ViewModelProviders.of(this, viewModelFactory).get(MediaEditorViewModel.class);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_media_editor;
    }

    @Override
    protected int getVariable() {
        return BR.viewModel;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupToolbar(dataBinding.toolbar, true);
        dataBinding.appBarLayout.bringToFront();


        try {
            pagerAdapter = new GalleryPagerAdapter(LoganSquare.parseList(getIntent().getStringExtra(MEDIA_LIST_EXTRA), Long.class));
        } catch (IOException e) {
            e.printStackTrace();
        }

        pagerAdapter.setSelectedMedias(viewModel.getSelectedMedias());
        /*try {
            pagerAdapter.setMedias(LoganSquare.parseList(getIntent().getStringExtra(MEDIA_LIST_EXTRA), Media.class));
        } catch (IOException e) {
            e.printStackTrace();
        }*/


        dataBinding.viewPager.setAdapter(pagerAdapter);
        dataBinding.viewPager.setCurrentItem(getIntent().getIntExtra(MEDIA_CURRENT_EXTRA, 0));
        viewModel.setCurrentMedia(pagerAdapter.getMedia(getIntent().getIntExtra(MEDIA_CURRENT_EXTRA, 0)));

        dataBinding.viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                viewModel.setCurrentMedia(pagerAdapter.getMedia(position));
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        pagerAdapter.pageSelectedEvent.observe(this, constraintLayout -> {
            stopVideo();
            if (playerView != null) {
                playerView.setPlayer(null);
                playerView = null;
            }

            Media media = pagerAdapter.getMedia(dataBinding.viewPager.getCurrentItem());
            if (FileType.video.name().equals(media.getType())) {
                playerView = constraintLayout.findViewById(R.id.player_view);
                photoView = constraintLayout.findViewById(R.id.photo_view);
                prepareVideo();
            }
        });

        viewModel.descriptionClickEvent.observe(this, aVoid -> {
            dataBinding.description.requestFocus();
            inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
        });
        viewModel.doneClickEvent.observe(this, aVoid -> {
            Media media = pagerAdapter.getMedia(dataBinding.viewPager.getCurrentItem());
            media.setDescription(dataBinding.description.getText().toString());
            viewModel.setCurrentMedia(media);

            inputMethodManager.hideSoftInputFromWindow(dataBinding.description.getWindowToken(), 0);
        });
        viewModel.sendClickEvent.observe(this, aVoid -> {
            setResult(Activity.RESULT_OK);
            finish();
        });

        pagerAdapter.clickEvent.observe(this, aVoid -> {
            if (player != null)
                player.setPlayWhenReady(!player.getPlayWhenReady());
        });

        dataBinding.getRoot().getViewTreeObserver().addOnGlobalLayoutListener(() -> {
            Rect r = new Rect();
            dataBinding.getRoot().getWindowVisibleDisplayFrame(r);
            int screenHeight = dataBinding.getRoot().getRootView().getHeight();

            // r.bottom is the position above soft keypad or device button.
            // if keypad is shown, the r.bottom is smaller than that before.
            int keypadHeight = screenHeight - r.bottom;

            if (keypadHeight > screenHeight * 0.15) { // 0.15 ratio is perhaps enough to determine keypad height.
                // keyboard is opened
                if (!viewModel.hasEdit.get()) {
                    viewModel.hasEdit.set(true);
                }
            }
            else {
                // keyboard is closed
                if (viewModel.hasEdit.get()) {
                    viewModel.hasEdit.set(false);
                    dataBinding.description.clearFocus();
                    //inputMethodManager.hideSoftInputFromWindow(dataBinding.description.getWindowToken(), 0);
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        initVideo();
        if (playerView != null)
            prepareVideo();
    }

    @Override
    protected void onStop() {
        super.onStop();
        releaseVideo();
    }

    public void initVideo() {
        dataSourceFactory = new DefaultDataSourceFactory(this,
                Util.getUserAgent(this, "Daily"));
        player = ExoPlayerFactory.newSimpleInstance(this);
        player.setVolume(0.5f);
        player.setRepeatMode(Player.REPEAT_MODE_ONE);
        player.setPlayWhenReady(true);
    }

    public void prepareVideo() {
        playerView.setPlayer(player);
        if (!player.getPlayWhenReady())
            player.setPlayWhenReady(true);
        player.addVideoListener(new VideoListener() {
            @Override
            public void onRenderedFirstFrame() {
                photoView.setVisibility(View.GONE);
            }
        });
        player.addListener(new Player.EventListener() {
            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                switch (playbackState) {
                    case 1:
                        photoView.setVisibility(View.VISIBLE);
                        break;
                }
            }
        });
        MediaSource videoSource = new ProgressiveMediaSource.Factory(dataSourceFactory)
                .createMediaSource(Uri.parse(pagerAdapter.getMedia(dataBinding.viewPager.getCurrentItem()).getFiles().get(0).getUrl()));
        player.prepare(videoSource);
    }

    public void releaseVideo() {
        if (player != null) {
            player.release();
            player = null;
        }
    }

    public void stopVideo() {
        if (player != null) {
            player.stop(true);
        }
    }

}
