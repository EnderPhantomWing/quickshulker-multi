plugins {
    id("maven-publish")
    id("signing")
    id("mod-plugin")
    id("net.fabricmc.fabric-loom")
    id("com.github.hierynomus.license")
    id("com.replaymod.preprocess")
}

version = fullProjectVersionName
group = modMavenGroup

repositories {
    fun strictMaven(url: String, vararg groups: String) = exclusiveContent {
        forRepository { maven(url) }
        filter {
            groups.forEach {
                includeGroupAndSubgroups(it)
                includeGroupAndSubgroups("$it.*")
            }
        }
    }
    strictMaven("https://maven.fallenbreath.me/releases")
    strictMaven("https://maven.fabricmc.net")
    strictMaven("https://maven.shedaniel.me/")
    strictMaven("https://raw.githubusercontent.com/Aton-Kish/mcmod/maven")
    strictMaven("https://cursemaven.com", "curse.maven")
    strictMaven("https://api.modrinth.com/maven", "maven.modrinth")
    strictMaven("https://maven.nucleoid.xyz/releases", "eu.pb4")
    strictMaven("https://maven.terraformersmc.com/releases", "com.terraformersmc")
    strictMaven("https://jitpack.io")
}

dependencies {
    minecraft("com.mojang:minecraft:${prop("minecraft_version")}")
    implementation("net.fabricmc:fabric-loader:${prop("loader_version")}")

    // Implementation Mods
    implementation("net.fabricmc.fabric-api:fabric-api:${prop("fabric_api_version")}")
    implementation("com.terraformersmc:modmenu:${prop("modmenu_version")}")

    implementation("me.fallenbreath:conditional-mixin-fabric:${prop("conditionalmixin_version")}")?.let { include(it) }
    compileOnly("com.google.code.findbugs:jsr305:3.0.2")
}

if (System.getenv("JITPACK") == "true") {
    base.archivesName.set("$modArchivesBaseName-mc$mcVersion")
} else {
    base.archivesName.set(modArchivesBaseName)
}

loom {
    accessWidenerPath.set(file("quickshulker.accesswidener"))

    val commonVmArgs = listOf("-Dmixin.debug.export=true", "-Dmixin.debug.verbose=true", "-Dmixin.env.remapRefMap=true")
    val commonProgramArgs = listOf("--width", "1366", "--height", "768", "--username", "ShulkerDev")

    runs {
        named("client") {
            ideConfigGenerated(true)
            vmArgs(commonVmArgs)
            programArgs(commonProgramArgs)
            runDir= "../../run/client"
        }
        named("server") {
            runDir = "../../run/server"
        }
    }
}

license {
    // use "gradle licenseFormat" to apply license headers
    header = rootProject.file("HEADER.txt")
    include("**/*.java")
    skipExistingHeaders = true

    headerDefinitions {
        register("SLASHSTAR_STYLE_NEWLINE") {
            // ref: https://github.com/mathieucarbou/license-maven-plugin/blob/4c42374bb737378f5022a3a36849d5e23ac326ea/license-maven-plugin/src/main/java/com/mycila/maven/plugin/license/header/HeaderType.java#L48
            // modification: add a newline at the end
            firstLine = "/*"
            beforeEachLine = " * "
            endLine = " */" + System.lineSeparator()
            afterEachLine = ""
            skipLinePattern = null
            firstLineDetectionPattern = "(\\s|\\t)*/\\*.*\$"
            lastLineDetectionPattern = ".*\\*/(\\s|\\t)*\$"
            allowBlankLines = false
            isMultiline = true
            padLines = false
        }
    }
    mapping(mapOf("java" to "SLASHSTAR_STYLE_NEWLINE"))
}

tasks.register("signJar") {
    val keyId = System.getenv("GPG_SIGNING_KEY_ID") ?: project.findProperty("signing.keyId") as? String
    val password = System.getenv("GPG_SIGNING_PASSWORD") ?: project.findProperty("signing.password") as? String
    val secretKey = System.getenv("GPG_SIGNING_KEY") ?: project.findProperty("signing.secretKey") as? String

    onlyIf {
        keyId != null && password != null && secretKey != null
    }

    doFirst {
        // 将密钥信息设置到项目扩展属性中，供签名插件使用
        project.extra["signing.keyId"] = keyId
        project.extra["signing.password"] = password

        // 配置签名插件使用内存中的 PGP 密钥
        signing {
            useInMemoryPgpKeys(keyId, secretKey, password)
            sign("jar")
        }
    }
}

tasks.named("build") {
    dependsOn("signJar")
}

publishing {
    publications {
        register<MavenPublication>("mavenJava") {
            from(components["java"])
        }
    }

    // See https://docs.gradle.org/current/userguide/publishing_maven.html for information on how to set up publishing.
    repositories {
        mavenLocal()
    }
}
