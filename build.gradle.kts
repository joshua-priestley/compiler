val antlrOutputDirectory = "build/generated-src/antlr/"
version = "1.0"

plugins {
    kotlin("jvm") version "1.4.21"
    antlr
    application
    id("com.github.johnrengelman.shadow") version "5.2.0"
}

application {
    mainClassName = "compiler.MainKt"
}

dependencies {
    val junitVer = "5.6.0"
    val antlrVer = "4.9.1"

    implementation(kotlin("stdlib-jdk8"))
    antlr("org.antlr:antlr4:$antlrVer")

    // JUnit5
    testImplementation("org.junit.jupiter:junit-jupiter:$junitVer")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:$junitVer")

    // Fuel and GSON for testing
    implementation("com.github.kittinunf.fuel:fuel:2.3.1")
    implementation("com.github.kittinunf.fuel:fuel-gson:2.3.1")
    implementation("com.google.code.gson:gson:2.8.5")
    // Use JLine3 command-line library
    implementation("org.jline:jline-builtins:3.11.0")
    implementation("org.jline:jline-reader:3.11.0")
    implementation("org.jline:jline-terminal:3.11.0")
}

repositories {
    mavenCentral()
}

sourceSets["main"].java.srcDir(antlrOutputDirectory)

tasks {
    generateGrammarSource {
        arguments = arguments + listOf("-package", "antlr", "-no-listener", "-visitor", "-Werror")
        this.outputDirectory = file(antlrOutputDirectory + "antlr")
    }

    test {
        systemProperty("test.type", System.getProperty("test.type"))
        useJUnitPlatform()
    }

    compileKotlin {
        dependsOn(generateGrammarSource);
    }
}