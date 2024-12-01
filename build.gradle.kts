plugins {
    kotlin("jvm") version "2.1.0"
    kotlin("plugin.allopen") version "2.0.20"
    id("org.jetbrains.kotlinx.benchmark") version "0.4.13"
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-benchmark-runtime:0.4.13")
}

sourceSets {
    main {
        kotlin.srcDir("src")
    }
}

tasks {
    wrapper {
        gradleVersion = "8.11.1"
    }
}

allOpen {
    annotation("org.openjdk.jmh.annotations.State")
}

benchmark {
    configurations {
        for (day in 1..25) {
            register("day${day.toString().padStart(2, '0')}") {
                include("Day${day.toString().padStart(2, '0')}")
            }
        }
    }

    targets {
        register("main")
    }
}