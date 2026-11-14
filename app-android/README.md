# КОНСТРУКТОР ДОМА TJ — Android
Native Android app built with Kotlin + Jetpack Compose.

## Build
From repository root:
`gradle -p app-android assembleDebug`

APK:
`app-android/app/build/outputs/apk/debug/app-debug.apk`

GitHub Actions workflow: **Android APK**. Run it from Actions → Android APK → Run workflow. The resulting artifact is named `KONSTRUKTOR-DOMA-TJ-debug`.

The current first build provides editable house dimensions, floor/room/opening counts, 2D schematic plan, stylized isometric 3D preview, furniture catalogue controls and an indicative cost estimate. It is a starter version, not a construction-grade CAD/BIM tool; quantities and prices are estimates and must be checked locally.
