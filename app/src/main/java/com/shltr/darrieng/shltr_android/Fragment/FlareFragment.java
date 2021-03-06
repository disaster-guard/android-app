package com.shltr.darrieng.shltr_android.Fragment;


import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.shltr.darrieng.shltr_android.Model.FlareModel;
import com.shltr.darrieng.shltr_android.Pojo.BaseResponse;
import com.shltr.darrieng.shltr_android.Pojo.FlarePojo;
import com.shltr.darrieng.shltr_android.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.shltr.darrieng.shltr_android.Activity.OnboardingActivity.BASE_URL;

/**
 * Fragment containing ability for users to fire flares.
 */
public class FlareFragment extends Fragment
    implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, Callback<BaseResponse> {

    public static final String ARG_PAGE = "ARG_PAGE";

    /**
     * Provides the entry point to Google Play services.
     */
    protected GoogleApiClient googleApiClient;

    /**
     * Timer used to reset flare button if connection is never established.
     */
    private TimerTask timerTask;

    /**
     * Reference to shared preferences database.
     */
    private SharedPreferences preferences;

    /**
     * User location.
     */
    private Location location;

    /**
     * Timer used in conjunction with timer task.
     */
    private Timer timer;

    /**
     * Number of times to retry firing a flare.
     */
    private int retry = 0;

    /**
     * Button used to fire a flare.
     */
    @BindView(R.id.flare_button)
    Button flareButton;

    /**
     * Loader displayed while firing a flare.
     */
    @BindView(R.id.flare_loader)
    ProgressBar flareLoader;

    /**
     * View displayed showing when the last flare was fired.
     */
    @BindView(R.id.last_fired_flare)
    TextView lastFiredFlareView;

    public FlareFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_flare, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        preferences = getActivity().getSharedPreferences(getString(R.string.base), Context.MODE_PRIVATE);

        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        }

        if (preferences.getString(getString(R.string.date), null) != null) {
            DateFormat df = new SimpleDateFormat("dd/MM/yy HH:mm:ss");
            Date dateobj = new Date();
            System.out.println(df.format(dateobj));
            String text = String.format(getString(R.string.last_fired_flare), df.format(dateobj));
            lastFiredFlareView.setText(text);
        }

        googleApiClient.connect();

        flareButton.setOnClickListener(v -> {
            flareButton.setVisibility(View.GONE);
            flareLoader.setVisibility(View.VISIBLE);
            if (!activateFlair() && retry < 3) {
                timerTask = new TimerTask() {
                    @Override
                    public void run() {
                        activateFlair();
                        ++retry;
                    }
                };
                timer = new Timer();
                timer.schedule(timerTask, 2500);
            }
            if (retry == 3) {
                retry = 0;
                flareButton.setVisibility(View.GONE);
                flareLoader.setVisibility(View.VISIBLE);
            }
        });
    }

    /**
     * Helper method used to determine if a flare can be fired.
     *
     * @return true if flare was fired, false otherwise.
     */
    private boolean activateFlair() {
        if (location == null) {
            return false;
        }
        Gson gson = new GsonBuilder().create();
        Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build();

        FlareModel fm = retrofit.create(FlareModel.class);

        Call<BaseResponse> call;

        call = fm.createUser(preferences.getString(getString(R.string.token), null),
            new FlarePojo(location.getLongitude(),
                location.getLatitude(),
                preferences.getInt(
                    getString(R.string.id), -1)));

        call.enqueue(this);
        return true;
    }

    @Override
    public void onDestroyView() {
        if (googleApiClient.isConnected()) {
            googleApiClient.disconnect();
        }
        super.onStop();
        super.onDestroyView();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(
            getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(getActivity(),
            Manifest.permission.ACCESS_COARSE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {

        } else {
            location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
        }

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    /**
     * Static initializer for fragment.
     *
     * @param page Page fragment is located on.
     * @return Fragment created.
     */
    public static FlareFragment newInstance(int page) {
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, page);
        FlareFragment fragment = new FlareFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
        if (response.isSuccessful()) {
            flareLoader.setVisibility(View.GONE);
            flareButton.setVisibility(View.VISIBLE);

            DateFormat df = new SimpleDateFormat("dd/MM/yy HH:mm:ss");
            Date dateobj = new Date();
            System.out.println(df.format(dateobj));

            SharedPreferences.Editor editor = preferences.edit();
            editor.putString(getString(R.string.date), df.format(dateobj));
            String text = String.format(getString(R.string.last_fired_flare), df.format(dateobj));
            lastFiredFlareView.setText(text);
            editor.apply();

            // flare fired: partay
        } else {
        }
    }

    @Override
    public void onFailure(Call<BaseResponse> call, Throwable t) {

    }
}
