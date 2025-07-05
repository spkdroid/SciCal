# SciCal

# 🔢 SciCalcX - The Scientific Calculator Reimagined in Kotlin

<img src="https://github.com/user-attachments/assets/673b2d49-fc05-43cf-ba55-858927c6d73f" alt="scical_app_icon" width="100"/>

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![Android](https://img.shields.io/badge/platform-Android-blue.svg)](https://developer.android.com)
[![Kotlin](https://img.shields.io/badge/language-Kotlin-orange.svg)](https://kotlinlang.org/)

**SciCalcX** is a modern scientific calculator Android app built using **Jetpack Compose**, **Kotlin**, **MVVM architecture**, and **Kotlin Coroutines**. It delivers lightning-fast computations, clean UI, and a solid foundation for extensibility in symbolic and advanced mathematics.

> 🔬 Currently supports basic arithmetic with a robust architecture to scale toward differentiation, integration, complex number operations, and more!

---

## 🚀 Features

- 📱 **Jetpack Compose UI** – Intuitive, modern interface using declarative UI principles.
- 🧠 **MVVM Architecture** – Clean separation of concerns for scalability and testability.
- 🧩 **Hilt DI** – Efficient dependency injection using best Android practices.
- ⚡ **Kotlin Coroutines + Flows** – Seamless async handling and state management.
- 💬 **Inline Expression Evaluation** – Evaluate string expressions without external libraries (no `mXparser` or similar).
- 💾 **Memory Register** – Store and recall intermediate values.
- 🚫 **Offline Ready** – Entirely local computation without internet.

---

## 🌱 Upcoming Roadmap

We aim to evolve SciCalcX into a full-featured scientific and symbolic calculator:

| Feature | Description | Status |
|--------|-------------|--------|
| ✅ Arithmetic Evaluation | Add, subtract, multiply, divide, parentheses | Complete |
| 🚧 Differentiation Engine | Compute symbolic derivatives using custom parser | In Progress |
| 🚧 Integration | Support definite/indefinite integral calculation | In Progress |
| 🧮 Complex Numbers | Native support for `a + bi` format | Planned |
| 📈 Graph Plotting | Real-time graphs for functions | Planned |
| 📚 History Mode | View past calculations and results | Planned |
| 🌐 Localization | Multi-language support (i18n) | Planned |

> 💡 Want to contribute to one of these? [Start here](#-contributing)!

---

## 📸 Screenshots

| Main UI | Light Mode | Dark Mode |
|---------|------------|-----------|
| *Coming Soon* | *Coming Soon* | *Coming Soon* |

---

## 🧑‍💻 Tech Stack

- **Language**: Kotlin
- **UI**: Jetpack Compose
- **Architecture**: MVVM
- **DI**: Hilt
- **State Management**: Kotlin Flow
- **Testing**: JUnit (coming soon)
- **Build Tool**: Gradle (KTS)

---

## 🏗️ Building & Running the App

### Prerequisites

- Android Studio Giraffe/Koala or later
- Android SDK 33+
- Kotlin 1.9+
- Gradle 8.0+

### Steps

```bash
git clone https://github.com/your-username/SciCalcX.git
cd SciCalcX
./gradlew build

