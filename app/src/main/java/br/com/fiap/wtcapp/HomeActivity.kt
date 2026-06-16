package br.com.fiap.wtcapp

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import br.com.fiap.wtcapp.domain.model.ThemeMode
import br.com.fiap.wtcapp.ui.theme.ThemeViewModel
import br.com.fiap.wtcapp.ui.theme.WTCTheme
import br.com.fiap.wtcapp.ui.theme.WtcAppTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WtcAppTheme {
                val themeViewModel: ThemeViewModel = hiltViewModel()
                val themeMode by themeViewModel.themeMode.collectAsStateWithLifecycle()
                Surface(modifier = Modifier.fillMaxSize()) {
                    HomeScreen(
                        onOpenContatos = { open(ContatosActivity::class.java) },
                        onOpenCampanhas = { open(CampanhasActivity::class.java) },
                        onOpenSegmentos = { open(SegmentosActivity::class.java) },
                        onOpenAuditoria = { open(AuditoriaActivity::class.java) },
                        themeMode = themeMode,
                        onThemeModeChange = themeViewModel::setThemeMode,
                    )
                }
            }
        }
    }

    private fun open(target: Class<out ComponentActivity>) {
        startActivity(Intent(this, target))
    }
}

@Composable
fun HomeScreen(
    onOpenContatos: () -> Unit,
    onOpenCampanhas: () -> Unit,
    onOpenSegmentos: () -> Unit,
    onOpenAuditoria: () -> Unit,
    themeMode: ThemeMode,
    onThemeModeChange: (ThemeMode) -> Unit,
) {
    var showThemeDialog by remember { mutableStateOf(false) }

    Box(
        modifier =
            Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(24.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "WTC",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground,
                )
                IconButton(onClick = { showThemeDialog = true }) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Tema",
                        tint = MaterialTheme.colorScheme.onBackground,
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Bem-vindo! Gerencie sua comunicação com clientes de forma inteligente.",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            Spacer(modifier = Modifier.height(32.dp))

            Column(modifier = Modifier.fillMaxWidth()) {
                MenuOption(
                    title = "Contatos",
                    description = "CRM, conversas e histórico de mensagens",
                    emoji = "📇",
                    onClick = onOpenContatos,
                )
                Spacer(modifier = Modifier.height(12.dp))
                MenuOption(
                    title = "Campanhas",
                    description = "Envios rápidos e segmentados com métricas",
                    emoji = "🚀",
                    onClick = onOpenCampanhas,
                )
                Spacer(modifier = Modifier.height(12.dp))
                MenuOption(
                    title = "Segmentos",
                    description = "Agrupamentos por tag, score e status",
                    emoji = "🧩",
                    onClick = onOpenSegmentos,
                )
                Spacer(modifier = Modifier.height(12.dp))
                MenuOption(
                    title = "Auditoria",
                    description = "Trilha de ações e eventos de compliance",
                    emoji = "🛡️",
                    onClick = onOpenAuditoria,
                )
            }
        }

        Text(
            text = "© 2025 WTC. Todos os direitos reservados.",
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier =
                Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 12.dp),
        )
    }

    if (showThemeDialog) {
        ThemePickerDialog(
            current = themeMode,
            onSelect = {
                onThemeModeChange(it)
                showThemeDialog = false
            },
            onDismiss = { showThemeDialog = false },
        )
    }
}

@Composable
fun MenuOption(
    title: String,
    description: String,
    emoji: String,
    onClick: () -> Unit,
) {
    Card(
        modifier =
            Modifier
                .fillMaxWidth()
                .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    ) {
        Row(
            modifier =
                Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier =
                    Modifier
                        .size(48.dp)
                        .background(MaterialTheme.colorScheme.primary, shape = CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Text(text = emoji, fontSize = 20.sp)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Text(
                    text = description,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Composable
private fun ThemePickerDialog(
    current: ThemeMode,
    onSelect: (ThemeMode) -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("Fechar") }
        },
        title = { Text("Tema") },
        text = {
            Column {
                ThemeOptionRow("Sistema", ThemeMode.SYSTEM, current, onSelect)
                ThemeOptionRow("Claro", ThemeMode.LIGHT, current, onSelect)
                ThemeOptionRow("Escuro", ThemeMode.DARK, current, onSelect)
            }
        },
    )
}

@Composable
private fun ThemeOptionRow(
    label: String,
    mode: ThemeMode,
    current: ThemeMode,
    onSelect: (ThemeMode) -> Unit,
) {
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .clickable { onSelect(mode) }
                .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        RadioButton(selected = current == mode, onClick = { onSelect(mode) })
        Spacer(modifier = Modifier.width(8.dp))
        Text(label)
    }
}

@Preview(showBackground = true)
@Composable
private fun HomeScreenPreview() {
    WTCTheme(darkTheme = true) {
        HomeScreen(
            onOpenContatos = {},
            onOpenCampanhas = {},
            onOpenSegmentos = {},
            onOpenAuditoria = {},
            themeMode = ThemeMode.SYSTEM,
            onThemeModeChange = {},
        )
    }
}
