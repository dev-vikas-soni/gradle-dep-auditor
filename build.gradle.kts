plugins {
    `kotlin-dsl`
    `java-gradle-plugin`
}

group = "com.github.vikas"
version = "1.0.3"

repositories {
    mavenCentral()
    gradlePluginPortal()
}

gradlePlugin {
    plugins {
        create("depAuditor") {
            id = "com.github.vikas.dep-auditor"
            displayName = "DepAuditor - Unused Dependencies + Line Numbers"
            description = """
                Finds unused dependencies with exact line numbers and APK savings.
                🚨 LINE 11 → sed -i '11d' → 9MB savings!
            """.trimIndent()
            implementationClass = "com.vikas.DepAuditorPlugin"
        }
    }
}

publishing {
    publications {
        create<MavenPublication>("mavenGradlePlugin") {
            from(components["java"])
        }
    }
}