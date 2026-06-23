import org.gradle.api.Project
import org.gradle.api.GradleException
import org.gradle.api.JavaVersion
import java.io.File

fun Project.propOrNull(key: String) = findProperty(key)
fun Project.prop(key: String) = propOrNull(key) ?: throw GradleException("buildSrc: Property $key is not configured or value is empty")

fun Project.propStrOrNull(key: String): String? = propOrNull(key)?.toString()
fun Project.propStr(key: String): String = propStrOrNull(key)
    ?: throw GradleException("buildSrc: Property $key is not configured, value is empty, or cannot be converted to string")

@Suppress("unused")
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

val Project.fullProjectVersionName: String get() = "v$fullProjectVersion"
val Project.fullProjectVersion: String get() = getFullProjectVersion(mcVersion, modVersion)

private fun getCommitCountNumber(workDir: File = File(".")): Int? {
    return try {
        val process = ProcessBuilder("git", "rev-list", "--count", "HEAD")
            .directory(workDir)
            .redirectErrorStream(true)
            .start()
        val output = process.inputStream.bufferedReader().readText().trim()
        val exitCode = process.waitFor()
        if (exitCode == 0) output.toInt() else null
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

private fun getCurrentGitBranch(workDir: File = File(".")): String? {
    return try {
        val process = ProcessBuilder("git", "rev-parse", "--abbrev-ref", "HEAD")
            .directory(workDir)
            .redirectErrorStream(true)
            .start()
        val output = process.inputStream.bufferedReader().readText().trim()
        val exitCode = process.waitFor()
        if (exitCode == 0) {
            if (output == "HEAD") null else output
        } else {
            "detached"
        }
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

private fun getFullProjectVersion(mcVersion: String?, modVersion: String): String {
    val timestampMillis = System.currentTimeMillis()
    val commitCount     = getCommitCountNumber()
    val currentBranch   = getCurrentGitBranch()
    val buildNumber     = System.getenv("GITHUB_RUN_NUMBER")
    val commitHash      = System.getenv("COMMIT_HASH")
    val isRelease       = System.getenv("BUILD_RELEASE")?.toBoolean() == true || System.getenv("IS_THIS_RELEASE")   ?.toBoolean() == true
    val isPR            = System.getenv("BUILD_PR")     ?.toBoolean() == true || System.getenv("IS_THIS_PR")        ?.toBoolean() == true
    val isCI            = System.getenv("BUILD_CI")     ?.toBoolean() == true || System.getenv("IS_THIS_CI")        ?.toBoolean() == true || System.getenv("GITHUB_ACTIONS") == "true"

    val base = "$modVersion-mc$mcVersion"
    return when {
        isRelease -> "${base}-${commitCount}-${commitHash}-release"
        isPR      -> "${base}-${currentBranch}.${commitCount}-${commitHash}-pr"
        else      -> "${base}-${currentBranch}.${
            if (isCI && buildNumber != null) "${commitCount}-${commitHash}-ci"
            else "${timestampMillis}-development"
        }"
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
    