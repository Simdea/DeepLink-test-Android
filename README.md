# DeepLink Tester for Android

[![Get it on Google Play](https://play.google.com/intl/en_us/badges/static/images/badges/en_badge_web_generic.png)](https://play.google.com/store/apps/details?id=com.simdea.deeplinktester)
[![GitHub release (latest by date)](https://img.shields.io/github/v/release/Simdea/DeepLink-test-Android)](https://github.com/Simdea/DeepLink-test-Android/releases/latest)
[![Version](https://img.shields.io/badge/version-2.0-blue.svg)](https://github.com/Simdea/DeepLink-test-Android/releases/tag/2.0)

**Version 2.0**

Tired of tedious `adb shell am start` commands just to test a single deep link?
Streamline your Android development and QA workflow with **DeepLink Tester**!

DeepLink Tester is a powerful, modern utility designed by developers, for developers. It provides an intuitive, comprehensive interface to launch, edit, inspect, and organize any URI or deep link on your Android device, making testing and debugging fast and effortless.

---

## 🚀 What's New in Version 2.0

Version 2.0 is a massive, ground-up reimagining of DeepLink Tester, bringing a complete redesign and major new capabilities:

- **Complete Jetpack Compose & Material 3 Redesign:** Rebuilt from the ground up with a sleek, responsive, and modern UI.
- **Collections Management:** Organize your deep links into custom named collections (e.g., Auth, Checkout, Staging, Production).
- **Integrated QR Code Scanner:** Point your camera to scan QR codes and immediately test encoded links using Google ML Kit.
- **Interactive URI Parameter Editor:** Visually inspect, add, modify, and delete query parameters in real time with bidirectional synchronization.
- **Real-Time Syntax Validation:** Instant feedback on URI structure to spot syntax errors before firing requests.
- **Searchable History & Favorites:** Quickly search across your deep link history and star your most frequently tested links.
- **DeepLink Inspector Screen:** Deep dive into specific links, review query parameters, assign to collections, copy, and share.
- **JSON Backup, Export & Import:** Export your history and collections to a JSON file to share with teammates or restore anytime.
- **Theme Customization:** Seamlessly switch between Light Mode, Dark Mode, or follow your System default.
- **Welcoming Onboarding:** Interactive onboarding flow introducing new users to the app's features.
- **Modernized Android Core:** Upgraded to Target SDK 36, Kotlin 2.2, Room with KSP, and modern Jetpack libraries.

---

## ✨ Features

- **Test Any DeepLink:** Launch custom URI schemes (`myapp://...`), Android App Links (`https://...`), or web URIs directly from the app.
- **QR Code Scanner:** Integrated camera scanner powered by CameraX and ML Kit to populate URIs without typing.
- **URI Parameter Editor:** Add, edit, or remove key-value query parameters dynamically with live URL updates.
- **Syntax Validation:** Visual indicators warn you if the entered URI has invalid syntax.
- **History & Favorites:** Automatically records every link you launch with timestamps. Star essential links to keep them at your fingertips.
- **Searchable History:** Instantly filter tested links by URI or parameter values.
- **Collections:** Create, rename, and manage collections to categorize deep links for different features or projects.
- **Retry & Delete:** Re-launch links with a single tap or remove obsolete entries individually or in bulk.
- **Export & Import:** Export your data as a JSON file for backup or team collaboration, and import backups with a tap.
- **DeepLink Inspector:** View parameter breakdowns, copy links to clipboard, or share via the Android system share sheet.
- **Theme Selector:** Choose Light, Dark, or System theme.
- **Graceful Error Handling:** Provides user-friendly alerts when no app is installed to handle a specific deep link scheme.
- **Ad-Supported:** Includes a clean, non-intrusive banner ad powered by Google AdMob.

---

## 🛠️ Tech Stack & Architecture

DeepLink Tester is built using the latest modern Android development best practices:

- **Language:** [Kotlin](https://kotlinlang.org/) (2.2+)
- **UI Framework:** [Jetpack Compose](https://developer.android.com/jetpack/compose) with [Material 3](https://m3.material.io/)
- **Architecture:** MVVM with unidirectional data flow (UDF)
- **Local Persistence:** [Room](https://developer.android.com/training/data-storage/room) with Kotlin Symbol Processing (KSP) and Kotlin Coroutines / Flow
- **Key-Value Storage:** [Jetpack DataStore Preferences](https://developer.android.com/topic/libraries/architecture/datastore)
- **Camera & Scanning:** [CameraX](https://developer.android.com/training/camerax) & [Google ML Kit Barcode Scanning](https://developers.google.com/ml-kit/vision/barcode-scanning)
- **Navigation:** [Jetpack Navigation Compose](https://developer.android.com/jetpack/compose/navigation)
- **Crash Reporting & Telemetry:** [Firebase Crashlytics](https://firebase.google.com/products/crashlytics) & Firebase Analytics
- **Monetization:** [Google Mobile Ads (AdMob)](https://admob.google.com/)
- **Build System:** Gradle Kotlin DSL with TOML Version Catalogs (`libs.versions.toml`)
- **Compatibility:** Min SDK 27 (Android 8.1 Oreo) / Target SDK 36 (Android 16)

---

## 📄 License

This project is open source and available under the [MIT License](LICENSE).