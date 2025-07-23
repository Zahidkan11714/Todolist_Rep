package com.example.todolist

import android.os.Build
import android.text.style.BackgroundColorSpan
import android.window.SplashScreen
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.format.TextStyle

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun SplashScreen(NavController : NavController)
{
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        coroutineScope.launch {
            // Delay for 3 seconds (3000 milliseconds)
            delay(3000L)
            // Navigate to the next screen after delay
            NavController.navigate("home") {
                popUpTo("splash") {
                    inclusive = true // Remove the splash screen itself
                }
            }
        }
    }

    //.background(colorResource(id = R.color.my_custom_color))
    Box(modifier = Modifier.fillMaxSize().
        background(colorResource(id = R.color.lighpink)),
        contentAlignment = Alignment.Center) {
        Column {
            Column(modifier = Modifier.align(Alignment.CenterHorizontally)) {
                Text(text = "Let's Set Your Plan!", style = androidx.compose.ui.text.TextStyle(
                    fontSize = 32.sp,
                    color = Color.Black,
                    fontFamily = FontFamily.Cursive,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                )
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Image(painter = painterResource(id = R.drawable.todolist), contentDescription = "Logo")
            Spacer(modifier = Modifier.height(16.dp))
            Column(modifier = Modifier.align(Alignment.CenterHorizontally)) {
                Text(text = "TODO List", style = androidx.compose.ui.text.TextStyle(
                    fontSize = 36.sp,
                    color = Color.Black,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                )
                )
            }

        }
    }
}