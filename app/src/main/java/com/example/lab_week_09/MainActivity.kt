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
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import java.net.URLDecoder
import java.net.URLEncoder

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
    val moshi = remember { Moshi.Builder().add(KotlinJsonAdapterFactory()).build() }
    val type = Types.newParameterizedType(List::class.java, Student::class.java)
    val adapter = moshi.adapter<List<Student>>(type)

    NavHost(navController = navController, startDestination = "home") {
        composable("home") {
            Home(onNavigate = { listData ->
                val json = adapter.toJson(listData)
                val encoded = URLEncoder.encode(json, "UTF-8")
                navController.navigate("result/$encoded")
            })
        }
        composable("result/{listJson}") { backStackEntry ->
            val encodedJson = backStackEntry.arguments?.getString("listJson") ?: ""
            val decodedJson = URLDecoder.decode(encodedJson, "UTF-8")
            val list = adapter.fromJson(decodedJson) ?: emptyList()
            ResultContent(list)
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
    var showError by remember { mutableStateOf(false) }

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
                onValueChange = {
                    inputText = it
                    showError = false
                },
                modifier = Modifier
                    .padding(top = 8.dp)
                    .fillMaxWidth(0.8f),
                isError = showError,
                supportingText = {
                    if (showError) {
                        Text("Please enter a name before submitting", color = MaterialTheme.colorScheme.error)
                    }
                }
            )
            Row(
                modifier = Modifier.padding(top = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                PrimaryTextButton(text = stringResource(id = R.string.button_click)) {
                    if (inputText.trim().isEmpty()) {
                        showError = true
                    } else {
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
fun ResultContent(listData: List<Student>) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Student List:",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        LazyColumn {
            items(listData) { student ->
                Text(
                    text = student.name,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }
        }
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
