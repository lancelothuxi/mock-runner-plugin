plugins {
    id("java")
    id("org.jetbrains.intellij") version "1.17.4"
    id("com.github.johnrengelman.shadow") version "8.1.1"
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

// Separate configuration for agent dependencies that need shading
val agentDeps: Configuration by configurations.creating

dependencies {
    implementation("net.bytebuddy:byte-buddy:1.14.9")
    implementation("net.bytebuddy:byte-buddy-agent:1.14.9")
    implementation("com.google.code.gson:gson:2.10.1")

    agentDeps("net.bytebuddy:byte-buddy:1.14.9")
    agentDeps("com.google.code.gson:gson:2.10.1")
    
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
        useTestNG {
            suites("src/test/resources/testng.xml")
        }
        
        // Add agent to test JVM
        doFirst {
            val agentJar = file("build/libs/mock-agent-1.0.6-agent.jar")
            val mockConfig = file("src/test/resources/mock-config-test.json")
            
            if (agentJar.exists() && mockConfig.exists()) {
                jvmArgs("-javaagent:${agentJar.absolutePath}=${mockConfig.absolutePath}")
                println("✓ Mock Agent attached: ${agentJar.absolutePath}")
                println("✓ Mock Config: ${mockConfig.absolutePath}")
            } else {
                println("⚠ Warning: Agent or config not found")
                println("  Agent: ${agentJar.absolutePath} (exists: ${agentJar.exists()})")
                println("  Config: ${mockConfig.absolutePath} (exists: ${mockConfig.exists()})")
                println("  Tests will run without mocking")
            }
        }
        
        // Ensure agent is built before tests
        dependsOn("agentJar")
    }
    
    // 创建 Agent JAR 任务
    val agentJar by registering(com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar::class) {
        archiveBaseName.set("mock-agent")
        archiveClassifier.set("agent")

        from(sourceSets.main.get().output)

        // 包含依赖
        configurations = listOf(project.configurations.runtimeClasspath.get())

        // Shade Gson to avoid conflicts with user's Gson
        relocate("com.google.gson", "io.github.lancelothuxi.idea.plugin.mock.shaded.com.google.gson")

        // Optionally shade ByteBuddy as well
        relocate("net.bytebuddy", "io.github.lancelothuxi.idea.plugin.mock.shaded.net.bytebuddy")

        manifest {
            attributes(
                mapOf(
                    "Premain-Class" to "io.github.lancelothuxi.idea.plugin.mock.agent.MockAgent",
                    "Can-Redefine-Classes" to "true",
                    "Can-Retransform-Classes" to "true",
                    "Implementation-Version" to project.version.toString(),
                    "Built-At" to System.currentTimeMillis().toString()
                )
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

    // 注释掉快速构建脚本，因为现在使用 Gradle Shadow 插件进行依赖遮蔽
    // // 在 prepareSandbox 后自动更新 agent jar
    // named("prepareSandbox") {
    //     doLast {
    //         println("========================================")
    //         println("正在用快速编译覆盖 agent jar...")
    //         println("========================================")
    //         exec {
    //             commandLine("bash", "-c", "./scripts/ultra-fast-build.sh")
    //         }
    //     }
    // }
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}
