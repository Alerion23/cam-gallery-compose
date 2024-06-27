# cam-gallery-compose
Fast implementing camera and gallery options.

[![](https://jitpack.io/v/Alerion23/cam-gallery-compose.svg)](https://jitpack.io/#Alerion23/cam-gallery-compose)

## Setup

##### build.gradle(Project)

```kotlin
buildscript {
    repositories {
        mavenCentral()
        gradlePluginPortal()
        maven { url = uri("https://jitpack.io") }
    }
}
```

##### build.gradle(Module)

```kotlin
dependencies {
     implementation("com.github.Alerion23:cam-gallery-compose:1.0.6")
}
```

## Example

```kotlin
 var capturedImageUri by remember {
    mutableStateOf<Uri>(Uri.EMPTY)
}
 var showImageChooser by remember {
     mutableStateOf(false)
}
CameraAndGalleryProvider(
  showChooser = showImageChooser,
  isGalleryOnly = true, // Set to false to enable either camera or gallery
  onDismiss = { showImageChooser = false }) {
  // Handle the image received from the camera or gallery
  showImageChooser = false
  capturedImageUri = it ?: Uri.EMPTY
}
```

### License

    Copyright 2024 Venher Kyrylo

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.

