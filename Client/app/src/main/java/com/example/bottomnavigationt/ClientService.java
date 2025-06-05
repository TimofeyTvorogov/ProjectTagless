package com.example.bottomnavigationt;


import java.util.HashMap;
import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.QueryMap;


public interface ClientService {

    @Multipart
    @POST("api/v1/vandalism/postImage/{name}")
    Call<Void> postImage(@Path("name") String fileName, @Part MultipartBody.Part filePart);

    @GET("api/v1/vandalism")
    Call<List<VandalismInfo>> getVandalism();

    @POST("api/v1/vandalism")
    Call<Void> postVandalism(@Body VandalismInfo vandalismInfo);

    @PUT("api/v1/vandalism/{id}")
    Call<Void> putVandalism(@Path("id") String id, @QueryMap HashMap<String,Object> params);

   @DELETE("api/v1/vandalism/{id}")
   Call<Void> deleteVandalism(@Path("id") String id);




}
