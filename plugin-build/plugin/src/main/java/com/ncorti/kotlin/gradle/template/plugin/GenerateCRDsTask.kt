package com.ncorti.kotlin.gradle.template.plugin

import com.cjbooms.fabrikt.cli.CodeGenerationType
import com.cjbooms.fabrikt.cli.CodeGenerator
import com.cjbooms.fabrikt.cli.ModelCodeGenOptionType
import com.cjbooms.fabrikt.cli.SerializationLibrary
import com.cjbooms.fabrikt.configurations.Packages
import com.cjbooms.fabrikt.generators.MutableSettings
import com.cjbooms.fabrikt.generators.model.ModelGenerator
import com.cjbooms.fabrikt.model.KotlinSourceSet
import com.cjbooms.fabrikt.model.SourceApi
import com.cjbooms.fabrikt.util.YamlUtils
import net.mamoe.yamlkt.Yaml
import net.mamoe.yamlkt.YamlList
import net.mamoe.yamlkt.YamlMap
import org.gradle.api.DefaultTask
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.plugins.BasePlugin
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction

abstract class GenerateCRDsTask : DefaultTask() {
    init {
        description = "Generates the kotlin classes corresponding to K8s CRDs"

        group = BasePlugin.BUILD_GROUP
    }

    @get:InputFiles
    abstract val source: ConfigurableFileCollection


    @get:OutputDirectory
    abstract val outputDirectory: DirectoryProperty

    init {
        source.finalizeValueOnRead()
        outputDirectory.finalizeValueOnRead()
    }

    @TaskAction
    fun generate() {
        source.forEach { file ->
            val yaml = org.yaml.snakeyaml.Yaml()
            val map: Map<String, Any> = yaml.load(file.inputStream())
            val maps = (map.get("spec") as Map<String, Any>).get("versions") as List<Map<String, Any>>
            val elem = ((maps.get(0) as Map<String, Any>).get("schema") as Map<String, Any>).get("openAPIV3Schema")
            val converted = mapOf(
                "components" to mapOf(
                    "schemas" to mapOf(
                        "SomeCRDSpec" to elem
                    )
                )
            )
            val serializedOapi = yaml.dump(converted)
            println("Generating CRDs for $serializedOapi")
           val generator =  ModelGenerator(
                Packages(
                    "com.examplek8s"
                ),
                SourceApi.create(serializedOapi, emptyList(), file.toPath().parent),
                )
            MutableSettings.updateSettings(
                genTypes = setOf(CodeGenerationType.HTTP_MODELS),
                serializationLibrary = SerializationLibrary.KOTLINX_SERIALIZATION,
//                modelOptions = setOf(
//                    ModelCodeGenOptionType.SER
//                )
            )
            val sourceSet = KotlinSourceSet(generator.generate().files, outputDirectory.get().asFile.toPath())
            sourceSet.writeFileTo(outputDirectory.get().asFile)
        }
    }
}
