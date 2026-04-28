# Placebo - A Modern Android Habit Tracker

Placebo is a minimalist, single-activity Android application built with 100% Kotlin and Jetpack Compose. It's designed to help users build and maintain positive habits by tracking their streaks in a clean, intuitive, and motivating interface.

## ✨ Key Features

*   **Streak Management**: Create, edit, and permanently delete streaks.
*   **Icon Personalization**: Assign unique, preset icons to each streak for better visual identification.
*   **Persistent Storage**: User data is saved locally using a robust **Room** database, ensuring progress is never lost between sessions.
*   **Archive System**: Declutter your main screen by archiving completed or inactive streaks. Archived streaks can be viewed separately, unarchived, or deleted permanently.
*   **Visual Progress Calendar**: A contribution-style graph provides a clear and motivating overview of your activity over the last five weeks.
*   **Streak Statistics**: The app automatically calculates and displays your **current streak** and **longest streak** to keep you motivated.
*   **Smart Reminders & Notifications**: 
    - Set custom reminder times for each streak to get daily notifications.
    - Reminders persist across device reboots with automatic rescheduling.
    - Notifications intelligently check if a streak is already completed today before reminding you.
    - Built using **AlarmManager** and **BroadcastReceivers** for reliable, system-level scheduling.
*   **Modern & Reactive UI**: Built entirely with **Jetpack Compose** and Material 3, following the latest Android development best practices.

---

## 🏛️ Architecture & Data Flow

This project follows a modern, reactive MVVM (Model-View-ViewModel) architecture designed for scalability and maintainability, enhanced with system-level services for reliable reminder scheduling.

### Architecture Layers

1.  **UI Layer (Jetpack Compose)**: The entire UI is built with Jetpack Compose. A single `MainActivity` hosts a `NavHost` which manages navigation between three main screens: `StreaksListScreen`, `StreakPage` (detail view), and `ArchiveScreen`.

2.  **ViewModel (`StreaksViewModel`)**: Acts as the central logic hub for the UI. It prepares and manages UI-related state, handles user events, and communicates with the Data Layer. It does not have any direct knowledge of the database.

3.  **Data Layer (`StreaksRepository`)**: The repository serves as the **single source of truth** for all application data. It abstracts the data source (the Room database) from the rest of the app and exposes clean, reactive data streams using Kotlin Flow.

4.  **System Integration Layer (Broadcast Receivers & Alarm Manager)**:
    - **`BootReceiver`**: Listens for device boot completion and reschedules all active reminders.
    - **`NotificationReceiver`**: Receives alarm broadcasts and intelligently displays notifications only if the streak hasn't been marked as complete today.
    - **`ReminderManager`**: Abstracts `AlarmManager` operations on Android 12+ with fallback support.
    - **`NotificationHelper`**: Centralizes notification creation and display with contextual information.

5.  **Database (Room)**: The local persistence library. `Streak` and `Completion` data are stored in a relational SQLite database managed by Room, with schema migrations for graceful updates. The `Streak` entity now includes reminder fields (`reminderTime`, `isReminderEnabled`) for persistent reminder configuration.

### Reactive Data Flow

The app is built on a reactive data flow model using **Kotlin Coroutines and Flow**.

*   **Data Exposure**: The `StreakDao` exposes database queries as `Flow<List<T>>`. This `Flow` is passed up through the `StreaksRepository`.

*   **State Management**: In the `StreaksViewModel`, this repository `Flow` is collected and converted into a `StateFlow` using `stateIn(viewModelScope, ...)`. This makes the data observable by the Compose UI in a lifecycle-aware manner.

*   **Efficient Detail Screen Loading**: To prevent memory leaks and race conditions, the detail screen uses a sophisticated data loading pattern. A private `MutableStateFlow` in the `ViewModel` is used as a trigger. This trigger is observed by a `flatMapLatest` operator, which automatically cancels the previous database observer and starts a new one whenever the user navigates to a different streak. The UI uses a `DisposableEffect` to ensure this trigger is cleared when the user navigates away, stopping the database observation.

*   **Reminder Scheduling**: When a user sets or updates a reminder, the `ReminderManager` schedules an alarm. On the scheduled time, the system wakes the `NotificationReceiver`, which queries the repository to check today's completions before showing a notification.

