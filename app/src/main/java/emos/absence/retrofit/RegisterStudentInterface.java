package emos.absence.retrofit;

import java.util.List;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Query;

public interface RegisterStudentInterface {
    public static final String API_URL = "http://truspoint.hanyang.ac.kr:30000";

    @GET("/log/reg.php")
    void studentListData (
            @Query("id") String tagID,
            @Query("name") String name,
            Callback<List<RegisterStudentData>> callback
    );
}
