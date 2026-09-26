package com.maya.assistant

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { MayaApp() }
    }
}

enum class MayaState { Sleeping, Listening, Verifying, Thinking, Working, Success, Error }

private val Bg = Color(0xFF070812)
private val Card = Color(0xFF101326)
private val TextPrimary = Color(0xFFF5F7FF)
private val TextSecondary = Color(0xFF9CA4BD)

@Composable
fun MayaApp() {
    val nav = rememberNavController()
    var state by remember { mutableStateOf(MayaState.Sleeping) }
    var wakeWord by remember { mutableStateOf("Hey Maya") }
    var stopWord by remember { mutableStateOf("Bye Bye") }

    MaterialTheme(colorScheme = darkColorScheme(background = Bg, surface = Card, primary = Color(0xFF9B8CFF))) {
        Surface(Modifier.fillMaxSize(), color = Bg) {
            NavHost(navController = nav, startDestination = "home") {
                composable("home") { HomeScreen(state, { state = it }, { nav.navigate("settings") }) }
                composable("settings") { SettingsScreen(wakeWord, stopWord, { wakeWord = it }, { stopWord = it }, { nav.popBackStack() }) }
                composable("history") { HistoryScreen { nav.popBackStack() } }
                composable("voice") { VoiceScreen(state, { state = it }, { nav.popBackStack() }) }
                composable("security") { SecurityScreen { nav.popBackStack() } }
                composable("about") { AboutScreen { nav.popBackStack() } }
            }
        }
    }
}

@Composable
fun MayaOrb(state: MayaState, modifier: Modifier = Modifier, onClick: () -> Unit = {}) {
    val label = when(state) {
        MayaState.Sleeping -> "Sleeping"
        MayaState.Listening -> "Listening"
        MayaState.Verifying -> "Verifying"
        MayaState.Thinking -> "Thinking"
        MayaState.Working -> "Working"
        MayaState.Success -> "Done"
        MayaState.Error -> "Needs attention"
    }
    val brush = Brush.radialGradient(listOf(Color(0xFFB6A8FF), Color(0xFF5D45D9), Color(0xFF17132F)))
    Column(modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Box(Modifier.size(180.dp).clip(CircleShape).background(brush).border(1.dp, Color(0x66FFFFFF), CircleShape).clickable { onClick() }, contentAlignment = Alignment.Center) {
            Box(Modifier.size(108.dp).clip(CircleShape).background(Color(0x22FFFFFF)), contentAlignment = Alignment.Center) {
                Text("M", fontSize = 58.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
            }
        }
        Spacer(Modifier.height(18.dp))
        Text(label, color = TextSecondary, fontSize = 14.sp)
    }
}

@Composable
fun HomeScreen(state: MayaState, setState: (MayaState) -> Unit, openSettings: () -> Unit) {
    val nav = rememberNavController()
    Box(Modifier.fillMaxSize().background(Bg)) {
        Column(Modifier.fillMaxSize().padding(horizontal = 24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Spacer(Modifier.height(52.dp))
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text("Maya", color = TextPrimary, fontSize = 28.sp, fontWeight = FontWeight.Bold)
                    Text("Your voice-first assistant", color = TextSecondary, fontSize = 13.sp)
                }
                IconButton(onClick = openSettings) { Icon(Icons.Default.Settings, "Settings", tint = TextSecondary) }
            }
            Spacer(Modifier.height(58.dp))
            MayaOrb(state, onClick = { /* icon is visual only; no panel opens */ })
            Spacer(Modifier.height(36.dp))
            Text("Say  “Hey Maya”", color = TextPrimary, fontSize = 20.sp, fontWeight = FontWeight.Medium)
            Spacer(Modifier.height(8.dp))
            Text("Give me a voice command. I’ll handle the steps.", color = TextSecondary, textAlign = TextAlign.Center, fontSize = 14.sp)
            Spacer(Modifier.weight(1f))
            Text("No buttons needed for everyday tasks", color = TextSecondary, fontSize = 12.sp)
            Spacer(Modifier.height(30.dp))
        }
    }
}

@Composable
fun SettingsScreen(wake: String, stop: String, setWake: (String)->Unit, setStop: (String)->Unit, back: ()->Unit) {
    Column(Modifier.fillMaxSize().background(Bg).padding(22.dp)) {
        TopBar("Settings", back)
        Spacer(Modifier.height(18.dp))
        SettingSection("Voice") {
            Text("Wake phrase", color = TextSecondary, fontSize = 12.sp)
            OutlinedTextField(wake, setWake, Modifier.fillMaxWidth(), singleLine = true)
            Spacer(Modifier.height(12.dp))
            Text("Stop command", color = TextSecondary, fontSize = 12.sp)
            OutlinedTextField(stop, setStop, Modifier.fillMaxWidth(), singleLine = true)
        }
        SettingRow("Voice & language", "Configure speech and language", Icons.Default.RecordVoiceOver) {}
        SettingRow("Security & face verification", "Permissions and verification", Icons.Default.Face) {}
        SettingRow("Task history", "Review completed tasks", Icons.Default.History) {}
        SettingRow("Notifications", "System status notifications", Icons.Default.Notifications) {}
        SettingRow("About Maya", "Version and information", Icons.Default.Info) {}
    }
}

