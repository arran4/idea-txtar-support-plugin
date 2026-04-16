plugins {
    id("java")
    id("org.jetbrains.kotlin.jvm") version "1.9.22"
    id("org.jetbrains.intellij") version "1.17.3"
}

group = "com.arran4.txtar"
val pluginVersion: String by project
val githubRefName = providers.environmentVariable("GITHUB_REF_NAME").getOrNull()
version = if (githubRefName != null && githubRefName.startsWith("v") && githubRefName.getOrNull(1)?.isDigit() == true) {
    githubRefName.removePrefix("v")
} else {
    pluginVersion
}

repositories {
    mavenCentral()
}

// Configure Gradle IntelliJ Plugin
// Read more: https://plugins.jetbrains.com/docs/intellij/tools-gradle-intellij-plugin.html
intellij {
    pluginName.set("txtar-support")
    version.set("2023.2.5")
    type.set("IC") // Target IDE Platform
    plugins.set(listOf(/* Plugin Dependencies */))
}

configurations {
    runtimeClasspath {
        exclude(group = "org.jetbrains.kotlin")
    }
}

tasks {
    // Set the JVM compatibility versions
    withType<JavaCompile> {
        sourceCompatibility = "17"
        targetCompatibility = "17"
    }
    withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions.jvmTarget = "17"
    }

    instrumentCode {
        enabled = false
    }

    patchPluginXml {
        sinceBuild.set(providers.gradleProperty("pluginSinceBuild"))
        untilBuild.set(providers.gradleProperty("pluginUntilBuild"))
        changeNotes.set(provider { file("CHANGELOG.md").readText() })
    }

    buildPlugin {
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    }

    signPlugin {
        val certFile = providers.environmentVariable("CERTIFICATE_CHAIN_FILE")
        val keyFile = providers.environmentVariable("PRIVATE_KEY_FILE")
        val pass = providers.environmentVariable("PRIVATE_KEY_PASSWORD")

        if (certFile.isPresent && keyFile.isPresent) {
            certificateChainFile.set(file(certFile.get()))
            privateKeyFile.set(file(keyFile.get()))

            if (pass.isPresent) {
                password.set(pass)
            }
        }
    }

    publishPlugin {
        token.set(System.getenv("PUBLISH_TOKEN"))
    }
}
