package com.alerion.cameragallery

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
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

class MainActivity : ComponentActivity() {
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
                    CameraAndGalleryProvider(showChooser = showImageChooser, onDismiss = { showImageChooser = false}) {
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
                            Text(text = "Capture Image From Camera")
                        }
                    }

                    if (capturedImageUri.path?.isNotEmpty() == true) {
                        Image(
                            modifier = Modifier
                                .padding(16.dp, 8.dp),
                            painter = rememberAsyncImagePainter(capturedImageUri),
                            contentDescription = null
                        )
                    }
                }
            }
        }
    }
}