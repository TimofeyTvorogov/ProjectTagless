package com.example.bottomnavigationt;


import android.content.ContentResolver;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.util.Log;

import android.widget.Toast;


import androidx.annotation.NonNull;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.squareup.picasso.Picasso;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class DataLoader implements OnSuccessListener<Location>, OnFailureListener {

    private static final Locale ru = new Locale("ru","RU");
    private final MainActivity activity;
    private final Geocoder geocoder;
    private final FusedLocationProviderClient locationClient;
    private final ContentResolver resolver;
    private List<Address> rawAddresses;
    private static Double lat,lon;
    private static String address;
    private final MapsFragment fragment;
    private final GoogleMap googleMap;
    private static DataLoader instance = null;
    private boolean isPostRequest;

    private static final String url = "http://94.241.169.69:8080/";

    private final Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(url)
            .addConverterFactory(GsonConverterFactory.create())
            .build();

    private final ClientService clientService = retrofit.create(ClientService.class);
   private File tmpFile;
    private List<VandalismInfo> vandalismList;

    private DataLoader(GoogleMap googleMap,MapsFragment fragment){
        this.googleMap = googleMap;
        this.fragment = fragment;
        activity = (MainActivity) fragment.getActivity();
        geocoder = new Geocoder(activity,ru);
        locationClient = LocationServices.getFusedLocationProviderClient(activity);
        resolver = activity.getApplicationContext().getContentResolver();
    }

    public static DataLoader getInstance(GoogleMap googleMap, MapsFragment fragment){
        if (instance == null) {
            instance = new DataLoader(googleMap,fragment);
        }
        return instance;
    }

    public static DataLoader getInstance() {
        if (instance == null)
            throw new IllegalStateException("одиночка ещё не был создан, " + "воспользуйтесь перегруженной функцией с аргументами");

        return instance;
    }
    public void getImage(String imageName){
        String fullUrl = url+"api/v1/vandalism/"+imageName;
        Picasso.get().load(fullUrl).into(activity.bottomSheetIV);

    }

    public void getVandalism(){

        Call<List<VandalismInfo>> call = clientService.getVandalism();
        call.enqueue(new GetVandalismCallBack());
    }


    /*метод в который передаётся uri, с помощью которого
    вновь создается файл с изображением
    из файла получаем его имя
    в очередь ставится мультипарт запрос на отправку картинки и её имени
    (как метод должен работать по задумке)
     */
    public void postImage(Uri uri){
        FileOutputStream fos = null;
        InputStream is = null;
        try {

            tmpFile = File.createTempFile("img",".jpg");


            is = resolver.openInputStream(uri);

            fos = new FileOutputStream(tmpFile);

            int readNum = 0;
            byte[] bytes = new byte[1024];

            while ((readNum = is.read(bytes)) != -1) {
                fos.write(bytes, 0, readNum);
            }

        }
        catch (IOException e) {e.printStackTrace();}
        finally {
            try{
                if (is != null) {
                    is.close();
                }
                if (fos != null) {
                    fos.close();
                }
        }
        catch (IOException e){e.printStackTrace();}
        }


//        file = new File(resolver.openInputStream(uri));
        MultipartBody.Part filePart = MultipartBody.Part.createFormData("file", tmpFile.getName(),
                RequestBody.create(MediaType.parse("image/*"), tmpFile));



        Call<Void> call = clientService.postImage(tmpFile.getName(),filePart);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {

                    //Toast.makeText(activity, response.message(), Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                //Toast.makeText(activity,t.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void postVandalism(VandalismInfo vandalismInfo){
        Call<Void> call = clientService.postVandalism(vandalismInfo);
        call.enqueue(new PPDVandalismCallBack());

    }

    public  void deleteVandalism(Long id){

        Call<Void> call =clientService.deleteVandalism(String.valueOf(id));
        call.enqueue(new PPDVandalismCallBack());
    }

    public void putVandalism(Long id, HashMap<String,Object> paramsMap){

        Call<Void> call = clientService.putVandalism(String.valueOf(id),paramsMap);
        call.enqueue(new PPDVandalismCallBack());
    }

    public void initLoc(boolean postRequest){

        if (activity.checkPermission()|| activity.isGranted()){
            locationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY,null)
                    .addOnSuccessListener(this).addOnFailureListener(this);
        }
        isPostRequest = postRequest;
    }
    public String getDecodedAddress(List<Address> rawAddresses){
        if (rawAddresses!=null){

            if (!rawAddresses.isEmpty()){

                Address rawAddress = rawAddresses.get(0);

                String[] rawAddressSplit = rawAddress.getAddressLine(0).split(", ");

                address = String.format(
                        "%s, %s",
                        rawAddressSplit[0],
                        rawAddressSplit[1]);

                if (rawAddress.getFeatureName()!=null&&!rawAddress.getFeatureName().equals(rawAddressSplit[1])){
                    address = address+", "+rawAddress.getFeatureName();
                }

                return address;
            }
            else throw new IllegalStateException("список адресов пуст");
        }
        else throw new NullPointerException("null список");

    }

    @Override
    public void onSuccess(Location location) {
        if (location != null) {

            lat = location.getLatitude();
            lon = location.getLongitude();

            if (!isPostRequest){
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat,lon),3));

            }
            else {
                try {
                    rawAddresses = geocoder.getFromLocation(lat, lon, 1);
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
                String fileName = tmpFile == null? "null": tmpFile.getName();
                postVandalism(new VandalismInfo(
                        lat,
                        lon,
                        getDecodedAddress(rawAddresses),
                        activity.addFragment.typeToAdd,
                        activity.addFragment.objectToAdd,
                        fileName)
                        );


            }

            //activity.addFragment.
        }
    }
    @Override
    public void onFailure(@NonNull Exception e) {
        //Toast.makeText(fragment.getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();

    }

    private class GetVandalismCallBack implements Callback<List<VandalismInfo>> {
        @Override
        public void onResponse(Call<List<VandalismInfo>> call, Response<List<VandalismInfo>> response) {
            if (response.isSuccessful()) {

                vandalismList = response.body();

                for (VandalismInfo vandalismInfo: vandalismList) {

                    LatLng latLng = new LatLng(vandalismInfo.getLat(), vandalismInfo.getLon());

                    Marker marker = googleMap.addMarker(new MarkerOptions().position(latLng));
                    marker.setTag(vandalismInfo);

                    if (vandalismInfo.getCleaned()){
                        marker.setVisible(false);
                    }
                }

                Log.d("onResponse","good answer" );


            }
            else {
                Log.d("onResponse","bad answer" );
//                Toast.makeText(fragment.getContext(), response.message(), Toast.LENGTH_SHORT).show();
                throw new IllegalStateException(response.message());
            }

        }
        @Override
        public void onFailure(Call<List<VandalismInfo>> call, Throwable t) {
            Log.d("onFailure","failed to get response");
            //Toast.makeText(fragment.getContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    private class PPDVandalismCallBack implements Callback<Void> {

        @Override
        public void onResponse(Call<Void> call, Response<Void> response) {
            if (response.isSuccessful()) {

                    googleMap.clear();
                    getVandalism();
//                    if (isPostRequest)
//                        Toast.makeText(activity, "случай вандализма добавлен", Toast.LENGTH_SHORT).show();
            }
            else{
                try {
                    throw new IllegalStateException(response.errorBody().string());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void onFailure(Call<Void> call, Throwable t) {
            Log.d("onFailure","failed to get response");
            //Toast.makeText(fragment.getContext(),"failed response\n"+t.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

}
