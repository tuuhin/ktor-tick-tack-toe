import io.ktor.plugin.features.*

plugins {
    alias(libs.plugins.jetbrains.kotlin.jvm)
    alias(libs.plugins.ktor.plugin)
    alias(libs.plugins.kotlin.plugin.serialization)
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

ktor {
    docker {
        localImageName.set("tick-tack-toe-image")
        imageTag.set("$version")
        jreVersion.set(JreVersion.JRE_17)
        portMappings.set(
            listOf(
                DockerPortMapping(
                    80,
                    8080,
                    DockerPortMappingProtocol.TCP
                )
            )
        )
    }
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
    implementation(libs.ktor.status.pages)
    implementation(libs.ktor.forwarded.headers)
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
    testImplementation(libs.app.cash.turbine)
}
