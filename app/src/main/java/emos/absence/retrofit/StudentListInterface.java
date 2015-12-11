package emos.absence.retrofit;

import java.util.List;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Query;

public interface StudentListInterface {
    public static final String API_URL = "http://truspoint.hanyang.ac.kr:30000";

    @GET("/log/view.php")
    void studentListData (
            @Query("date") String date,
            Callback<List<StudentListData>> callback
    );
}
