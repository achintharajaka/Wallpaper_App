# Wallpaper_App

An Android app that delivers high-quality black and AMOLED wallpapers optimized for modern phones. This repository contains the full Android project (Gradle-based) including signing artifacts and release bundles under `app/release/`.

## Key highlights

- Clean, high-contrast wallpapers ideal for AMOLED screens
- Multiple resolutions and crop-friendly images
- Ready-to-build Android Studio project with release AABs included in `app/release/`
- Firebase / Google services configuration file is present at `app/google-services.json`

## Repository layout

- `app/` — Android app module (source, resources, Gradle config)
  - `app/release/` — pre-built AAB release bundles included in this repo
  - `app/src/` — app source code and resources
  - `app/google-services.json` — Firebase/Play-services configuration (if present)
- `gradle/`, `gradlew`, `gradlew.bat` — Gradle wrapper
- `Other_Doc/` — images, keys, and supporting docs (screenshots, keystore, policy)
- `LICENSE` — project license

## Requirements

- Android Studio 2022.3 or newer (Arctic Fox or higher recommended)
- JDK 11 or newer
- Gradle (the wrapper is included; no system Gradle required)

## Quick start (Windows cmd)

Clone the repository and open the project in Android Studio:

```cmd
git clone https://github.com/achintharajaka/Wallpaper_App.git
cd Wallpaper_App
rem Open the project in Android Studio (File → Open... → select this folder)
```

From the command line you can build APK/AAB using the Gradle wrapper:

```cmd
cd app
..\gradlew.bat assembleDebug
..\gradlew.bat assembleRelease
..\gradlew.bat bundleRelease
```

Note: Use the included `gradlew.bat` from the repository root if you run from the project root.

## Signing and release

- A sample signing keystore and private key references are stored in `Other_Doc/` — do NOT commit your real production keys.
- If you want to build a release AAB for Play Store, provide your own keystore and update `app/build.gradle` or use Gradle command line properties to inject signing config.

## Pre-built artifacts

This repo already contains release bundles in `app/release/`:

- `app/release/Black_Wallpapers_v4.aab` (example)

If you need to regenerate these, run `..\gradlew.bat bundleRelease` and sign with your keystore.

## Screenshots and assets

Sample screenshots are available in `Other_Doc/screenshot/` and launcher icons in `Other_Doc/`.

## Configuration

- `app/google-services.json` is included if Firebase services were used. Remove or replace with your own configuration when building.

## Contributing

1. Fork the repository.
2. Create a feature branch: `git checkout -b feat/your-change`.
3. Make changes and include tests where applicable.
4. Open a pull request describing your change.

Please follow standard GitHub pull request etiquette. For UI changes, include before/after screenshots.

## Known issues & notes

- Some release artifacts and keys are present in the repo for convenience. Treat them as samples and replace with your own credentials for production.
- If you see build errors, confirm your JDK/Gradle/Android SDK versions match the `compileSdkVersion` and `targetSdkVersion` defined in `app/build.gradle`.

## License

This project includes a `LICENSE` file in the repository root. Review it for terms and attribution.
