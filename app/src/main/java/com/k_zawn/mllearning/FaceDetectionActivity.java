package com.k_zawn.mllearning;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.common.FirebaseVisionPoint;
import com.google.firebase.ml.vision.face.FirebaseVisionFace;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceContour;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetector;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetectorOptions;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceLandmark;

import java.io.IOException;
import java.util.List;

public class FaceDetectionActivity extends AppCompatActivity {

    private static final int CAMERA_REQUEST = 1888;
    private static final int GALLERY_PICTURE = 1999;
    private static final int MY_CAMERA_PERMISSION_CODE = 100;
    private static final int MY_GALLERY_PERMISSION_CODE=200;
    private static final String TAG = "FaceDetectionActivity";
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    Bitmap photo;
    ImageView uploadImage;
    Button detectFace;
    GraphicOverlay graphicOverlay;
    Canvas canvas;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_face_detection);
        uploadImage = findViewById(R.id.uploadImage);
        detectFace = findViewById(R.id.detectFace);
        graphicOverlay = findViewById(R.id.graphic_overlay2);
        graphicOverlay.bringToFront();
        detectFace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startDialog();
            }
        });
    }


    private void startDialog() {
        AlertDialog.Builder myAlertDialog = new AlertDialog.Builder(
                this);
        myAlertDialog.setTitle("Upload Pictures Option");
        myAlertDialog.setMessage("How do you want to upload your picture?");

        myAlertDialog.setPositiveButton("Gallery",
                new DialogInterface.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.M)
                    public void onClick(DialogInterface arg0, int arg1) {
                        if (ContextCompat.checkSelfPermission(FaceDetectionActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(
                                    FaceDetectionActivity.this,
                                    PERMISSIONS_STORAGE,
                                    MY_GALLERY_PERMISSION_CODE
                            );
                        } else {

                            Intent intent = new Intent();
                            intent.setType("image/*");
                            // intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                            intent.setAction(Intent.ACTION_GET_CONTENT);
                            startActivityForResult(Intent.createChooser(intent,"Select Picture"), GALLERY_PICTURE);
                        }

                    }
                });

        myAlertDialog.setNegativeButton("Camera",
                new DialogInterface.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.M)
                    public void onClick(DialogInterface arg0, int arg1) {
                        Intent intent = new Intent(FaceDetectionActivity.this,MyFaceDetector.class);
                        startActivity(intent);
//                        if (ContextCompat.checkSelfPermission(FaceDetectionActivity.this,Manifest.permission.CAMERA)
//                                != PackageManager.PERMISSION_GRANTED) {
//                            requestPermissions(new String[]{Manifest.permission.CAMERA},
//                                    MY_CAMERA_PERMISSION_CODE);
//                        } else {
//                            Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
////                            if (cameraIntent.resolveActivity(getPackageManager()) != null) {
//                            startActivityForResult(cameraIntent, CAMERA_REQUEST);
////                            }
//                        }

                    }
                });
        myAlertDialog.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_CAMERA_PERMISSION_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(FaceDetectionActivity.this, "camera permission granted", Toast.LENGTH_LONG).show();
                Intent cameraIntent = new
                        Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
            } else {
                Toast.makeText(FaceDetectionActivity.this, "camera permission denied", Toast.LENGTH_LONG).show();
            }

        } else if(requestCode==MY_GALLERY_PERMISSION_CODE){
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(FaceDetectionActivity.this, "gallery read permission granted", Toast.LENGTH_LONG).show();
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,"Select Picture"), GALLERY_PICTURE);
            } else {
                Toast.makeText(FaceDetectionActivity.this, "gallery read permission denied", Toast.LENGTH_LONG).show();
            }
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
//            photo = (Bitmap) data.getExtras().get("data");
//
//            uploadImage.setImageBitmap(photo);
//
//            FirebaseVisionImage image;
//            try {
//                image = FirebaseVisionImage.fromFilePath(getApplicationContext(), data.getData());
//                FirebaseVisionFaceDetectorOptions options =
//                        new FirebaseVisionFaceDetectorOptions.Builder()
//                                .setPerformanceMode(FirebaseVisionFaceDetectorOptions.ACCURATE)
//                                .setLandmarkMode(FirebaseVisionFaceDetectorOptions.ALL_LANDMARKS)
//                                .setClassificationMode(FirebaseVisionFaceDetectorOptions.ALL_CLASSIFICATIONS)
//                                .build();
//
//                FirebaseVisionFaceDetector detector = FirebaseVision.getInstance()
//                        .getVisionFaceDetector(options);
//
//                Task<List<FirebaseVisionFace>> result =
//                        detector.detectInImage(image)
//                                .addOnSuccessListener(
//                                        new OnSuccessListener<List<FirebaseVisionFace>>() {
//                                            @Override
//                                            public void onSuccess(List<FirebaseVisionFace> faces) {
//
//                                                if(faces.size() == 0){
//                                                    Toast.makeText(FaceDetectionActivity.this, "No face detected", Toast.LENGTH_SHORT).show();
//                                                }
//                                                else {
//                                                    Toast.makeText(FaceDetectionActivity.this, "Number of faces detected: " + faces.size(), Toast.LENGTH_SHORT).show();
//                                                    int count = 1;
//                                                    for (FirebaseVisionFace face : faces) {
//                                                        StringBuilder stringBuilder = new StringBuilder();
//                                                        Rect bounds = face.getBoundingBox();
//                                                        RectOverlay rect = new RectOverlay(graphicOverlay, bounds);
//                                                        graphicOverlay.add(rect);
//                                                        Log.d("rectValue", "onSuccess: rect value: " + rect);
//
//                                                        float rotY = face.getHeadEulerAngleY();  // Head is rotated to the right rotY degrees
//                                                        float rotZ = face.getHeadEulerAngleZ();  // Head is tilted sideways rotZ degrees
//
//                                                        // If landmark detection was enabled (mouth, ears, eyes, cheeks, and
//                                                        // nose available):
//                                                        FirebaseVisionFaceLandmark leftEar = face.getLandmark(FirebaseVisionFaceLandmark.LEFT_EAR);
//                                                        if (leftEar != null) {
//                                                            FirebaseVisionPoint leftEarPos = leftEar.getPosition();
//                                                        }
//
//                                                        // If contour detection was enabled:
//                                                        List<FirebaseVisionPoint> leftEyeContour =
//                                                                face.getContour(FirebaseVisionFaceContour.LEFT_EYE).getPoints();
//                                                        List<FirebaseVisionPoint> upperLipBottomContour =
//                                                                face.getContour(FirebaseVisionFaceContour.UPPER_LIP_BOTTOM).getPoints();
//
//                                                        // If classification was enabled:
//                                                        if (face.getSmilingProbability() != FirebaseVisionFace.UNCOMPUTED_PROBABILITY) {
//                                                            float smileProb = face.getSmilingProbability();
//                                                            if (smileProb > 0.5) {
//                                                                Toast.makeText(FaceDetectionActivity.this, count+ "face Smiling", Toast.LENGTH_SHORT).show();
//                                                            }
//                                                        }
//                                                        if (face.getRightEyeOpenProbability() != FirebaseVisionFace.UNCOMPUTED_PROBABILITY) {
//                                                            float rightEyeOpenProb = face.getRightEyeOpenProbability();
//                                                        }
//
//                                                        // If face tracking was enabled:
//                                                        if (face.getTrackingId() != FirebaseVisionFace.INVALID_ID) {
//                                                            int id = face.getTrackingId();
//                                                        }
//                                                        count++;
//                                                    }
//                                                }
//                                            }
//                                        })
//                                .addOnFailureListener(
//                                        new OnFailureListener() {
//                                            @Override
//                                            public void onFailure(@NonNull Exception e) {
//                                                // Task failed with an exception
//                                                // ...
//                                                Log.d("failed", "onFailure: task failed");
//                                            }
//                                        });
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
        } else if (requestCode == GALLERY_PICTURE && resultCode == Activity.RESULT_OK) {
            // The following code snipet is used when Intent for single image selection is set
            Uri imageUri = data.getData();
            try {
                photo = MediaStore.Images.Media.getBitmap(FaceDetectionActivity.this.getContentResolver(), imageUri);
            } catch (IOException e) {
                e.printStackTrace();
            }

            uploadImage.setImageBitmap(photo);
            FirebaseVisionImage image;
            try {
                image = FirebaseVisionImage.fromFilePath(getApplicationContext(), data.getData());
                FirebaseVisionFaceDetectorOptions options =
                        new FirebaseVisionFaceDetectorOptions.Builder()
                                .setPerformanceMode(FirebaseVisionFaceDetectorOptions.ACCURATE)
                                .setLandmarkMode(FirebaseVisionFaceDetectorOptions.ALL_LANDMARKS)
                                .setClassificationMode(FirebaseVisionFaceDetectorOptions.ALL_CLASSIFICATIONS)
                                .build();

                FirebaseVisionFaceDetector detector = FirebaseVision.getInstance()
                        .getVisionFaceDetector(options);

                Task<List<FirebaseVisionFace>> result =
                        detector.detectInImage(image)
                                .addOnSuccessListener(
                                        new OnSuccessListener<List<FirebaseVisionFace>>() {
                                            @Override
                                            public void onSuccess(List<FirebaseVisionFace> faces) {
                                                if(faces.size() == 0){
                                                    Toast.makeText(FaceDetectionActivity.this, "No face detected", Toast.LENGTH_SHORT).show();
                                                }
                                                else {
                                                    Toast.makeText(FaceDetectionActivity.this, "Number of faces detected: " + faces.size(), Toast.LENGTH_SHORT).show();
                                                    int count = 1;
                                                    for (FirebaseVisionFace face : faces) {
                                                        Rect bounds = face.getBoundingBox();
                                                        RectOverlay rect = new RectOverlay(graphicOverlay, bounds);
                                                        graphicOverlay.add(rect);
                                                        Log.d("rectValue", "onSuccess: rect value: " + rect);
                                                        float rotY = face.getHeadEulerAngleY();  // Head is rotated to the right rotY degrees
                                                        float rotZ = face.getHeadEulerAngleZ();  // Head is tilted sideways rotZ degrees

                                                        // If landmark detection was enabled (mouth, ears, eyes, cheeks, and
                                                        // nose available):
                                                        FirebaseVisionFaceLandmark leftEar = face.getLandmark(FirebaseVisionFaceLandmark.LEFT_EAR);
                                                        if (leftEar != null) {
                                                            FirebaseVisionPoint leftEarPos = leftEar.getPosition();
                                                        }

                                                        // If contour detection was enabled:
                                                        List<FirebaseVisionPoint> leftEyeContour =
                                                                face.getContour(FirebaseVisionFaceContour.LEFT_EYE).getPoints();
                                                        List<FirebaseVisionPoint> upperLipBottomContour =
                                                                face.getContour(FirebaseVisionFaceContour.UPPER_LIP_BOTTOM).getPoints();

                                                        // If classification was enabled:
                                                        if (face.getSmilingProbability() != FirebaseVisionFace.UNCOMPUTED_PROBABILITY) {
                                                            float smileProb = face.getSmilingProbability();
                                                            if (smileProb > 0.5) {
                                                                Toast.makeText(FaceDetectionActivity.this, count+ " face Smiling", Toast.LENGTH_SHORT).show();
                                                            }
                                                        }
                                                        if (face.getRightEyeOpenProbability() != FirebaseVisionFace.UNCOMPUTED_PROBABILITY) {
                                                            float rightEyeOpenProb = face.getRightEyeOpenProbability();
                                                        }

                                                        // If face tracking was enabled:
                                                        if (face.getTrackingId() != FirebaseVisionFace.INVALID_ID) {
                                                            int id = face.getTrackingId();
                                                        }
                                                        count++;
                                                    }
                                                }
                                            }
                                        })
                                .addOnFailureListener(
                                        new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                // Task failed with an exception
                                                // ...
                                                Log.d("failed", "onFailure: task failed");
                                            }
                                        });
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (resultCode == Activity.RESULT_CANCELED) {

        }
    }
}