package emos.absence.retrofit;

import java.util.List;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Query;

public interface AttendStudentInterface {
    public static final String API_URL = "http://truspoint.hanyang.ac.kr:30000";

    @GET("/log/sensor.php")
    void studentListData (
            @Query("id") String tagID,
            @Query("date") String date,
            @Query("time") String time,
            @Query("type") String isLate,
            Callback<List<AttendStudentData>> callback
    );
}


