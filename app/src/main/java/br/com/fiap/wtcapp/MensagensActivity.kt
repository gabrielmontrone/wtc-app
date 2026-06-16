package br.com.fiap.wtcapp

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import br.com.fiap.wtcapp.domain.model.Campaign
import br.com.fiap.wtcapp.domain.model.ChatMessage
import br.com.fiap.wtcapp.ui.common.LaunchedErrorToast
import br.com.fiap.wtcapp.ui.mensagens.MensagensUiState
import br.com.fiap.wtcapp.ui.mensagens.MensagensViewModel
import br.com.fiap.wtcapp.ui.theme.WTCTheme
import br.com.fiap.wtcapp.ui.theme.WtcAppTheme
import coil.compose.AsyncImage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class MensagensActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WtcAppTheme {
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
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val photoPicker =
        rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri: Uri? ->
            if (uri != null) {
                scope.launch {
                    val payload =
                        withContext(Dispatchers.IO) {
                            val bytes = context.contentResolver.openInputStream(uri)?.use { it.readBytes() }
                            val type = context.contentResolver.getType(uri) ?: "image/jpeg"
                            bytes?.let { Triple("foto_${System.currentTimeMillis()}", type, it) }
                        }
                    payload?.let { (name, type, bytes) ->
                        viewModel.onPhotoPicked(name, type, bytes)
                    }
                }
            }
        }

    LaunchedErrorToast(uiState.errorMessage) { viewModel.onErrorShown() }

    MensagensScreen(
        state = uiState,
        onReplyChange = viewModel::onReplyChange,
        onSend = viewModel::send,
        onConfirmSend = viewModel::confirmSend,
        onDismissRiskWarning = viewModel::dismissRiskWarning,
        onCampaignSelected = viewModel::onCampaignSelected,
        onRemovePendingPhoto = viewModel::onRemovePendingPhoto,
        onPickPhoto = {
            photoPicker.launch(
                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly),
            )
        },
    )
}

@Composable
fun MensagensScreen(
    state: MensagensUiState,
    onReplyChange: (String) -> Unit,
    onSend: () -> Unit,
    onConfirmSend: () -> Unit,
    onDismissRiskWarning: () -> Unit,
    onCampaignSelected: (Campaign) -> Unit,
    onRemovePendingPhoto: () -> Unit,
    onPickPhoto: () -> Unit,
) {
    state.riskWarning?.let { flags ->
        RiskWarningDialog(flags = flags, onConfirm = onConfirmSend, onDismiss = onDismissRiskWarning)
    }

    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(24.dp),
    ) {
        Text(
            text = "Mensagens",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(bottom = 16.dp),
        )

        when {
            state.isLoading ->
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                ) { CircularProgressIndicator(color = MaterialTheme.colorScheme.primary) }
            state.messages.isEmpty() ->
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                ) {
                    Text(
                        "Nenhuma mensagem nesta conversa",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
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

        // Campaign quick-insert: shown when the reply starts with "/".
        if (state.campaignSuggestions.isNotEmpty()) {
            CampaignSuggestions(
                campaigns = state.campaignSuggestions,
                onCampaignSelected = onCampaignSelected,
            )
        }

        state.pendingImageUrl?.let { url ->
            PendingPhotoPreview(url = url, onRemove = onRemovePendingPhoto)
        }

        Spacer(modifier = Modifier.padding(top = 8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(onClick = onPickPhoto, enabled = !state.isUploading) {
                if (state.isUploading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = MaterialTheme.colorScheme.primary,
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Anexar foto",
                        tint = MaterialTheme.colorScheme.primary,
                    )
                }
            }
            OutlinedTextField(
                value = state.reply,
                onValueChange = onReplyChange,
                label = { Text("Responder (use / para campanhas)") },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp),
            )
            Spacer(modifier = Modifier.width(8.dp))
            Button(
                onClick = onSend,
                enabled = state.canSend,
                shape = RoundedCornerShape(12.dp),
                colors =
                    ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary,
                    ),
            ) {
                Text(if (state.isSending) "..." else "Enviar")
            }
        }
    }
}

