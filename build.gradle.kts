import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.3.41"
    jacoco
    java
    `maven-publish`
    id("com.gradle.build-scan") version "2.0.2"
    id("org.springframework.boot") version "2.1.6.RELEASE"
    id("io.spring.dependency-management") version "1.0.7.RELEASE"
    id("org.jetbrains.kotlin.plugin.allopen") version "1.3.41"
    id("org.jetbrains.kotlin.plugin.jpa") version "1.3.41"
    id("org.jetbrains.kotlin.plugin.noarg") version "1.3.41"
    id("org.jetbrains.kotlin.plugin.spring") version "1.3.41"
    id("org.jlleitschuh.gradle.ktlint") version "8.2.0"
}

repositories {
    mavenCentral()
    jcenter()
}

val springBootVersion = "2.1.6.RELEASE"
val junitVersion = "5.5.1"
val jacksonVersion = "2.9.9"

fun springBootStarter(name: String) = "org.springframework.boot:spring-boot-starter-$name:$springBootVersion"
fun junitJupiter(name: String? = null) = "org.junit.jupiter:junit-jupiter${name?.let { "-$it" } ?: ""}:$junitVersion"

fun jacksonCore(artifact: String = "core", version: String = jacksonVersion) =
    jackson("core", "jackson", artifact, version)

fun jacksonModule(artifact: String, version: String = jacksonVersion) =
    jackson("module", "jackson-module", artifact, version)

fun jackson(group: String, artifact: String, artifactType: String, version: String = jacksonVersion) =
    "com.fasterxml.jackson.$group:$artifact-$artifactType:$version"

dependencies {
    compile("io.springfox:springfox-swagger-ui:2.9.2")
    compile("io.springfox:springfox-swagger2:2.9.2")
    compile(jacksonCore("databind", "2.9.9.1"))
    compile(jacksonCore())
    compile(jacksonModule("kotlin"))
    compile(kotlin("reflect"))
    compile(kotlin("stdlib"))
    compile(kotlin("stdlib-jdk8"))
    compile(springBootStarter("cache"))
    compile(springBootStarter("hateoas"))
    compile(springBootStarter("data-jpa"))
    compile(springBootStarter("data-rest"))
    compile(springBootStarter("web"))
    runtime("mysql:mysql-connector-java:8.0.16")
    runtime("org.hsqldb:hsqldb:2.4.1")
    runtime("com.h2database:h2:1.4.199")
    runtime("org.postgresql:postgresql:42.2.5")
    runtime(springBootStarter("tomcat"))
    testCompile("com.jayway.jsonpath:json-path:2.4.0")
    testCompile("org.mockito:mockito-core:3.0.0")
    testCompile("org.mockito:mockito-junit-jupiter:3.0.0")
    testCompile(kotlin("test"))
    testCompile(kotlin("test-junit5"))
    testCompile(springBootStarter("test"))
    testImplementation(junitJupiter())
}

group = "org.springframework.samples"
version = "1.5.2"
description = "petclinic"

tasks.withType<KotlinCompile>().configureEach {
    kotlinOptions.jvmTarget = "1.8"
}

tasks.test {
    useJUnitPlatform()
    testLogging {
        events("passed", "skipped", "failed")
    }
}
