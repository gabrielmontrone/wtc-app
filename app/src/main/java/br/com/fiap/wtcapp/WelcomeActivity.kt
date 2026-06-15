package br.com.fiap.wtcapp

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.com.fiap.wtcapp.ui.theme.WTCTheme

class WelcomeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WTCTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    WelcomeScreen()
                }
            }
        }
    }
}

@Composable
fun WelcomeScreen() {
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
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            Spacer(modifier = Modifier.height(48.dp))

            // 🔷 Logo
            Box(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                contentAlignment = Alignment.Center,
            ) {
                Image(
                    painter = painterResource(id = R.drawable.logo_wtc),
                    contentDescription = "Logo WTC",
                    modifier = Modifier.size(180.dp),
                )
            }

            // 🔷 Descrição
            Text(
                text = "Plataforma de mensagens e CRM para comunicação inteligente com clientes",
                fontSize = 16.sp,
                color = Color(0xFF555555),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(vertical = 16.dp),
            )

            // 🔷 Cards informativos
            Column(modifier = Modifier.fillMaxWidth()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                ) {
                    Text(
                        text = "💬 Mensagens Ricas – Interações personalizadas",
                        fontSize = 14.sp,
                        modifier = Modifier.padding(16.dp),
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                ) {
                    Text(
                        text = "📇 CRM Integrado – Gestão completa de clientes",
                        fontSize = 14.sp,
                        modifier = Modifier.padding(16.dp),
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                ) {
                    Text(
                        text = "🚀 Campanhas Express – Envios rápidos e segmentados",
                        fontSize = 14.sp,
                        modifier = Modifier.padding(16.dp),
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            val context = LocalContext.current

            // 🔷 Botão de login
            Button(
                onClick = {
                    context.startActivity(Intent(context, LoginActivity::class.java))
                },
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1565C0)),
            ) {
                Text("Começar Agora", color = Color.White, fontWeight = FontWeight.Bold)
            }

            // 🔷 Link alternativo
            TextButton(onClick = { /* Ação futura */ }) {
                Text("Acesse como cliente ou operador", color = Color(0xFF1976D2))
            }

            // 🔷 Rodapé
            Text(
                text = "© 2025 WTC. Todos os direitos reservados.",
                fontSize = 12.sp,
                color = Color(0xFF999999),
                modifier = Modifier.padding(bottom = 8.dp),
            )
        }
    }
}
