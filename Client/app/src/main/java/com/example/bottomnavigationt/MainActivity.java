package com.example.bottomnavigationt;

import static com.google.android.material.bottomsheet.BottomSheetBehavior.BottomSheetCallback;
import static com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_COLLAPSED;
import static com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_DRAGGING;
import static com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_EXPANDED;
import static com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_HIDDEN;
import static com.google.android.material.bottomsheet.BottomSheetBehavior.from;

import android.Manifest;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.HashMap;

import me.ibrahimsn.lib.OnItemSelectedListener;
import me.ibrahimsn.lib.SmoothBottomBar;


public class MainActivity extends AppCompatActivity implements OnItemSelectedListener, View.OnClickListener {

    private static final int REQUEST_CODE = 1001;
    private static final String FINE_LOC = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String CRS_LOC = Manifest.permission.ACCESS_COARSE_LOCATION;

    private boolean granted = false;

    private final FragmentManager fm = getSupportFragmentManager();
    private FragmentTransaction ft;

    private LinearLayout bottomSheet;
    private TextView addressTv, objectTv, typeTv, votesTv;
    private MaterialButton voteButton, deleteButton;

    private AboutFragment aboutFragment = new AboutFragment();
    private DataLoader dataLoader;

    protected AddFragment addFragment = new AddFragment();
    private SmoothBottomBar bottomBar;
    protected MapsFragment mapsFragment = new MapsFragment();
    protected FloatingActionButton fab;
    protected BottomSheetBehavior behavior;
    protected ImageView bottomSheetIV;

    private final BottomSheetCallback bottomSheetCallback = new BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                switch (newState) {
                    case STATE_DRAGGING:
                    case STATE_EXPANDED:
                        fab.animate().scaleX(0).scaleY(0).setDuration(300).start();
                        //fm.beginTransaction().hide(mapsFragment).commit();
                        break;
                    case STATE_COLLAPSED:
                    case STATE_HIDDEN:
                        fab.animate().scaleX(1).scaleY(1).setDuration(300).start();
                        //fm.beginTransaction().show(mapsFragment).commit();
                        break;


                    case BottomSheetBehavior.STATE_HALF_EXPANDED:
                        break;
                    case BottomSheetBehavior.STATE_SETTLING:
                        break;
                }

            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        };




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        findViewsByIds();
        setupBottomSheet();
        setCallBacks();
        //todo check if necessary
        bottomBar.setItemActiveIndex(0);
        ft = fm.beginTransaction()
                .add(R.id.fragment_container,mapsFragment)
                .add(R.id.fragment_container,addFragment).hide(addFragment)
                .add(R.id.fragment_container, aboutFragment).hide(aboutFragment);
        ft.commit();
    }
    private void setCallBacks() {
        deleteButton.setOnClickListener(this);
        voteButton.setOnClickListener(this);
        fab.setOnClickListener(this);
        bottomBar.setOnItemSelectedListener(this);
        behavior.addBottomSheetCallback(bottomSheetCallback);
    }
    private void setupBottomSheet(){
        behavior = from(bottomSheet);
        behavior.setHideable(true);
        behavior.setState(STATE_HIDDEN);
        behavior.setPeekHeight(280);
    }
    private void findViewsByIds(){
        bottomSheet = findViewById(R.id.bottom_sheet);
        addressTv = findViewById(R.id.address_tv);
        objectTv = findViewById(R.id.object_tv);
        typeTv = findViewById(R.id.type_tv);
        votesTv = findViewById(R.id.votes_tv);
        voteButton = findViewById(R.id.vote_b);
        deleteButton = findViewById(R.id.delete_b);
        fab = findViewById(R.id.open_addFragment_fab);
        bottomBar = findViewById(R.id.bottomBar);
        bottomSheetIV = findViewById(R.id.b_sheet_iv);
    }
    @Override
    public void onClick(View view) {


        if (dataLoader == null) {
            dataLoader = DataLoader.getInstance();
        }

        switch (view.getId()){
            case R.id.open_addFragment_fab:
                fab.hide();
                ft = fm.beginTransaction()
                        .show(addFragment)
                        .hide(mapsFragment)
                        .setReorderingAllowed(true)
                        .addToBackStack(null);
                ft.commit();
                bottomBar.setVisibility(View.GONE);
                break;

            case R.id.vote_b:
                VandalismInfo vandalismInfo = (VandalismInfo) mapsFragment.getCurrentMarker().getTag();
                Long votes = vandalismInfo.getVotes();
                HashMap<String,Object> queryMap = new HashMap<>();
                queryMap.put("votes",++votes);
                dataLoader.putVandalism(vandalismInfo.getId(),queryMap);
                voteButton.setClickable(false);
                behavior.setState(STATE_HIDDEN);
                break;

            case R.id.delete_b:
                vandalismInfo = (VandalismInfo) mapsFragment.getCurrentMarker().getTag();
                dataLoader.deleteVandalism(vandalismInfo.getId());
                behavior.setState(STATE_HIDDEN);
                break;

            }

        }





    @Override
    public boolean onItemSelect(int i) {
        if (i==0 && aboutFragment.adminSwitch.isChecked()){
            fab.show();
        }
        else{
            fab.hide();
        }
        ft = fm.beginTransaction();
        switch (i){
            case 0:
            ft.hide(aboutFragment);
            ft.show(mapsFragment);
            break;
            case 1:
                ft.hide(mapsFragment);
                ft.show(aboutFragment);
            break;
        }
        ft.commit();
        return true;
    }

    @Override
    public void onBackPressed() {
        mapsFragment.currentMarker = null;
        switch (bottomBar.getVisibility()){

            case View.VISIBLE:
                if(behavior.getState()== STATE_HIDDEN){
                    finish();
                }
                else{
                    behavior.setState(STATE_HIDDEN);

                }
                break;

            case View.GONE:
                returnUI();
                break;

        }

    }
    public void returnUI(){
        bottomBar.setVisibility(View.VISIBLE);
        fm.popBackStack();
        fab.show();
    }
    public void setInfoOnBottomSheet(VandalismInfo vandalismInfo,DataLoader loader) {

        addressTv.setText(vandalismInfo.getAddress());
        objectTv.setText(String.format("Объект: %s",vandalismInfo.getObject()));
        typeTv.setText(String.format("Тип: %s",vandalismInfo.getType()));
        votesTv.setText(String.format("Голосов: %s",vandalismInfo.getVotes().toString()));
        loader.getImage(vandalismInfo.getImageName());
        behavior.setState(STATE_EXPANDED);
    }

    public boolean checkPermission(){

        if (ActivityCompat.checkSelfPermission(this, FINE_LOC) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, CRS_LOC) != PackageManager.PERMISSION_GRANTED) {
            //что делать, если разрешение не дано: попробовать запросить
            ActivityCompat.requestPermissions(this, new String[]{FINE_LOC, CRS_LOC}, REQUEST_CODE);
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE) {
            granted = true;
            if (grantResults.length > 0) {
                for (int i = 0; i < grantResults.length; i++) {
                    if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                        granted = false;

                        break;

                    }
                }
            }
            else {
                granted = false;

            }
        }
    }

    public boolean isGranted() {
        return granted;
    }
}
