# Placebo - A Minimalist Streak Tracking App

Placebo is a modern Android application built to help you build and maintain habits by tracking your streaks. It offers a clean, minimalist interface focused on simplicity and motivation.

## ✨ Features

- **Create & Manage Streaks**: Easily add, edit, or delete the habits you want to track.
- **Persistent Storage**: Your data is saved locally on your device using a robust Room database, so you never lose your progress.
- **Visual Progress Calendar**: A contribution-style graph provides a clear and motivating overview of your activity over the last 5 weeks.
- **Streak Statistics**: See your current and longest streaks at a glance to stay motivated.
- **Mark Daily Progress**: A simple, accessible button to mark your streak as complete for the day.
- **Clean, Modern UI**: Built entirely with Jetpack Compose and Material 3 for a beautiful and responsive user experience.

## 🚀 Technology Stack

This project showcases modern Android development practices and leverages the following key technologies:

- **UI**: [Jetpack Compose](https://developer.android.com/jetpack/compose) with a Material 3 design.
- **Architecture**: Follows a standard MVVM (Model-View-ViewModel) pattern to separate concerns.
- **Data Persistence**: [Room Database](https://developer.android.com/training/data-storage/room) for local, persistent storage of streak data.
- **Asynchronous Operations**: [Kotlin Coroutines](https://kotlinlang.org/docs/coroutines-overview.html) and [Flow](https://kotlinlang.org/docs/flow.html) for managing background threads and handling data streams.
- **Navigation**: [Jetpack Navigation for Compose](https://developer.android.com/jetpack/compose/navigation) to manage screen transitions.
- **Dependency Injection**: A manual DI pattern is used via a custom `Application` class to provide the database and repository.

## 📂 Project Structure

The codebase is organized into feature-based packages to promote scalability and maintainability:

- **`data/`**: Contains all data-related classes, including Room entities (`Streak`, `Completion`), Data Access Objects (DAOs), the `AppDatabase` class, and the `StreaksRepository`.
- **`streaksList/`**: UI composables for the main screen that displays the list of all streaks.
- **`streakView/`**: UI composables for the detail screen, including the progress calendar and streak statistics.
- **`util/`**: Houses utility functions, such as the `StreakCalculator` for processing streak data.

## ⚙️ Getting Started

To build and run the project, follow these steps:

1.  Clone the repository:
    ```sh
    git clone <repository-url>
    ```
2.  Open the project in Android Studio.
3.  Let Gradle sync the dependencies.
4.  Run the app on an emulator or a physical device.

That's it! The app should build and run without any additional configuration.
