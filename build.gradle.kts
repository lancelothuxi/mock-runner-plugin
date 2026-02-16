plugins {
    id("java")
    id("org.jetbrains.intellij") version "1.17.4"
}

group = "io.github.lancelothuxi"
version = "1.0.6"

repositories {
    maven { url = uri("https://maven.aliyun.com/repository/public") }
    maven { url = uri("https://maven.aliyun.com/repository/central") }
    maven { url = uri("https://maven.aliyun.com/repository/gradle-plugin") }
    maven { url = uri("https://maven.aliyun.com/repository/google") }
    maven { url = uri("https://cache-redirector.jetbrains.com/intellij-dependencies") }
    maven { url = uri("https://www.jetbrains.com/intellij-repository/releases") }
    maven { url = uri("https://www.jetbrains.com/intellij-repository/snapshots") }
    gradlePluginPortal()
    mavenCentral()
}

dependencies {
    implementation("net.bytebuddy:byte-buddy:1.14.9")
    implementation("net.bytebuddy:byte-buddy-agent:1.14.9")
    implementation("com.google.code.gson:gson:2.10.1")
    
    // TestNG for testing
    testImplementation("org.testng:testng:7.8.0")
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
    
    // Configure TestNG
    test {
        useTestNG()
    }
    
    // 创建 Agent JAR 任务
    val agentJar by registering(Jar::class) {
        archiveBaseName.set("mock-agent")
        archiveClassifier.set("agent")
        
        from(sourceSets.main.get().output)
        
        // 包含依赖
        from(configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) })
        
        manifest {
            attributes(
                "Premain-Class" to "com.example.plugin.agent.MockAgent",
                "Can-Redefine-Classes" to "true",
                "Can-Retransform-Classes" to "true"
            )
        }
        
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    }
    
    // 确保在构建插件前先构建 agent jar
    prepareSandbox {
        dependsOn(agentJar)
        
        // 将 agent jar 复制到插件目录
        from(agentJar) {
            into("${intellij.pluginName.get()}/lib")
        }
    }
    
    // 在 prepareSandbox 后自动更新 agent jar
    named("prepareSandbox") {
        doLast {
            println("========================================")
            println("正在用快速编译覆盖 agent jar...")
            println("========================================")
            exec {
                commandLine("bash", "-c", "./scripts/ultra-fast-build.sh")
            }
        }
    }
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}
