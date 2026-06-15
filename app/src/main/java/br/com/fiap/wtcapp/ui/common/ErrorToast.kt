package br.com.fiap.wtcapp.ui.common

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext

/**
 * Shows [message] as a toast once whenever it becomes non-null, then calls [onShown]
 * so the ViewModel can clear it. Shared by every Route to surface one-off errors.
 */
@Composable
fun LaunchedErrorToast(
    message: String?,
    onShown: () -> Unit,
) {
    val context = LocalContext.current
    LaunchedEffect(message) {
        message?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            onShown()
        }
    }
}
