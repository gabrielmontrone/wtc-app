package br.com.fiap.wtcapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import br.com.fiap.wtcapp.domain.model.Segment
import br.com.fiap.wtcapp.ui.common.LaunchedErrorToast
import br.com.fiap.wtcapp.ui.segmentos.SegmentosUiState
import br.com.fiap.wtcapp.ui.segmentos.SegmentosViewModel
import br.com.fiap.wtcapp.ui.theme.WTCTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SegmentosActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WTCTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    SegmentosRoute()
                }
            }
        }
    }
}

@Composable
fun SegmentosRoute(viewModel: SegmentosViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedErrorToast(uiState.errorMessage) { viewModel.onErrorShown() }

    SegmentosScreen(
        state = uiState,
        onSearchChange = viewModel::onSearchChange,
    )
}

@Composable
fun SegmentosScreen(
    state: SegmentosUiState,
    onSearchChange: (String) -> Unit,
) {
    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(24.dp),
    ) {
        Text(
            text = "Segmentos",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1976D2),
            modifier = Modifier.padding(bottom = 16.dp),
        )

        OutlinedTextField(
            value = state.search,
            onValueChange = onSearchChange,
            label = { Text("Buscar segmento") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
        )

        Spacer(modifier = Modifier.height(24.dp))

        when {
            state.isLoading ->
                Centered { CircularProgressIndicator(color = Color(0xFF1976D2)) }
            state.visibleSegments.isEmpty() ->
                Centered { Text("Nenhum segmento encontrado", color = Color(0xFF555555)) }
            else ->
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    items(state.visibleSegments, key = { it.id }) { segment ->
                        SegmentoCard(segment)
                    }
                }
        }
    }
}

@Composable
private fun Centered(content: @Composable () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        content()
    }
}

@Composable
fun SegmentoCard(segment: Segment) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(segment.name, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1976D2))
            val criteria =
                buildList {
                    if (segment.vip) add("VIP")
                    segment.minScore?.let { add("Score ≥ $it") }
                    segment.minLoyalty?.let { add("Fidelidade ≥ $it") }
                    add(if (segment.active) "Ativo" else "Inativo")
                }.joinToString(" · ")
            Text(criteria, fontSize = 14.sp, color = Color(0xFF555555))
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SegmentosScreenPreview() {
    WTCTheme {
        SegmentosScreen(
            state =
                SegmentosUiState(
                    segments =
                        listOf(
                            Segment("1", "VIP", vip = true, active = true, minScore = 80, minLoyalty = null),
                            Segment("2", "Recentes", vip = false, active = true, minScore = null, minLoyalty = 1),
                        ),
                ),
            onSearchChange = {},
        )
    }
}
