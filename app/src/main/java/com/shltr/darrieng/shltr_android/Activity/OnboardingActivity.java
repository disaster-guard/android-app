package com.shltr.darrieng.shltr_android.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.shltr.darrieng.shltr_android.Model.LoginModel;
import com.shltr.darrieng.shltr_android.Model.RegisterModel;
import com.shltr.darrieng.shltr_android.Pojo.BaseResponse;
import com.shltr.darrieng.shltr_android.Pojo.LoginPojo;
import com.shltr.darrieng.shltr_android.Pojo.RegisterPojo;
import com.shltr.darrieng.shltr_android.Pojo.UserToken;
import com.shltr.darrieng.shltr_android.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.view.View.GONE;

/**
 * Activity for user onboarding, login, and sign up.
 */
public class OnboardingActivity extends AppCompatActivity implements Callback<UserToken> {

    public static final String BASE_URL = "http://52.55.141.18";

    /**
     * Button used start sign up process.
     */
    @BindView(R.id.signup_button)
    Button signupButton;

    /**
     * Button used to start login process.
     */
    @BindView(R.id.login_button)
    Button loginButton;

    /**
     * View containing login and signup buttons.
     */
    @BindView(R.id.button_screen)
    LinearLayout buttonView;

    /**
     * View containing registration and signup screen.
     */
    @BindView(R.id.text_screen)
    LinearLayout textScreenView;

    /**
     * View for inputting user email.
     */
    @BindView(R.id.enterInputView)
    TextInputEditText enterInputView;

    /**
     * View for inputting user password.
     */
    @BindView(R.id.enter_pw_view)
    TextInputEditText enterPwView;

    /**
     * Button used for registration and sign up.
     */
    @BindView(R.id.go_button)
    FloatingActionButton goButton;

    /**
     * Parent view for inputting user name.
     */
    @BindView(R.id.name_layout)
    TextInputLayout nameLayout;

    /**
     * View for inputting user name.
     */
    @BindView(R.id.name_view)
    TextInputEditText nameView;

    /**
     * Determine if user is signing up.
     */
    private Boolean isSigningUp;

    /**
     * Reference to shared preferences.
     */
    private SharedPreferences preferences;

    /**
     * Reference to shared preferences editor.
     */
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        ButterKnife.bind(this);
        Window window = this.getWindow();
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
        preferences = getSharedPreferences(getString(R.string.base), MODE_PRIVATE);
        getSupportActionBar().hide();
        deterministicSkip();
        signupButton.setOnClickListener(v -> setUpForInput(true));
        loginButton.setOnClickListener(v -> setUpForInput(false));
        goButton.setOnClickListener(v -> {
            if (validateInput()) {
                // send to server, partay
                startNetworking();
            } else {
                // no-op
            }
        });

        enterInputView.setText("dave@davemachado.com");
        enterPwView.setText("password");
        nameView.setText("Darrien Glasser");

    }

    /**
     * Helper method used to determine if user input is valid.
     *
     * @return True if input is valid, false otherwise.
     */
    private boolean validateInput() {
        String nameInputView = enterInputView.getText().toString();
        String passwordInput = enterPwView.getText().toString();
        boolean isValid = false;
        try {
            Integer.parseInt(nameInputView);
            isValid = true;
        } catch (Exception e) {
            // no-op
        }

        if (nameInputView.contains("@")) {
            if (nameInputView.substring(nameInputView.indexOf("@")).contains(".")) {
                isValid = true;
            }
        }

        if (passwordInput.isEmpty()) {
            isValid = false;
        }

        return isValid;
    }

    /**
     * Helper method used to prep views for signing up or registration.
     *
     * @param isSigningUp True if user is signing up, false otherwise.
     */
    private void setUpForInput(boolean isSigningUp) {
        buttonView.setVisibility(GONE);
        this.isSigningUp = isSigningUp;
        textScreenView.setVisibility(View.VISIBLE);
        if (isSigningUp) {
            nameLayout.setVisibility(View.VISIBLE);
        } else {
            nameLayout.setVisibility(GONE);
        }
    }

    /**
     * Helper method used to set up screen for login or registration.
     */
    private void setUpForChoice() {
        textScreenView.setVisibility(View.GONE);
        buttonView.setVisibility(View.VISIBLE);
    }

    /**
     * Skip this activity if user is already logged in.
     */
    private void deterministicSkip() {
        if (preferences.contains(getString(R.string.token))) {
            Intent intent = new Intent(this, RescueeActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public void onBackPressed() {
        if (isSigningUp != null) {
            setUpForChoice();
            isSigningUp = null;
        }
    }

    /**
     * Put token in shared preferences database.
     *
     * @param token User token.
     */
    private void passData(String token) {
        editor = preferences.edit();
        editor.putString(getString(R.string.token), token);
        editor.apply();
    }

    @Override
    public void onResponse(Call<UserToken> call, Response<UserToken> response) {
        if (response.isSuccessful()) {
            Toast.makeText(this, "made it", Toast.LENGTH_SHORT).show();
            passData(response.body().getAccess_token());
            Intent intent = new Intent(this, RescueeActivity.class);
            intent.putExtra(getString(R.string.email), enterInputView.getText().toString());
            editor.putString(getString(R.string.email), enterInputView.getText().toString());
            editor.apply();
            startActivity(intent);
        } else {
            Toast.makeText(this, "Failed to login " + response.code() + " " + response.message(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onFailure(Call<UserToken> call, Throwable t) {
        Toast.makeText(this, "login failure: " + t.getMessage(), Toast.LENGTH_SHORT).show();
    }

    /**
     * Helper method used to begin network requests.
     */
    private void startNetworking() {
        Gson gson = new GsonBuilder().create();
        if (!isSigningUp) {
            Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

            LoginModel passwordModel = retrofit.create(LoginModel.class);

            Call<UserToken> call;
            LoginPojo pwpj =
                new LoginPojo(enterInputView.getText().toString(), enterPwView.getText().toString());
            call = passwordModel.loginUser(pwpj);
            call.enqueue(this);
        } else {
            Call<BaseResponse> call;
            Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson)).build();

            RegisterModel rm = retrofit.create(RegisterModel.class);
            call = rm.createUser(
                new RegisterPojo(
                    nameView.getText().toString(),
                    enterInputView.getText().toString(),
                    enterPwView.getText().toString()));

            call.enqueue(new Callback<BaseResponse>() {
                @Override
                public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(OnboardingActivity.this, "Successful register, please log in", Toast.LENGTH_SHORT).show();
                        setUpForInput(false);
                    } else {
                        String msg = "¢∞§¢£";
                        try {
                            msg = response.errorBody().string();
                        } catch (Exception e) {

                        }
                        Toast.makeText(OnboardingActivity.this, response.code() + " " + msg, Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<BaseResponse> call, Throwable t) {
                    Toast.makeText(OnboardingActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

        }
    }
}

