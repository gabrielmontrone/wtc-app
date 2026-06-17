package br.com.fiap.wtcapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Percent
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import br.com.fiap.wtcapp.domain.model.AuditEvent
import br.com.fiap.wtcapp.domain.model.AuditSummary
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

// Distinct colors per action so the breakdown and event cards read consistently.
private val SuspiciousColor = Color(0xFFE5484D)
private val FailedLoginColor = Color(0xFFFFB020)
private val LoginColor = Color(0xFF30A46C)
private val MessageColor = Color(0xFF4C8DFF)
private val NewUserColor = Color(0xFF8E4EC6)
private val NewCustomerColor = Color(0xFF12A594)
private val DefaultActionColor = Color(0xFF8B8D98)
private val LowRiskColor = Color(0xFFF2C94C)

private fun riskColor(level: String): Color =
    when (level.uppercase()) {
        "HIGH" -> SuspiciousColor
        "MEDIUM" -> FailedLoginColor
        "LOW" -> LowRiskColor
        else -> DefaultActionColor
    }

private fun riskLabel(level: String): String =
    when (level.uppercase()) {
        "HIGH" -> "Alto"
        "MEDIUM" -> "Médio"
        "LOW" -> "Baixo"
        else -> level
    }

private fun actionColor(action: String): Color =
    when (action) {
        "SUSPICIOUS_MESSAGE" -> SuspiciousColor
        "LOGIN_FAILED" -> FailedLoginColor
        "LOGIN_SUCCESS" -> LoginColor
        "SEND_MESSAGE" -> MessageColor
        "CREATE_USER" -> NewUserColor
        "CREATE_CUSTOMER" -> NewCustomerColor
        else -> DefaultActionColor
    }

private fun actionLabel(action: String): String =
    when (action) {
        "SUSPICIOUS_MESSAGE" -> "Mensagem suspeita"
        "LOGIN_FAILED" -> "Login falho"
        "LOGIN_SUCCESS" -> "Login"
        "SEND_MESSAGE" -> "Mensagem enviada"
        "CREATE_USER" -> "Novo usuário"
        "CREATE_CUSTOMER" -> "Novo cliente"
        else -> action
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
                .padding(horizontal = 20.dp, vertical = 24.dp),
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
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    item { KpiGrid(state.summary) }
                    if (state.summary.riskDistribution.isNotEmpty()) {
                        item { RiskCard(state.summary) }
                    }
                    item { ByTypeCard(state.summary) }
                    if (state.summary.topUsers.isNotEmpty()) {
                        item { TopUsersCard(state.summary) }
                    }
                    item { RecentHeader(state.summary.totalEvents) }
                    items(state.events, key = { it.id }) { event ->
                        AuditEventCard(event)
                    }
                }
        }
    }
}

@Composable
private fun KpiGrid(summary: AuditSummary) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            KpiCard(
                modifier = Modifier.weight(1f),
                label = "Mensagens suspeitas",
                value = summary.suspiciousCount.toString(),
                accent = SuspiciousColor,
                icon = Icons.Filled.Warning,
            )
            KpiCard(
                modifier = Modifier.weight(1f),
                label = "Logins falhos",
                value = summary.failedLoginCount.toString(),
                accent = FailedLoginColor,
                icon = Icons.Filled.Lock,
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            KpiCard(
                modifier = Modifier.weight(1f),
                label = "Total de eventos",
                value = summary.totalEvents.toString(),
                accent = MessageColor,
                icon = Icons.Filled.Assessment,
            )
            KpiCard(
                modifier = Modifier.weight(1f),
                label = "% suspeitas",
                value = "${summary.suspiciousRatePercent}%",
                accent = if (summary.suspiciousRatePercent > 0) SuspiciousColor else LoginColor,
                icon = Icons.Filled.Percent,
            )
        }
    }
}

