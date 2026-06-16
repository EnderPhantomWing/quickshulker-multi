import org.gradle.api.Project
import org.gradle.api.initialization.Settings

object BuildToolUtils {
    fun parseMcVersionToNumber(mcVersionStr: String): Int {
        // Handle empty/blank strings
        if (mcVersionStr.isBlank()) return 0

        try {
            // Step 1: Remove suffixes (-fabric/-pre/-rc/-snapshot, etc.)
            val cleanVersion = mcVersionStr.split("-")[0]
                // Step 2: Keep only numbers and dots (filter out non-version characters)
                .replace(Regex("[^0-9.]"), "")

            // Step 3: Split version segments and convert to numbers
            val versionParts = cleanVersion.split(".")
                .filter { it.isNotEmpty() } // Filter empty segments (avoid malformed splits)

            val major = versionParts.getOrNull(0)?.toIntOrNull() ?: 0
            val minor = versionParts.getOrNull(1)?.toIntOrNull() ?: 0
            val patch = versionParts.getOrNull(2)?.toIntOrNull() ?: 0

            // Combine into a 5-digit number (e.g., 1.21.11 → 1*10000 + 21*100 + 11 = 12111)
            return major * 10000 + minor * 100 + patch
        } catch (e: Exception) {
            // For invalid version strings (e.g., "invalid"), return 0 to avoid breaking the build
            println("Failed to parse Minecraft version: $mcVersionStr, error: ${e.message}")
            return 0
        }
    }

    /**
     * Reverse: Convert numeric version back to string (e.g., 12111 → "1.21.11", 12006 → "1.20.6")
     */
    fun formatMcVersionNumber(mcVersionInt: Int): String {
        if (mcVersionInt <= 0) return "unknown"
        val major = mcVersionInt / 10000
        val minor = (mcVersionInt % 10000) / 100
        val patch = mcVersionInt % 100
        return if (patch > 0) "$major.$minor.$patch" else "$major.$minor"
    }

    // ========== Extendable other global utility functions ==========
    /**
     * Example: Clean special characters from a string (for file names / mod IDs)
     */
    fun cleanSpecialChars(str: String): String {
        return str.replace(Regex("[^a-zA-Z0-9_-]"), "_")
    }
}

fun parseMcVersionToNumber(mcVersionStr: String): Int = BuildToolUtils.parseMcVersionToNumber(mcVersionStr)
fun formatMcVersionNumber(mcVersionInt: Int): String = BuildToolUtils.formatMcVersionNumber(mcVersionInt)
fun cleanSpecialChars(str: String): String = BuildToolUtils.cleanSpecialChars(str)

fun Settings.parseMcVersionToNumber(mcVersionStr: String): Int = BuildToolUtils.parseMcVersionToNumber(mcVersionStr)
fun Settings.formatMcVersionNumber(mcVersionInt: Int): String = BuildToolUtils.formatMcVersionNumber(mcVersionInt)

fun Project.parseMcVersionToNumber(mcVersionStr: String): Int = BuildToolUtils.parseMcVersionToNumber(mcVersionStr)
fun Project.formatMcVersionNumber(mcVersionInt: Int): String = BuildToolUtils.formatMcVersionNumber(mcVersionInt)