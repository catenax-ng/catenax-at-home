plugins {
    `java-library`
    id("application")
    id("com.github.johnrengelman.shadow") version "7.0.0"
}

val javaVersion = 11

dependencies {
    val edcGroup = "org.eclipse.dataspaceconnector"
    val edcVersion = "0.0.1-SNAPSHOT"

    api("$edcGroup:core:$edcVersion")
    api("$edcGroup:ids:$edcVersion")
    api("$edcGroup:http:$edcVersion")

    api("$edcGroup:data-management-api:$edcVersion")
    api("$edcGroup:iam-mock:$edcVersion")

    api("$edcGroup:assetindex-memory:$edcVersion")
    api("$edcGroup:transfer-process-store-memory:$edcVersion")
    api("$edcGroup:contractnegotiation-store-memory:$edcVersion")
    api("$edcGroup:contractdefinition-store-memory:$edcVersion")
    api("$edcGroup:policy-store-memory:$edcVersion")

    api("$edcGroup:data-plane-transfer-spi:$edcVersion")
    api("$edcGroup:data-plane-transfer-core:$edcVersion")
    api("$edcGroup:data-plane-transfer-sync:$edcVersion")
    api("$edcGroup:http-receiver:$edcVersion")
    api("$edcGroup:token-generation-lib:$edcVersion")

    api("$edcGroup:filesystem-configuration:$edcVersion")
    api("$edcGroup:filesystem-vault:$edcVersion")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(javaVersion))
    }
}

application {
    mainClass.set("org.eclipse.dataspaceconnector.boot.system.runtime.BaseRuntime")
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