@Composable fun SettingSection(title: String, content: @Composable ColumnScope.() -> Unit) {
    Text(title, color = TextPrimary, fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
    Spacer(Modifier.height(10.dp))
    Card(shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = Card)) { Column(Modifier.padding(16.dp), content = content) }
    Spacer(Modifier.height(16.dp))
}

@Composable fun SettingRow(title: String, subtitle: String, icon: ImageVector, onClick: () -> Unit) {
    Row(Modifier.fillMaxWidth().clip(RoundedCornerShape(18.dp)).clickable { onClick() }.padding(vertical = 15.dp), verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, null, tint = Color(0xFFB6A8FF), modifier = Modifier.size(24.dp))
        Spacer(Modifier.width(16.dp))
        Column(Modifier.weight(1f)) { Text(title, color = TextPrimary, fontSize = 15.sp); Text(subtitle, color = TextSecondary, fontSize = 12.sp) }
        Icon(Icons.Default.ChevronRight, null, tint = TextSecondary)
    }
}

@Composable fun TopBar(title: String, back: () -> Unit) {
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        IconButton(onClick = back) { Icon(Icons.Default.ArrowBack, "Back", tint = TextPrimary) }
        Text(title, color = TextPrimary, fontSize = 24.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun VoiceScreen(state: MayaState, setState: (MayaState)->Unit, back: ()->Unit) {
    Column(Modifier.fillMaxSize().background(Bg).padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        TopBar("Voice", back)
        Spacer(Modifier.height(70.dp))
        MayaOrb(state)
        Spacer(Modifier.height(30.dp))
        Text("Voice control is the primary interface.", color = TextPrimary, fontSize = 18.sp, textAlign = TextAlign.Center)
        Spacer(Modifier.height(8.dp))
        Text("The implementation can connect this UI to the microphone, wake-word engine and Gemini task layer.", color = TextSecondary, textAlign = TextAlign.Center)
    }
}

@Composable
fun SecurityScreen(back: ()->Unit) {
    Column(Modifier.fillMaxSize().background(Bg).padding(22.dp)) {
        TopBar("Security", back)
        Spacer(Modifier.height(20.dp))
        SettingSection("Face verification") {
            Text("Maya can request camera permission and perform user verification only through transparent, permission-based Android APIs.", color = TextSecondary, fontSize = 14.sp)
            Spacer(Modifier.height(14.dp))
            Text("Camera: permission required", color = TextPrimary)
            Text("Microphone: permission required", color = TextPrimary)
        }
        SettingSection("Safety") {
            Text("No hidden camera or microphone access. No bypass of Android security, authentication, CAPTCHA or permissions.", color = TextSecondary, fontSize = 14.sp)
        }
    }
}

@Composable
fun HistoryScreen(back: ()->Unit) {
    val tasks = listOf("Opened browser", "Checked Downloads", "Created a project", "Organized files")
    Column(Modifier.fillMaxSize().background(Bg).padding(22.dp)) {
        TopBar("Task history", back)
        Spacer(Modifier.height(12.dp))
        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            items(tasks) { task ->
                Card(colors = CardDefaults.cardColors(containerColor = Card), shape = RoundedCornerShape(18.dp)) {
                    Row(Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.CheckCircle, null, tint = Color(0xFF8FE0A5))
                        Spacer(Modifier.width(14.dp)); Text(task, color = TextPrimary)
                    }
                }
            }
        }
    }
}

@Composable
fun AboutScreen(back: ()->Unit) {
    Column(Modifier.fillMaxSize().background(Bg).padding(22.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        TopBar("About Maya", back)
        Spacer(Modifier.height(50.dp)); MayaOrb(MayaState.Sleeping)
        Spacer(Modifier.height(28.dp)); Text("Maya", color = TextPrimary, fontSize = 30.sp, fontWeight = FontWeight.Bold)
        Text("Voice-first native Android assistant", color = TextSecondary)
        Spacer(Modifier.height(24.dp)); Text("The UI is designed so everyday interaction happens by voice. The main app is for setup, permissions and configuration.", color = TextSecondary, textAlign = TextAlign.Center)
    }
}