@Composable
private fun KpiCard(
    label: String,
    value: String,
    accent: Color,
    icon: ImageVector,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = accent.copy(alpha = 0.12f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Box(
                modifier =
                    Modifier
                        .size(34.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(accent.copy(alpha = 0.18f)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(icon, contentDescription = null, tint = accent, modifier = Modifier.size(20.dp))
            }
            Spacer(Modifier.height(12.dp))
            Text(value, fontSize = 28.sp, fontWeight = FontWeight.Bold, color = accent)
            Text(
                label,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun ByTypeCard(summary: AuditSummary) {
    BarBreakdownCard(
        title = "Eventos por tipo",
        icon = Icons.Filled.BarChart,
        items = summary.countsByAction,
        labelFor = ::actionLabel,
        colorFor = ::actionColor,
    )
}

@Composable
private fun RiskCard(summary: AuditSummary) {
    BarBreakdownCard(
        title = "Distribuição de risco (mensagens suspeitas)",
        icon = Icons.Filled.Warning,
        items = summary.riskDistribution,
        labelFor = ::riskLabel,
        colorFor = ::riskColor,
    )
}

@Composable
private fun BarBreakdownCard(
    title: String,
    icon: ImageVector,
    items: List<Pair<String, Int>>,
    labelFor: (String) -> String,
    colorFor: (String) -> Color,
) {
    SectionCard(title = title, icon = icon) {
        val maxCount = items.maxOfOrNull { it.second } ?: 1
        items.forEach { (key, count) ->
            val fraction = (count.toFloat() / maxCount).coerceIn(0.06f, 1f)
            val color = colorFor(key)
            Column(modifier = Modifier.padding(top = 12.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        labelFor(key),
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.weight(1f),
                    )
                    Text(count.toString(), fontSize = 13.sp, fontWeight = FontWeight.Bold, color = color)
                }
                Spacer(Modifier.height(6.dp))
                Box(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f)),
                ) {
                    Box(
                        modifier =
                            Modifier
                                .fillMaxWidth(fraction)
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(color),
                    )
                }
            }
        }
    }
}

@Composable
private fun TopUsersCard(summary: AuditSummary) {
    SectionCard(title = "Usuários mais ativos", icon = Icons.Filled.Group) {
        summary.topUsers.forEach { (email, count) ->
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    email,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f),
                )
                CountBadge(count)
            }
        }
    }
}

@Composable
private fun CountBadge(count: Int) {
    Box(
        modifier =
            Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.14f))
                .padding(horizontal = 10.dp, vertical = 3.dp),
    ) {
        Text(
            count.toString(),
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
        )
    }
}

@Composable
private fun SectionCard(
    title: String,
    icon: ImageVector,
    content: @Composable () -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(18.dp),
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                )
            }
            content()
        }
    }
}

@Composable
private fun RecentHeader(total: Int) {
    Column(modifier = Modifier.padding(top = 4.dp)) {
        Text(
            "Eventos recentes",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
        )
        Text(
            "Baseado nos $total eventos mais recentes",
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun AuditEventCard(event: AuditEvent) {
    val color = actionColor(event.action)
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    ) {
        Row(modifier = Modifier.fillMaxWidth().padding(14.dp)) {
            // Color rail keyed to the action type.
            Box(
                modifier =
                    Modifier
                        .width(4.dp)
                        .height(40.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(color),
            )
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = actionLabel(event.action),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = color,
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
}

@Preview(showBackground = true)
@Composable
private fun AuditoriaScreenPreview() {
    val events =
        listOf(
            AuditEvent("1", "SUSPICIOUS_MESSAGE", "ana@wtc.com", "Conversa 9 — risco HIGH [cartão]", "2026-06-16T12:00:00Z"),
            AuditEvent("2", "SEND_MESSAGE", "joao@wtc.com", "Mensagem enviada na conversa: 9", "2026-06-16T11:58:00Z"),
            AuditEvent("3", "LOGIN_FAILED", "ana@wtc.com", "Tentativa de login inválida", "2026-06-16T11:50:00Z"),
            AuditEvent("4", "LOGIN_SUCCESS", "joao@wtc.com", "Autenticado via JWT", "2026-06-16T11:40:00Z"),
            AuditEvent("5", "SEND_MESSAGE", "ana@wtc.com", "Mensagem enviada na conversa: 4", "2026-06-16T11:30:00Z"),
        )
    val summary = AuditSummary.from(events).copy(riskDistribution = listOf("HIGH" to 2, "MEDIUM" to 1))
    WTCTheme(darkTheme = true) {
        Surface {
            AuditoriaScreen(state = AuditoriaUiState(events = events, summary = summary))
        }
    }
}
