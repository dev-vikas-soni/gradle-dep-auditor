plugins {
    `kotlin-dsl`
    `java-gradle-plugin`
}

group = "com.github.vikas"
version = "1.0.4"

repositories {
    mavenCentral()
}

gradlePlugin {
    plugins {
        create("depAuditor") {
            id = "com.github.vikas.dep-auditor"
            displayName = "DepAuditor - Unused Dependencies + Line Numbers"
            description = "Finds unused deps with exact line numbers + APK savings"
            implementationClass = "com.vikas.DepAuditorPlugin"
        }
    }
}