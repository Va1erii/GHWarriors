# GH Warriors

A modular GitHub user search app built with Jetpack Compose and Decompose for scalable, testable navigation and clean architecture.

## 🧩 Key Tech Decisions

### ✅ Why Decompose (over Jetpack Navigation)
The application uses [Decompose](https://github.com/arkivanov/Decompose) for navigation instead of Jetpack Navigation for several key advantages:
- Type-safe navigation: Sealed Config classes replace fragile string routes.
- No fragments: Compose-native, lifecycle-aware navigation.
- Built-in state & instance preservation: Via StateKeeper and InstanceKeeper, no need for ViewModels tied to Activity.
- Composable structure: Encourages clean separation between screen logic and UI.

> Ideal for complex navigation and deep linking without reflection, safe for future multiplatform refactor.

### 🏗️ Architecture & Module Structure
Follows a modular clean architecture with clear separation:
```
app/                     // Root DI + entry point
├── tabs/                // Tab navigation via BottomNavBar
├── RootComponent        // RootComponent (connects features)
core/
├── data/                // Shared logic (e.g., models, repo interfaces)
├── designsystem/        // Design system: Typography, theme, etc.
├── network/, database/  // Common infrastructure (Retrofit, Room)
feature/
├── shared/              // Shared logic between features
├── search/
│   ├── data/            // Optional local repo impl
│   └── presentation/    // Decompose component + ViewModel + Composables
├── bookmarks/
├── profile/
└── settings/
```
Each feature module contains:
- data/: Optional per-feature repo impl
- presentation/: Decompose Component interface + ViewModel + Compose screens

### 🔀 Navigation Structure (Simplified)
```kotlin
@Serializable
sealed class Config {
    object Tabs : Config()
    data class Profile(val userId: Int) : Config()
}
```
- TabsComponent hosts: Search, Bookmarks, Settings
- ProfileComponent is opened via deep link or user click
- No strings or backstack hacks; components push/pop configs

### 🧠 State & Deep Link Handling
- StateKeeper: Remembers search queries, scroll state
- InstanceKeeper: Survives configuration changes
- Deep Link: ProfileComponent(userId: Int) can be created directly via config

### Decompose Component Pattern
Each screen is represented by a Component interface with a default implementation:

```kotlin
interface SearchComponent {
    val query: Flow<String>
    val users: Flow<PagingData<UserWithBookmarkInfo>>
    
    fun search(query: String)
    fun onUserSelected(user: UserWithBookmarkInfo)
    fun onBookmarkClick(user: UserWithBookmarkInfo)
}
```

This pattern provides:
- Clear contracts for each screen
- Easy testing and mocking
- Separation of concerns between navigation and business logic

## 🔐 GitHub Token Setup
App uses a GitHub Personal Access Token (PAT) to avoid rate limiting.
Set in `~/gradle.properties`:
```ini
access_token=YOUR_GITHUB_PERSONAL_ACCESS_TOKEN
```

## 📌 Core Features
- **Search**: GitHub users, paged, real-time
- **Bookmarks**: Save/remove with Room, swipe to delete
- **Profile**: User details, repo list (paged), deep links
- **Settings**: App theme management

## Technology Stack
- **Kotlin**: 2.2.0
- **Compose**: 2025.07.00 BOM
- **Decompose**: 3.3.0
- **Hilt**: 2.56.2
- **Retrofit**: 3.0.0
- **Room**: 2.7.2
- **Paging**: 3.3.6
- **Coil**: 3.3.0 for image loading