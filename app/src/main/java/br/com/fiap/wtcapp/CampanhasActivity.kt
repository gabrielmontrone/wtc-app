package br.com.fiap.wtcapp

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import br.com.fiap.wtcapp.domain.model.Campaign
import br.com.fiap.wtcapp.ui.campanhas.CampanhasUiState
import br.com.fiap.wtcapp.ui.campanhas.CampanhasViewModel
import br.com.fiap.wtcapp.ui.common.LaunchedErrorToast
import br.com.fiap.wtcapp.ui.theme.WTCTheme
import br.com.fiap.wtcapp.ui.theme.WtcAppTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CampanhasActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WtcAppTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    CampanhasRoute(
                        onCreateCampaign = {
                            startActivity(Intent(this, CriarCampanhaActivity::class.java))
                        },
                    )
                }
            }
        }
    }
}

@Composable
fun CampanhasRoute(
    onCreateCampaign: () -> Unit,
    viewModel: CampanhasViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedErrorToast(uiState.errorMessage) { viewModel.onErrorShown() }

    CampanhasScreen(state = uiState, onCreateCampaign = onCreateCampaign)
}

@Composable
fun CampanhasScreen(
    state: CampanhasUiState,
    onCreateCampaign: () -> Unit,
) {
    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(24.dp),
    ) {
        Text(
            text = "Campanhas",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(bottom = 16.dp),
        )

        MetricGrid(state)

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Campanhas Recentes",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(bottom = 8.dp),
        )

        when {
            state.isLoading ->
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                ) { CircularProgressIndicator(color = MaterialTheme.colorScheme.primary) }
            state.campaigns.isEmpty() ->
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                ) { Text("Nenhuma campanha ainda", color = MaterialTheme.colorScheme.onSurfaceVariant) }
            else ->
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    items(state.campaigns, key = { it.id }) { campaign ->
                        CampanhaCard(campaign)
                    }
                }
        }

        Button(
            onClick = onCreateCampaign,
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(52.dp),
            shape = RoundedCornerShape(12.dp),
            colors =
                ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                ),
        ) {
            Text("Criar Nova Campanha", fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun MetricGrid(state: CampanhasUiState) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            MetricCard("📣", "Campanhas", state.totalCampaigns.toString())
            MetricCard("📨", "Envios", state.totalSends.toString())
        }
        Spacer(modifier = Modifier.height(12.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            MetricCard("✅", "Sucesso", "${state.successRate}%")
            MetricCard("📥", "Respostas", state.totalResponses.toString())
        }
    }
}

@Composable
fun MetricCard(
    emoji: String,
    label: String,
    value: String,
) {
    Card(
        modifier =
            Modifier
                .width(160.dp)
                .height(80.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    ) {
        Row(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(emoji, fontSize = 24.sp)
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    value,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Text(label, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Composable
fun CampanhaCard(campaign: Campaign) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                campaign.name,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Text(
                "Status: ${campaign.status}",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                text = "Envios: ${campaign.totalSends} · Respostas: ${campaign.responseCount}",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun CampanhasScreenPreview() {
    WTCTheme(darkTheme = true) {
        Surface {
            CampanhasScreen(
                state =
                    CampanhasUiState(
                        campaigns =
                            listOf(
                                Campaign("1", "Black Friday", null, "MESSAGE", "SENT", 120, 110, 18),
                                Campaign("2", "Novidades", null, "MESSAGE", "DRAFT", 0, 0, 0),
                            ),
                    ),
                onCreateCampaign = {},
            )
        }
    }
}
