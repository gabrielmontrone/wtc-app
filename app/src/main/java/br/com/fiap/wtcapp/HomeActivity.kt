package br.com.fiap.wtcapp

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.com.fiap.wtcapp.ui.theme.WTCTheme

class HomeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WTCTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    HomeScreen(
                        onOpenContatos = { open(ContatosActivity::class.java) },
                        onOpenCampanhas = { open(CampanhasActivity::class.java) },
                        onOpenSegmentos = { open(SegmentosActivity::class.java) },
                    )
                }
            }
        }
    }

    private fun open(target: Class<out ComponentActivity>) {
        startActivity(Intent(this, target))
    }
}

@Composable
fun HomeScreen(
    onOpenContatos: () -> Unit,
    onOpenCampanhas: () -> Unit,
    onOpenSegmentos: () -> Unit,
) {
    Box(
        modifier =
            Modifier
                .fillMaxSize()
                .background(Color.White),
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(24.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(text = "WTC", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1976D2))
                IconButton(onClick = { }) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Configurações",
                        tint = Color(0xFF1976D2),
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Bem-vindo! Gerencie sua comunicação com clientes de forma inteligente.",
                fontSize = 14.sp,
                color = Color(0xFF555555),
            )

            Spacer(modifier = Modifier.height(32.dp))

            Column(modifier = Modifier.fillMaxWidth()) {
                MenuOption(
                    title = "Contatos",
                    description = "CRM, conversas e histórico de mensagens",
                    emoji = "📇",
                    onClick = onOpenContatos,
                )
                Spacer(modifier = Modifier.height(16.dp))
                MenuOption(
                    title = "Campanhas",
                    description = "Envios rápidos e segmentados com métricas",
                    emoji = "🚀",
                    onClick = onOpenCampanhas,
                )
                Spacer(modifier = Modifier.height(16.dp))
                MenuOption(
                    title = "Segmentos",
                    description = "Agrupamentos por tag, score e status",
                    emoji = "🧩",
                    onClick = onOpenSegmentos,
                )
            }
        }

        Text(
            text = "© 2025 WTC. Todos os direitos reservados.",
            fontSize = 12.sp,
            color = Color(0xFF999999),
            modifier =
                Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 12.dp),
        )
    }
}

@Composable
fun MenuOption(
    title: String,
    description: String,
    emoji: String,
    onClick: () -> Unit,
) {
    Card(
        modifier =
            Modifier
                .fillMaxWidth()
                .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5)),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
    ) {
        Row(
            modifier =
                Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier =
                    Modifier
                        .size(48.dp)
                        .background(Color(0xFF1976D2), shape = CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Text(text = emoji, fontSize = 20.sp)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(text = title, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1976D2))
                Text(text = description, fontSize = 13.sp, color = Color(0xFF555555))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun HomeScreenPreview() {
    WTCTheme {
        HomeScreen(onOpenContatos = {}, onOpenCampanhas = {}, onOpenSegmentos = {})
    }
}
