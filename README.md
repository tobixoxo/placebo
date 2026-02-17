# Placebo - A Modern Android Habit Tracker

Placebo is a minimalist, single-activity Android application built with 100% Kotlin and Jetpack Compose. It's designed to help users build and maintain positive habits by tracking their streaks in a clean, intuitive, and motivating interface.

## ✨ Key Features

*   **Streak Management**: Create, edit, and permanently delete streaks.
*   **Icon Personalization**: Assign unique, preset icons to each streak for better visual identification.
*   **Persistent Storage**: User data is saved locally using a robust **Room** database, ensuring progress is never lost between sessions.
*   **Archive System**: Declutter your main screen by archiving completed or inactive streaks. Archived streaks can be viewed separately, unarchived, or deleted permanently.
*   **Visual Progress Calendar**: A contribution-style graph provides a clear and motivating overview of your activity over the last five weeks.
*   **Streak Statistics**: The app automatically calculates and displays your **current streak** and **longest streak** to keep you motivated.
*   **Modern & Reactive UI**: Built entirely with **Jetpack Compose** and Material 3, following the latest Android development best practices.

---

## 🏛️ Architecture & Data Flow

This project follows a modern, reactive MVVM (Model-View-ViewModel) architecture designed for scalability and maintainability.

### Architecture Layers

1.  **UI Layer (Jetpack Compose)**: The entire UI is built with Jetpack Compose. A single `MainActivity` hosts a `NavHost` which manages navigation between three main screens: `StreaksListScreen`, `StreakPage` (detail view), and `ArchiveScreen`.

2.  **ViewModel (`StreaksViewModel`)**: Acts as the central logic hub for the UI. It prepares and manages UI-related state, handles user events, and communicates with the Data Layer. It does not have any direct knowledge of the database.

3.  **Data Layer (`StreaksRepository`)**: The repository serves as the **single source of truth** for all application data. It abstracts the data source (the Room database) from the rest of the app and exposes clean, reactive data streams using Kotlin Flow.

4.  **Database (Room)**: The local persistence library. `Streak` and `Completion` data are stored in a relational SQLite database managed by Room. The app includes database migrations to handle schema changes gracefully.

### Reactive Data Flow

The app is built on a reactive data flow model using **Kotlin Coroutines and Flow**.

*   **Data Exposure**: The `StreakDao` exposes database queries as `Flow<List<T>>`. This `Flow` is passed up through the `StreaksRepository`.

*   **State Management**: In the `StreaksViewModel`, this repository `Flow` is collected and converted into a `StateFlow` using `stateIn(viewModelScope, ...)`. This makes the data observable by the Compose UI in a lifecycle-aware manner.

*   **Efficient Detail Screen Loading**: To prevent memory leaks and race conditions, the detail screen uses a sophisticated data loading pattern. A private `MutableStateFlow` in the `ViewModel` is used as a trigger. This trigger is observed by a `flatMapLatest` operator, which automatically cancels the previous database observer and starts a new one whenever the user navigates to a different streak. The UI uses a `DisposableEffect` to ensure this trigger is cleared when the user navigates away, stopping the database observation.

```
UI (Composable) -> ViewModel (Function) -> Repository (Suspend Function) -> DAO (Suspend Function)

DAO (Flow) -> Repository (Flow) -> ViewModel (StateFlow) -> UI (collectAsStateWithLifecycle)
```

---

## 🚀 Technology Stack

- **UI**: [Jetpack Compose](https://developer.android.com/jetpack/compose) with Material 3
- **Architecture**: MVVM, Single-Activity
- **Navigation**: [Jetpack Navigation for Compose](https://developer.android.com/jetpack/compose/navigation)
- **Data Persistence**: [Room Database](https://developer.android.com/training/data-storage/room)
- **Asynchronous Operations**: [Kotlin Coroutines & Flow](https://kotlinlang.org/docs/coroutines-overview.html)
- **State Management**: `ViewModel`, `StateFlow`, `collectAsStateWithLifecycle`
- **Code Generation**: KSP for the Room compiler

## 📂 Project Structure

- **`data/`**: Contains all data-related classes (Entities, DAOs, Repository, Database).
- **`archive/`**: Composables for the archived streaks screen.
- **`streaksList/`**: Composables for the main list screen.
- **`streakView/`**: Composables for the streak detail screen.
- **`util/`**: Helper functions (`StreakCalculator`, `IconUtils`).
