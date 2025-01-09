plugins {
    id("java-library")
    id("application")
    id("com.github.johnrengelman.shadow") version ("7.1.2")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.github.lipinskipawel:maelstrom-java:0.4.0")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.14.2")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jdk8:2.14.2")

    testImplementation("org.junit.jupiter:junit-jupiter:5.8.2")
    testImplementation("org.assertj:assertj-core:3.24.2")
}

tasks {
    java {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(21))
        }
    }

    application {
        mainClass = "com.github.lipinskipawel.whirlpool.App"
    }

    jar {
        manifest {
            attributes["Main-Class"] = application.mainClass
        }
    }

    shadowJar {
        archiveFileName = project.name + ".jar"
    }

    test {
        useJUnitPlatform()
    }

    wrapper {
        gradleVersion = "8.4"
        distributionType = Wrapper.DistributionType.ALL
    }

    distZip {
        dependsOn("shadowJar")
    }

    distTar {
        dependsOn("shadowJar")
    }

    startScripts {
        dependsOn("shadowJar")
    }

    startShadowScripts {
        dependsOn("jar")
    }
}
