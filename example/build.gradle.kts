plugins {
    id("com.github.vikas.dep-auditor") version "1.0.0-SNAPSHOT"
    id("java-library")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.apache.commons:commons-lang3:3.12.0")
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.9.22")
    implementation("com.google.guava:guava:33.0.0-jre")
    testImplementation("junit:junit:4.13.2")
}