@Composable
private fun CampaignSuggestions(
    campaigns: List<Campaign>,
    onCampaignSelected: (Campaign) -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    ) {
        LazyColumn(modifier = Modifier.heightIn(max = 180.dp)) {
            items(campaigns, key = { it.id }) { campaign ->
                Column(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .clickable { onCampaignSelected(campaign) }
                            .padding(12.dp),
                ) {
                    Text(
                        "/${campaign.name}",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    Text(
                        campaign.content,
                        fontSize = 12.sp,
                        maxLines = 1,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }
    }
}

@Composable
private fun PendingPhotoPreview(
    url: String,
    onRemove: () -> Unit,
) {
    Row(
        modifier = Modifier.padding(top = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        AsyncImage(
            model = url,
            contentDescription = "Foto anexada",
            contentScale = ContentScale.Crop,
            modifier =
                Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(8.dp)),
        )
        IconButton(onClick = onRemove) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Remover foto",
                tint = MaterialTheme.colorScheme.error,
            )
        }
    }
}

private fun riskFlagLabel(code: String): String =
    when (code) {
        "CPF" -> "CPF"
        "CNPJ" -> "CNPJ"
        "CARD" -> "Cartão de crédito"
        "SUSPICIOUS_LINK" -> "Link suspeito"
        else -> code
    }

@Composable
private fun RiskWarningDialog(
    flags: List<String>,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Dados sensíveis detectados") },
        text = {
            Column {
                Text("Esta mensagem parece conter:")
                flags.forEach { Text("• ${riskFlagLabel(it)}") }
                Spacer(modifier = Modifier.padding(top = 4.dp))
                Text("Deseja enviar mesmo assim?")
            }
        },
        confirmButton = { TextButton(onClick = onConfirm) { Text("Enviar mesmo assim") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } },
    )
}

@Composable
private fun RiskBadges(
    level: String,
    flags: List<String>,
) {
    val color = if (level == "HIGH") MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.tertiary
    Row(
        modifier = Modifier.padding(top = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        flags.forEach { code ->
            Surface(color = color.copy(alpha = 0.15f), shape = RoundedCornerShape(6.dp)) {
                Text(
                    "⚠ ${riskFlagLabel(code)}",
                    color = color,
                    fontSize = 11.sp,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                )
            }
        }
    }
}

@Composable
fun MensagemCard(message: ChatMessage) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            message.subject?.takeIf { it.isNotBlank() }?.let {
                Text(
                    it,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                )
            }
            message.imageUrl?.takeIf { it.isNotBlank() }?.let { url ->
                AsyncImage(
                    model = url,
                    contentDescription = "Foto da mensagem",
                    contentScale = ContentScale.Fit,
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .heightIn(max = 220.dp)
                            .clip(RoundedCornerShape(8.dp)),
                )
                Spacer(modifier = Modifier.padding(top = 4.dp))
            }
            message.content.takeIf { it.isNotBlank() }?.let {
                Text(it, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurface)
            }
            if ((message.riskLevel == "MEDIUM" || message.riskLevel == "HIGH") && message.riskFlags.isNotEmpty()) {
                RiskBadges(message.riskLevel, message.riskFlags)
            }
            message.senderRole?.let {
                Text("De: $it", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun MensagensScreenPreview() {
    WTCTheme(darkTheme = true) {
        Surface {
            MensagensScreen(
                state =
                    MensagensUiState(
                        messages =
                            listOf(
                                ChatMessage("1", "Promoção", "Aproveite 30% de desconto!", "SENT", "OPERATOR"),
                                ChatMessage("2", null, "Obrigado!", "SENT", "CUSTOMER"),
                                ChatMessage(
                                    id = "3",
                                    subject = null,
                                    content = "meu cartão é 4111 1111 1111 1111",
                                    status = "SENT",
                                    senderRole = "CUSTOMER",
                                    riskLevel = "HIGH",
                                    riskFlags = listOf("CARD"),
                                ),
                            ),
                    ),
                onReplyChange = {},
                onSend = {},
                onConfirmSend = {},
                onDismissRiskWarning = {},
                onCampaignSelected = {},
                onRemovePendingPhoto = {},
                onPickPhoto = {},
            )
        }
    }
}
