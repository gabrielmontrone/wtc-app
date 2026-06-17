package br.com.fiap.wtcapp.ui.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

/**
 * A gear button that opens a dialog for viewing/changing the backend URL at runtime.
 * Kept separate from the static welcome layout so previews don't need Hilt.
 */
@Composable
fun ServerSettingsButton(
    modifier: Modifier = Modifier,
    viewModel: ServerSettingsViewModel = hiltViewModel(),
) {
    var showDialog by rememberSaveable { mutableStateOf(false) }

    IconButton(onClick = { showDialog = true }, modifier = modifier) {
        Icon(
            imageVector = Icons.Filled.Settings,
            contentDescription = "Configurar servidor",
            tint = MaterialTheme.colorScheme.onBackground,
        )
    }

    if (showDialog) {
        val current by viewModel.baseUrl.collectAsStateWithLifecycle()
        ServerSettingsDialog(
            initialUrl = current,
            defaultUrl = viewModel.defaultBaseUrl,
            onSave = {
                viewModel.save(it)
                showDialog = false
            },
            onDismiss = { showDialog = false },
        )
    }
}

@Composable
private fun ServerSettingsDialog(
    initialUrl: String,
    defaultUrl: String,
    onSave: (String) -> Unit,
    onDismiss: () -> Unit,
) {
    var text by remember(initialUrl) { mutableStateOf(initialUrl) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Servidor da API") },
        text = {
            Column {
                Text(
                    text =
                        "Endereço do backend usado pelo app.\n" +
                            "• Emulador: http://10.0.2.2:8080/\n" +
                            "• Celular físico (mesma Wi-Fi): http://<IP-do-PC>:8080/",
                    style = MaterialTheme.typography.bodySmall,
                )
                Spacer(Modifier.height(12.dp))
                OutlinedTextField(
                    value = text,
                    onValueChange = { text = it },
                    label = { Text("URL") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )
                Spacer(Modifier.height(4.dp))
                TextButton(onClick = { text = defaultUrl }) {
                    Text("Restaurar padrão")
                }
            }
        },
        confirmButton = {
            TextButton(onClick = { onSave(text) }) { Text("Salvar") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        },
    )
}
