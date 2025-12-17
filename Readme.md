# Abbozzo ⚡

**Brain Buffer for Android.**
脳のメモリを解放する、無骨な「掃き溜め」ツール。

## 🇯🇵 概要 (Concept)

Abbozzo（アボッツォ：イタリア語で「下書き」「ラフスケッチ」）は、高機能なタスク管理アプリでも、整理されたメモ帳でもありません。
**「今、頭にあるノイズ」を一時的に退避させるためのバッファ**です。

思考の断片、URL、突然の思いつき……整理されていない情報を、Android の「共有」メニューから即座に投げ込んでください。整理は後でやればいいのです。

- **Cyberpunk / Neo-Brutalism UI:** 丸みのないデザイン、高コントラスト、そして動的なデジタルノイズ。
- **Share-First:** アプリを開いて書くのではなく、他アプリからの「共有」で受け取ることに特化。
- **Clean Architecture:** 見た目はパンクでも、中身は堅牢なモダン Android 設計。

---

## 🇺🇸 Overview

Abbozzo is not a task manager, nor a shiny PKM tool.
It is a **temporary holding ground** for your chaotic thoughts.
Throw text at it via Android System Share, and deal with it later.

## 🛠 Tech Stack (The "Golden" State)

This project enforces a strict modern tech stack. **Do not downgrade these.**

- **Language:** Kotlin 2.x
- **UI:** Jetpack Compose (Material3)
- **DI:** Hilt (Dagger)
- **Database:** Room (SQLite)
  - **Annotation Processor:** **KSP** (Kotlin Symbol Processing) - _No kapt allowed._
- **Architecture:** MVVM + Clean Architecture (Data / Domain / UI)
- **Build:** Gradle Kotlin DSL (`.kts`) + Version Catalog (`libs.versions.toml`)

## 📂 Project Structure

```text
com.gadgeski.abbozzo
├── di/             # Hilt Modules
├── data/           # Repository Impl, Room Entities (DAO)
├── ui/
│   ├── component/  # Brutalist UI components (Noise, BruteButton)
│   ├── screen/     # Compose Screens
│   └── theme/      # Cyberpunk Theme Definitions
└── AbbozzoApp.kt   # Hilt Application Entry Point
```
