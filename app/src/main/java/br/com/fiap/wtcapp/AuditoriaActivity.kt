package br.com.fiap.wtcapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import br.com.fiap.wtcapp.domain.model.AuditEvent
import br.com.fiap.wtcapp.ui.auditoria.AuditoriaUiState
import br.com.fiap.wtcapp.ui.auditoria.AuditoriaViewModel
import br.com.fiap.wtcapp.ui.common.LaunchedErrorToast
import br.com.fiap.wtcapp.ui.theme.WTCTheme
import br.com.fiap.wtcapp.ui.theme.WtcAppTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AuditoriaActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WtcAppTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    AuditoriaRoute()
                }
            }
        }
    }
}

@Composable
fun AuditoriaRoute(viewModel: AuditoriaViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedErrorToast(uiState.errorMessage) { viewModel.onErrorShown() }

    AuditoriaScreen(state = uiState)
}

@Composable
fun AuditoriaScreen(state: AuditoriaUiState) {
    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(24.dp),
    ) {
        Text(
            text = "Auditoria",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
        )
        Text(
            text = "Trilha de ações e eventos de compliance",
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 16.dp),
        )

        when {
            state.isLoading ->
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                ) { CircularProgressIndicator(color = MaterialTheme.colorScheme.primary) }
            state.events.isEmpty() ->
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                ) {
                    Text(
                        "Nenhum evento de auditoria registrado",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            else ->
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    items(state.events, key = { it.id }) { event ->
                        AuditEventCard(event)
                    }
                }
        }
    }
}

@Composable
private fun AuditEventCard(event: AuditEvent) {
    val suspicious = event.action == "SUSPICIOUS_MESSAGE"
    val accent = if (suspicious) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(
                text = if (suspicious) "⚠ ${event.action}" else event.action,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = accent,
            )
            event.details?.takeIf { it.isNotBlank() }?.let {
                Text(it, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurface)
            }
            event.userEmail?.takeIf { it.isNotBlank() }?.let {
                Text("Por: $it", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Text(event.timestamp, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun AuditoriaScreenPreview() {
    WTCTheme(darkTheme = true) {
        Surface {
            AuditoriaScreen(
                state =
                    AuditoriaUiState(
                        events =
                            listOf(
                                AuditEvent(
                                    id = "1",
                                    action = "SUSPICIOUS_MESSAGE",
                                    userEmail = "ana@wtc.com",
                                    details = "Conversa 9 — risco HIGH [Possível número de cartão]",
                                    timestamp = "2026-06-16T12:00:00Z",
                                ),
                                AuditEvent(
                                    id = "2",
                                    action = "SEND_MESSAGE",
                                    userEmail = "joao@wtc.com",
                                    details = "Mensagem enviada na conversa: 9",
                                    timestamp = "2026-06-16T11:58:00Z",
                                ),
                            ),
                    ),
            )
        }
    }
}