```
UI (Composable) -> ViewModel (Function) -> Repository (Suspend Function) -> DAO (Suspend Function)

DAO (Flow) -> Repository (Flow) -> ViewModel (StateFlow) -> UI (collectAsStateWithLifecycle)

Device Boot -> BootReceiver -> ReminderManager -> AlarmManager -> NotificationReceiver -> NotificationHelper -> User
```

---

## 🚀 Technology Stack

- **UI**: [Jetpack Compose](https://developer.android.com/jetpack/compose) with Material 3
- **Architecture**: MVVM, Single-Activity
- **Navigation**: [Jetpack Navigation for Compose](https://developer.android.com/jetpack/compose/navigation)
- **Data Persistence**: [Room Database](https://developer.android.com/training/data-storage/room)
- **Asynchronous Operations**: [Kotlin Coroutines & Flow](https://kotlinlang.org/docs/coroutines-overview.html)
- **State Management**: `ViewModel`, `StateFlow`, `collectAsStateWithLifecycle`
- **System Integration**: [AlarmManager](https://developer.android.com/training/scheduling/alarms) for reminder scheduling, [BroadcastReceiver](https://developer.android.com/guide/components/broadcasts) for system events
- **Notifications**: [NotificationCompat](https://developer.android.com/reference/androidx/core/app/NotificationCompat.Builder) for cross-version notification support
- **Code Generation**: KSP for the Room compiler

## 📂 Project Structure

- **`data/`**: Contains all data-related classes (Entities, DAOs, Repository, Database).
  - **`Streak.kt`**: Entity with fields for reminder configuration (`reminderTime`, `isReminderEnabled`).
  - **`persistence/`**: Data migration scripts for Room schema evolution.
- **`receiver/`**: BroadcastReceivers for system-level events.
  - **`BootReceiver.kt`**: Reschedules reminders on device boot.
  - **`NotificationReceiver.kt`**: Handles alarm broadcasts and displays reminders intelligently.
- **`archive/`**: Composables for the archived streaks screen.
- **`streaksList/`**: Composables for the main list screen.
- **`streakView/`**: Composables for the streak detail screen.
- **`util/`**: Helper functions and utilities.
  - **`StreakCalculator.kt`**: Calculates current and longest streaks.
  - **`IconUtils.kt`**: Icon mapping and selection utilities.
  - **`NotificationHelper.kt`**: Creates and displays notifications.
  - **`ReminderManager.kt`**: Abstracts AlarmManager operations with Android 12+ support.

---

## 🔔 Reminder System Architecture

The app features a sophisticated reminder system that ensures users stay on top of their streaks. Here's how it works:

### How Reminders Work

1. **User Sets a Reminder**: When the user enables a reminder for a streak and selects a time, the app saves this configuration in the database and schedules an alarm using `ReminderManager.scheduleReminder()`.

2. **Alarm Scheduling**: The `ReminderManager` uses **AlarmManager** to schedule a precise alarm. On Android 12+, it checks for the `SCHEDULE_EXACT_ALARM` permission and falls back gracefully if unavailable.

3. **Alarm Fires**: When the scheduled time arrives, the system broadcasts an intent to `NotificationReceiver`.

4. **Smart Notification Decision**: The receiver queries the database to check:
   - Is the streak archived? (Skip if yes)
   - Has the user already completed this streak today? (Skip if yes)
   - Is the reminder still enabled? (Skip if no)
   
   Only if all checks pass does it display a notification.

5. **Device Boot Handling**: On device restart, `BootReceiver` runs and reschedules all active reminders by querying the database for enabled reminders.

### Permissions Required

The app requests three permissions for the reminder system to function:

```xml
<uses-permission android:name="android.permission.POST_NOTIFICATIONS" />
<uses-permission android:name="android.permission.SCHEDULE_EXACT_ALARM" />
<uses-permission android:name="android.permission.RECEIVE_BOOT_COMPLETED" />
```

- **`POST_NOTIFICATIONS`**: Required on Android 13+ to display notifications.
- **`SCHEDULE_EXACT_ALARM`**: Allows precise alarm scheduling on Android 12+.
- **`RECEIVE_BOOT_COMPLETED`**: Enables `BootReceiver` to reschedule reminders after restart.

### Notification Channel

The app creates a notification channel named `"Streak Reminders"` on Android 8+ in the `StreaksApplication.onCreate()` method. This ensures consistency and gives users control over reminder notifications through system settings.

---
