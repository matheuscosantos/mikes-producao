import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val mockkVersion: String by project
val kluentVersion: String by project
val awsSdkVersion: String by project
val cucumberVersion: String by project
val restAssuredVersion: String by project
val springCloudAwsVersion: String by project
val testContainersVersion: String by project
val kotlinxCoroutinesCoreVersion: String by project

plugins {
    jacoco
    application

    id("org.springframework.boot") version "3.2.1"
    id("io.spring.dependency-management") version "1.1.4"
    //id("se.thinkcode.cucumber-runner") version "0.0.11"

    kotlin("jvm") version "1.9.21"
    kotlin("plugin.jpa") version "1.9.21"
    kotlin("plugin.spring") version "1.9.21"
    kotlin("plugin.allopen") version "1.9.21"
}

group = "br.com.fiap.mikes.production"

java {
    sourceCompatibility = JavaVersion.VERSION_21
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("io.projectreactor.kotlin:reactor-kotlin-extensions")
    implementation("org.jetbrains.kotlin:kotlin-reflect")

    implementation("io.awspring.cloud:spring-cloud-aws-starter-sqs:$springCloudAwsVersion")
    implementation("io.awspring.cloud:spring-cloud-aws-starter-sns:$springCloudAwsVersion")

    implementation("com.fasterxml.jackson.core:jackson-databind")

    implementation("org.flywaydb:flyway-core")

    runtimeOnly("org.postgresql:postgresql")
    runtimeOnly("com.h2database:h2")

    testImplementation(platform("io.cucumber:cucumber-bom:$cucumberVersion"))

    testImplementation("org.springframework.boot:spring-boot-starter-test")

    testImplementation("io.cucumber:cucumber-java")
    testImplementation("io.cucumber:cucumber-spring")
    testImplementation("io.cucumber:cucumber-core")

    testImplementation("io.mockk:mockk:$mockkVersion")
    testImplementation("org.amshove.kluent:kluent:$kluentVersion")

    testImplementation("io.rest-assured:rest-assured:$restAssuredVersion")

    testImplementation("org.testcontainers:localstack:$testContainersVersion")
    testImplementation("org.testcontainers:testcontainers:$testContainersVersion")

    testImplementation("com.amazonaws:aws-java-sdk-sqs:$awsSdkVersion")
    testImplementation("com.amazonaws:aws-java-sdk-sns:$awsSdkVersion")

    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$kotlinxCoroutinesCoreVersion")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs += "-Xjsr305=strict"
        jvmTarget = "21"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

jacoco {
    toolVersion = "0.8.9"
}

tasks.test {
    finalizedBy(tasks.jacocoTestReport)
}

tasks.jacocoTestReport {
    dependsOn(tasks.test)

    reports {
        csv.required = false
        xml.required = false
        html.required = true
    }
}

configurations {
    val cucumberRuntime by creating {
        extendsFrom(
            configurations.testImplementation.get(),
            configurations.implementation.get(),
            configurations.runtimeOnly.get()
        )
    }
}

task("cucumber") {
    dependsOn("assemble", "testClasses")
    doLast {
        javaexec {
            mainClass = "io.cucumber.core.cli.Main"
            classpath = configurations["cucumberRuntime"] + sourceSets.main.get().output + sourceSets.test.get().output
            args = listOf(
                "--plugin", "pretty",
                "--glue", "classpath:br.com.fiap.mikes.production.cucumber",
                "src/test/resources/cucumber/features"
            )
        }
    }
}

//cucumber {
//    main = "io.cucumber.core.cli.Main"
//    glue = "classpath:br.com.fiap.mikes.production.cucumber"
//    plugin = arrayOf("pretty", "html:build/reports/cucumber/index.html")
//    shorten = "manifest"
//    featurePath = "src/test/resources/cucumber/features"
//}