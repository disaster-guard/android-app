package com.shltr.darrieng.shltr_android.Fragment;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.messages.Message;
import com.google.android.gms.nearby.messages.MessageListener;
import com.google.android.gms.nearby.messages.Strategy;
import com.google.android.gms.nearby.messages.SubscribeCallback;
import com.google.android.gms.nearby.messages.SubscribeOptions;
import com.google.gson.Gson;
import com.shltr.darrieng.shltr_android.Model.UserRetrievalModel;
import com.shltr.darrieng.shltr_android.NearbyPersonAdapter;
import com.shltr.darrieng.shltr_android.Pojo.DeviceMessage;
import com.shltr.darrieng.shltr_android.Pojo.UserPojo;
import com.shltr.darrieng.shltr_android.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.view.View.GONE;
import static com.shltr.darrieng.shltr_android.Activity.OnboardingActivity.BASE_URL;
import static com.shltr.darrieng.shltr_android.Fragment.FlareFragment.ARG_PAGE;

/**
 * Fragment containing the Nearby feature.
 */
public class NearbyFragment extends Fragment
    implements Callback<UserPojo>, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    /**
     * Reference to shared preferences database.
     */
    SharedPreferences preferences;

    /**
     * Listener callback used to tell when a new nearby message is heard by the fragment.
     */
    MessageListener messageListener;

    /**
     * Reference to Google API client.
     */
    GoogleApiClient googleApiClient;

    /**
     * List of current users.
     */
    List<UserPojo> userList;

    /**
     * Adapter for list of current users.
     */
    NearbyPersonAdapter personAdapter;

    /**
     * View containing list of users.
     */
    @BindView(R.id.person_list)
    RecyclerView personList;

    /**
     * View containing loading symbol while waiting for other users.
     */
    @BindView(R.id.loader_view)
    LinearLayout loaderView;

    public NearbyFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_nearby, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        userList = new ArrayList<>();

        preferences = getActivity().getSharedPreferences(
            getString(R.string.base), Context.MODE_PRIVATE);

        messageListener = new MessageListener() {
            @Override
            public void onFound(Message message) {
                super.onFound(message);
                startNetworking(message);
                loaderView.setVisibility(GONE);
            }
        };

        personAdapter = new NearbyPersonAdapter(getContext(), userList);
        personList.setLayoutManager(new LinearLayoutManager(getActivity()));
        personList.setAdapter(personAdapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(getActivity())
                .addApi(Nearby.MESSAGES_API)
                .addConnectionCallbacks(this)
                .enableAutoManage(getActivity(), R.string.api_key, NearbyFragment.this)
                .build();
        }

        googleApiClient.connect();
    }

    /**
     * Helper method. Used to start networking when another user is heard.
     *
     * @param message Message containing user information.
     */
    public void startNetworking(Message message) {
        Gson gson = new Gson();
        // make call to API with email: network to pull down Name and profile
        Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build();

        UserRetrievalModel uModel = retrofit.create(UserRetrievalModel.class);

        Call<UserPojo> call = uModel.retrieveId("Bearer " +
                preferences.getString(getString(R.string.token), null),
            DeviceMessage.fromNearbyMessage(message).getMessageBody());

        call.enqueue(this);
    }

    /**
     * Static initializer used to instantiate fragment.
     *
     * @param page Page to put fragment on.
     * @return Newly initialized fragment.
     */
    public static NearbyFragment newInstance(int page) {
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, page);
        NearbyFragment fragment = new NearbyFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        SubscribeOptions options = new SubscribeOptions.Builder()
            .setStrategy(Strategy.DEFAULT)
            .setCallback(new SubscribeCallback() {
                @Override
                public void onExpired() {
                    super.onExpired();
                }
            }).build();

        Nearby.Messages.subscribe(googleApiClient, messageListener, options);
        Message message = DeviceMessage.newNearbyMessage(preferences.getString(getString(R.string.email), null));
        Nearby.Messages.publish(googleApiClient, message);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onResponse(Call<UserPojo> call, Response<UserPojo> response) {
        if (response.isSuccessful()) {
            personAdapter.addUser(
                new UserPojo(response.body().getPicture(), response.body().getName()));
        }
    }

    @Override
    public void onFailure(Call<UserPojo> call, Throwable t) {
    }

    @Override
    public void onPause() {
        super.onPause();
        googleApiClient.stopAutoManage(getActivity());
        googleApiClient.disconnect();
    }
}
