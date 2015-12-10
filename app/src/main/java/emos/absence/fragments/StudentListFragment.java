package emos.absence.fragments;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.internal.bind.DateTypeAdapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import emos.absence.R;
import emos.absence.listview.CustomAdapter;
import emos.absence.retrofit.StudentListData;
import emos.absence.retrofit.StudentListInterface;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.converter.GsonConverter;


public class StudentListFragment extends Fragment {
    final static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

    @InjectView(R.id.dayTextView) TextView dayTextView;
    @InjectView(R.id.monthTextView) TextView monthTextView;
    @InjectView(R.id.yearTextView) TextView yearTextView;
    @InjectView(R.id.studentListView) ListView studentListView;
    @OnClick(R.id.dateLinearLayout)
    public void onClick(){
        datePickerDialog.show();
    }

    private DatePickerDialog datePickerDialog;
    private CustomAdapter customAdapter;
    private Calendar datePickerCalendar;
    private ProgressDialog progressDialog;

    public static StudentListFragment newInstance() {
        StudentListFragment fragment = new StudentListFragment();
        return fragment;
    }

    public StudentListFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_student_list, container, false);

        ButterKnife.inject(this, rootView);

        datePickerCalendar = Calendar.getInstance();

        setDateTextView();

        datePickerDialog = new DatePickerDialog(getActivity(),
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear,
                                          int dayOfMonth) {
                        setDatePickerCalendar(year, monthOfYear, dayOfMonth);
                        setDateTextView();
                        studentListCommunication();
                    }
                }, datePickerCalendar.get(Calendar.YEAR), datePickerCalendar.get(Calendar.MONTH),
                datePickerCalendar.get(Calendar.DAY_OF_MONTH));


        customAdapter = new CustomAdapter();
        studentListView.setAdapter(customAdapter);

        studentListCommunication();

        return rootView;
    }

    public void studentListCommunication() {
        Gson gson = new GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .registerTypeAdapter(Date.class, new DateTypeAdapter())
                .create();

        RestAdapter restAdapter = new RestAdapter.Builder()
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setEndpoint(StudentListInterface.API_URL)
                .setConverter(new GsonConverter(gson))
                .build();

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage("Loading....");
        progressDialog.show();

        restAdapter.create(StudentListInterface.class).studentListData(getDate(),
                new Callback<List<StudentListData>>() {
            @Override
            public void success(List<StudentListData> studentListDatas, Response response) {
                try {
                    customAdapter = new CustomAdapter();
                    studentListView.setAdapter(customAdapter);

                    List<String> nameList = new ArrayList<String>();
                    for (int i = 0; i < studentListDatas.size(); i++) {
                        boolean distinct = true;
                        for (int j = 0; j < nameList.size(); j++)
                            if (nameList.get(j).equals(studentListDatas.get(i).name))
                                distinct = false;
                        if (distinct) nameList.add(studentListDatas.get(i).name);
                    }

                    for (int i = 0; i < nameList.size(); i++) {
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        Date tempDate = new Date();
                        long time = tempDate.getTime();
                        String isLate = "N";
                        for (int j = 0; j < studentListDatas.size(); j++) {
                            if (studentListDatas.get(j).name.equals(nameList.get(i))
                                    && simpleDateFormat.parse(studentListDatas.get(j).time).getTime() < time) {
                                time = simpleDateFormat.parse(studentListDatas.get(j).time).getTime();
                                isLate = studentListDatas.get(j).type;
                            }
                        }
                        String message = (isLate.equals("N")) ? "OK" : "Late";
                        customAdapter.add(nameList.get(i), message);
                    }
                    progressDialog.dismiss();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void failure(RetrofitError error) {
                progressDialog.dismiss();
            }
        });
    }

    private String getDate() {
        return simpleDateFormat.format(datePickerCalendar.getTimeInMillis());
    }

    public void setDatePickerCalendar(int year, int month, int day) {
        datePickerCalendar.set(year, month, day);
    }

    public void setDateTextView() {
        dayTextView.setText(new SimpleDateFormat("dd").format(datePickerCalendar.getTime()));
        yearTextView.setText(new SimpleDateFormat("yyyy").format(datePickerCalendar.getTime()));
        monthTextView.setText(new SimpleDateFormat("MMM").format(datePickerCalendar.getTime()));
    }
}
