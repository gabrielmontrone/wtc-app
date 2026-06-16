# WTC — Roteiro de demonstração

Guia prático para ver na tela os recursos da **camada de Trust & Safety / Compliance** e as
demais funcionalidades recentes. Para a visão de arquitetura/positioning, veja o
[README](README.md).

## ▶️ Pré-requisitos (uma vez)

1. **Backend rodando** com o código mais recente:
   ```bash
   cd backend/wtc
   ./mvnw spring-boot:run        # Windows: .\mvnw.cmd spring-boot:run
   ```
   O `.env` (git-ignored) é carregado automaticamente.
2. **App** apontando para esse backend em `app/src/main/java/br/com/fiap/wtcapp/api/ApiConfig.kt`,
   rodando em emulador/dispositivo.
3. Faça login — alguns recursos pedem conta **OPERADOR** (campanhas); outros, qualquer conta.

## 🗺️ Mapa de navegação

```
Welcome → Login → Home ─┬─ Contatos → (toca um contato) → Conversas → (toca uma conversa) → Mensagens
                        └─ Auditoria
```

---

## Recursos e como acessá-los

### 1. "Continuar com Google" — tela de Login
- **Onde:** botão **Continuar com Google**, abaixo de "Entrar".
- **Ação:** toque → seletor de conta Google → entra/cria conta (CLIENTE no 1º acesso).
- *Requer Web Client ID configurado no Firebase e Google Play Services no aparelho.*

### 2. Atalho `/` de campanhas — tela de Mensagens
- **Onde:** Home → **Contatos** → toque num contato → toque numa conversa → campo
  **"Responder (use / para campanhas)"**.
- **Ação:** digite `/` (ou `/` + parte do nome da campanha) → toque numa sugestão → o conteúdo
  da campanha entra no campo.
- *Popula apenas com conta **OPERADOR** (campanhas são restritas a operador).*

### 3. Envio de foto — tela de Mensagens
- **Onde:** ícone **+** à esquerda do campo de texto.
- **Ação:** toque no **+** → escolha uma imagem → prévia com opção de remover → **Enviar** →
  a foto aparece _inline_ no chat.
- *O upload real exige storage (MinIO/S3) ativo no backend.*

### 4. Scan DLP — aviso e selos — tela de Mensagens ⭐
- **Aviso antes de enviar:** digite algo sensível, ex.: `meu CPF é 529.982.247-25` ou um cartão
  `4111 1111 1111 1111`, e toque **Enviar** → abre **"Dados sensíveis detectados"** →
  *Enviar mesmo assim* / *Cancelar*.
- **Selos de risco:** mensagens com dados sensíveis exibem **⚠ CPF / ⚠ Cartão de crédito** no
  balão (vermelho para risco alto).

### 5. Trilha de auditoria — tela Auditoria ⭐
- **Onde:** Home → card **🛡️ Auditoria**.
- **O que mostra:** eventos recentes (login, envio de mensagem, criação de cliente) e, em destaque
  com ⚠, os **`SUSPICIOUS_MESSAGE`** gerados pelo scan DLP.
- **Dica:** envie uma mensagem com CPF/cartão (passo 4) e abra **Auditoria** — o evento aparece no topo.

### 6. Bastidores (sem tela)
- **Swagger** em português: `http://localhost:8080/swagger-ui.html`.
- Backend carrega o `.env` automaticamente (rodar local sem exportar variáveis).

---

## 🎥 Checklist para gravar um GIF de demonstração

1. **Login com Google** (3s) — toque no botão, escolha a conta, chega na Home.
2. **DLP em ação** (8s) — abra uma conversa, digite uma mensagem com **CPF** e **cartão**,
   toque **Enviar** → mostre o **diálogo de aviso** → *Enviar mesmo assim* → mostre o **selo ⚠**.
3. **Atalho `/`** (4s) — digite `/` e escolha uma campanha (conta operador).
4. **Foto** (4s) — toque **+**, escolha uma imagem, envie, mostre _inline_.
5. **Auditoria** (5s) — volte à Home → **Auditoria** → mostre o `SUSPICIOUS_MESSAGE` no topo.

> Dica: grave em ~720p, mantenha o emulador no tema escuro e use dados fictícios.
