# 🎙️ PODCASTer: Autonomous Podcast Agent with LangChain4j

[![Java](https://img.shields.io/badge/Java-25-orange.svg)](https://openjdk.org/)
[![LangChain4j](https://img.shields.io/badge/LangChain4j-1.22.0-blue.svg)](https://github.com/langchain4j/langchain4j)
[![Model](https://img.shields.io/badge/LLM-Google%20Gemini-4285F4.svg)](https://ai.google.dev/)
[![Build](https://img.shields.io/badge/Build-Maven-C71A36.svg)](https://maven.apache.org/)

An autonomous multi-agent podcast generator and news briefing producer built in Java using **LangChain4j** and **Google Gemini**.

The primary purpose of this project is to practice and demonstrate modern **AI & LLM engineering patterns** in the Java ecosystem—such as agent orchestration, declarative AI services, tool calling, live web research (RAG), and prompt engineering tailored for Text-to-Speech (TTS) audio delivery.

---

## 💡 Overview & Core Concepts

Creating a compelling news podcast from scratch typically requires several roles: an editorial producer to scope the episode, a researcher to find and synthesize source material, and a broadcast copywriter to script spoken narration.

**PODCASTer** automates this lifecycle by orchestrating specialized AI agents:

1. **Interactive Producer (Supervisor)**: Engages the user in a natural conversation to gather the topic and desired duration, forming a crisp podcast briefing.
2. **Web Query Agent**: Transforms the briefing into focused search queries.
3. **Live Web Harvester**: Queries live search news feeds and extracts clean article text using Mozilla Readability algorithms.
4. **Research Analyst**: Distills key facts, data points, and context from harvested articles.
5. **TTS Scriptwriter Agent**: Writes an engaging, broadcast-ready solo narration script written specifically "for the ear" (expanded numbers, natural punctuation for pauses, zero distracting markdown or stage directions).

---

## 🏗️ Architecture & Multi-Agent Flow

```mermaid
flowchart TD
    User([User]) <-->|Interactive Chat Loop| Producer[PodcastAgent / Producer]
    Producer -->|ChatMemory| Memory[(MessageWindowChatMemory)]
    
    subgraph Tool Pipeline: initiate_podcast
        Producer -->|@Tool initiate_podcast| Coordinator[PodcastTool]
        Coordinator -->|1. Generate Search Query| WebQueryAgent[PodcastWebQueryAgent]
        WebQueryAgent -->|Search Query| GoogleSearch[GoogleSearch Client]
        GoogleSearch -->|News Article URLs| WebpageReader[WebpageReader + Readability4J]
        WebpageReader -->|Extracted Clean Articles| ResearchAgent[PodcastResearchAgent]
        ResearchAgent -->|Synthesized Research & Facts| ScriptWriter[PodcastScriptWriterAgent]
        ScriptWriter -->|Broadcast-Ready Audio Script| Coordinator
    end

    Coordinator -->|Completed Podcast Script| Producer
```

---

## 🤖 Agent Roles & Components

| Component | Class | Description |
| :--- | :--- | :--- |
| **Podcast Producer** | [`PodcastAgent`](src/main/java/com/krakpaip/PodcastAgent.java) | The supervisor agent built with LangChain4j `AiServices`. Manages conversational memory, enforces a step-by-step information gathering plan, and invokes the podcast generator tool. |
| **Tool Coordinator** | [`PodcastTool`](src/main/java/com/krakpaip/PodcastTool.java) | Exposes the `@Tool` method `initiate_podcast`, executing the sequence of research and scripting tasks. |
| **Search Query Agent** | [`PodcastWebQueryAgent`](src/main/java/com/krakpaip/PodcastWebQueryAgent.java) | Formulates succinct, high-recall search queries (&le; 6 words) optimized for news search engines. |
| **Search & Ingestion** | [`GoogleSearch`](src/main/java/com/krakpaip/GoogleSearch.java) & [`WebpageReader`](src/main/java/com/krakpaip/WebpageReader.java) | Discovers news links and strips HTML boilerplate, ads, and navigation to yield clean article text using `Readability4J` and `Jsoup`. |
| **Research Analyst** | [`PodcastResearchAgent`](src/main/java/com/krakpaip/PodcastResearchAgent.java) | Evaluates scraped content against the podcast summary, discarding irrelevant data and extracting coherent insights. |
| **Broadcast Scriptwriter** | [`PodcastScriptWriterAgent`](src/main/java/com/krakpaip/PodcastScriptWriterAgent.java) | Uses structured LangChain4j annotations (`@SystemMessage`, `@UserMessage`, `@V`) to compose the spoken script formatted specifically for TTS voice synthesizers. |

---

## 🔬 LLM Engineering Patterns Demonstrated

- **Agent Tool Calling (`@Tool`)**: Autonomous transition from conversational requirements elicitation to deterministic pipeline execution.
- **Hierarchical Multi-Agent Architecture**: Decoupling complex workflows into specialized, single-responsibility LLM prompts rather than a single monolithic prompt.
- **Conversational Memory Management**: Utilizing `MessageWindowChatMemory` to preserve context across multiple user dialogue turns.
- **External Grounding & RAG**: Live web search combined with DOM readability parsing eliminates hallucinations and enables real-time news reporting.
- **TTS-Oriented Prompt Design**:
  - Script written for audio delivery (phonetic clarity, short punchy sentences).
  - Explicit expansion of symbols and figures (e.g., `$1.5B` &rarr; `one point five billion dollars`, `%` &rarr; `percent`).
  - Natural rhythm control via em-dashes and ellipses.
  - Strict output constraints preventing markdown tags, stage directions (`[pause]`), and speaker labels from bleeding into TTS audio engines.
- **Type-Safe Declarative AI Services**: Leveraging LangChain4j `AiServices` interfaces for clean, idiomatic Java integration without manual prompt string plumbing.

---

## 🛠️ Tech Stack & Dependencies

- **Language**: Java 25 (utilizing modern Java features such as Records and instance `main`)
- **LLM Framework**: [LangChain4j](https://github.com/langchain4j/langchain4j) (`1.22.0`)
- **LLM Provider**: Google GenAI / Gemini (`langchain4j-google-genai`)
- **Web Extraction**: [Jsoup](https://jsoup.org/) (`1.21.2`) & [Readability4J](https://github.com/dankito/Readability4J) (`1.0.8`)
- **Configuration**: [Dotenv Java](https://github.com/cdimascio/dotenv-java) (`3.2.0`)
- **Build Tool**: Apache Maven

---

## 🚀 Getting Started

### Prerequisites

- **JDK 25** (or compatible modern JDK) installed and configured in your `JAVA_HOME`.
- **Maven 3.9+** installed.
- A **Google Gemini API Key** ([Google AI Studio](https://aistudio.google.com/)).
- A **Search API Key** (e.g., [SerpApi](https://serpapi.com/) supporting Google News engine endpoints).

### 1. Clone & Configure

```bash
git clone https://github.com/your-username/podcast-agent.git
cd podcast-agent
```

Create a `.env` file in the project root by copying the template:

```bash
cp .env.example .env
```

Configure your credentials inside `.env`:

```env
# Google Gemini LLM Configuration
GEMINI_API_KEY=your_gemini_api_key_here
GEMINI_MODEL=gemini-1.5-flash

# Web Search API (e.g., SerpApi Google News engine)
SEARCH_API=https://serpapi.com/search.json?engine=google_news
SEARCH_API_KEY=your_search_api_key_here
```

### 2. Build the Project

```bash
mvn clean compile
```

### 3. Run the Podcast Agent

You can launch the application using Maven exec:

```bash
mvn compile exec:java -Dexec.mainClass="com.krakpaip.App"
```

---

## 💬 Example Interactive Run

```text
=== PODCASTer v0.1 ===
Assistant: What news podcast would you want me to create? (type 'exit' to quit)
User: 
> I'd like a quick update on recent breakthroughs in fusion energy.
Assistant: How long would you like the podcast to be? (For example, 3 minutes or 5 minutes)
User: 
> About 3 minutes.
Assistant: I am preparing a 3-minute podcast on the latest news, insights, and updates in the field of fusion energy...
[PODCAST SUMMARY]: A 3-minute podcast on the latest news, insights, and updates in fusion energy
[PODCAST WEB QUERY]: fusion energy latest breakthroughs news
[PODCAST SEARCH RESULT]: [https://..., https://...]
[RESEARCHED AND SYNTHESIZED CONTENT]: Extracted breakthroughs, ignition records, and facility updates...
[PODCAST SCRIPT]: Clean, broadcast-ready solo narration script...
```

---

## 📁 Project Structure

```text
podcast-agent/
├── pom.xml
├── .env.example
├── src/
│   ├── main/java/com/krakpaip/
│   │   ├── App.java                      # Application entry point
│   │   ├── AppConfiguration.java         # Environment and settings loader
│   │   ├── PodcastChatLoop.java          # CLI interaction loop
│   │   ├── PodcastAgent.java             # Supervisor producer agent with LangChain4j
│   │   ├── PodcastTool.java              # Pipeline tool (@Tool initiate_podcast)
│   │   ├── PodcastWebQueryAgent.java     # Search query generator
│   │   ├── GoogleSearch.java             # HTTP client for news search results
│   │   ├── WebpageReader.java            # Jsoup & Readability4J article scraper
│   │   ├── WebpageContentResult.java     # Content retrieval result model
│   │   ├── PodcastResearchAgent.java     # Content relevance & facts extractor
│   │   └── PodcastScriptWriterAgent.java # Broadcast copywriter agent for TTS
│   └── test/java/com/krakpaip/
│       └── AppTest.java
```

---

## 🔮 Future Roadmap

- [ ] **Text-to-Speech (TTS) Integration**: Direct audio synthesis using ElevenLabs or Gemini Multi-modal Audio API to output playable `.mp3` / `.wav` files.
- [ ] **Multi-Voice / Host Dialogue**: Extend scriptwriter and TTS to produce two-host banter and conversational discussions.
- [ ] **Audio Mastering**: Layering intro/outro music beds and sound effects using FFmpeg.
- [ ] **RSS Feed Generation**: Automated publishing to podcast hosting platforms and RSS readers.

---

## 📄 License

This project is open-source and available under the [MIT License](LICENSE).
