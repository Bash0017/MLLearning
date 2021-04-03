package com.k_zawn.mllearning;

import androidx.annotation.NonNull;
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
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;

import java.io.IOException;
import java.util.List;

public class TextRecognize extends AppCompatActivity {

    private static final int CAMERA_REQUEST = 1888;
    private static final int GALLERY_PICTURE = 1999;
    private static final int MY_CAMERA_PERMISSION_CODE = 100;
    private static final int MY_GALLERY_PERMISSION_CODE=200;
    private static final String TAG = "MainActivity";
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    ImageView imgUpload;
    ImageView imageView;
    Bitmap photo;
    Button imageText;
    TextView retriveText;

    GraphicOverlay mGraphicOverlay;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imgUpload = findViewById(R.id.imgUpload);
        imageView = findViewById(R.id.imgUpload);
        imageText = findViewById(R.id.getTextFromImage);
        retriveText = findViewById(R.id.retriveText);
        mGraphicOverlay = findViewById(R.id.graphic_overlay);
        mGraphicOverlay.bringToFront();
        retriveText.bringToFront();
        imgUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startDialog();
            }
        });

        imageText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                runTextRecognition();
            }
        });
    }
    private void runTextRecognition() {
        FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(photo);
        FirebaseVisionTextRecognizer recognizer = FirebaseVision.getInstance()
                .getOnDeviceTextRecognizer();
        imageText.setEnabled(false);
        recognizer.processImage(image)
                .addOnSuccessListener(
                        new OnSuccessListener<FirebaseVisionText>() {
                            @Override
                            public void onSuccess(FirebaseVisionText texts) {
                                imageText.setEnabled(true);
                                for (FirebaseVisionText.TextBlock block : texts.getTextBlocks()) {
                                    String blockText = block.getText();
                                    retriveText.append(blockText+"\n"); //Separate each paragraph by new line
                                }
                                processTextRecognitionResult(texts);
                            }
                        })
                .addOnFailureListener(
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Task failed with an exception
                                imageText.setEnabled(true);
                                e.printStackTrace();
                            }
                        });
    }

    private void processTextRecognitionResult(FirebaseVisionText texts) {
        List<FirebaseVisionText.TextBlock> blocks = texts.getTextBlocks();
        if (blocks.size() == 0) {
            showToast("No text found");
            return;
        }
        mGraphicOverlay.clear();
        for (int i = 0; i < blocks.size(); i++) {
            List<FirebaseVisionText.Line> lines = blocks.get(i).getLines();
            for (int j = 0; j < lines.size(); j++) {
                List<FirebaseVisionText.Element> elements = lines.get(j).getElements();
                for (int k = 0; k < elements.size(); k++) {
                    GraphicOverlay.Graphic textGraphic = new TextGraphic(mGraphicOverlay, elements.get(k));
                    mGraphicOverlay.add(textGraphic);
                }
            }
        }
    }

    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
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
                        if (ContextCompat.checkSelfPermission(TextRecognize.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(
                                    TextRecognize.this,
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
                        if (ContextCompat.checkSelfPermission(TextRecognize.this,Manifest.permission.CAMERA)
                                != PackageManager.PERMISSION_GRANTED) {
                            requestPermissions(new String[]{Manifest.permission.CAMERA},
                                    MY_CAMERA_PERMISSION_CODE);
                        } else {
                            Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
//                            if (cameraIntent.resolveActivity(getPackageManager()) != null) {
                            startActivityForResult(cameraIntent, CAMERA_REQUEST);
//                            }
                        }

                    }
                });
        myAlertDialog.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_CAMERA_PERMISSION_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(TextRecognize.this, "camera permission granted", Toast.LENGTH_LONG).show();
                Intent cameraIntent = new
                        Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
            } else {
                Toast.makeText(TextRecognize.this, "camera permission denied", Toast.LENGTH_LONG).show();
            }

        } else if(requestCode==MY_GALLERY_PERMISSION_CODE){
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(TextRecognize.this, "gallery read permission granted", Toast.LENGTH_LONG).show();
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,"Select Picture"), GALLERY_PICTURE);
            } else {
                Toast.makeText(TextRecognize.this, "gallery read permission denied", Toast.LENGTH_LONG).show();
            }
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
            photo = (Bitmap) data.getExtras().get("data");

            imageView.setImageBitmap(photo);
        } else if (requestCode == GALLERY_PICTURE && resultCode == Activity.RESULT_OK) {
            // The following code snipet is used when Intent for single image selection is set
            Uri imageUri = data.getData();
            try {
                photo = MediaStore.Images.Media.getBitmap(TextRecognize.this.getContentResolver(), imageUri);
            } catch (IOException e) {
                e.printStackTrace();
            }

            imageView.setImageBitmap(photo);
        } else if (resultCode == Activity.RESULT_CANCELED) {

        }
    }
}
