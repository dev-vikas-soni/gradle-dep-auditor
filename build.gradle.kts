plugins {
    `kotlin-dsl`
    `java-gradle-plugin`
    `maven-publish`
}

gradlePlugin {
    plugins {
        create("depAuditor") {
            id = "com.github.vikas.dep-auditor"
            displayName = "DepAuditor - Unused Dependencies + Line Numbers"
            description = "Finds unused dependencies with exact line numbers and APK savings"
            implementationClass = "com.vikas.DepAuditorPlugin"
            tags.set(listOf("dependency", "audit", "unused", "android", "kotlin"))
        }
    }
}

publishing {
    publications {
        create<MavenPublication>("mavenGradlePlugin") {
            from(components["java"])

            groupId = "com.github.vikas"
            artifactId = "dep-auditor"
            version = "1.0.0"
        }
    }
}