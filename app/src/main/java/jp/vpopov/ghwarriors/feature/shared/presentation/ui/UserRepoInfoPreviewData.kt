package jp.vpopov.ghwarriors.feature.shared.presentation.ui

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import jp.vpopov.ghwarriors.core.domain.model.UserRepoInfo
import jp.vpopov.ghwarriors.feature.shared.presentation.ui.UserRepoInfoPreviewData.sampleRepositories

object UserRepoInfoPreviewData {
    val sampleRepository = UserRepoInfo(
        id = 1,
        name = "awesome-android-app",
        fullName = "johndoe/awesome-android-app",
        description = "A modern Android application built with Jetpack Compose and clean architecture principles",
        url = "https://github.com/johndoe/awesome-android-app",
        language = "Kotlin",
        starsCount = 1247,
        isFork = false
    )

    val kotlinRepository = UserRepoInfo(
        id = 2,
        name = "kotlin-dsl-gradle",
        fullName = "johndoe/kotlin-dsl-gradle",
        description = "Gradle build scripts written in Kotlin DSL for Android projects",
        url = "https://github.com/johndoe/kotlin-dsl-gradle",
        language = "Kotlin",
        starsCount = 89,
        isFork = false
    )

    val javaRepository = UserRepoInfo(
        id = 3,
        name = "spring-boot-api",
        fullName = "johndoe/spring-boot-api",
        description = "RESTful API built with Spring Boot and PostgreSQL",
        url = "https://github.com/johndoe/spring-boot-api",
        language = "Java",
        starsCount = 456,
        isFork = false
    )

    val javascriptRepository = UserRepoInfo(
        id = 4,
        name = "react-dashboard",
        fullName = "johndoe/react-dashboard",
        description = "Modern dashboard built with React, TypeScript, and Material-UI components",
        url = "https://github.com/johndoe/react-dashboard",
        language = "JavaScript",
        starsCount = 234,
        isFork = false
    )

    val pythonRepository = UserRepoInfo(
        id = 5,
        name = "ml-data-analysis",
        fullName = "johndoe/ml-data-analysis",
        description = "Machine learning project for data analysis using pandas, scikit-learn, and matplotlib",
        url = "https://github.com/johndoe/ml-data-analysis",
        language = "Python",
        starsCount = 678,
        isFork = false
    )

    val forkedRepository = UserRepoInfo(
        id = 6,
        name = "awesome-compose",
        fullName = "johndoe/awesome-compose",
        description = "Curated list of awesome Jetpack Compose libraries and resources",
        url = "https://github.com/johndoe/awesome-compose",
        language = "Kotlin",
        starsCount = 15234,
        isFork = true
    )

    val repositoryWithoutDescription = UserRepoInfo(
        id = 7,
        name = "config-files",
        fullName = "johndoe/config-files",
        description = null,
        url = "https://github.com/johndoe/config-files",
        language = null,
        starsCount = 12,
        isFork = false
    )

    val highStarRepository = UserRepoInfo(
        id = 8,
        name = "android-architecture-components",
        fullName = "johndoe/android-architecture-components",
        description = "Sample app demonstrating Android Architecture Components: Room, LiveData, ViewModel, and Data Binding",
        url = "https://github.com/johndoe/android-architecture-components",
        language = "Kotlin",
        starsCount = 25847,
        isFork = false
    )

    val cppRepository = UserRepoInfo(
        id = 9,
        name = "native-cpp-library",
        fullName = "johndoe/native-cpp-library",
        description = "High-performance C++ library for mathematical computations with JNI bindings for Android",
        url = "https://github.com/johndoe/native-cpp-library",
        language = "C++",
        starsCount = 156,
        isFork = false
    )

    val dartRepository = UserRepoInfo(
        id = 10,
        name = "flutter-shopping-app",
        fullName = "johndoe/flutter-shopping-app",
        description = "Cross-platform shopping app built with Flutter and Firebase backend",
        url = "https://github.com/johndoe/flutter-shopping-app",
        language = "Dart",
        starsCount = 892,
        isFork = false
    )

    val sampleRepositories = listOf(
        sampleRepository,
        kotlinRepository,
        javaRepository,
        javascriptRepository,
        pythonRepository,
        forkedRepository,
        repositoryWithoutDescription,
        highStarRepository,
        cppRepository,
        dartRepository
    )
}

class UserRepoInfoPreviewParameterProvider : PreviewParameterProvider<List<UserRepoInfo>> {
    override val values: Sequence<List<UserRepoInfo>> = sequenceOf(
        sampleRepositories
    )
}