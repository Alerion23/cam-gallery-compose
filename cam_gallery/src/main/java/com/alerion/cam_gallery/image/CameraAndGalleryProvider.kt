package com.alerion.cam_gallery.image

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Environment
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.alerion.cam_gallery.R
import java.io.File

typealias ImageActivityResult = ManagedActivityResultLauncher<PickVisualMediaRequest, Uri?>

@Composable
fun CameraAndGalleryProvider(
    context: Context = LocalContext.current,
    authority: String = stringResource(R.string.library_file_provider, context.packageName),
    storageDir: File? = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES),
    showChooser: Boolean,
    isGalleryOnly: Boolean,
    onDismiss: () -> Unit,
    onImageReceived: (Uri?) -> Unit
) {
    val tempUri = remember { mutableStateOf<Uri?>(null) }
    val imageChooser = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) {
        onImageReceived(it)
    }
    val takePhotoLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { isSuccess ->
        if (isSuccess) {
            tempUri.value?.let {
                onImageReceived(it)
            }
        } else {
            onImageReceived(null)
        }
    }
    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            val tmpUri = getTemporaryUri(storageDir, context, authority)
            tempUri.value = tmpUri
            tempUri.value?.let { takePhotoLauncher.launch(it) }
        } else {
            imageChooser.start()
        }
    }
    if (showChooser) {
        if (isGalleryOnly) {
            imageChooser.start()
        } else {
            ImageChooserBottomSheet(
                onDismiss = onDismiss,
                onTakePhoto = {
                    checkCameraPermission(context, isGranted = {
                        tempUri.value?.let { takePhotoLauncher.launch(it) }
                    }, isNotGranted = {
                        cameraPermissionLauncher.launch(it)
                    })
                },
                onChooseImage = { imageChooser.start() }
            )
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ImageChooserBottomSheet(
    onDismiss: () -> Unit,
    onTakePhoto: () -> Unit,
    onChooseImage: () -> Unit
) {
    val state = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp),
        sheetState = state,
        windowInsets = WindowInsets(0, 0, 0, 0)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    bottom = WindowInsets.navigationBars
                        .asPaddingValues()
                        .calculateBottomPadding()
                )
        ) {
            Text(
                text = "Choose option",
                fontSize = 30.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.size(16.dp))
            BottomSheetItem(imageVector = Icons.Default.PhotoCamera, title = "Camera") {
                onTakePhoto()
            }
            Spacer(modifier = Modifier.size(8.dp))
            BottomSheetItem(imageVector = Icons.Default.Image, title = "Gallery") {
                onChooseImage()
            }
            Spacer(modifier = Modifier.size(16.dp))
        }
    }
}

@Composable
private fun BottomSheetItem(
    modifier: Modifier = Modifier,
    imageVector: ImageVector,
    title: String,
    onClick: () -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clickable {
                onClick()
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = imageVector,
            contentDescription = "photo",
            modifier = Modifier.size(45.dp)
        )
        Spacer(modifier = Modifier.padding(16.dp))
        Text(text = title, fontSize = 25.sp)
    }
}

private fun ImageActivityResult.start() {
    launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
}

private fun getTemporaryUri(storageDir: File?, context: Context, authority: String): Uri? {
    storageDir?.let {
        it.mkdirs()
        val file = File.createTempFile(
            "JPEG_" + System.currentTimeMillis().toString() + "_",
            ".jpg",
            it
        )

        return FileProvider.getUriForFile(
            context,
            authority,
            file
        )
    }
    return null
}

private fun checkCameraPermission(
    context: Context, isGranted: () -> Unit, isNotGranted: (String) -> Unit
) {
    val permission = Manifest.permission.CAMERA
    val hasPermission = ContextCompat.checkSelfPermission(
        context, permission
    ) == PackageManager.PERMISSION_GRANTED
    if (hasPermission) {
        isGranted()
    } else {
        isNotGranted(permission)
    }
}