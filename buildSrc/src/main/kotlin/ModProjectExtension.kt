import org.gradle.api.Project
import org.gradle.api.GradleException
import org.gradle.api.JavaVersion
import java.io.File

fun Project.propOrNull(key: String) = findProperty(key)
fun Project.prop(key: String) = propOrNull(key) ?: throw GradleException("buildSrc: Property $key is not configured or value is empty")

fun Project.propStrOrNull(key: String): String? = propOrNull(key)?.toString()
fun Project.propStr(key: String): String = propStrOrNull(key)
    ?: throw GradleException("buildSrc: Property $key is not configured, value is empty, or cannot be converted to string")

fun Project.downloadDependencyMod(downloadUrl: String, fileName: String? = null): File? {
    return rootProject.downloadFile(
        downloadUrl = downloadUrl,
        outputDirPath = "${rootProject.projectDir}/libs",
        fileName = fileName
    )
}

val Project.modId get() = propStr("mod_id")
val Project.modName get() = propStr("mod_name")
val Project.modVersion get() = propStr("mod_version")
val Project.modMavenGroup get() = propStr("mod_maven_group")
val Project.modArchivesBaseName get() = propStr("mod_archives_base_name")

val Project.modHomepage get() = propStrOrNull("mod_homepage")
val Project.modLicense get() = propStrOrNull("mod_license")
val Project.modSources get() = propStrOrNull("mod_sources")

val Project.mcDependency get() = propStrOrNull("minecraft_dependency")
val Project.mcVersion get() = propStrOrNull("minecraft_version")
//val Project.mcVersionInt get() = propStrOrNull("mcVersion")?.toIntOrNull() ?: -1
val Project.mcVersionInt get() = parseMcVersionToNumber(mcVersion ?: "")
val Project.fabricLoaderVersion get() = propStrOrNull("loader_version")
val Project.fabricApiVersion get() = propStrOrNull("fabric_version")

val Project.javaVersion
    get() = when {
        mcVersionInt >= 260000  -> JavaVersion.VERSION_25
        mcVersionInt >= 12005   -> JavaVersion.VERSION_21
        mcVersionInt >= 11800   -> JavaVersion.VERSION_17
        mcVersionInt >= 11700   -> JavaVersion.VERSION_16
        else                    -> JavaVersion.VERSION_1_8
    }
val Project.mixinJavaVersion get() = "JAVA_${javaVersion}"

fun String.removeBuildSuffix(): String {
    // 匹配三种模式并移除（从末尾匹配）
    val regex = Regex("""-(?:[A-Za-z0-9]+-(?:release|\d+)|development)$""")
    return this.replace(regex, "")
}

val Project.fullProjectMavenVersion: String get() = fullProjectVersion.removeBuildSuffix()
val Project.fullProjectVersionName: String get() = "v$fullProjectVersion"
val Project.fullProjectVersion: String get() = getFullProjectVersion(mcVersion, modVersion)

private fun getFullProjectVersion(mcVersion: String?, modVersion: String): String {
    val buildNumber     = System.getenv("GITHUB_RUN_NUMBER")
    val commitHash      = System.getenv("COMMIT_HASH")
    val isRelease       = System.getenv("IS_THIS_RELEASE")?.toBoolean() == true || System.getenv("BUILD_RELEASE")?.toBoolean() == true
    val isPR            = System.getenv("BUILD_PR")?.toBoolean() == true
    val isDEV           = System.getenv("CI") == "true" || System.getenv("GITHUB_ACTIONS") == "true" || System.getenv("BUILD_DEV")?.toBoolean() == true
    val timestampMillis = System.currentTimeMillis()

    return when {
        isRelease   -> "$modVersion-mc$mcVersion-$commitHash-release"
        isPR        -> "$modVersion-mc$mcVersion-$commitHash-pr.$buildNumber"
        isDEV       -> {
            if (buildNumber != null) {
                "$modVersion-mc$mcVersion-$commitHash-$buildNumber"
            } else {
                "$modVersion-mc$mcVersion-$timestampMillis-development"
            }
        }
        else -> {
            "$modVersion-mc$mcVersion-$timestampMillis-development"
        }
    }
}

val Project.placeholderProps: Map<String, Any?>
    get() = mapOf(
        "mod_id" to modId,
        "mod_name" to modName,
        "mod_version" to fullProjectVersion,
        "mod_homepage" to modHomepage,
        "mod_license" to modLicense,
        "mod_sources" to modSources,
        "loader_version" to fabricLoaderVersion,
        "fabric_api_version" to fabricApiVersion,
        "minecraft_dependency" to mcDependency,
        "compatibility_level" to mixinJavaVersion,
    ).filterValues { it != null }.mapValues { it.value!! }
    