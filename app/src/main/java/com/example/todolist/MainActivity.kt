package com.example.todolist

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.todolist.ui.theme.TODOListTheme

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppNavigations()
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AppNavigations()
{
    val context = LocalContext.current
    var navController = rememberNavController()
    //val db = NoteDatabase.getDatabase(this)
    val db = NoteDatabase.getInstance(context)
    val noteDao = db.noteDao()

    NavHost(navController = navController, startDestination = "splash") {

        composable("splash") {
            SplashScreen(navController)
        }

        composable("home") {
            Homescreen(navController, noteDao)
        }

        composable("${"addnotescreen"}/{noteId}") { backStackEntry ->
            val noteId = backStackEntry.arguments?.getString("noteId")?.toIntOrNull() ?: -1
            //AddNoteScreen(noteId, noteDao, navController)
            AddNoteScreen(navController, noteDao, noteId)
        }
    }
}