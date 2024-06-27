package com.alerion.cameragallery

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.alerion.cam_gallery.image.CameraAndGalleryProvider
import com.alerion.cameragallery.ui.theme.CameraGalleryTheme

class TemplateActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CameraGalleryTheme {
                var capturedImageUri by remember {
                    mutableStateOf<Uri>(Uri.EMPTY)
                }
                var showImageChooser by remember {
                    mutableStateOf(false)
                }
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    CameraAndGalleryProvider(
                        showChooser = showImageChooser,
                        isGalleryOnly = true, // Set to false to enable either camera or gallery
                        onDismiss = { showImageChooser = false }) {
                        // Handle the image received from the camera or gallery
                        showImageChooser = false
                        capturedImageUri = it ?: Uri.EMPTY
                    }
                    Column(
                        Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Button(onClick = {
                            showImageChooser = true
                        }) {
                            Text(text = "Get image")
                        }
                    }

                    if (capturedImageUri.path?.isNotEmpty() == true) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Image(
                                modifier = Modifier
                                    .height(300.dp)
                                    .padding(16.dp, 8.dp),
                                painter = rememberAsyncImagePainter(capturedImageUri),
                                contentDescription = null
                            )
                            Spacer(modifier = Modifier.padding(16.dp))
                            Button(onClick = {
                                capturedImageUri = Uri.EMPTY
                            }) {
                                Text(text = "Clear Image")
                            }
                        }
                    }
                }
            }
        }
    }
}