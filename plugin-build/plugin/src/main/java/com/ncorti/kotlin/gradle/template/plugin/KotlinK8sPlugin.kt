package com.ncorti.kotlin.gradle.template.plugin

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.internal.tasks.DefaultSourceSet
import org.gradle.api.model.ObjectFactory
import org.gradle.api.plugins.JavaPluginExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinProjectExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSetContainer
import javax.inject.Inject

const val EXTENSION_NAME = "kotlinK8s"
const val TASK_NAME = "generateCRDs"

@Suppress("UnnecessaryAbstractClass")
abstract class TemplatePlugin
@Inject constructor(val objectFactory: ObjectFactory) : Plugin<Project> {


    override fun apply(project: Project) {
        // Add the 'template' extension object
        val extension = project.extensions.create(EXTENSION_NAME, KotlinK8sExtension::class.java, project)

        project.pluginManager.apply("org.jetbrains.kotlin.jvm")

        project.extensions.configure(JavaPluginExtension::class.java) { it ->
            it.sourceSets.all { sourceSet ->
                val k8sset = createSourceDirectorySet((sourceSet as DefaultSourceSet).displayName, objectFactory)
                sourceSet.extensions.add(
                    KotlinK8sSourceDirectorySet::class.java,
                    KotlinK8sSourceDirectorySet.NAME,
                    k8sset
                )
                k8sset.srcDir("src/" + sourceSet.name + "/kubernetes")
                sourceSet.allSource.source(k8sset)

                val taskName = sourceSet.getTaskName("generate","CRDs")
                val outputDirectory = project.layout.buildDirectory.dir("generated/kotlin-k8s/${sourceSet.name}")
                project.extensions.configure(KotlinSourceSetContainer::class.java) {
                    it.sourceSets.findByName(sourceSet.name)?.kotlin?.srcDir(outputDirectory)
                }

                project.tasks.register(taskName, GenerateCRDsTask::class.java) {
                    it.description = "Generates Kotlin K8S CRDs for '${sourceSet.name}'"
                    it.source.setFrom(k8sset)
                    it.outputDirectory.set(outputDirectory)
                }
                // Add a task that uses configuration from the extension object
//                project.tasks.register(TASK_NAME, TemplateExampleTask::class.java) {
//                    it.tag.set(extension.tag)
//                    it.message.set(extension.message)
//                    it.outputFile.set(extension.outputFile)
//                }
            }
        }


    }

    private fun createSourceDirectorySet(
        parentDisplayName: String,
        objectFactory: ObjectFactory
    ): KotlinK8sSourceDirectorySet {
        val newInstance = objectFactory.newInstance(
            KotlinK8sSourceDirectorySet::class.java, objectFactory.sourceDirectorySet(
                "$parentDisplayName.yaml",
                "$parentDisplayName Generated Kubernetes CRDs classes"
            )
        )
        newInstance.filter.include("**/*.yaml")
        return newInstance
    }
}
