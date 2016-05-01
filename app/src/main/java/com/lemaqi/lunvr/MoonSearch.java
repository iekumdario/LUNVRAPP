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
 * Use the {@link MoonSearch#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MoonSearch extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private GoogleApiClient googleApliClient;
    private WebView lunvrView;
    private Location location;
    private LunvrIntegrations lunvrIntegrations;
    private LocationRequest locationRequest;
    private static final int REQUEST_LOCATION = 2;
    private static final int REQUEST_CHECK_SETTINGS = 3;

    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private Sensor mMagnetometer;
    private float[] mLastAccelerometer = new float[3];
    private float[] mLastMagnetometer = new float[3];
    private boolean mLastAccelerometerSet = false;
    private boolean mLastMagnetometerSet = false;
    private float[] mR = new float[9];
    private float[] mOrientation = new float[3];
    private float mCurrentDegree = 0f;
    private boolean azimothLoaded = false;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private MoonSearchInterface moonSearchInterface;

    public MoonSearch() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MoonSearch.
     */
    // TODO: Rename and change types and number of parameters
    public static MoonSearch newInstance(String param1, String param2) {
        MoonSearch fragment = new MoonSearch();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        //setContentView(R.layout.activity_lunvr_main);
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
