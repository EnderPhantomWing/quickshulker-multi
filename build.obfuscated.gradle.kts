plugins {
	id("maven-publish")
	id("mod-plugin")
	id("net.fabricmc.fabric-loom-remap")
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
				@Suppress("UnstableApiUsage")
				includeGroupAndSubgroups(it)
				@Suppress("UnstableApiUsage")
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
	mappings(loom.officialMojangMappings())
	modImplementation("net.fabricmc:fabric-loader:${prop("loader_version")}")

	// Implementation Mods
	modImplementation("net.fabricmc.fabric-api:fabric-api:${prop("fabric_api_version")}")
	modImplementation("com.terraformersmc:modmenu:${prop("modmenu_version")}")
	modImplementation("curse.maven:reinforced-shulker-boxes-529874:${prop("reinforced_shulker_boxes_version")}")
	modImplementation("atonkish.reinfcore:reinforced-core:${prop("reinforced_core_version")}")

	modImplementation("me.fallenbreath:conditional-mixin-fabric:${prop("conditionalmixin_version")}")?.let { include(it) }
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
	val commonProgramArgs = listOf("--width", "1280", "--height", "720", "--username", "ShulkerDev")

	runs {
		named("client") {
            generateRunConfig.set(true)
            jvmArguments.set(commonVmArgs)
			programArguments.set(commonProgramArgs)
			runDirectory.file("../../run/client")
		}
		named("server") {
			runDirectory.file("../../run/server")
		}
	}
}

// https://github.com/hierynomus/license-gradle-plugin
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
tasks.named("classes") {
	dependsOn(tasks.named("licenseFormatMain"))
}
tasks.named("testClasses") {
	dependsOn(tasks.named("licenseFormatTest"))
}

publishing {
	publications {
		register("mavenJava", MavenPublication::class) {
			from(components["java"])
			artifactId = "${prop("minecraft_version")}"
			version = modVersion
		}
	}

	// See https://docs.gradle.org/current/userguide/publishing_maven.html for information on how to set up publishing.
	repositories {
		maven {
			name = "GitHubPackages"
			url = uri("https://maven.pkg.github.com/EnderPhantomWing/quickshulker-multi")
			credentials {
				username = System.getenv("GH_USERNAME")
				password = System.getenv("GH_TOKEN")
			}
		}
	}
}
