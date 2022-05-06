plugins {
    `java-library`
    id("application")
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

val javaVersion = 11

dependencies {
    val edcGroup = "org.eclipse.dataspaceconnector"
    val edcVersion = "0.0.1-SNAPSHOT"

    // Core
    implementation("$edcGroup:core:$edcVersion")
    implementation("$edcGroup:transfer:$edcVersion")
    implementation("$edcGroup:contract:$edcVersion")

    // Configurations
    implementation("$edcGroup:filesystem-configuration:$edcVersion")
    implementation("$edcGroup:filesystem-vault:$edcVersion")

    // API
    implementation("$edcGroup:data-management-api:$edcVersion")
    implementation("$edcGroup:observability-api:$edcVersion")

    // IDS
    implementation("$edcGroup:ids:$edcVersion")

    // IAM
    implementation("$edcGroup:iam-mock:$edcVersion")

    // Dataplane
    implementation("$edcGroup:data-plane-transfer-sync:$edcVersion")

    // Supportive
    implementation("$edcGroup:http:$edcVersion")
    implementation("$edcGroup:http-receiver:$edcVersion")
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
