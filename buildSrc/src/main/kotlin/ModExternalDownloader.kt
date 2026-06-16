import org.gradle.api.Project
import org.gradle.api.logging.LogLevel
import org.gradle.util.GradleVersion
import java.io.File
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URI
import java.nio.file.Files
import java.nio.file.StandardCopyOption

/**
 * Universal file download tool
 * Features:
 * 1. Supports any HTTP/HTTPS link
 * 2. Custom output directory (auto created)
 * 3. Optional file name (priority: user specified > server response header > extracted from URL)
 * 4. Timeout control (connect 10 seconds, read 30 seconds)
 * 5. File integrity check (non-empty validation)
 * 6. Friendly logging output
 */
object ExternalModDownloader {
    // Default timeout configuration (milliseconds)
    private const val CONNECT_TIMEOUT = 10000
    private const val READ_TIMEOUT = 30000

    // Default User-Agent (to avoid being rejected by some servers)
    private val USER_AGENT = "Gradle/${GradleVersion.current().version}"

    /**
     * Download a file
     * @param project Gradle project instance (for logging and path handling)
     * @param downloadUrl Download link (required)
     * @param outputDir Output directory (required, auto created)
     * @param fileName Custom file name (optional, auto-detected if null)
     * @return Downloaded file object, or null if failed
     */
    fun download(
        project: Project,
        downloadUrl: String,
        outputDir: File,
        fileName: String? = null
    ): File? {
        val trimmedUrl = downloadUrl.trim()
        require(trimmedUrl.isNotBlank()) { "Download URL cannot be empty!" }
        require(outputDir.isDirectory || outputDir.mkdirs()) { "Cannot create output directory: ${outputDir.absolutePath}" }
        println()

        return try {
            // 2. Determine file name (priority: user specified > response header > extracted from URL)
            // val targetFileName = fileName ?: getFileNameFromResponse(connection) ?: extractFileNameFromUrl(trimmedUrl)
            val targetFileName = fileName ?: extractFileNameFromUrl(trimmedUrl)
            ?: throw IOException("Unable to identify file name, please manually specify the fileName parameter")
            // 3. Build target file
            val targetFile = outputDir.resolve(targetFileName)
            // 4. Check if file already exists (avoid re-downloading)
            if (targetFile.exists() && targetFile.length() > 0) {
                project.logger.log(LogLevel.LIFECYCLE, "File already exists, skipping download: ${targetFile.absolutePath}")
                return targetFile
            }
            project.logger.log(LogLevel.LIFECYCLE, "Starting download: $trimmedUrl")
            project.logger.log(LogLevel.LIFECYCLE, "Output directory: ${outputDir.absolutePath}")
            // 1. Establish connection and get response info (for file name extraction and validation)
            val connection = createConnection(trimmedUrl)
            connection.connect()
            // 5. Perform download
            project.logger.log(LogLevel.LIFECYCLE, "Downloading: ${targetFile.absolutePath}")
            downloadFile(connection, targetFile)
            // 6. Verify file integrity
            if (!targetFile.exists() || targetFile.length() == 0L) {
                throw IOException("Downloaded file is empty or corrupted")
            }
            project.logger.log(LogLevel.LIFECYCLE, "Download successful: ${targetFile.absolutePath}")
            targetFile

        } catch (e: IllegalArgumentException) {
            project.logger.log(LogLevel.ERROR, "Download parameter error: ${e.message}")
            null
        } catch (e: IOException) {
            project.logger.log(LogLevel.ERROR, "Download failed: ${e.message}", e)
            null
        } catch (e: Exception) {
            project.logger.log(LogLevel.ERROR, "Unknown error: ${e.message}", e)
            null
        }
    }

    /**
     * Create an HTTP connection and configure timeout and request headers
     */
    private fun createConnection(urlString: String): HttpURLConnection {
        val url = URI.create(urlString).toURL()
        val connection = url.openConnection() as HttpURLConnection
        // Configure timeouts
        connection.connectTimeout = CONNECT_TIMEOUT
        connection.readTimeout = READ_TIMEOUT
        // Configure request headers
        connection.setRequestProperty("User-Agent", USER_AGENT)
        connection.setRequestProperty("Accept", "*/*")
        connection.instanceFollowRedirects = true  // Automatically follow redirects
        return connection
    }

    /**
     * Extract file name from server response header
     * Supports Content-Disposition header (e.g., attachment; filename="xxx.jar")
     */
    private fun getFileNameFromResponse(connection: HttpURLConnection): String? {
        return try {
            val disposition = connection.getHeaderField("Content-Disposition")
            if (disposition.isNullOrBlank()) return null
            // Match format filename="xxx" or filename=xxx
            val filenamePattern = Regex("filename[\"=]?([^\";]+)")
            val matchResult = filenamePattern.find(disposition)
            matchResult?.groupValues?.get(1)?.trim()?.takeIf { it.contains('.') }
        } catch (e: Exception) {
            null  // Return null if extraction fails, fallback to URL extraction
        }
    }

    /**
     * Extract file name from URL (handles links with parameters)
     * Examples:
     * - https://xxx.com/mod.jar → mod.jar
     * - https://xxx.com/download?file=mod-1.0.jar → mod-1.0.jar
     * - https://xxx.com/mod.jar?v=123 → mod.jar
     */
    private fun extractFileNameFromUrl(urlString: String): String? {
        return try {
            // Remove content after ? and #
            val cleanUrl = urlString.split('?', '#').first()
            // Extract the part after the last /
            val fileName = cleanUrl.substringAfterLast('/')
            // Ensure file name has an extension (at least 3 characters, e.g., .jar, .zip)
            if (fileName.contains('.') && fileName.substringAfterLast('.').length >= 2) {
                fileName
            } else {
                // Default to .jar when no valid extension (for modding scenarios)
                "downloaded-file-${System.currentTimeMillis()}.jar"
            }
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Actually write the file
     */
    private fun downloadFile(connection: HttpURLConnection, targetFile: File) {
        connection.inputStream.use { inputStream ->
            Files.copy(inputStream, targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING)
        }
    }
}

/**
 * Gradle project extension function (simplifies calls)
 * Example: project.downloadFile("url", file("outputDir"), "custom.jar")
 */
fun Project.downloadFile(
    downloadUrl: String,
    outputDir: File,
    fileName: String? = null
): File? {
    return ExternalModDownloader.download(this, downloadUrl, outputDir, fileName)
}

/**
 * Overloaded extension function (supports output directory path as a string)
 * Example: project.downloadFile("url", "outputDir", "custom.jar")
 */
fun Project.downloadFile(
    downloadUrl: String,
    outputDirPath: String,
    fileName: String? = null
): File? {
    return downloadFile(downloadUrl, file(outputDirPath), fileName)
}
