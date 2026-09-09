package com.example.makeupstoreapp.ui.screens

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.makeupstoreapp.data.model.Product
import com.example.makeupstoreapp.presentation.data.ai.FaceLandmarkerHelper
import com.google.mediapipe.tasks.components.containers.NormalizedLandmark


@Composable
fun TryOnScreen(
    product: Product,
    onBack: () -> Unit
) {

    val context = LocalContext.current

    val faceLandmarkerHelper = remember {
        FaceLandmarkerHelper(context)
    }

    var selectedBitmap by remember {
        mutableStateOf<Bitmap?>(null)
    }

    var faceDetected by remember {
        mutableStateOf<Boolean?>(null)
    }

    var lipPoints by remember {
        mutableStateOf<List<NormalizedLandmark>>(emptyList())
    }

    var facePoints by remember {
        mutableStateOf<List<NormalizedLandmark>>(emptyList())
    }

    var showAfter by remember {
        mutableStateOf(true)
    }



    //alegerea imaginii
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->

        if (uri != null) {

            val bitmap = uriToBitmap(context, uri)

            if (bitmap != null) {

                selectedBitmap = bitmap

                val result =
                    faceLandmarkerHelper.detectFace(bitmap)

                val landmarks =
                    result
                        ?.faceLandmarks()
                        ?.firstOrNull()

                faceDetected = landmarks != null

                if (landmarks != null) {

                    facePoints = landmarks

                    val lipIndexes = listOf(

                        // contur exterior sus
                        61, 185, 40, 39, 37,
                        0,
                        267, 269, 270, 409, 291,

                        // contur exterior jos
                        291, 375, 321, 405, 314,
                        17,
                        84, 181, 91, 146, 61,

                        // contur interior sus
                        78, 191, 80, 81, 82,
                        13,
                        312, 311, 310, 415, 308,

                        // contur interior jos
                        308, 324, 318, 402, 317,
                        14,
                        87, 178, 88, 95, 78
                    )

                    lipPoints = lipIndexes.map { index ->
                        landmarks[index]
                    }

                } else {

                    facePoints = emptyList()
                    lipPoints = emptyList()
                }
            }
        }
    }

    //UI
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {

            IconButton(
                onClick = onBack
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Înapoi"
                )
            }

            Text(
                text = "Probă virtuală",
                style = MaterialTheme.typography.titleLarge
            )
        }


        Text(
            text = "Produs: ${product.name}"
        )


        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {

            Text(
                text = "Nuanță: ${product.shadeName}"
            )

            Spacer(
                modifier = Modifier.width(8.dp)
            )

            Canvas(
                modifier = Modifier.size(22.dp)
            ) {

                drawCircle(
                    color = parseColor(product.color)
                )
            }
        }


        Spacer(
            modifier = Modifier.height(16.dp)
        )


        Button(
            onClick = {
                photoPickerLauncher.launch("image/*")
            }
        ) {
            Text("Alege o imagine")
        }


        Spacer(
            modifier = Modifier.height(16.dp)
        )


        selectedBitmap?.let { bitmap ->

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(350.dp),
                contentAlignment = Alignment.Center
            ) {

                Image(
                    bitmap = bitmap.asImageBitmap(),
                    contentDescription = "Imagine selectată",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.fillMaxSize()
                )


                Canvas(
                    modifier = Modifier.fillMaxSize()
                ) {

                    val imageAspectRatio = bitmap.width.toFloat() / bitmap.height.toFloat()

                    val boxAspectRatio = size.width / size.height


                    val displayedWidth: Float
                    val displayedHeight: Float

                    val offsetX: Float
                    val offsetY: Float


                    if (imageAspectRatio > boxAspectRatio) {
                        //imaginea este mai lata

                        displayedWidth = size.width

                        displayedHeight =
                            size.width / imageAspectRatio

                        offsetX = 0f

                        offsetY =
                            (size.height - displayedHeight) / 2f

                    } else {
                        //imaginea este mai inalta
                        displayedHeight = size.height

                        displayedWidth =
                            size.height * imageAspectRatio

                        offsetX =
                            (size.width - displayedWidth) / 2f

                        offsetY = 0f
                    }

                    //fond de ten
                    if (
                        showAfter &&
                        product.subcategory == "Fond de ten" &&
                        facePoints.isNotEmpty()
                    ) {

                        val faceOvalIndexes = listOf(
                            10, 338, 297, 332, 284,
                            251, 389, 356, 454, 323,
                            361, 288, 397, 365, 379,
                            378, 400, 377, 152, 148,
                            176, 149, 150, 136, 172,
                            58, 132, 93, 234, 127,
                            162, 21, 54, 103, 67, 109
                        )

                        val facePath = Path().apply {
                            fillType = PathFillType.EvenOdd
                        }


                        faceOvalIndexes.forEachIndexed {
                                index,
                                landmarkIndex ->

                            val point =
                                facePoints[landmarkIndex]

                            val x =
                                offsetX +
                                        point.x() *
                                        displayedWidth

                            val originalY =
                                offsetY +
                                        point.y() *
                                        displayedHeight

                            val foreheadIndexes = setOf(
                                10, 338, 297, 332,
                                284, 109, 67, 103
                            )

                            val y =
                                if (landmarkIndex in foreheadIndexes) {

                                    originalY -
                                            displayedHeight * 0.045f

                                } else {

                                    originalY
                                }


                            if (index == 0) {

                                facePath.moveTo(x, y)

                            } else {

                                facePath.lineTo(x, y)
                            }
                        }


                        facePath.close()

                        //ochi stang
                        val leftEyeIndexes = listOf(
                            33, 160, 158,
                            133, 153, 144
                        )

                        leftEyeIndexes.forEachIndexed {
                                index,
                                landmarkIndex ->

                            val point =
                                facePoints[landmarkIndex]

                            val x =
                                offsetX +
                                        point.x() *
                                        displayedWidth

                            val y =
                                offsetY +
                                        point.y() *
                                        displayedHeight


                            if (index == 0) {

                                facePath.moveTo(x, y)

                            } else {

                                facePath.lineTo(x, y)
                            }
                        }

                        facePath.close()

                        //ochi drept
                        val rightEyeIndexes = listOf(
                            362, 385, 387,
                            263, 373, 380
                        )

                        rightEyeIndexes.forEachIndexed {
                                index,
                                landmarkIndex ->

                            val point =
                                facePoints[landmarkIndex]

                            val x =
                                offsetX +
                                        point.x() *
                                        displayedWidth

                            val y =
                                offsetY +
                                        point.y() *
                                        displayedHeight


                            if (index == 0) {

                                facePath.moveTo(x, y)

                            } else {

                                facePath.lineTo(x, y)
                            }
                        }

                        facePath.close()

                        //buze
                        val mouthIndexes = listOf(
                            61, 185, 40, 39, 37,
                            0,
                            267, 269, 270, 409, 291,
                            375, 321, 405, 314,
                            17,
                            84, 181, 91, 146
                        )

                        mouthIndexes.forEachIndexed {
                                index,
                                landmarkIndex ->

                            val point =
                                facePoints[landmarkIndex]

                            val x =
                                offsetX +
                                        point.x() *
                                        displayedWidth

                            val y =
                                offsetY +
                                        point.y() *
                                        displayedHeight


                            if (index == 0) {

                                facePath.moveTo(x, y)

                            } else {

                                facePath.lineTo(x, y)
                            }
                        }

                        facePath.close()


                        drawPath(
                            path = facePath,
                            color = parseColor(product.color),
                            alpha = 0.28f
                        )
                    }

                    //contouring
                    if (
                        showAfter &&
                        product.subcategory == "Contouring" &&
                        facePoints.isNotEmpty()
                    ) {

                        val contourColor =
                            parseColor(product.color)

                        val leftOuter =
                            facePoints[234]

                        val rightOuter =
                            facePoints[454]

                        val leftCheek =
                            facePoints[205]

                        val rightCheek =
                            facePoints[425]

                        //stanga
                        val leftOuterX =
                            offsetX +
                                    leftOuter.x() *
                                    displayedWidth

                        val leftOuterY =
                            offsetY +
                                    leftOuter.y() *
                                    displayedHeight

                        val leftCheekX =
                            offsetX +
                                    leftCheek.x() *
                                    displayedWidth

                        val leftCheekY =
                            offsetY +
                                    leftCheek.y() *
                                    displayedHeight


                        val leftStartX =
                            leftOuterX +
                                    (leftCheekX -
                                            leftOuterX) *
                                    0.25f

                        val leftStartY =
                            leftOuterY +
                                    (leftCheekY -
                                            leftOuterY) *
                                    0.25f +
                                    displayedHeight *
                                    0.045f

                        val leftEndX =
                            leftOuterX +
                                    (leftCheekX -
                                            leftOuterX) *
                                    0.85f

                        val leftEndY =
                            leftOuterY +
                                    (leftCheekY -
                                            leftOuterY) *
                                    0.85f +
                                    displayedHeight *
                                    0.045f

                        //dreapta
                        val rightOuterX =
                            offsetX +
                                    rightOuter.x() *
                                    displayedWidth

                        val rightOuterY =
                            offsetY +
                                    rightOuter.y() *
                                    displayedHeight

                        val rightCheekX =
                            offsetX +
                                    rightCheek.x() *
                                    displayedWidth

                        val rightCheekY =
                            offsetY +
                                    rightCheek.y() *
                                    displayedHeight


                        val rightStartX =
                            rightOuterX +
                                    (rightCheekX -
                                            rightOuterX) *
                                    0.25f

                        val rightStartY =
                            rightOuterY +
                                    (rightCheekY -
                                            rightOuterY) *
                                    0.25f +
                                    displayedHeight *
                                    0.045f

                        val rightEndX =
                            rightOuterX +
                                    (rightCheekX -
                                            rightOuterX) *
                                    0.85f

                        val rightEndY =
                            rightOuterY +
                                    (rightCheekY -
                                            rightOuterY) *
                                    0.85f +
                                    displayedHeight *
                                    0.045f


                        val contourThickness =
                            displayedHeight * 0.030f


                        val leftContourPath =
                            Path().apply {

                                moveTo(
                                    leftStartX,
                                    leftStartY -
                                            contourThickness
                                )

                                lineTo(
                                    leftEndX,
                                    leftEndY
                                )

                                lineTo(
                                    leftStartX,
                                    leftStartY +
                                            contourThickness *
                                            0.35f
                                )

                                close()
                            }


                        val leftContourBrush =
                            Brush.verticalGradient(

                                colors = listOf(

                                    contourColor.copy(
                                        alpha = 0.00f
                                    ),

                                    contourColor.copy(
                                        alpha = 0.06f
                                    ),

                                    contourColor.copy(
                                        alpha = 0.16f
                                    )
                                ),

                                startY =
                                    leftStartY -
                                            contourThickness,

                                endY =
                                    leftStartY +
                                            contourThickness
                            )


                        drawPath(
                            path = leftContourPath,
                            brush = leftContourBrush
                        )


                        val rightContourPath =
                            Path().apply {

                                moveTo(
                                    rightStartX,
                                    rightStartY -
                                            contourThickness
                                )

                                lineTo(
                                    rightEndX,
                                    rightEndY
                                )

                                lineTo(
                                    rightStartX,
                                    rightStartY +
                                            contourThickness *
                                            0.35f
                                )

                                close()
                            }


                        val rightContourBrush =
                            Brush.verticalGradient(

                                colors = listOf(

                                    contourColor.copy(
                                        alpha = 0.00f
                                    ),

                                    contourColor.copy(
                                        alpha = 0.06f
                                    ),

                                    contourColor.copy(
                                        alpha = 0.16f
                                    )
                                ),

                                startY =
                                    rightStartY -
                                            contourThickness,

                                endY =
                                    rightStartY +
                                            contourThickness
                            )


                        drawPath(
                            path = rightContourPath,
                            brush = rightContourBrush
                        )
                    }

                    //iluminator
                    if (
                        showAfter &&
                        product.subcategory == "Iluminatoare" &&
                        facePoints.isNotEmpty()
                    ) {

                        val highlightColor =
                            parseColor(product.color)

                        val leftPoint =
                            facePoints[205]

                        val rightPoint =
                            facePoints[425]


                        val leftX =
                            offsetX +
                                    leftPoint.x() *
                                    displayedWidth

                        val leftY =
                            offsetY +
                                    leftPoint.y() *
                                    displayedHeight


                        val leftHighlightPath =
                            Path().apply {

                                moveTo(
                                    leftX -
                                            displayedWidth *
                                            0.020f,

                                    leftY +
                                            displayedHeight *
                                            0.005f
                                )

                                quadraticTo(
                                    leftX -
                                            displayedWidth *
                                            0.065f,

                                    leftY -
                                            displayedHeight *
                                            0.005f,

                                    leftX -
                                            displayedWidth *
                                            0.090f,

                                    leftY -
                                            displayedHeight *
                                            0.035f
                                )

                                quadraticTo(
                                    leftX -
                                            displayedWidth *
                                            0.060f,

                                    leftY -
                                            displayedHeight *
                                            0.020f,

                                    leftX -
                                            displayedWidth *
                                            0.010f,

                                    leftY +
                                            displayedHeight *
                                            0.015f
                                )

                                close()
                            }


                        val rightX =
                            offsetX +
                                    rightPoint.x() *
                                    displayedWidth

                        val rightY =
                            offsetY +
                                    rightPoint.y() *
                                    displayedHeight


                        val rightHighlightPath =
                            Path().apply {

                                moveTo(
                                    rightX +
                                            displayedWidth *
                                            0.020f,

                                    rightY +
                                            displayedHeight *
                                            0.005f
                                )

                                quadraticTo(
                                    rightX +
                                            displayedWidth *
                                            0.065f,

                                    rightY -
                                            displayedHeight *
                                            0.005f,

                                    rightX +
                                            displayedWidth *
                                            0.090f,

                                    rightY -
                                            displayedHeight *
                                            0.035f
                                )

                                quadraticTo(
                                    rightX +
                                            displayedWidth *
                                            0.060f,

                                    rightY -
                                            displayedHeight *
                                            0.020f,

                                    rightX +
                                            displayedWidth *
                                            0.010f,

                                    rightY +
                                            displayedHeight *
                                            0.015f
                                )

                                close()
                            }


                        val leftBrush =
                            Brush.radialGradient(

                                colors = listOf(

                                    Color.White.copy(
                                        alpha = 0.30f
                                    ),

                                    highlightColor.copy(
                                        alpha = 0.16f
                                    ),

                                    highlightColor.copy(
                                        alpha = 0.00f
                                    )
                                ),

                                center = Offset(
                                    x =
                                        leftX -
                                                displayedWidth *
                                                0.045f,

                                    y =
                                        leftY -
                                                displayedHeight *
                                                0.010f
                                ),

                                radius =
                                    displayedWidth *
                                            0.10f
                            )


                        val rightBrush =
                            Brush.radialGradient(

                                colors = listOf(

                                    Color.White.copy(
                                        alpha = 0.30f
                                    ),

                                    highlightColor.copy(
                                        alpha = 0.16f
                                    ),

                                    highlightColor.copy(
                                        alpha = 0.00f
                                    )
                                ),

                                center = Offset(
                                    x =
                                        rightX +
                                                displayedWidth *
                                                0.045f,

                                    y =
                                        rightY -
                                                displayedHeight *
                                                0.010f
                                ),

                                radius =
                                    displayedWidth *
                                            0.10f
                            )


                        drawPath(
                            path = leftHighlightPath,
                            brush = leftBrush
                        )

                        drawPath(
                            path = rightHighlightPath,
                            brush = rightBrush
                        )
                    }

                    //concealer
                    if (
                        showAfter &&
                        product.subcategory == "Concealer" &&
                        facePoints.isNotEmpty()
                    ) {

                        val concealerColor =
                            parseColor(product.color)

                        val outerExtension =
                            displayedWidth * 0.025f

                        //stanga
                        val leftInner =
                            facePoints[133]

                        val leftOuter =
                            facePoints[33]

                        val leftWaterline =
                            facePoints[145]


                        val leftInnerX =
                            offsetX +
                                    leftInner.x() *
                                    displayedWidth

                        val leftInnerY =
                            offsetY +
                                    leftInner.y() *
                                    displayedHeight +
                                    displayedHeight *
                                    0.006f

                        val leftOuterX =
                            offsetX +
                                    leftOuter.x() *
                                    displayedWidth -
                                    outerExtension

                        val leftOuterY =
                            offsetY +
                                    leftOuter.y() *
                                    displayedHeight +
                                    displayedHeight *
                                    0.006f

                        val leftBottomX =
                            offsetX +
                                    leftWaterline.x() *
                                    displayedWidth

                        val leftBottomY =
                            offsetY +
                                    leftWaterline.y() *
                                    displayedHeight +
                                    displayedHeight *
                                    0.045f


                        val leftConcealerPath =
                            Path().apply {

                                moveTo(
                                    leftInnerX,
                                    leftInnerY
                                )

                                lineTo(
                                    leftOuterX,
                                    leftOuterY
                                )

                                lineTo(
                                    leftBottomX,
                                    leftBottomY
                                )

                                close()
                            }

                        //dreapta
                        val rightInner =
                            facePoints[362]

                        val rightOuter =
                            facePoints[263]

                        val rightWaterline =
                            facePoints[374]


                        val rightInnerX =
                            offsetX +
                                    rightInner.x() *
                                    displayedWidth

                        val rightInnerY =
                            offsetY +
                                    rightInner.y() *
                                    displayedHeight +
                                    displayedHeight *
                                    0.006f

                        val rightOuterX =
                            offsetX +
                                    rightOuter.x() *
                                    displayedWidth +
                                    outerExtension

                        val rightOuterY =
                            offsetY +
                                    rightOuter.y() *
                                    displayedHeight +
                                    displayedHeight *
                                    0.006f

                        val rightBottomX =
                            offsetX +
                                    rightWaterline.x() *
                                    displayedWidth

                        val rightBottomY =
                            offsetY +
                                    rightWaterline.y() *
                                    displayedHeight +
                                    displayedHeight *
                                    0.045f


                        val rightConcealerPath =
                            Path().apply {

                                moveTo(
                                    rightInnerX,
                                    rightInnerY
                                )

                                lineTo(
                                    rightOuterX,
                                    rightOuterY
                                )

                                lineTo(
                                    rightBottomX,
                                    rightBottomY
                                )

                                close()
                            }


                        val leftBrush =
                            Brush.verticalGradient(

                                colors = listOf(

                                    concealerColor.copy(
                                        alpha = 0.28f
                                    ),

                                    concealerColor.copy(
                                        alpha = 0.14f
                                    ),

                                    concealerColor.copy(
                                        alpha = 0.00f
                                    )
                                ),

                                startY =
                                    leftInnerY,

                                endY =
                                    leftBottomY
                            )


                        val rightBrush =
                            Brush.verticalGradient(

                                colors = listOf(

                                    concealerColor.copy(
                                        alpha = 0.28f
                                    ),

                                    concealerColor.copy(
                                        alpha = 0.14f
                                    ),

                                    concealerColor.copy(
                                        alpha = 0.00f
                                    )
                                ),

                                startY =
                                    rightInnerY,

                                endY =
                                    rightBottomY
                            )


                        drawPath(
                            path = leftConcealerPath,
                            brush = leftBrush
                        )

                        drawPath(
                            path = rightConcealerPath,
                            brush = rightBrush
                        )
                    }

                    //rujuri
                    if (
                        showAfter &&
                        lipPoints.size == 44 &&
                        product.subcategory in listOf(
                            "Ruj mat",
                            "Gloss",
                            "Creion de buze"
                        )
                    ) {

                        val outerLipPoints =
                            lipPoints.take(22)

                        val innerLipPoints =
                            lipPoints.drop(22)


                        //path complet ruj si gloss
                        val lipPath = Path().apply {

                            fillType =
                                PathFillType.EvenOdd
                        }


                        outerLipPoints.forEachIndexed {
                                index,
                                point ->

                            val x =
                                offsetX +
                                        point.x() *
                                        displayedWidth

                            val y =
                                offsetY +
                                        point.y() *
                                        displayedHeight


                            if (index == 0) {

                                lipPath.moveTo(x, y)

                            } else {

                                lipPath.lineTo(x, y)
                            }
                        }

                        lipPath.close()


                        innerLipPoints.forEachIndexed {
                                index,
                                point ->

                            val x =
                                offsetX +
                                        point.x() *
                                        displayedWidth

                            val y =
                                offsetY +
                                        point.y() *
                                        displayedHeight


                            if (index == 0) {

                                lipPath.moveTo(x, y)

                            } else {

                                lipPath.lineTo(x, y)
                            }
                        }

                        lipPath.close()

                        //path exterior creion de buze
                        val outerLipPath = Path()


                        outerLipPoints.forEachIndexed {
                                index,
                                point ->

                            val x =
                                offsetX +
                                        point.x() *
                                        displayedWidth

                            val y =
                                offsetY +
                                        point.y() *
                                        displayedHeight


                            if (index == 0) {

                                outerLipPath.moveTo(x, y)

                            } else {

                                outerLipPath.lineTo(x, y)
                            }
                        }

                        outerLipPath.close()

                        //efect
                        when (product.subcategory) {


                            "Gloss" -> {

                                drawPath(
                                    path = lipPath,
                                    color =
                                        parseColor(product.color),
                                    alpha = 0.30f
                                )
                            }


                            "Creion de buze" -> {

                                drawPath(
                                    path = outerLipPath,
                                    color =
                                        parseColor(product.color),
                                    style = Stroke(
                                        width = 4f
                                    )
                                )
                            }


                            "Ruj mat" -> {

                                drawPath(
                                    path = lipPath,
                                    color =
                                        parseColor(product.color),
                                    alpha = 0.45f
                                )
                            }
                        }
                    }
                }
            }

            //inainte/dupa
            Spacer(
                modifier = Modifier.height(12.dp)
            )


            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement =
                    Arrangement.spacedBy(12.dp)
            ) {

                OutlinedButton(
                    onClick = {
                        showAfter = false
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Înainte")
                }


                Button(
                    onClick = {
                        showAfter = true
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("După")
                }
            }


            Spacer(
                modifier = Modifier.height(12.dp)
            )

            when (faceDetected) {

                true -> {

                    Text(
                        "Face detected successfully!"
                    )
                }

                false -> {

                    Text(
                        "No face detected."
                    )
                }

                null -> {

                    Text(
                        "Waiting for face detection..."
                    )
                }
            }
        }
    }
}

//conversie
fun uriToBitmap(
    context: android.content.Context,
    uri: Uri
): Bitmap? {

    return context.contentResolver
        .openInputStream(uri)
        ?.use { inputStream ->

            BitmapFactory.decodeStream(
                inputStream
            )
        }
}