# DeepWiki — Neo-Brutalist Autonomous AI Chatbot

Aplikasi Chatbot AI Android generasi modern berbasis **Kotlin + Jetpack Compose** dengan gaya **Neo-Brutalism**, integrasi **10 Provider AI**, protokol **Model Context Protocol (MCP)**, **Autonomous AI Tools**, kontrol **Thinking Effort**, dan **Model Fetching** langsung dari server penyedia.

---

## Karakteristik Desain & Aturan Vektor
- **Bebas Elemen Emoji**: Seluruh elemen antarmuka, indikator status, tombol, dan badge menggunakan **Asset Vektor Asli (SVG/XML)** dan tipografi bersih.
- **Rasio Sudut (60% Kotak : 40% Bulat)**: Kurva squircle presisi (`10.dp` corner radius) dengan garis tepi hitam tegas `2.5.dp` solid (`#121212`) serta hard drop shadow (`4.dp` offset tanpa blur).
- **Hirarki Ukuran Icon Fungsional**:
  - Icon aksi tombol input: `18.dp` – `20.dp`
  - Icon tombol thinking effort & search di dalam input: `18.dp` – `19.dp`
  - Icon status & badge tool: `14.dp` – `17.dp`
  - Icon tab bar provider: `16.dp` – `18.dp`
  - Logo brand top bar: `22.dp` di dalam container `32.dp`
- **Format Obrolan Kontras**:
  - **Pengguna**: Menggunakan *Bubble Chat* tebal di kanan dengan aksen kuning dan bayangan brutalist.
  - **DeepWiki AI**: **Bukan Bubble** — menggunakan format *Full-Width Technical Wiki Document* terbuka untuk kenyamanan membaca riset, tabel data markdown, dan formula matematika LaTeX.

---

## Fitur Baru & Pembaruan Vektor

### 1. Thinking Effort Control di Sudut Bawah Kiri Bar Input
- Icon vektor petir `ic_thinking_effort.xml` terintegrasi langsung di **sudut bawah kiri** di dalam kotak input chat.
- Mengatur kekuatan komputasi penalaran AI:
  - `OFF`: Penalaran non-aktif (0 token budget)
  - `LOW`: 1,024 token budget
  - `MED`: 4,096 token budget
  - `HIGH`: 16,384+ token budget
- Badge status level penalaran aktif tertera langsung di samping icon vektor.

### 2. Icon Web Search / Fetch Vektor
- Menggunakan asset vektor `ic_web_search.xml` untuk fitur pencarian web otonom (`web_search`).
- Terpasang di dalam bar input chat serta pada kartu eksekusi tool di riwayat CoT.

### 3. Icon Pemanggilan Alat (Tool Call) di CoT
- Menggunakan asset vektor `ic_tool_call.xml` untuk merepresentasikan pemanggilan autonomous tools:
  - `web_search`
  - `memory_tools`
  - `mcp_check`
  - `mcp_list`
  - `mcp_use`
  - `mcp_tools_use`

### 4. Fitur Fetch Model Langsung dari Server
- Mengambil daftar model AI secara real-time langsung dari server penyedia melalui endpoint `/v1/models` atau Ollama `/api/tags`.
- Tersedia tombol cepat **FETCH MODEL** dengan icon `ic_model_fetch.xml` pada header chat dan dialog konfigurasi provider di menu Pengaturan.

---

## 10 Provider AI Didukung
1. **Ollama (Cloud)** (`ic_ollama.xml`)
2. **Qwen (Alibaba Cloud / DashScope)** (`ic_qwen.xml`)
3. **OpenRouter** (`ic_openrouter.xml`)
4. **Groq (LPU Inference)** (`ic_groq.xml`)
5. **xAI Grok** (`ic_grok.xml`)
6. **Nvidia NIM** (`ic_nvidia.xml`)
7. **Google Gemini** (`ic_gemini.xml`)
8. **OpenAI** (`ic_openai.xml`)
9. **DeepSeek (R1 Reasoner)** (`ic_deepseek.xml`)
10. **Anthropic Claude (Sonnet / Opus)** (`ic_anthropic.xml`)

---

## Struktur Direktori Proyek

```text
DeepWiki/
├── app/
│   ├── build.gradle.kts
│   ├── proguard-rules.pro
│   └── src/main/
│       ├── AndroidManifest.xml
│       ├── java/com/deepwiki/app/
│       │   ├── engine/
│       │   │   ├── AiClient.kt           # SSE Streaming & Model Fetching
│       │   │   ├── McpManager.kt         # MCP registry & remote tool caller
│       │   │   └── ToolExecutor.kt       # 6 AI tools executor
│       │   ├── model/
│       │   │   ├── AiProvider.kt         # 10 Provider definition & brand assets
│       │   │   ├── AiTool.kt             # Schema 6 Built-in AI tools
│       │   │   ├── AppSettings.kt        # All app settings state
│       │   │   ├── ChatMessage.kt        # Message & CoT models
│       │   │   ├── McpModels.kt          # MCP configurations
│       │   │   └── ThinkingEffort.kt     # OFF, LOW, MED, HIGH reasoning effort
│       │   ├── prompt/
│       │   │   └── SystemPromptBuilder.kt# Autonomous instructions, Markdown & LaTeX rules
│       │   ├── theme/
│       │   │   ├── NeoColors.kt          # Neo-Brutalist palette
│       │   │   ├── NeoComponents.kt      # NeoCard, NeoButton, NeoBadge, NeoIconButton
│       │   │   └── NeoTheme.kt           # Theme tokens & custom fonts
│       │   ├── ui/
│       │   │   ├── MainActivity.kt       # Scaffold, Top Bar, Tab router
│       │   │   ├── components/
│       │   │   │   ├── MarkdownLatexRenderer.kt # Rich Markdown & Math
│       │   │   │   ├── NeoFloatingNavbar.kt     # Bottom dock
│       │   │   │   ├── NeoTabBar.kt             # Provider horizontal tabs
│       │   │   │   ├── ThinkingCoTView.kt       # CoT reasoning accordion
│       │   │   │   └── ToolCallView.kt          # Tool invocation banner
│       │   │   └── screens/
│       │   │       ├── chat/
│       │   │       │   ├── ChatInputBar.kt      # Input with Thinking Effort & Vectors
│       │   │       │   ├── ChatMessageItem.kt   # Bubble user vs AI wiki document
│       │   │       │   └── ChatScreen.kt        # Chat viewport + Model Fetch header
│       │   │       ├── settings/
│       │   │       │   └── SettingsScreen.kt    # Full configuration & Fetch dialog
│       │   │       └── tools/
│       │   │           └── ToolsScreen.kt       # Tool testing & MCP console
│       │   └── viewmodel/
│       │       └── ChatViewModel.kt      # State management, coroutines, & fetch logic
│       └── res/
│           ├── drawable/                 # 15 Vector Assets XML (10 providers, thinking effort, search, tool call, model fetch, logo)
│           └── values/                   # strings.xml, colors.xml, themes.xml
├── build.gradle.kts
├── settings.gradle.kts
└── gradle.properties
```
