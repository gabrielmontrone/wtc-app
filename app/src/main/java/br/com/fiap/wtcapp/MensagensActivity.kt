package br.com.fiap.wtcapp

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
import br.com.fiap.wtcapp.domain.model.ChatMessage
import br.com.fiap.wtcapp.ui.common.LaunchedErrorToast
import br.com.fiap.wtcapp.ui.mensagens.MensagensUiState
import br.com.fiap.wtcapp.ui.mensagens.MensagensViewModel
import br.com.fiap.wtcapp.ui.theme.WTCTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MensagensActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WTCTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    MensagensRoute()
                }
            }
        }
    }

    companion object {
        const val EXTRA_CONVERSATION_ID = "conversationId"
    }
}

@Composable
fun MensagensRoute(viewModel: MensagensViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedErrorToast(uiState.errorMessage) { viewModel.onErrorShown() }

    MensagensScreen(
        state = uiState,
        onReplyChange = viewModel::onReplyChange,
        onSend = viewModel::send,
    )
}

@Composable
fun MensagensScreen(
    state: MensagensUiState,
    onReplyChange: (String) -> Unit,
    onSend: () -> Unit,
) {
    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(24.dp),
    ) {
        Text(
            text = "Mensagens",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1976D2),
            modifier = Modifier.padding(bottom = 16.dp),
        )

        when {
            state.isLoading ->
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                ) { CircularProgressIndicator(color = Color(0xFF1976D2)) }
            state.messages.isEmpty() ->
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                ) { Text("Nenhuma mensagem nesta conversa", color = Color(0xFF555555)) }
            else ->
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    items(state.messages, key = { it.id }) { message ->
                        MensagemCard(message)
                    }
                }
        }

        Spacer(modifier = Modifier.padding(top = 8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            OutlinedTextField(
                value = state.reply,
                onValueChange = onReplyChange,
                label = { Text("Responder") },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp),
            )
            Spacer(modifier = Modifier.width(8.dp))
            Button(
                onClick = onSend,
                enabled = state.canSend,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1565C0)),
            ) {
                Text(if (state.isSending) "..." else "Enviar", color = Color.White)
            }
        }
    }
}

@Composable
fun MensagemCard(message: ChatMessage) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD)),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            message.subject?.takeIf { it.isNotBlank() }?.let {
                Text(it, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1976D2))
            }
            Text(message.content, fontSize = 14.sp, color = Color(0xFF555555))
            message.senderRole?.let {
                Text("De: $it", fontSize = 12.sp, color = Color(0xFF999999))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun MensagensScreenPreview() {
    WTCTheme {
        MensagensScreen(
            state =
                MensagensUiState(
                    messages =
                        listOf(
                            ChatMessage("1", "Promoção", "Aproveite 30% de desconto!", "SENT", "OPERATOR"),
                            ChatMessage("2", null, "Obrigado!", "SENT", "CUSTOMER"),
                        ),
                ),
            onReplyChange = {},
            onSend = {},
        )
    }
}
