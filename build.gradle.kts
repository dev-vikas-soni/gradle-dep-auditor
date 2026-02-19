plugins {
    `kotlin-dsl`
    `java-gradle-plugin`
    `maven-publish`
}

group = "com.github.vikas.dep-auditor"
version = "1.0.0-SNAPSHOT"

repositories {
    gradlePluginPortal()
    mavenCentral()
}

gradlePlugin {
    plugins {
        create("depAuditor") {
            id = "com.github.vikas.dep-auditor"
            displayName = "🚀 Dependency Auditor"
            description = "Unused deps, conflicts, vulns scanner"
            implementationClass = "com.vikas.DepAuditorPlugin"
        }
    }
}

publishing {
    repositories {
        mavenLocal()
    }
}

dependencies {
    implementation("org.ow2.asm:asm:9.7")
    implementation("org.ow2.asm:asm-tree:9.7")
}