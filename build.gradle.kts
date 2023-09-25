plugins {
    java
    kotlin("jvm") version "1.8.22"
    kotlin("plugin.spring") version "1.8.22" apply false
    id("org.springframework.boot") version "3.1.4" apply false
}

allprojects {
    group = "example"
    version = "0.0.1-SNAPSHOT"
}
