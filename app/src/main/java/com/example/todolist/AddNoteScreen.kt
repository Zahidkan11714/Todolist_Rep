package com.example.todolist

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.DatePickerDialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.DatePicker
import android.widget.TimePicker
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.NotificationCompat
import androidx.navigation.NavController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@Composable
fun AddNoteScreen(navController: NavController, noteDao: NoteDao, noteId_p: Int) {
    var title by remember { mutableStateOf("") }
    var selectedDate by remember { mutableStateOf("") }
    var reminderTime by remember { mutableStateOf<Long?>(null) }
    var context = LocalContext.current
    var note by remember { mutableStateOf<Note?>(null) }
    var buttonText by remember { mutableStateOf("Add Note") }
    var noteId by remember { mutableStateOf(noteId_p) }
    val coroutineScope = rememberCoroutineScope()
    val calendar = Calendar.getInstance()

    // Load note if updating
    LaunchedEffect(noteId) {
        if (noteId != 0) {
            val fetchedNote = withContext(Dispatchers.IO) {
                noteDao.getAllNotes().find { it.id == noteId }
            }
            fetchedNote?.let {
                note = it
                title = it.title
                selectedDate = it.datetime
                buttonText = "Update"
            }
        }
    }

    // Date & Time picker
    val datePickerDialog = DatePickerDialog(
        context,
        { _: DatePicker, year: Int, month: Int, dayOfMonth: Int ->
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, month)
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)

            val timePickerDialog = android.app.TimePickerDialog(
                context,
                { _: TimePicker, hourOfDay: Int, minute: Int ->
                    calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                    calendar.set(Calendar.MINUTE, minute)
                    calendar.set(Calendar.SECOND, 0)
                    calendar.set(Calendar.MILLISECOND, 0)

                    reminderTime = calendar.timeInMillis
                    val sdf = SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.getDefault())
                    selectedDate = sdf.format(calendar.time)
                },
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                true
            )
            timePickerDialog.show()
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(id = R.color.lighpink)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(22.dp)
                .background(colorResource(id = R.color.lighpink)),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Add Note", fontSize = 28.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(20.dp))

            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Note Title") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(Modifier.height(16.dp))

            OutlinedTextField(
                value = selectedDate,
                onValueChange = { selectedDate = it },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                label = { Text("Select Date & Time") },
                trailingIcon = {
                    IconButton(onClick = { datePickerDialog.show() }) {
                        Icon(imageVector = Icons.Default.DateRange, contentDescription = "Calendar Icon")
                    }
                }
            )

            Spacer(Modifier.height(16.dp))

            Button(
                onClick = {
                    if (title.isBlank() || reminderTime == null) {
                        Toast.makeText(context, "Please fill all fields", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    coroutineScope.launch {
                        if (noteId > 0) {
                            // Update existing note
                            note!!.title = title
                            note!!.datetime = selectedDate
                            noteDao.updateNote(note!!)

                            updateReminder(context, note!!, reminderTime!!)
                            Toast.makeText(context, "Note Updated", Toast.LENGTH_SHORT).show()
                        } else {
                            // Add new note
                            val newNote = Note(
                                title = title,
                                status = "uncomplete",
                                datetime = selectedDate
                            )
                            val insertedId = noteDao.insertNote(newNote)
                            newNote.id = insertedId.toInt()

                            setReminder(context, newNote, reminderTime!!)
                            Toast.makeText(context, "Note Added", Toast.LENGTH_SHORT).show()
                        }

                        title = ""
                        selectedDate = ""
                        reminderTime = null
                        buttonText = "Add Note"
                        noteId = 0
                        navController.popBackStack()
                    }
                },
                enabled = title.isNotBlank() && selectedDate.isNotBlank(),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(text = buttonText)
            }
        }
    }
}


class ReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val noteTitle = intent.getStringExtra("note_title")
        val notification = NotificationCompat.Builder(context, "reminder_channel")
            .setSmallIcon(R.drawable.todolist)
            .setContentTitle("Action Time")
            .setContentText(noteTitle)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()

        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "reminder_channel",
                "Reminders",
                NotificationManager.IMPORTANCE_HIGH
            )
            manager.createNotificationChannel(channel)
        }

        manager.notify(1, notification)
    }
}


fun getReminderPendingIntent(context: Context, noteId: Int, note: Note): PendingIntent {
    val intent = Intent(context, ReminderReceiver::class.java)
    intent.putExtra("note_title", note.title)
    intent.action = "NOTE_REMINDER_$noteId" // unique action
    return PendingIntent.getBroadcast(
        context,
        noteId,
        intent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )
}

fun setReminder(context: Context, note: Note, timeInMillis: Long) {
    val pendingIntent = getReminderPendingIntent(context, note.id, note)
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    alarmManager.setExact(AlarmManager.RTC_WAKEUP, timeInMillis, pendingIntent)
    Toast.makeText(context, "Reminder set!", Toast.LENGTH_SHORT).show()
}

fun cancelReminder(context: Context, note: Note) {
    val pendingIntent = getReminderPendingIntent(context, note.id, note)
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    alarmManager.cancel(pendingIntent)
}

fun updateReminder(context: Context, note: Note, newTimeMillis: Long) {
    cancelReminder(context, note)
    setReminder(context, note, newTimeMillis)
}
