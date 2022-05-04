plugins {
    `java-library`
    id("application")
    id("com.github.johnrengelman.shadow") version "7.0.0"
    id("com.rameshkp.openapi-merger-gradle-plugin") version "1.0.4"
    id("io.swagger.core.v3.swagger-gradle-plugin") version "2.2.0"
}

val javaVersion = 11
val edcGroup = "org.eclipse.dataspaceconnector"
val edcVersion = "0.0.1-SNAPSHOT"
val rsApi = "3.0.0"
val swaggerJaxrs2Version = "2.1.11"

dependencies {
    api("$edcGroup:core-boot:$edcVersion")
    api("$edcGroup:core-base:$edcVersion")
    api("$edcGroup:http:$edcVersion")

    api("$edcGroup:filesystem-configuration:$edcVersion")

    api("$edcGroup:catalog-spi:$edcVersion")
    api("$edcGroup:contract-spi:$edcVersion")
    api("$edcGroup:transfer-spi:$edcVersion")
    api("$edcGroup:auth-spi:$edcVersion")

    api("jakarta.ws.rs:jakarta.ws.rs-api:3.0.0")

    implementation("com.auth0:java-jwt:3.19.1")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(javaVersion))
    }
}

application {
    mainClass.set("$edcGroup.boot.system.runtime.BaseRuntime")
}

tasks.withType<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar> {
    exclude("**/pom.properties", "**/pom.xm")
    mergeServiceFiles()
    archiveFileName.set("edc.jar")
}

repositories {
    mavenLocal()
    mavenCentral()
    maven {
        url = uri("https://maven.iais.fraunhofer.de/artifactory/eis-ids-public/")
    }
}

buildscript {
    dependencies {
        classpath("io.swagger.core.v3:swagger-gradle-plugin:2.1.12")
    }
}

pluginManager.withPlugin("io.swagger.core.v3.swagger-gradle-plugin") {

    dependencies {
        // this is used to scan the classpath and generate an openapi yaml file
        implementation("io.swagger.core.v3:swagger-jaxrs2-jakarta:${swaggerJaxrs2Version}")
        implementation("jakarta.ws.rs:jakarta.ws.rs-api:${rsApi}")
    }
// this is used to scan the classpath and generate an openapi yaml file
    tasks.withType<io.swagger.v3.plugins.gradle.tasks.ResolveTask> {
        outputFileName = project.name
        outputFormat = io.swagger.v3.plugins.gradle.tasks.ResolveTask.Format.YAML
        prettyPrint = true
        classpath = java.sourceSets["main"].runtimeClasspath
        buildClasspath = classpath
        resourcePackages = setOf("org.eclipse.dataspaceconnector")
        outputDir = file("${rootProject.projectDir.path}/resources/openapi/yaml")
    }
    configurations {
        all {
            exclude(group = "com.fasterxml.jackson.jaxrs", module = "jackson-jaxrs-json-provider")
        }
    }
}

openApiMerger {
    val yamlDirectory = file("${rootProject.projectDir.path}/resources/openapi/yaml")

    inputDirectory.set(yamlDirectory)
    output {
        directory.set(file("${rootProject.projectDir.path}/resources/openapi/"))
        fileName.set("openapi")
        fileExtension.set("yaml")
    }
    openApi {
        openApiVersion.set("3.0.1")
        info {
            title.set("API-Wrapper REST API")
            description.set("All files merged by open api merger")
            version.set("1.0.0-SNAPSHOT")
            license {
                name.set("Apache License v2.0")
                url.set("http://apache.org/v2")
            }
        }
    }
}

