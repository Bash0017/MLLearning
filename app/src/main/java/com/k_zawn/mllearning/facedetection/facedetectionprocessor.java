package com.k_zawn.mllearning.facedetection;

import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.Task;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.face.FirebaseVisionFace;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetector;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetectorOptions;
import com.k_zawn.mllearning.FrameMetadata;
import com.k_zawn.mllearning.GraphicOverlay;
import com.k_zawn.mllearning.VisionProcessorBase;

import java.io.IOException;
import java.util.List;

public class facedetectionprocessor extends VisionProcessorBase<List<FirebaseVisionFace>> {
    private static final String TAG = "faceDetectionprocessor";
    private FirebaseVisionFaceDetector detector;

    public facedetectionprocessor() {
//        Toast.makeText(, "", Toast.LENGTH_SHORT).show();
        Log.d("const", "facedetectionprocessor: const called");
        FirebaseVisionFaceDetectorOptions options =
                new FirebaseVisionFaceDetectorOptions.Builder()
                        .setPerformanceMode(FirebaseVisionFaceDetectorOptions.ACCURATE)
                        .setLandmarkMode(FirebaseVisionFaceDetectorOptions.ALL_LANDMARKS)
                        .setClassificationMode(FirebaseVisionFaceDetectorOptions.ALL_CLASSIFICATIONS)
                        .build();
        detector = FirebaseVision.getInstance().getVisionFaceDetector(options);
    }

    @Override
    public void stop() {
        try {
            detector.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected Task<List<FirebaseVisionFace>> detectInImage(FirebaseVisionImage image) {
        return detector.detectInImage(image);
    }

    @Override
    protected void onSuccess(@NonNull List<FirebaseVisionFace> results, @NonNull FrameMetadata frameMetadata, @NonNull GraphicOverlay graphicOverlay) {
        graphicOverlay.clear();
        for (int i = 0; i < results.size(); i++) {
            FirebaseVisionFace face = results.get(i);
            faceGraphic faceGraphic = new faceGraphic(graphicOverlay);
            graphicOverlay.add(faceGraphic);
            faceGraphic.updateFace(face, frameMetadata.getCameraFacing());
        }
    }

    @Override
    protected void onFailure(@NonNull Exception e) {
        Log.d(TAG, "onFailure: " + e);
    }
}
