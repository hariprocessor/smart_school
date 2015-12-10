package emos.absence.fragments;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.internal.bind.DateTypeAdapter;

import java.util.Date;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import emos.absence.R;
import emos.absence.retrofit.RegisterStudentData;
import emos.absence.retrofit.RegisterStudentInterface;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.converter.GsonConverter;

public class RegisterStudentFragment extends Fragment {

    @InjectView(R.id.tagIDEditText) android.support.v7.widget.AppCompatEditText tagIDEditText;
    @InjectView(R.id.registerButton) Button registerButton;
    @InjectView(R.id.studentNameEditText) android.support.v7.widget.AppCompatEditText studentNameEditText;
    @InjectView(R.id.messageTextView) TextView messageTextView;
    @OnClick(R.id.registerButton)
    public void onClick() {
        if (tagIDEditText.getText().toString().equals("")
                || studentNameEditText.toString().equals(""))
            messageTextView.setText("Fill in the blanks.");
        else {
            registerStudentCommunication(tagIDEditText.getText().toString(),
                    studentNameEditText.getText().toString());
            tagIDEditText.setText("");
            studentNameEditText.setText("");
        }
    }

    private ProgressDialog progressDialog;

	public static RegisterStudentFragment newInstance() {
		RegisterStudentFragment fragment = new RegisterStudentFragment();
		return fragment;
	}

	public RegisterStudentFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_register_student, container, false);

        ButterKnife.inject(this, rootView);

		return rootView;
	}

	public void setTagID(String tagID) {
		if (tagIDEditText != null)
			tagIDEditText.setText(tagID);
	}

    private void registerStudentCommunication(String tagID, final String studentName) {
        Gson gson = new GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .registerTypeAdapter(Date.class, new DateTypeAdapter())
                .create();

        RestAdapter restAdapter = new RestAdapter.Builder()
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setEndpoint(RegisterStudentInterface.API_URL)
                .setConverter(new GsonConverter(gson))
                .build();

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage("Loading....");
        progressDialog.show();

        restAdapter.create(RegisterStudentInterface.class).studentListData(tagID, studentName,
                new Callback<List<RegisterStudentData>>() {
            @Override
            public void success(List<RegisterStudentData> registerStudentDatas, Response response) {
                String message = studentName+" is registered successfully.";
                messageTextView.setText(message);
                progressDialog.dismiss();
            }

            @Override
            public void failure(RetrofitError error) {
                String message = studentName+ " is already registered.";
                messageTextView.setText(message);
                progressDialog.dismiss();
            }
        });
    }
}
