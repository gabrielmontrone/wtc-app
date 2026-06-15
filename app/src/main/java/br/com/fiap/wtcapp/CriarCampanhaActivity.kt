package br.com.fiap.wtcapp

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import br.com.fiap.wtcapp.ui.criarcampanha.CriarCampanhaUiState
import br.com.fiap.wtcapp.ui.criarcampanha.CriarCampanhaViewModel
import br.com.fiap.wtcapp.ui.theme.WTCTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CriarCampanhaActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WTCTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    CriarCampanhaRoute(
                        onCreated = {
                            startActivity(Intent(this, CampanhasActivity::class.java))
                            finish()
                        },
                    )
                }
            }
        }
    }
}

@Composable
fun CriarCampanhaRoute(
    onCreated: () -> Unit,
    viewModel: CriarCampanhaViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedErrorToast(uiState.errorMessage) { viewModel.onErrorShown() }
    LaunchedEffect(uiState.isCreated) {
        if (uiState.isCreated) onCreated()
    }

    CriarCampanhaScreen(
        state = uiState,
        onTitleChange = viewModel::onTitleChange,
        onMessageChange = viewModel::onMessageChange,
        onSegmentSelect = viewModel::onSegmentSelect,
        onSubmit = viewModel::submit,
    )
}

@Composable
fun CriarCampanhaScreen(
    state: CriarCampanhaUiState,
    onTitleChange: (String) -> Unit,
    onMessageChange: (String) -> Unit,
    onSegmentSelect: (String) -> Unit,
    onSubmit: () -> Unit,
) {
    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(24.dp),
    ) {
        Text(
            text = "Nova Campanha",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1976D2),
            modifier = Modifier.padding(bottom = 16.dp),
        )

        OutlinedTextField(
            value = state.title,
            onValueChange = onTitleChange,
            label = { Text("Título da campanha") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = state.message,
            onValueChange = onMessageChange,
            label = { Text("Mensagem") },
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(150.dp),
            shape = RoundedCornerShape(12.dp),
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text("Segmento", fontWeight = FontWeight.SemiBold, color = Color(0xFF555555))
        Spacer(modifier = Modifier.height(8.dp))

        SegmentPicker(state = state, onSegmentSelect = onSegmentSelect)

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onSubmit,
            enabled = state.canSubmit,
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(50.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1565C0)),
        ) {
            Text(
                text = if (state.isSubmitting) "Enviando..." else "Enviar Campanha",
                color = Color.White,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}

@Composable
private fun SegmentPicker(
    state: CriarCampanhaUiState,
    onSegmentSelect: (String) -> Unit,
) {
    when {
        state.isLoadingSegments ->
            Text("Carregando segmentos...", fontSize = 14.sp, color = Color(0xFF555555))
        state.segments.isEmpty() ->
            Text("Nenhum segmento encontrado", fontSize = 14.sp, color = Color(0xFF555555))
        else ->
            state.segments.forEach { segment ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clickable { onSegmentSelect(segment.id) },
                ) {
                    RadioButton(
                        selected = state.selectedSegmentId == segment.id,
                        onClick = { onSegmentSelect(segment.id) },
                    )
                    Text(text = segment.name, fontSize = 14.sp, color = Color(0xFF333333))
                }
            }
    }
}

@Preview(showBackground = true)
@Composable
private fun CriarCampanhaScreenPreview() {
    WTCTheme {
        CriarCampanhaScreen(
            state =
                CriarCampanhaUiState(
                    title = "Black Friday",
                    isLoadingSegments = false,
                    segments =
                        listOf(
                            Segment("1", "VIP", vip = true, active = true, minScore = null, minLoyalty = null),
                        ),
                    selectedSegmentId = "1",
                ),
            onTitleChange = {},
            onMessageChange = {},
            onSegmentSelect = {},
            onSubmit = {},
        )
    }
}
