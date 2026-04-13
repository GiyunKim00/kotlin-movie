plugins {
    kotlin("jvm") version "2.3.0"
    id("org.jlleitschuh.gradle.ktlint") version "14.0.1"
    id("org.springframework.boot") version "4.0.5"
    id("io.spring.dependency-management") version "1.1.7"
}

group = "camp.nextstep.edu"
version = "1.0-SNAPSHOT"

kotlin {
    jvmToolchain(21)
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.google.code.gson:gson:2.10.1")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testImplementation("org.junit.jupiter", "junit-jupiter", "5.14.3")
    testImplementation("org.assertj", "assertj-core", "3.27.7")
    testImplementation("io.kotest", "kotest-runner-junit5", "6.0.7")
}

tasks {
    test {
        useJUnitPlatform()
    }
    ktlint {
        verbose.set(true)
    }
}
