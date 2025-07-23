To-Do List App (Jetpack Compose)
A fully functional and modern To-Do List Android Application built using Jetpack Compose and Room Database.
This is not a simple app; it comes with essential productivity features like adding, updating, deleting, searching tasks, and triggering reminders with notifications.

Features
Splash Screen – Displays app logo before navigating to Home screen.

Home Screen

Search Bar: Filter tasks dynamically by typing text (e.g., typing H shows tasks starting with "H").

Task List: Displayed using LazyColumn with well-designed cards showing:

Task Title

Task Date & Time

Status Button to mark tasks as complete or incomplete.

Expiry Indicator: Tasks with past dates are marked with red “Expired” text.

Floating Action Button (FAB): Add new tasks easily.

Add & Update Tasks

Navigate to the Add Note screen by clicking the FAB or tapping an existing task card.

Enter a Title and select Date & Time using pickers.

Save a new task or update an existing one in the Room database.

Notifications

Task reminders trigger a local notification when the scheduled date and time are reached.

Delete Tasks

Long press on a task card to show a confirmation dialog.

Click "Delete" to permanently remove the task.

Tech Stack
Jetpack Compose – Modern UI Toolkit for Android

Room Database – Local database for storing tasks

Kotlin Coroutines – For background operations

ViewModel – To handle UI state efficiently

AlarmManager – For scheduling task reminders

Material3 Components – For modern UI design
