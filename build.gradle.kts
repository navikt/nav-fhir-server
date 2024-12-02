plugins {
    kotlin("jvm") version "2.0.21"
    kotlin("plugin.spring") version "2.0.21"
    id("org.springframework.boot") version "3.4.0"
    id("io.spring.dependency-management") version "1.1.6"
}

group = "no.nav.helse"

version = "0.0.1-SNAPSHOT"

java { toolchain { languageVersion = JavaLanguageVersion.of(21) } }

repositories { mavenCentral() }

val hapiVersion = "7.4.5"

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("org.springframework.boot:spring-boot-starter-thymeleaf")
    implementation("org.springframework.boot:spring-boot-starter-oauth2-resource-server")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("ca.uhn.hapi.fhir:hapi-fhir-base:${hapiVersion}")
    implementation("ca.uhn.hapi.fhir:hapi-fhir-structures-dstu2:${hapiVersion}")
    implementation("ca.uhn.hapi.fhir:hapi-fhir-structures-r4:${hapiVersion}") {
        // Fix commons-io vulnerability: https://osv.dev/vulnerability/GHSA-78wr-2p64-hpwj
        constraints {
            implementation("commons-io:commons-io:2.17.0")
            implementation("ca.uhn.hapi.fhir:org.hl7.fhir.r4:6.4.0")
        }
    }
    implementation("org.springframework.boot:spring-boot-starter-actuator")

    developmentOnly("org.springframework.boot:spring-boot-devtools")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testImplementation("io.projectreactor:reactor-test")

    // Fix xmlunit vulnerability: https://osv.dev/vulnerability/GHSA-chfm-68vv-pvw5
    testImplementation("org.xmlunit:xmlunit-core:2.10.0")

    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

kotlin { compilerOptions { freeCompilerArgs.addAll("-Xjsr305=strict") } }

tasks.withType<Test> { useJUnitPlatform() }
