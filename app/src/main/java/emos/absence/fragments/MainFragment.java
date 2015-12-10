package emos.absence.fragments;

import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.TimePicker;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.internal.bind.DateTypeAdapter;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import emos.absence.R;
import emos.absence.retrofit.AttendStudentData;
import emos.absence.retrofit.AttendStudentInterface;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.converter.GsonConverter;

public class MainFragment extends Fragment {
    @InjectView(R.id.tagIDTextView) TextView tagIDTextView;
    @InjectView(R.id.hourTextView) TextView hourTextView;
    @InjectView(R.id.minuteTextView) TextView minuteTextView;
    @InjectView(R.id.messageTextView) TextView messageTextView;
    @OnClick(R.id.timeLinearLayout)
    public void onClick(){
        if (timePickerCalendar == null) timePickerCalendar = Calendar.getInstance();
        TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(),
                new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                setTimePickerCalendar(hourOfDay, minute);
                setTimeTextView();
            }
        }, timePickerCalendar.get(Calendar.HOUR_OF_DAY), timePickerCalendar.get(Calendar.MINUTE),
                false);
        timePickerDialog.show();
    }

    private Calendar timePickerCalendar;
    private ProgressDialog progressDialog;

    public void setTagIDTextView(String tagID) {
        tagIDTextView.setText(tagID);
    }

    public void setTimePickerCalendar(int hour, int minute) {
        timePickerCalendar.set(Calendar.HOUR_OF_DAY, hour);
        timePickerCalendar.set(Calendar.MINUTE, minute);
    }

    public void setTimeTextView() {
        hourTextView.setText(new SimpleDateFormat("HH").format(timePickerCalendar.getTime()));
        minuteTextView.setText(new SimpleDateFormat("mm").format(timePickerCalendar.getTime()));
    }

    public static MainFragment newInstance() {
        MainFragment fragment = new MainFragment();
        return fragment;
    }

    public MainFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        ButterKnife.inject(this, rootView);

        timePickerCalendar = Calendar.getInstance();
        setTimeTextView();

        return rootView;
    }

    public String isLate() {
        Calendar currentCalendar = Calendar.getInstance();
        if(currentCalendar.getTimeInMillis() <= timePickerCalendar.getTimeInMillis())
            return "N";
        else
            return "L";
    }

    public void attendStudentCommunication(String tagID, String date, String time,
                                           final String isLate) {
        Gson gson = new GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .registerTypeAdapter(Date.class, new DateTypeAdapter())
                .create();

        RestAdapter restAdapter = new RestAdapter.Builder()
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setEndpoint(AttendStudentInterface.API_URL)
                .setConverter(new GsonConverter(gson))
                .build();

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage("Loading....");
        progressDialog.show();

        restAdapter.create(AttendStudentInterface.class).studentListData(tagID, date, time, isLate,
                new Callback<List<AttendStudentData>>() {
            @Override
            public void success(List<AttendStudentData> attendStudentDatas, Response response) {
                String message = (isLate.equals("N")) ? "Success to attend" : "You're late.";
                messageTextView.setText(message);
                progressDialog.dismiss();
            }

            @Override
            public void failure(RetrofitError error) {
                String message = "Fail to attend";
                messageTextView.setText(message);
                progressDialog.dismiss();
            }
        });
    }
}
