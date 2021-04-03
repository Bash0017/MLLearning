package com.k_zawn.mllearning;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class ContainerActivity extends AppCompatActivity implements View.OnClickListener{

    Button textRecog,faceDetect,objDetect;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_container);
        textRecog = findViewById(R.id.textRecognize);
        faceDetect = findViewById(R.id.faceDetection);
        objDetect = findViewById(R.id.detectObjectByModel);
        textRecog.setOnClickListener(this);
        faceDetect.setOnClickListener(this);
        objDetect.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.textRecognize) {
            Intent intent = new Intent(ContainerActivity.this, TextRecognize.class);
            startActivity(intent);
        }
        else if(view.getId() == R.id.faceDetection){
            Intent intent = new Intent(ContainerActivity.this,FaceDetectionActivity.class);
            startActivity(intent);
        }
        else if(view.getId() == R.id.detectObjectByModel){
            Intent intent = new Intent(ContainerActivity.this,ObbjectRecognition.class);
            startActivity(intent);
        }

    }
}