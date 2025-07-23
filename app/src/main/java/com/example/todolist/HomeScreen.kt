package com.example.todolist

import android.annotation.SuppressLint
import android.os.Build
import android.service.autofill.OnClickAction
import android.text.style.BackgroundColorSpan
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.collection.emptyLongSet
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.BlendMode.Companion.Screen
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.AlignmentLine
import androidx.compose.ui.layout.VerticalAlignmentLine
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.TextButton
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale


@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun Homescreen(navController: NavController, noteDao: NoteDao) {
    var notes by remember { mutableStateOf(listOf<Note>()) }
    var status_state by remember { mutableStateOf("") }
    var showDialog by remember { mutableStateOf(false) }
    var selectedNote by remember { mutableStateOf<Note?>(null) }
    var searchText by remember { mutableStateOf("") }
    var final_notes by remember { mutableStateOf(listOf<Note>()) }
    val coroutineScope = rememberCoroutineScope()  // 👈 1. Add this at the top
    var id = 0

    LaunchedEffect(Unit) {
        notes = withContext(Dispatchers.IO) { noteDao.getAllNotes() }
    }

    val filteredNotes = notes.filter {
        it.title.startsWith(searchText, ignoreCase = true)
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate("addnotescreen/id") },
                containerColor = Color.Cyan,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add"
                )
            }
        }
    ) { innerPadding ->
        // Main content
        Column {
            Column(
                modifier = Modifier
                    .fillMaxSize().background(colorResource(id = R.color.lighpink)),
            ) {

                Spacer(Modifier.height(26.dp))

                // 🔍 Enhanced Search Bar
                OutlinedTextField(
                    value = searchText,
                    onValueChange = { searchText = it },
                    placeholder = { Text("Search notes...") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search icon"
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 8.dp),
                    singleLine = true,
                    shape = MaterialTheme.shapes.large,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                        focusedTextColor = MaterialTheme.colorScheme.onSurface,
                        unfocusedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    ),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    keyboardActions = KeyboardActions(
                        onSearch = {
                            // Handle search action
                        }
                    )
                )

                if (searchText.isNotEmpty())
                {
                    final_notes = filteredNotes;
                }
                else
                {
                    final_notes = notes;
                }

                LazyColumn(modifier = Modifier.padding(16.dp)) {
                    items(final_notes) { note ->
                        var statusState by remember { mutableStateOf(note.status) }

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp)
                                .combinedClickable(
                                    onClick = {
                                        navController.navigate("addnotescreen/${note.id}")
                                    },
                                    onLongClick = {
                                        selectedNote = note
                                        showDialog = true
                                    }
                                ),
                            shape = RoundedCornerShape(16.dp),
                            elevation = CardDefaults.cardElevation(6.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = colorResource(id = R.color.milktea) // Your color here
                            )
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp).background(colorResource(id = R.color.milktea))
                            ) {
                                Text(
                                    text = note.title,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                Text(
                                    text = "Date: ${note.datetime}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.End
                                ) {
                                    var statusState by remember { mutableStateOf(note.status) }
                                    var buttoncolor = if (statusState == "complete") {
                                        Color.Blue
                                    } else {
                                        Color.Gray
                                    }

                                    val isExpired = remember(note.datetime) { isNoteExpired(note.datetime) }

                                    if (isExpired && statusState == "uncomplete") {

                                        Box(modifier = Modifier.padding(4.dp).align(Alignment.CenterVertically))
                                        {
                                            Text(text = "Expiry Date", style = MaterialTheme.typography.bodyMedium, color = Color.Red)
                                        }
                                        Spacer(modifier = Modifier.width(28.dp))
                                    }



                                    ElevatedButton(
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = buttoncolor,
                                            contentColor = Color.White
                                        ),
                                        onClick = {
                                            if (statusState == "uncomplete") {
                                                note.status = "complete"
                                                statusState = "complete"
                                            } else {
                                                note.status = "uncomplete"
                                                statusState = "uncomplete"
                                            }
                                            //update in the database
                                            coroutineScope.launch {
                                                noteDao.updateNote(note)
                                            }
                                        },
                                        shape = RoundedCornerShape(12.dp)

                                    ) {
                                        Text(statusState)
                                    }
                                }
                            }
                        }
                    }
                }
            }

        }
        // 🧨 AlertDialog shown when long pressed
        if (showDialog && selectedNote != null) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = {
                    Text(
                        text = "Are you sure you want to delete this item?",
                        color = Color.Black  // Title text color
                    )
                },
                confirmButton = {
                    TextButton(onClick = {
                        selectedNote?.let { noteToDelete ->
                            coroutineScope.launch {
                                noteDao.deleteNote(noteToDelete)
                                notes = withContext(Dispatchers.IO) {
                                    noteDao.getAllNotes()
                                }
                            }
                        }
                        showDialog = false
                    }) {
                        Text("Delete", color = Color.Red)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDialog = false }) {
                        Text("Cancel", color = Color.Blue)
                    }
                },
                containerColor = colorResource(id = R.color.HotYellow), // Example: dark red background
                tonalElevation = 8.dp,              // Optional for shadow depth
                shape = RoundedCornerShape(12.dp)   // Optional for rounded corners
            )
        }

    }
}

@RequiresApi(Build.VERSION_CODES.O)
fun isNoteExpired(reminderDateTime: String): Boolean {
    val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy hh:mm a", Locale.ENGLISH)

    // Parse full date-time
    val reminderDate = LocalDateTime.parse(reminderDateTime.uppercase(Locale.ENGLISH), formatter).toLocalDate()

    // Current date (only day/month/year)
    val today = LocalDate.now()

    // Expired only if reminder date is before today
    return reminderDate.isBefore(today)
}

//fun isNoteExpired(reminderDateTime: String): Boolean {
//    val fixedDateTime = reminderDateTime.uppercase(Locale.ENGLISH)  // converts pm → PM
//    val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy hh:mm a", Locale.ENGLISH)
//    val reminder = LocalDateTime.parse(fixedDateTime, formatter)
//    val now = LocalDateTime.now()
//
//    //return now.isAfter(reminder)   // true = expired
//    return now.isBefore(reminder)
//}