# ☁️ Uncloud - Quit Smoking Companion

**Uncloud** is a modern, privacy-first Android application designed to help you break free from smoking. Track your progress, visualize your health recovery, and stay motivated with a beautiful, dark-themed interface built completely with **Jetpack Compose**.

![Kotlin](https://img.shields.io/badge/Kotlin-2.0-7F52FF.svg?style=flat&logo=kotlin)
![Android](https://img.shields.io/badge/Android-MinSDK%2024-3DDC84.svg?style=flat&logo=android)
![Compose](https://img.shields.io/badge/Jetpack%20Compose-UI-4285F4.svg?style=flat&logo=google-play)
![Hilt](https://img.shields.io/badge/DI-Hilt-blue)

## ✨ Features

### 🚭 Smart Tracking
- **Smoke-Free Timer**: Watch the seconds, minutes, and days stack up since your last cigarette.
- **Money Saved**: Calculate exactly how much cash you've reclaimed based on your habits.
- **Life Regained**: Estimate the biological time you've added back to your life.
- **Cigarettes Avoided**: See the physical number of cigarettes you *didn't* smoke.

### 📱 Home Screen Widgets
- **Commitment Grid**: Visualize your last 14 days of success (or slips) right on your home screen.
- **Daily Pledge**: Sign your daily contract to stay smoke-free without opening the app.
- **Emergency Shield**: One-tap access to immediate help and breathing exercises.
- **Daily Clarity**: Quick glance at your current streak and daily motivation.

### 🏥 Health Milestones
- **Recovery Timeline**: Track your body's healing process based on real medical data (e.g., "Nicotine Free", "Lung Capacity Improved").
- **Visual Progress**: Beautiful circular progress indicators for upcoming health goals.

### 🔔 Motivation & Support
- **Daily Inspiration**: Receive a fresh motivational quote every morning to start your day strong.
- **Milestone Alerts**: Get celebrated when you hit major streaks or health achievements.
- **Pledge System**: Sign a personal contract with yourself during onboarding to solidify your commitment.

### 🔒 Privacy-First Design
- **100% Offline**: Your data (quit date, habits, costs) lives **only** on your device.
- **No Cloud Sync**: We don't send your personal health data to any server.
- **Secure**: No login required. Your journey is yours alone.

### 🎨 Modern Experience
- **Sleek Dark Mode**: Easy on the eyes, perfect for checking your stats at any time.
- **Fluid Animations**: Built with Jetpack Compose for buttery smooth transitions.
- **Dynamic Theming**: Custom splash screen and consistent styling throughout.

---

## 🛠️ Tech Stack

Built with modern Android development best practices:

- **Language**: [Kotlin](https://kotlinlang.org/)
- **UI**: [Jetpack Compose](https://developer.android.com/jetpack/compose) (Material 3)
- **Widgets**: [Jetpack Glance](https://developer.android.com/jetpack/compose/glance)
- **Architecture**: MVVM (Model-View-ViewModel) + Clean Architecture
- **Dependency Injection**: [Hilt](https://dagger.dev/hilt/)
- **Local Persistence**: 
  - [Room](https://developer.android.com/training/data-storage/room) (Achievements)
  - [DataStore](https://developer.android.com/topic/libraries/architecture/datastore) (User Preferences)
- **Background Tasks**: [WorkManager](https://developer.android.com/topic/libraries/architecture/workmanager) (Notifications)
- **Navigation**: [Compose Navigation](https://developer.android.com/guide/navigation/navigation-compose)

## 📸 Screenshots

| Home Dashboard | Health Milestones | Settings |
|:---:|:---:|:---:|
| <!-- Add screenshot link here --> | <!-- Add screenshot link here --> | <!-- Add screenshot link here --> |

## 🚀 Getting Started

1. **Clone the repository**:
   ```bash
   git clone https://github.com/yourusername/uncloud.git
   ```
2. **Open in Android Studio**:
   - Requires Android Studio Koala or newer.
3. **Build & Run**:
   - The application ID is `com.arlabs.uncloud`.
   - Sync Gradle and run on an emulator or physical device (Min SDK 24).

## 🤝 Contributing

Contributions are welcome! If you have ideas for new features or find a bug, please open an issue or submit a pull request.

1. Fork the Project
2. Create your Feature Branch (`git checkout -b feature/AmazingFeature`)
3. Commit your Changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the Branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

---
*Built with ❤️ to help you breathe free.*
