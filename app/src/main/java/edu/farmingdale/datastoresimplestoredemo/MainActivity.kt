package edu.farmingdale.datastoresimplestoredemo
import android.content.Context
import java.io.PrintWriter
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import edu.farmingdale.datastoresimplestoredemo.data.AppPreferences
import edu.farmingdale.datastoresimplestoredemo.data.AppStorage
import edu.farmingdale.datastoresimplestoredemo.ui.theme.DataStoreSimpleStoreDemoTheme
import kotlinx.coroutines.launch
import java.io.FileOutputStream



class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DataStoreSimpleStoreDemoTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    DataStoreDemo(modifier = Modifier.padding(innerPadding))
                }
            }
        }
        writeToInternalFile()
        val fileContents = readFromInternalFile()
        Log.d("MainActivity", fileContents)
    }
    private fun writeToInternalFile() {
        val outputStream: FileOutputStream = openFileOutput("edufilename", Context.MODE_PRIVATE)
        val writer = PrintWriter(outputStream)

        // Write three lines
        writer.println("This world of fun")
        writer.println("and yet, and yet.")

        writer.close()
    }

    private fun readFromInternalFile(): String {
        val inputStream = openFileInput("edufilename")
        val reader = inputStream.bufferedReader()
        val stringBuilder = StringBuilder()

        // Append each line and newline character to stringBuilder
        reader.forEachLine {
            stringBuilder.append(it).append("\n CSC 371 \n").append(System.lineSeparator())
        }

        return stringBuilder.toString()
    }
}

@Composable
fun DataStoreDemo(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val store = remember { AppStorage(context) }
    val appPrefs = store.appPreferenceFlow.collectAsState(AppPreferences())
    val coroutineScope = rememberCoroutineScope()

    var inputText by remember { mutableStateOf("") } // user input

    Column(modifier = Modifier.padding(40.dp)) {
        // For todo4, display the values stored in the DataStore
        Text("Current values:")
        Text("Username: ${appPrefs.value.userName}")
        Text("High Score: ${appPrefs.value.highScore}")
        Text("Dark Mode: ${appPrefs.value.darkMode}")

        Spacer(Modifier.padding(8.dp))

        // For todo2, store the username through a text field (then officially saved in todo3)
        TextField(
            value = inputText,
            onValueChange = { inputText = it },
            label = { Text("Enter Username") }
        )

        Spacer(Modifier.padding(8.dp))

        // For todo3, Button to save username
        Button(onClick = {
            coroutineScope.launch {
                store.saveUsername(inputText)
            }
        }) {
            Text("Save Username")
        }

        Spacer(Modifier.padding(8.dp))

        // increase high score by 1
        Button(onClick = {
            coroutineScope.launch {
                val nextScore = appPrefs.value.highScore + 1
                store.saveHighScore(nextScore)
            }
        }) {
            Text("Increase High Score")
        }

        Spacer(Modifier.padding(8.dp))

        // toggle dark mode
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Dark Mode")
            Switch(
                checked = appPrefs.value.darkMode,
                onCheckedChange = { checked ->
                    coroutineScope.launch {
                        store.saveDarkMode(checked)
                    }
                }
            )
        }
    }
}


// ToDo 1: Modify the App to store a high score and a dark mode preference
// ToDo 2: Modify the APP to store the username through a text field
// ToDo 3: Modify the App to save the username when the button is clicked
// ToDo 4: Modify the App to display the values stored in the DataStore


