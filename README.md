# DeepLink Tester for Android

[![Get it on Google Play](https://play.google.com/intl/en_us/badges/static/images/badges/en_badge_web_generic.png)](https://play.google.com/store/apps/details?id=com.simdea.deeplinktester)
[![GitHub release (latest by date)](https://img.shields.io/github/v/release/Simdea/DeepLink-test-Android)](https://github.com/Simdea/DeepLink-test-Android/releases/latest)

Tired of the tedious adb shell command just to test a single deep link?
Streamline your Android development workflow with DeepLink Tester!
DeepLink Tester is a minimalist, yet powerful utility designed by a
developer, for developers. It provides a straightforward interface to
launch any URI or deep link on your device, making the debugging
process faster and more efficient.

## Features

-   **Test any deeplink:** Enter any URI and launch it directly from the app.
-   **History:** Automatically saves a history of all tested deeplinks.
-   **Retry:** Quickly re-test any deeplink from your history with a single tap.
-   **Remove:** Clean up your history by removing individual entries.
-   **Error Handling:** The app gracefully handles cases where no application can open the deeplink, preventing crashes and providing user-friendly feedback.
-   **Ad-Supported:** Includes a banner ad for monetization, implemented with Google AdMob.

## Tech Stack

This project is built with a modern Android tech stack, including:

-   **Kotlin:** The primary programming language.
-   **Jetpack Compose:** The UI is built entirely with Jetpack Compose for a modern, declarative approach.
-   **Room:** Used for creating and managing the local database for the deeplink history.
-   **Material 3:** The application uses the latest Material Design components and theming.
-   **Kotlin Coroutines & Flow:** Used for asynchronous operations and managing data streams from the database.
-   **Jetpack Navigation:** For navigating between screens.
-   **Gradle Kotlin DSL & Version Catalogs:** The build system is configured using modern and maintainable best practices.