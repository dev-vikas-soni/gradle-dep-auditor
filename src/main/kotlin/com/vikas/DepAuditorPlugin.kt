package com.vikas

import org.gradle.api.Plugin
import org.gradle.api.Project

class DepAuditorPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        println("🚀 Auditor v${project.rootProject.version} → ${project.name}")

        project.tasks.register("depAudit") {
            group = "auditor"
            description = "🔍 Run full audit"
            doLast { println("✅ MILESTONE 1 COMPLETE!") }
        }
    }
}