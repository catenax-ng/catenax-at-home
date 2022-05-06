plugins {
    `java-library`
    id("application")
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

val javaVersion = 11

dependencies {
    val edcGroup = "org.eclipse.dataspaceconnector"
    val edcVersion = "0.0.1-SNAPSHOT"

    // Config
    implementation("$edcGroup:filesystem-configuration:$edcVersion")
    implementation("$edcGroup:filesystem-vault:$edcVersion")

    // APIs
    implementation("$edcGroup:data-plane-framework:$edcVersion")
    implementation("$edcGroup:data-plane-http:$edcVersion")
    implementation("$edcGroup:data-plane-api:$edcVersion")
    implementation("$edcGroup:observability-api:$edcVersion")

    // Core
    implementation("$edcGroup:core-base:$edcVersion")
    implementation("$edcGroup:core-boot:$edcVersion")

    // Supportive
    implementation("$edcGroup:http:$edcVersion")
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
    exclude("**/pom.properties", "**/pom.xm", "jndi.properties", "jetty-dir.css", "META-INF/maven/**")
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
