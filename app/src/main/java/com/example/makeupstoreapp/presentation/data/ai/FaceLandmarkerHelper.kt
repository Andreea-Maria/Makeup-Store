package com.example.makeupstoreapp.presentation.data.ai

import android.content.Context
import com.google.mediapipe.tasks.core.BaseOptions
import com.google.mediapipe.tasks.vision.core.RunningMode
import com.google.mediapipe.tasks.vision.facelandmarker.FaceLandmarker
import android.graphics.Bitmap
import com.google.mediapipe.framework.image.BitmapImageBuilder
import com.google.mediapipe.tasks.vision.facelandmarker.FaceLandmarkerResult

class FaceLandmarkerHelper(
    private val context: Context
) {

    private var faceLandmarker: FaceLandmarker? = null

    init {
        setupFaceLandmarker()
    }

    private fun setupFaceLandmarker() {

        val baseOptions = BaseOptions.builder()
            .setModelAssetPath("face_landmarker.task")
            .build()

        val options = FaceLandmarker.FaceLandmarkerOptions.builder()
            .setBaseOptions(baseOptions)
            .setRunningMode(RunningMode.IMAGE)
            .setNumFaces(1)
            .build()

        faceLandmarker = FaceLandmarker.createFromOptions(
            context,
            options
        )
    }

    fun detectFace(bitmap: Bitmap): FaceLandmarkerResult? {

        val mpImage = BitmapImageBuilder(bitmap).build()

        return faceLandmarker?.detect(mpImage)
    }
}