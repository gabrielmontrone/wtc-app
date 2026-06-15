# WTC — Android App

Native Android client for the **WTC** customer relationship / messaging platform.
Built with **Kotlin** and **Jetpack Compose**, talking to the
**[WTC API backend](https://github.com/gabrielmontrone/wtc)**.

> 📱 This is a mobile app — it runs on an Android device/emulator, not in a web browser.
> Recruiters can review the **source here** and try the **[live API](https://github.com/gabrielmontrone/wtc#-live-demo)** directly.

---

## ✨ Screens / features

- **Login & Welcome** — JWT authentication against the WTC API.
- **Contacts (Contatos)** — browse and manage customers.
- **Conversations (Conversas) & Messages (Mensagens)** — chat and message history.
- **Campaigns (Campanhas)** — view and create messaging campaigns.
- **Segments (Segmentos)** — customer segmentation.
- **Push notifications** — via Firebase Cloud Messaging.

## 🧱 Tech stack

| Area | Technology |
|---|---|
| Language | Kotlin |
| UI | Jetpack Compose + Material 3 |
| Architecture | Activity-based screens + REST API client layer (`api/`) |
| Networking | `WtcApiClient` against the WTC REST API |
| Notifications | Firebase Cloud Messaging |
| Min SDK / Target | 27 / 36 |
| Build | Gradle (Kotlin DSL) |

## 🏗️ Project structure

```
app/src/main/java/br/com/fiap/wtcapp/
├── LoginActivity / WelcomeActivity / HomeActivity
├── ContatosActivity / ConversasActivity / MensagensActivity
├── CampanhasActivity / CriarCampanhaActivity / SegmentosActivity
├── api/            # ApiConfig, WtcApiClient, ApiModels, AuthSession
└── ui/theme/       # Compose theme (Color, Type, Theme)
```

## 🚀 Running locally

**Prerequisites:** Android Studio (latest), an Android emulator or device, and a running
[WTC backend](https://github.com/gabrielmontrone/wtc).

1. Open the project in Android Studio and let Gradle sync.
2. Point the app at your backend in
   [`app/src/main/java/br/com/fiap/wtcapp/api/ApiConfig.kt`](app/src/main/java/br/com/fiap/wtcapp/api/ApiConfig.kt):
   ```kotlin
   object ApiConfig {
       // Local backend on the emulator host:
       const val BASE_URL = "http://10.0.2.2:8080/"
       // Or your deployed backend:
       // const val BASE_URL = "https://<your-service>.onrender.com/"
   }
   ```
   > On the Android **emulator**, `10.0.2.2` maps to your computer's `localhost`.
   > For a **physical device**, use your machine's LAN IP or the deployed HTTPS URL.
3. Run the app (▶) on the emulator/device.

> **Firebase:** this app expects a `google-services.json` in `app/`. Use your own Firebase
> project's file if you fork this repo.

---

_This project was developed as part of coursework at FIAP._
