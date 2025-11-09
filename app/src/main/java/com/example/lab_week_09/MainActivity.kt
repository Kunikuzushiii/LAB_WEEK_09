package com.example.lab_week_09

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.lab_week_09.ui.theme.LAB_WEEK_09Theme
import com.example.lab_week_09.ui.theme.OnBackgroundItemText
import com.example.lab_week_09.ui.theme.OnBackgroundTitleText
import com.example.lab_week_09.ui.theme.PrimaryTextButton

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LAB_WEEK_09Theme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    App(navController)
                }
            }
        }
    }
}

@Composable
fun App(navController: NavHostController) {
    NavHost(navController = navController, startDestination = "home") {
        composable("home") {
            Home(onNavigate = { listData ->
                // kirim list sebagai string
                val listString = listData.toString()
                navController.navigate("result/${listString}")
            })
        }

        composable("result/{listString}") { backStackEntry ->
            val listString = backStackEntry.arguments?.getString("listString") ?: ""
            ResultContent(listString = listString)
        }
    }
}

@Composable
fun Home(onNavigate: (List<Student>) -> Unit) {
    val listData = remember {
        mutableStateListOf(
            Student("Tanu"),
            Student("Tina"),
            Student("Tono")
        )
    }

    var inputText by remember { mutableStateOf("") }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            OnBackgroundTitleText(text = stringResource(id = R.string.enter_item))

            TextField(
                value = inputText,
                onValueChange = { inputText = it },
                modifier = Modifier
                    .padding(top = 8.dp)
                    .fillMaxWidth(0.8f)
            )
            Row(
                modifier = Modifier
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                PrimaryTextButton(text = stringResource(id = R.string.button_click)) {
                    if (inputText.isNotBlank()) {
                        listData.add(Student(inputText.trim()))
                        inputText = ""
                    }
                }

                PrimaryTextButton(text = stringResource(id = R.string.button_navigate)) {
                    onNavigate(listData)
                }
            }
        }

        items(listData) { student ->
            OnBackgroundItemText(text = student.name)
        }
    }
}

@Composable
fun ResultContent(listString: String) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = listString,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}
data class Student(val name: String)

@Preview(showBackground = true)
@Composable
fun PreviewHome() {
    LAB_WEEK_09Theme {
        Home(onNavigate = {})
    }
}
