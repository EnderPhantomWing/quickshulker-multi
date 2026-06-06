plugins {
    id("maven-publish")
    id("com.github.hierynomus.license") version "0.16.1" apply false
    id("net.fabricmc.fabric-loom") version "1.16.2" apply false
    id("net.fabricmc.fabric-loom-remap") version "1.16.2" apply false
    id("com.replaymod.preprocess") version "c5abb4fb12"
}

preprocess {
    strictExtraMappings = false

    val mc12006     = createNode("1.20.6",  1_20_06,    "official")
    val mc12101     = createNode("1.21.1",  1_21_01,    "official")
    val mc12103     = createNode("1.21.3",  1_21_03,    "official")
    val mc12104     = createNode("1.21.4",  1_21_04,    "official")
    val mc12105     = createNode("1.21.5",  1_21_05,    "official")
    val mc12108     = createNode("1.21.8",  1_21_08,    "official")
    val mc12110     = createNode("1.21.10", 1_21_10,    "official")
    val mc12111     = createNode("1.21.11", 1_21_11,    "official")
    val mc260102    = createNode("26.1.2",  26_01_02,   "official")
    val mc260200    = createNode("26.2",    26_02_00,   "official")

    //1.20.6~before
    mc12101.link(   mc12006,    null)
    //1.21.1~latest
    mc12101.link(   mc12103,    null)
    mc12103.link(   mc12104,    null)
    mc12104.link(   mc12105,    null)
    mc12105.link(   mc12108,    null)
    mc12108.link(   mc12110,    null)
    mc12110.link(   mc12111,    null)
    mc12111.link(   mc260102,   file("mappings/mapping-1.21.11-26.1.2.txt"))
    mc260102.link(  mc260200,   file("mappings/mapping-26.1.2-26.2.txt"))

    // See https://github.com/Fallen-Breath/fabric-mod-template/blob/1d72d77a1c5ce0bf060c2501270298a12adab679/build.gradle#L55-L63
    for (node in getNodes()) {
        findProject(node.project)
            ?.ext
            ?.set("mcVersion", node.mcVersion)
    }
}

tasks.register("buildAndGather") {
    subprojects {
        dependsOn(tasks.named("build"))
    }
    doFirst {
        println("Gathering builds")
        val buildLibs = { p: Project -> p.layout.buildDirectory.dir("libs").get().asFile.toPath() }
        delete(fileTree(buildLibs(rootProject)) { include("*") })
        subprojects {
            copy {
                from(buildLibs(project)) {
                    include("*.jar")
                    exclude("*-dev.jar", "*-sources.jar", "*-shadow.jar")
                }
                into(buildLibs(rootProject))
                duplicatesStrategy = DuplicatesStrategy.INCLUDE
            }
        }
    }
}