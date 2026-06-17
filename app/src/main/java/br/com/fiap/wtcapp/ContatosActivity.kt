package br.com.fiap.wtcapp

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import br.com.fiap.wtcapp.domain.model.Customer
import br.com.fiap.wtcapp.ui.common.LaunchedErrorToast
import br.com.fiap.wtcapp.ui.contatos.AddContactForm
import br.com.fiap.wtcapp.ui.contatos.ContatoFiltro
import br.com.fiap.wtcapp.ui.contatos.ContatosUiState
import br.com.fiap.wtcapp.ui.contatos.ContatosViewModel
import br.com.fiap.wtcapp.ui.theme.WTCTheme
import br.com.fiap.wtcapp.ui.theme.WtcAppTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ContatosActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WtcAppTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    ContatosRoute(
                        onContactClick = { customerId ->
                            startActivity(
                                Intent(this, ConversasActivity::class.java)
                                    .putExtra(ConversasActivity.EXTRA_CUSTOMER_ID, customerId),
                            )
                        },
                    )
                }
            }
        }
    }
}

@Composable
fun ContatosRoute(
    onContactClick: (String) -> Unit,
    viewModel: ContatosViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedErrorToast(uiState.errorMessage) { viewModel.onErrorShown() }

    ContatosScreen(
        state = uiState,
        onSearchChange = viewModel::onSearchChange,
        onFilterChange = viewModel::onFilterChange,
        onContactClick = onContactClick,
        onAddContactClick = viewModel::onAddContactClick,
    )

    uiState.addForm?.let { form ->
        AddContactDialog(
            form = form,
            onNameChange = viewModel::onFormNameChange,
            onDocumentChange = viewModel::onFormDocumentChange,
            onEmailChange = viewModel::onFormEmailChange,
            onVipChange = viewModel::onFormVipChange,
            onLoyaltyChange = viewModel::onFormLoyaltyChange,
            onActiveChange = viewModel::onFormActiveChange,
            onSave = viewModel::saveContact,
            onDismiss = viewModel::onAddContactDismiss,
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ContatosScreen(
    state: ContatosUiState,
    onSearchChange: (String) -> Unit,
    onFilterChange: (ContatoFiltro) -> Unit,
    onContactClick: (String) -> Unit,
    onAddContactClick: () -> Unit,
) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddContactClick,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
            ) {
                Icon(Icons.Default.Add, contentDescription = "Novo contato")
            }
        },
    ) { innerPadding ->
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .background(MaterialTheme.colorScheme.background)
                    .padding(24.dp),
        ) {
            Text(
                text = "Contatos",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(bottom = 16.dp),
            )

            OutlinedTextField(
                value = state.search,
                onValueChange = onSearchChange,
                label = { Text("Buscar por nome ou documento") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
            )

            Spacer(modifier = Modifier.height(16.dp))

            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                ContatoFiltro.entries.forEach { filtro ->
                    ContatoFilterChip(
                        label = filtro.label,
                        selected = state.filter == filtro,
                        onClick = { onFilterChange(filtro) },
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            when {
                state.isLoading -> CenteredContent { CircularProgressIndicator(color = MaterialTheme.colorScheme.primary) }
                state.visibleCustomers.isEmpty() ->
                    CenteredContent {
                        Text("Nenhum contato encontrado", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                else ->
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        items(state.visibleCustomers, key = { it.id }) { customer ->
                            ContactCard(customer = customer, onClick = { onContactClick(customer.id) })
                        }
                    }
            }
        }
    }
}

@Composable
fun AddContactDialog(
    form: AddContactForm,
    onNameChange: (String) -> Unit,
    onDocumentChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onVipChange: (Boolean) -> Unit,
    onLoyaltyChange: (Boolean) -> Unit,
    onActiveChange: (Boolean) -> Unit,
    onSave: () -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = { if (!form.isSaving) onDismiss() },
        title = { Text("Novo contato") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = form.name,
                    onValueChange = onNameChange,
                    label = { Text("Nome") },
                    singleLine = true,
                    enabled = !form.isSaving,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                )
                OutlinedTextField(
                    value = form.document,
                    onValueChange = { onDocumentChange(it.filter(Char::isDigit)) },
                    label = { Text("Documento (CPF/CNPJ)") },
                    singleLine = true,
                    enabled = !form.isSaving,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                )
                OutlinedTextField(
                    value = form.email,
                    onValueChange = onEmailChange,
                    label = { Text("E-mail do cliente (opcional)") },
                    supportingText = { Text("Vincula o contato a uma conta; ela verá as mensagens ao entrar.") },
                    singleLine = true,
                    enabled = !form.isSaving,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                )
                ContactSwitchRow("VIP", form.vip, !form.isSaving, onVipChange)
                ContactSwitchRow("Fidelidade", form.loyalty, !form.isSaving, onLoyaltyChange)
                ContactSwitchRow("Ativo", form.active, !form.isSaving, onActiveChange)
            }
        },
        confirmButton = {
            Button(onClick = onSave, enabled = !form.isSaving) {
                Text(if (form.isSaving) "Salvando..." else "Salvar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss, enabled = !form.isSaving) { Text("Cancelar") }
        },
    )
}

@Composable
private fun ContactSwitchRow(
    label: String,
    checked: Boolean,
    enabled: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(label, color = MaterialTheme.colorScheme.onSurface)
        Switch(checked = checked, onCheckedChange = onCheckedChange, enabled = enabled)
    }
}

@Composable
private fun CenteredContent(content: @Composable () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        content()
    }
}

@Composable
fun ContatoFilterChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    AssistChip(
        onClick = onClick,
        label = { Text(label) },
        shape = RoundedCornerShape(50),
        colors =
            AssistChipDefaults.assistChipColors(
                containerColor =
                    if (selected) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.surfaceVariant
                    },
                labelColor =
                    if (selected) {
                        MaterialTheme.colorScheme.onPrimary
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    },
            ),
    )
}

@Composable
fun ContactCard(
    customer: Customer,
    onClick: () -> Unit,
) {
    Card(
        modifier =
            Modifier
                .fillMaxWidth()
                .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                customer.name,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Text(
                "Documento: ${customer.document}",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = if (customer.active) "Ativo" else "Inativo",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                if (customer.vip) {
                    Text("VIP", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                if (customer.loyalty) {
                    Text("Fidelidade", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ContatosScreenPreview() {
    WTCTheme(darkTheme = true) {
        Surface {
            ContatosScreen(
                state =
                    ContatosUiState(
                        customers =
                            listOf(
                                Customer("1", "Ana Souza", "12345678901", vip = true, loyalty = false, active = true),
                                Customer("2", "Carlos Lima", "98765432100", vip = false, loyalty = true, active = false),
                            ),
                    ),
                onSearchChange = {},
                onFilterChange = {},
                onContactClick = {},
                onAddContactClick = {},
            )
        }
    }
}
