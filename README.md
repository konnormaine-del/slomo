# VideoProofPOC

A tiny Android proof-of-concept app that opens the phone's built-in camera app to record a video, then stores the returned video URI and lets you share it.

## Build APK

Open this folder in Android Studio, or run:

```bash
./gradlew assembleDebug
```

The debug APK will be at:

```text
app/build/outputs/apk/debug/app-debug.apk
```

## Notes

- Package: `com.example.videoproofpoc`
- Min SDK: 23
- Target SDK: 35
- This uses `MediaStore.ACTION_VIDEO_CAPTURE`, so it is proof-of-concept simple and relies on the device's camera app.
