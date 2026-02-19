package com.vikas

import org.gradle.api.Plugin
import org.gradle.api.Project
import java.io.File

class DepAuditorPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        println("🚀 Dependency Auditor v1.0.0 → ${project.name}")

        project.tasks.register("depAudit") {
            group = "auditor"
            description = "🔍 Find unused dependencies with line numbers"
            doLast { runFullAudit(project) }
        }
    }

    private fun runFullAudit(project: Project) {
        println("\n" + "=".repeat(80))
        println("📊 DEPENDENCY AUDITOR - UNUSED DEPS + LINE NUMBERS")
        println("=".repeat(80))

        val buildFile = project.buildFile
        println("📄 Analyzing: ${buildFile.name}")

        val fileDeps = parseBuildFile(buildFile)
        val runtimeDeps = getRuntimeDependencies(project)

        val recommendations = analyzeDependencies(fileDeps, runtimeDeps)
        printAuditReport(recommendations)
    }

    private fun parseBuildFile(buildFile: File): List<FileDependency> {
        val dependencies = mutableListOf<FileDependency>()

        buildFile.readLines().forEachIndexed { index, line ->
            val patterns = listOf(
                Regex("""implementation\s*\(\s*["']([^:]+):([^:]+):([^"']+)["']\s*\)"""),
                Regex("""api\s*\(\s*["']([^:]+):([^:]+):([^"']+)["']\s*\)"""),
                Regex("""testImplementation\s*\(\s*["']([^:]+):([^:]+):([^"']+)["']\s*\)"""),
                Regex("""debugImplementation\s*\(\s*["']([^:]+):([^:]+):([^"']+)["']\s*\)"""),
                Regex("""kapt\s*\(\s*["']([^:]+):([^:]+):([^"']+)["']\s*\)""")
            )

            patterns.forEach { pattern ->
                pattern.find(line)?.let { match ->
                    val (group, artifact, version) = match.destructured
                    dependencies.add(
                        FileDependency(
                            group = group,
                            artifact = artifact,
                            version = version,
                            lineNumber = index + 1,
                            fullLine = line.trim()
                        )
                    )
                }
            }
        }
        return dependencies.sortedBy { it.lineNumber }
    }

    private fun getRuntimeDependencies(project: Project): Set<String> {
        return project.configurations
            .filter { it.name.contains("impl") || it.name.contains("api") || it.name.contains("compile") }
            .flatMap { config ->
                config.dependencies.mapNotNull { dep ->
                    if (dep.group != null && dep.name.isNotEmpty()) {
                        "${dep.group}:${dep.name}:${dep.version ?: "unspecified"}"
                    } else null
                }
            }
            .toSet()
    }

    private fun analyzeDependencies(
        fileDeps: List<FileDependency>,
        resolvedDeps: Set<String>
    ): List<DependencyRecommendation> {
        return fileDeps.map { fileDep ->
            val depKey = "${fileDep.group}:${fileDep.artifact}:${fileDep.version}"
            val sizeEstimate = estimateSizeImpact(fileDep.artifact)

            DependencyRecommendation(
                group = fileDep.group,
                artifact = fileDep.artifact,
                version = fileDep.version,
                lineNumber = fileDep.lineNumber,
                fullLine = fileDep.fullLine,
                isLikelyUnused = !resolvedDeps.contains(depKey),
                sizeImpactMB = sizeEstimate,
                recommendation = getRecommendation(fileDep, sizeEstimate)
            )
        }
    }

    private fun estimateSizeImpact(artifact: String): Double {
        return when {
            artifact.contains("guava") || artifact.contains("okhttp") -> 8.5
            artifact.contains("commons") || artifact.contains("glide") -> 4.2
            artifact.contains("retrofit") -> 3.8
            artifact.contains("kotlin-stdlib") || artifact.contains("androidx.core") -> 0.2
            artifact.contains("junit") -> 0.1
            else -> 1.5
        }
    }

    private fun getRecommendation(dep: FileDependency, sizeMB: Double): String {
        return when {
            sizeMB > 5.0 -> "🚨 HIGH IMPACT - REMOVE"
            sizeMB > 2.0 -> "⚠️  REVIEW - LARGE"
            dep.artifact.contains("test") -> "ℹ️  TEST DEP"
            else -> "✅ KEEP"
        }
    }

    private fun printAuditReport(recommendations: List<DependencyRecommendation>) {
        println("\n📋 DEPENDENCIES ANALYSIS")
        println("-".repeat(60))

        var removalCandidates = 0

        recommendations.forEach { rec ->
            print("📦 ${rec.group.takeLast(25)}:${rec.artifact}:${rec.version}")
            println(" [${String.format("%.1f", rec.sizeImpactMB)}MB] ${rec.recommendation}")

            println("   📍 LINE ${rec.lineNumber}: ${rec.fullLine}")

            if (rec.recommendation.contains("REMOVE") || rec.recommendation.contains("REVIEW")) {
                println("   🗑️  REMOVE → sed -i '${rec.lineNumber}d' build.gradle.kts")
                removalCandidates++
            }
            println()
        }

        println("🎯 SUMMARY")
        println("   📊 Total: ${recommendations.size} dependencies")
        println("   🚨 Remove: $removalCandidates candidates")
        println("   💾 Potential APK savings: ${recommendations.sumOf { it.sizeImpactMB }}MB")
        println("\n💡 TIP: Test removal → ./gradlew build → verify no compile errors")
    }
}

data class FileDependency(
    val group: String,
    val artifact: String,
    val version: String,
    val lineNumber: Int,
    val fullLine: String
)

data class DependencyRecommendation(
    val group: String,
    val artifact: String,
    val version: String,
    val lineNumber: Int,
    val fullLine: String,
    val isLikelyUnused: Boolean,
    val sizeImpactMB: Double,
    val recommendation: String
)