val antlrOutputDirectory = "build/generated-src/antlr/"
version = "1.0"

plugins {
    kotlin("jvm") version "1.4.21"
    antlr
    application
    id("com.github.johnrengelman.shadow") version "5.2.0"
}

application {
    mainClassName = "MainKt"
}

dependencies {
    val junitVer = "5.6.0"
    val antlrVer = "4.9.1"

    implementation(kotlin("stdlib-jdk8"))
    antlr("org.antlr:antlr4:$antlrVer")

    // JUnit5
    testImplementation("org.junit.jupiter:junit-jupiter:$junitVer")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:$junitVer")

}

repositories {
    mavenCentral()
}

sourceSets["main"].java.srcDir(antlrOutputDirectory)

tasks {
    generateGrammarSource {
        arguments = arguments + listOf("-package", "antlr")
        this.outputDirectory = file(antlrOutputDirectory + "antlr")
    }
    test {
        useJUnitPlatform()
    }

    compileKotlin {
        dependsOn(generateGrammarSource);
    }
}