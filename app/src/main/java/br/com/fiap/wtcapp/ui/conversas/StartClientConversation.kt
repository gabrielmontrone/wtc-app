package br.com.fiap.wtcapp.ui.conversas

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import br.com.fiap.wtcapp.ui.common.LaunchedErrorToast

/**
 * Operator action: a mail button that opens a dialog to start a conversation with a client by
 * email, then hands the opened conversation id back to the caller to navigate into it.
 */
@Composable
fun StartClientConversationButton(
    onOpenConversation: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: StartClientConversationViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var showDialog by rememberSaveable { mutableStateOf(false) }

    LaunchedErrorToast(state.errorMessage) { viewModel.onErrorShown() }

    LaunchedEffect(state.openedConversationId) {
        state.openedConversationId?.let { id ->
            showDialog = false
            viewModel.onConversationOpened()
            onOpenConversation(id)
        }
    }

    IconButton(onClick = { showDialog = true }, modifier = modifier) {
        Icon(
            imageVector = Icons.Filled.MailOutline,
            contentDescription = "Conversar com cliente por e-mail",
            tint = MaterialTheme.colorScheme.primary,
        )
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { if (!state.isLoading) showDialog = false },
            title = { Text("Conversar com um cliente") },
            text = {
                Column {
                    Text(
                        "Informe o e-mail da conta do cliente. A conversa abre para você e fica " +
                            "visível para o cliente quando ele entrar no app.",
                        style = MaterialTheme.typography.bodySmall,
                    )
                    Spacer(Modifier.height(12.dp))
                    OutlinedTextField(
                        value = state.email,
                        onValueChange = viewModel::onEmailChange,
                        label = { Text("E-mail do cliente") },
                        singleLine = true,
                        enabled = !state.isLoading,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            },
            confirmButton = {
                Button(onClick = viewModel::start, enabled = !state.isLoading && state.email.isNotBlank()) {
                    Text(if (state.isLoading) "Abrindo..." else "Abrir conversa")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }, enabled = !state.isLoading) { Text("Cancelar") }
            },
        )
    }
}
