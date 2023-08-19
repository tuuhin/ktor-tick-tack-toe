plugins {
    kotlin("jvm") version "1.8.20"
    id("io.ktor.plugin") version "2.3.3"
    id("org.jetbrains.kotlin.plugin.serialization") version "1.9.0"
}

group = "com.eva"
version = "0.0.1"

application {
    mainClass.set("io.ktor.server.netty.EngineMain")

    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

repositories {
    mavenCentral()
}

dependencies {
    //core
    implementation(libs.server.core)
    implementation(libs.server.sessions)
    implementation(libs.server.calllogging)
    implementation(libs.server.contentnegotiation)
    implementation(libs.server.kotlinxserialization)
    implementation(libs.server.websockets)
    implementation(libs.server.netty)
    implementation(libs.server.logger)
    //kotlin
    implementation(libs.kotlin.immutable)
    //koin-di
    implementation(libs.koin.ktor)
    implementation(libs.koin.logger)
    //test dependencies
    testImplementation(libs.server.test)
    testImplementation(libs.kotlin.test)
    testImplementation(libs.kotlin.coroutine.test)
    testImplementation(libs.client.contentnegotiation)
}
