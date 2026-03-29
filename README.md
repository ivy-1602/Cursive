# 𝑪𝒖𝒓𝒔𝒊𝒗𝒆 ✍ 
*Your Daily Reflections*

A privacy-first journaling app built with Kotlin and Firebase. Designed for daily reflection, mood tracking, and building sustainable writing habits through intelligent streak systems.

**Built:** January 21-26, 2026 (6 days)  
**Platform:** Android (Min SDK 24, Target SDK 34)  
**Status:** v1.01 Complete

---

## 📸 App Preview

### 🔐 Login / Register
![Login/Register](https://i.imgur.com/G8KeOtn.jpeg)

### 💫 Splash Screen & Lock Screen
![Splash Screen & Lock Screen](https://i.imgur.com/cOO4ZHk.jpeg)

### 🏠 Home Screen & Edit/View Entryy
![Home Screen & Edit/View Entry](https://i.imgur.com/PwfFhYK.jpeg)

### 🧭 Navigation & Analytics
![Navigation & Analytics](https://i.imgur.com/hyzfoSB.jpeg)

### 👤 Profile & All Entries
![Profile & All Entries](https://i.imgur.com/8h6U7rV.jpeg)

### 📅 Calendar & Settings
![Calendar & Settings](https://i.imgur.com/TXo242x.jpeg)

---

## 📜 Overview

Cursive is a reflection and gratitude journaling application that combines thoughtful design with robust functionality. It addresses the common challenge of maintaining consistent journaling habits while respecting user privacy.

**Key Differentiators:**
- **Privacy-First:** Complete data isolation per user, optional PIN lock
- **Vintage Aesthetic:** Parchment-inspired design with serif typography
- **Smart Streaks:** Intelligent tracking with 1-day grace period
- **Comprehensive Prompts:** Seven daily reflection fields beyond free writing

---

## 📜 Core Features

### Journaling System
Each entry includes:
- **Mood Selection** (Happy, Calm, Neutral, Sad, Anxious)
- **Title of the Day**
- **Free Writing Space**
- **Gratitude Section**
- **5 Reflection Prompts:**
  - What made you smile today?
  - What challenged you today?
  - What are you quietly hoping for?
  - Who/what impacted your day?
  - What are you looking forward to tomorrow?

### Navigation & Discovery
- **Search** entries by keyword
- **Filter** by mood with color-coded indicators
- **Calendar View** for visual entry history
- **Recent Entries** quick access list

### Analytics Dashboard
- Current streak counter
- Longest streak record
- Total entries count
- Mood distribution breakdown

### Security & Authentication
- Firebase email/password authentication
- Optional 4-digit PIN lock
- Secure data storage with Firestore
- Logout with confirmation dialog

### Profile & Personalization
- Editable user name and motto
- Gradient initial circle avatar
- Member since display
- Account management

---

## Technical Implementation

### Architecture
- **Pattern:** Simplified MVVM (Model-View-ViewModel)
- **Language:** Kotlin 1.9+
- **UI Framework:** XML Layouts with Material Design Components
- **Binding:** View Binding enabled

### ⚙️ Tech Stack

**Frontend:**
- Material Design Components 1.11.0
- RecyclerView & CardView for list displays
- CalendarView for date selection
- Navigation Components for screen transitions

**Backend:**
- Firebase Authentication 22.3.1
- Cloud Firestore (NoSQL database)
- SharedPreferences for local PIN storage

**Key Libraries:**
```gradle
// Firebase
firebase-auth:22.3.1
firebase-firestore:latest

// Lifecycle & Coroutines
lifecycle-viewmodel-ktx:2.7.0
kotlinx-coroutines-android:1.7.3

// Navigation
navigation-fragment-ktx:2.7.6
```

### Project Structure
```
com.uss.cursive/
├── adapters/         # RecyclerView adapters
├── models/           # Data classes (User, JournalEntry, Mood)
├── screens/          # Activity controllers (13 screens)
└── utils/            # Helper functions
```

---

## 🎨 Design System

**Color Palette:**
- Primary: Deep brown (#6B5244)
- Background: Parchment cream (#F5EFE6)
- Moods: Pastel variants (sage, blue, taupe, lavender, peach)

**Typography:**
- App name: Cursive italic
- Dates: Serif italic
- Headers: Serif bold
- Body: Serif regular

**UI Approach:**  
Vintage journal aesthetic meets modern Android UX. Designed to feel calm and intentional, avoiding aggressive notifications or guilt-inducing patterns.

---

## 🕰️ Build Timeline

| Day | Focus Area | Deliverables |
|-----|------------|--------------|
| **1** | Foundation & Auth | Firebase setup, login/register screens, data models, initial debugging |
| **2** | Core Journaling | Home screen, 7-field entry form, mood tracking, view entry functionality |
| **3** | Navigation & Insights | Calendar implementation, analytics dashboard, streak logic, recent entries |
| **4** | Discovery Features | All entries screen, search functionality, mood filters, entry adapter |
| **5** | User Experience | Profile UI, PIN lock, settings, about screen |
| **6** | Polish & Deployment | Final builds, UI consistency pass, documentation, APK generation |

---

## 📱 Screens Overview

**13 Total Screens:**

1. Splash Screen - Initial loading and branding
2. PIN Lock - Optional security layer
3. Login - Firebase authentication
4. Register - New account creation
5. Home - Dashboard with recent entries and stats
6. Add Entry - 7-field journaling interface
7. View Entry - Display and edit past entries
8. All Entries - Searchable and filterable entry list
9. Calendar - Date-based entry navigation
10. Analytics - Streaks and mood distribution
11. Profile - User information and customization
12. Settings - App configuration options
13. About - Developer information

---

## Key Challenges Solved

1. **Firebase Integration** - Implemented secure authentication and Firestore database with proper security rules
2. **Complex UI Management** - Coordinated RecyclerViews, Cards, CalendarView, and FloatingActionButtons
3. **Data Architecture** - Designed scalable models for User, Entry, and Mood entities
4. **Streak Algorithm** - Built smart tracking system with grace period to handle real-world usage
5. **Real-time Search** - Implemented efficient keyword search and mood filtering
6. **Local Security** - Integrated PIN lock using SharedPreferences
7. **Data Privacy** - Ensured complete user isolation in multi-tenant Firestore structure

---

## Device Requirements

**Minimum:**
- Android 7.0 (API 24)
- 100 MB storage
- Internet connection for sync

**Recommended:**
- Android 10.0+ (API 29)
- 500 MB storage
- Stable internet connection

---

## Future Roadmap (v2.0)

**Photo Attachments**  
Firebase Storage integration for image uploads with compression

**Data Export**  
Export journal entries as PDF or CSV for backup and analysis

**Advanced Analytics**  
Mood trend line charts, weekly/monthly summaries, writing time patterns

**Smart Reminders**  
Configurable daily writing notifications

**Themes**  
Dark mode and alternative color schemes

---

## 🔏 Privacy & Security

- All journal entries are **completely private** and isolated per user
- Firebase Authentication ensures secure account access
- Optional PIN lock provides device-level security
- Cloud Firestore security rules prevent unauthorized data access
- No third-party analytics or tracking
- No advertisements or data monetization

---

## Installation

1. Download APK from releases
2. Enable "Install from Unknown Sources" in device settings
3. Install and launch application
4. Create account with email/password
5. Optionally enable PIN lock in settings
6. Begin journaling
---

## Known Limitations (v1.0)

- Photo attachment feature is reserved for future release
- Search is case-sensitive (improvement planned)
- Calendar may experience minor lag with 1000+ entries
- Internet connection required for initial sync

---

## Development Insights

This is my second Android application. The first taught me the fundamentals; this one taught me structure and workflow optimization.

**Key Learnings:**
- Proper project architecture reduces debugging time significantly
- Material Design guidelines provide excellent UX foundations
- View Binding eliminates entire categories of runtime errors
- Kotlin coroutines make asynchronous operations manageable
- User-centric features (like grace periods) matter more than technical complexity

The development process was notably smoother than my first project. Gradle builds succeeded more often than they failed, and the overall experience felt more like iterative refinement than problem-solving under pressure.

---

## Technical Notes

**Firestore Query Examples:**
```kotlin
// Get user entries
firestore.collection("entries")
    .whereEqualTo("userId", currentUserId)
    .orderBy("timestamp", Query.Direction.DESCENDING)
    .get()

// Search entries
firestore.collection("entries")
    .whereEqualTo("userId", currentUserId)
    .whereArrayContains("searchableText", keyword)
    .get()
```

**ProGuard Configuration:**
```kotlin
-keep class com.google.firebase.** { *; }
-keep class com.uss.cursive.models.** { *; }
```

---

## 📄License

Copyright © 2026 Uma Salunke. All rights reserved.

This project is available for portfolio review and educational reference. Commercial use or redistribution requires explicit permission.

---

## 📫Contact

**Uma Salunke**  
AI & ML Engineering Student

[LinkedIn](https://www.linkedin.com/in/umasalunke7) • [GitHub](https://github.com/ivy-1602) • [Email](mailto:umasalunke7@gmail.com)

---

## Closing Note

Much of this app came together while listening to *"All my mornings are Mondays stuck in an endless February"* — where focus and emotion coexisted in productive tension.

> *"I am in my melancholy lot but I am so productive, it's an art.*  
> *Breaking down, I hit the floor.*  
> *All the pieces of me shattered as the crowd was chanting 'More!'*  
> *I was grinning like I'm winning, I was hitting my marks*  
> *Because I can do it with a broken…"*

This app exists in that space between technical precision and human experience.

---

**Made with 🫀 & 🧠**

*That's the reason I build 𝑪𝒖𝒓𝒔𝒊𝒗𝒆, because analytical minds need emotional check-ins just as much as anyone else.*
