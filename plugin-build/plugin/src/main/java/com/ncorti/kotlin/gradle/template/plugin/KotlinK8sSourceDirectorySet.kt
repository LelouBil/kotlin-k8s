package com.ncorti.kotlin.gradle.template.plugin

import org.gradle.api.file.SourceDirectorySet
import org.gradle.api.internal.file.DefaultSourceDirectorySet
import org.gradle.api.internal.tasks.TaskDependencyFactory
import javax.inject.Inject

open class KotlinK8sSourceDirectorySet
    @Inject constructor(sourceDirectorySet: SourceDirectorySet, taskDependentFactory: TaskDependencyFactory) :
    DefaultSourceDirectorySet(
        sourceDirectorySet,
        taskDependentFactory
    ) {
    companion object {
        const val NAME = "kubernetes"
    }
}
