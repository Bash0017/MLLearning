package com.k_zawn.mllearning;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import com.k_zawn.mllearning.facedetection.facedetectionprocessor;

public class MyFaceDetector extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback,
        AdapterView.OnItemSelectedListener,
        CompoundButton.OnCheckedChangeListener {

    private CameraSource cameraSource = null;
    private CameraSourcePreview cameraSourcePreview;
    private GraphicOverlay graphicOverlay;
    private ToggleButton toggleButton;
    private final String FACE_DETECTION = "FACE DETECTION";
    private static final String TEXt_DETECTION = "TEXT DETECTION";

    private String select = FACE_DETECTION;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_face_detector);
        cameraSourcePreview = (CameraSourcePreview) findViewById(R.id.firePreview);

        graphicOverlay = (GraphicOverlay) findViewById(R.id.fireFaceOverlay);


        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        List<String> options = new ArrayList<>();
        options.add(FACE_DETECTION);

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this, R.layout.spinner_style, options);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // attaching data adapter to spinner
        spinner.setAdapter(dataAdapter);
        spinner.setOnItemSelectedListener(this);

        ToggleButton facingSwitch = (ToggleButton) findViewById(R.id.facingswitch);
        facingSwitch.setOnCheckedChangeListener(this);

        createCameraSource(select);


    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        select = parent.getItemAtPosition(position).toString();
        cameraSourcePreview.stop();
        createCameraSource(select);
        startCameraSource();
    }

    private void createCameraSource(String model) {

        if (cameraSource == null) {
            cameraSource = new CameraSource(this, graphicOverlay);
        }
        else
        {
            cameraSource.setMachineLearningFrameProcessor(new facedetectionprocessor());
        }
//        switch (model) {
//            case FACE_DETECTION:

//                break;
//            default:
//        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void startCameraSource() {
        if (cameraSource != null) {
            try {
                cameraSourcePreview.start(cameraSource, graphicOverlay);
            } catch (IOException e) {
                cameraSource.release();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        cameraSourcePreview.stop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        cameraSourcePreview.release();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cameraSource.release();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        Toast.makeText(this, "camera toggled", Toast.LENGTH_SHORT).show();
        createCameraSource(FACE_DETECTION);
        startCameraSource();
        cameraSource.setMachineLearningFrameProcessor(new facedetectionprocessor());
    }
}