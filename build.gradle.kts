plugins {
    id("java")
    id("org.jetbrains.intellij") version "1.16.1"
}

group = "com.example"
version = "1.0.0"

repositories {
    mavenCentral()
}

intellij {
    version.set("2022.3")
    type.set("IC") // IC = Community, IU = Ultimate
    plugins.set(listOf("java"))
}

tasks {
    patchPluginXml {
        sinceBuild.set("223")
        untilBuild.set("233.*")
    }
    
    buildSearchableOptions {
        enabled = false
    }
    
    withType<JavaCompile> {
        options.encoding = "UTF-8"
    }
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}
