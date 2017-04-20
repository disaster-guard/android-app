package com.shltr.darrieng.shltr_android.Fragment;


import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialcamera.MaterialCamera;
import com.shltr.darrieng.shltr_android.Model.UploaderServiceModel;
import com.shltr.darrieng.shltr_android.Pojo.CompleteIdentificationModel;
import com.shltr.darrieng.shltr_android.Pojo.Match;
import com.shltr.darrieng.shltr_android.R;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;
import static com.shltr.darrieng.shltr_android.Activity.OnboardingActivity.BASE_URL;
import static com.shltr.darrieng.shltr_android.Fragment.FlareFragment.ARG_PAGE;

/**
 * Fragment used for user identification feature.
 */
public class IdentiferFragment extends Fragment implements Callback<CompleteIdentificationModel> {

    /**
     * Button used to take a picture.
     */
    @BindView(R.id.fire_image_button)
    ImageButton fireImageButton;

    /**
     * Location image is stored when picture is taken.
     */
    @BindView(R.id.ivPreview)
    ImageView ivPreview;

    /**
     * View holding text that shows similar users.
     */
    @BindView(R.id.agglomerate_text_view)
    TextView agglomerateTextView;

    /**
     * Referenec to shared preferences database.
     */
    SharedPreferences preferences;

    /**
     * Boolean used to determine if called from onActivityResult, or not.
     */
    boolean returningWithResult;

    /**
     * Path to file.
     */
    String filePath;

    /**
     * Camera request code.
     */
    private final static int CAMERA_RQ = 6969;

    public IdentiferFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_identifer, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

        preferences = getActivity().getSharedPreferences(getString(R.string.base), MODE_PRIVATE);

        fireImageButton.setOnClickListener((v) -> {
            File saveFolder = new File(Environment.getExternalStorageDirectory(), "camera");
            if (!saveFolder.mkdirs())
                new MaterialCamera(this)
                    .saveDir(saveFolder)
                    .stillShot()
                    .defaultToFrontFacing(true)
                    .start(CAMERA_RQ);
        });
    }

    /**
     * Static initializer for fragment.
     *
     * @param page Page fragment is located on.
     * @return Created fragment.
     */
    public static IdentiferFragment newInstance(int page) {
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, page);
        IdentiferFragment fragment = new IdentiferFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_RQ && resultCode == RESULT_OK) {
            returningWithResult = true;
            filePath = data.getDataString().substring(5);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (returningWithResult) {
            try {
                File f = new File(filePath);
                Bitmap myBitmap = BitmapFactory.decodeFile(f.getAbsolutePath());
                ivPreview.setImageBitmap(myBitmap);

                UploaderServiceModel service = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                    .create(UploaderServiceModel.class);

                File file = new File(filePath);

                RequestBody reqFile = RequestBody.create(MediaType.parse("image/*"), file);
                MultipartBody.Part body = MultipartBody.Part.createFormData("image", file.getName(), reqFile);
                RequestBody name = RequestBody.create(MediaType.parse("text/plain"), "image");

                service.postImage(body, name).enqueue(this);

            } catch (Exception exc) {
                Toast.makeText(getActivity(), exc.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }

        returningWithResult = false;
    }

    @Override
    public void onResponse(Call<CompleteIdentificationModel> call, Response<CompleteIdentificationModel> response) {
        if (response.isSuccessful()) {
            String probabilities = "";
            for (Match match: response.body().getUser_model().getMatches()) {
                probabilities += "Match: " + match.getName() + ", probability: " + match.getProb() + "\n";
            }
            agglomerateTextView.setText(probabilities);
        } else {
            Toast.makeText(getActivity(), response.code() + "", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onFailure(Call<CompleteIdentificationModel> call, Throwable t) {
        Toast.makeText(getActivity(), t.getMessage(), Toast.LENGTH_SHORT).show();
    }
}
