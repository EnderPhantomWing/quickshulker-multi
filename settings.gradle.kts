import groovy.json.JsonSlurper

pluginManagement {
    repositories {
        maven {
            name = "Fabric"
            url = uri("https://maven.fabricmc.net/")
        }
        maven {
            name = "Jitpack"
            url = uri("https://jitpack.io")
            content {
                @Suppress("UnstableApiUsage")
                includeGroupAndSubgroups("com.github")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }

    resolutionStrategy {
        eachPlugin {
            if (requested.id.id == "com.replaymod.preprocess") {
                useModule("com.github.Fallen-Breath:preprocessor:${requested.version}")
            }
        }
    }
}

val jsonFile = file("settings.json")
val jsonText = jsonFile.readText()
val settings = JsonSlurper().parseText(jsonText) as Map<*, *>
val versions = settings["versions"] as List<*>

for (version in versions) {
    include(":$version")
    project(":$version").apply {
        projectDir = file("versions/$version")
        buildFileName = if (parseMcVersionToNumber(version as String) > 260000) {
            "../../build.unobfuscated.gradle.kts"
        } else {
            "../../build.obfuscated.gradle.kts"
        }
    }
}

fun parseMcVersionToNumber(mcVersionStr: String): Int {
    if (mcVersionStr.isBlank()) return 0
    return try {
        val cleanVersion = mcVersionStr.split("-")[0].replace(Regex("[^0-9.]"), "")
        val versionParts = cleanVersion.split(".").filter { it.isNotEmpty() }
        val major = versionParts.getOrNull(0)?.toIntOrNull() ?: 0
        val minor = versionParts.getOrNull(1)?.toIntOrNull() ?: 0
        val patch = versionParts.getOrNull(2)?.toIntOrNull() ?: 0
        major * 10000 + minor * 100 + patch
    } catch (_: Exception) {
        0
    }
}

rootProject.name = "quickshulker-multi"
