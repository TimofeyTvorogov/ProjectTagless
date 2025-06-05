package com.example.bottomnavigationt;

import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.android.material.button.MaterialButton;

import java.io.File;
import java.io.IOException;


public class AddFragment extends Fragment implements View.OnClickListener {
    MaterialButton backIb;
    EditText addObjectEt, addTypeEt;
    MaterialButton uploadVandalismB;

    MainActivity activity;
    DataLoader dataLoader;
    protected String typeToAdd, objectToAdd;
    protected VandalismInfo vandalismToPost;

    private Uri latestTmpUri = null;
    private ImageView previewImage = null;

    File tmpFile;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_add, container, false);
        activity = (MainActivity) getActivity();
        backIb = view.findViewById(R.id.back_ib);

        addObjectEt = view.findViewById(R.id.add_object_et);
        addTypeEt = view.findViewById(R.id.add_type_et);
        previewImage = view.findViewById(R.id.vandalism_iv);
        uploadVandalismB = view.findViewById(R.id.upload_vandalism_b);


        backIb.setOnClickListener(this);
        uploadVandalismB.setOnClickListener(this);
        MaterialButton addFromCameraBtn = view.findViewById(R.id.add_camera_image),
                addFromGalleryBtn = view.findViewById(R.id.add_gallery_image);
        addFromGalleryBtn.setOnClickListener(this);
        addFromCameraBtn.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){

            case R.id.back_ib: activity.returnUI(); break;

            case R.id.add_gallery_image: selectImageFromGallery(); break;

            case R.id.add_camera_image: takeImage(); break;

            case R.id.upload_vandalism_b:

                if ((addTypeEt.getText() == null && addObjectEt.getText() == null)||
                        (addTypeEt.getText().equals("") && addObjectEt.getText().equals(""))) {

                    typeToAdd = "default type";
                    objectToAdd = "default object";

                }
                else {
                    typeToAdd = addTypeEt.getText().toString();
                    objectToAdd = addObjectEt.getText().toString();
                }

                //vandalismToPost = new VandalismInfo(null,null,null,typeToAdd,objectToAdd,null);

                dataLoader = DataLoader.getInstance();
                dataLoader.initLoc(true);
                if (latestTmpUri != null) {
                    dataLoader.postImage(latestTmpUri);
                }
                activity.returnUI();
                break;
        }
    }



    private final ActivityResultLauncher<String> selectImageFromGalleryResult =
            registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
                if (uri != null) {
                    //глобальная переменная для вытаскивания uri из колбека
                    latestTmpUri = uri;
                    previewImage.setImageURI(uri);

                }
            });
    private void selectImageFromGallery() {
        selectImageFromGalleryResult.launch("image/*");
    }

    private final ActivityResultLauncher<Uri> takeImageResult =
            registerForActivityResult(new ActivityResultContracts.TakePicture(), isSuccess -> {
                if (isSuccess) {
                    if (latestTmpUri != null) {
                        previewImage.setImageURI(latestTmpUri);
                    }
                }
            });

    private void takeImage() {
        final Uri uri;
        try {
            uri = getTmpFileUri();
        } catch (IOException exception) {
            throw new RuntimeException("Cannot create temp file", exception);
        }
        latestTmpUri = uri;

        takeImageResult.launch(uri);
    }


    private Uri getTmpFileUri() throws IOException {

        tmpFile = File.createTempFile("tmp_image_file", ".jpg", activity.getCacheDir());
        //noinspection ResultOfMethodCallIgnored
        tmpFile.createNewFile();
        tmpFile.deleteOnExit();

        final String authority = String.format("%s.provider", BuildConfig.APPLICATION_ID);
        return FileProvider.getUriForFile(getContext(), authority, tmpFile);}}