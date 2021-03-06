package com.lewy.videoutil;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.RelativeLayout;

import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;
import com.lewy.videoutil.fragments.ShowMessageDialogFragment;
import com.lewy.videoutil.fragments.YouTubeDialogFragment;
import com.lewy.videoutil.interfaces.MessageDialogCallback;
import com.lewy.videoutil.managers.NetworkManager;

public class MainActivity extends AppCompatActivity implements YouTubePlayer.OnInitializedListener, MessageDialogCallback {

    private static final String TAG = "MainActivity";

    private static final int RECOVERY_REQUEST = 1;

    private YouTubePlayerSupportFragment youTubePlayerSupportFragment;
    private YouTubeDialogFragment youTubeDialogFragment;

    protected Toolbar toolbar;
    protected RelativeLayout toolbarLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getLayoutInflater().setFactory(this);
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        toolbarLayout = (RelativeLayout) findViewById(R.id.toolbar_layout);
        toolbar = (Toolbar) toolbarLayout.findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.app_name));
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        setSupportActionBar(toolbar);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(youTubePlayerSupportFragment == null) {
            youTubePlayerSupportFragment = (YouTubePlayerSupportFragment) getSupportFragmentManager().findFragmentById(R.id.player_view);

            if (NetworkManager.isNetworkAvailable(this)) {
                youTubePlayerSupportFragment.initialize(getString(R.string.youtube_api_key), this);
            } else {
                showMessage(this, "", getString(R.string.internet_disable));
            }
        }
    }

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
        youTubePlayer.setPlayerStyle(YouTubePlayer.PlayerStyle.CHROMELESS);

        youTubePlayer.setShowFullscreenButton(false);

        initializeYouTubeDialogFragment(youTubePlayer);
    }

    private void initializeYouTubeDialogFragment(YouTubePlayer youTubePlayer) {
        youTubeDialogFragment = new YouTubeDialogFragment().newInstance();
        youTubeDialogFragment.show(getSupportFragmentManager(), "YouTubeDialogFragment");
        youTubeDialogFragment.setYouTubePlayerSupportFragment(youTubePlayerSupportFragment);
        youTubeDialogFragment.setYouTubePlayer(youTubePlayer);
    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
        if (youTubeInitializationResult.isUserRecoverableError()) {
            youTubeInitializationResult.getErrorDialog(this, RECOVERY_REQUEST).show();
        } else {
            Log.e(TAG, getString(R.string.youtube_player_error) + "\n" + youTubeInitializationResult.toString());
            showMessage(this, "", getString(R.string.youtube_player_error));
        }
    }

    public void showMessage(MessageDialogCallback messageDialogCallback, String message, String title) {
        ShowMessageDialogFragment.newInstance(messageDialogCallback, message, title).show(getSupportFragmentManager().beginTransaction(), "ShowMessageDialogFragment");
    }

    @Override
    public void messageClose() {
        finish();
    }
}
