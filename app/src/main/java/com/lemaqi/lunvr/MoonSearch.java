package com.lemaqi.lunvr;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.lemaqi.lunvr.integrations.LunvrIntegrations;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MoonSearch.MoonSearchInterface} interface
 * to handle interaction events.
 */
public class MoonSearch extends Fragment {
    private WebView lunvrView;
    private LunvrIntegrations lunvrIntegrations;
    private MoonSearchInterface moonSearchInterface;

    public MoonSearch() {
        // Required empty public constructor
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        lunvrView = (WebView) getActivity().findViewById(R.id.lunvrview);
        WebSettings settings = lunvrView.getSettings();
        settings.setAllowFileAccessFromFileURLs(true);
        settings.setAllowUniversalAccessFromFileURLs(true);
        settings.setJavaScriptEnabled(true);

        lunvrIntegrations = moonSearchInterface.getIntegrations();
        lunvrView.addJavascriptInterface(lunvrIntegrations, "Android");
        loadVr();
    }

    @Override
    public void onPause() {
        if (lunvrIntegrations != null) {
            MediaPlayer player = lunvrIntegrations.getMediaPlayer();
            if (player != null) {
                player.stop();
            }
        }
        super.onPause();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_moon_search, container, false);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof MoonSearchInterface) {
            moonSearchInterface = (MoonSearchInterface) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement MoonSearchInterface");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        moonSearchInterface = null;
    }

    private void loadVr() {
        lunvrView.loadUrl("file:///android_asset/lunvrjs/index.html");
    }

    public interface MoonSearchInterface {
        LunvrIntegrations getIntegrations();
    }
}
