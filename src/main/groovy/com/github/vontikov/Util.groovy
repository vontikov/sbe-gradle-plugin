package com.github.vontikov

import static com.github.vontikov.Const.*

import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration
import org.gradle.api.file.ConfigurableFileTree
import org.gradle.api.file.FileCollection

final class Util {

    private Util() {
    }

    static ConfigurableFileTree tree(Project project, Src src) {
        def m = [dir: src.dir, includes: ['*.xml']]
        if (src.includes) {
            m.put('includes', src.includes)
        }
        if (src.excludes) {
            m.put('excludes', src.excludes)
        }
        return project.fileTree(m)
    }

    static void prepareRepositories(Project project) {
        project.repositories {
            mavenLocal()
            mavenCentral()
            jcenter()
        }
        project.repositories.addAll(project.buildscript.repositories)
    }

    static Configuration addSbeDependency(Project project,
            SbeGeneratorPluginExtension extension, String configName) {
        def logger = project.logger

        def sbeVersion = project.getProperties().computeIfAbsent(PROJECT_PROPERTY_SBE_VERSION,
            {k -> DEFAULT_SBE_VERSION})

        logger.info("Using SBE version: $sbeVersion")

        def sbeAllArtifact = "uk.co.real-logic:sbe-all:$sbeVersion"
        def sbeToolArtifact = "uk.co.real-logic:sbe-tool:$sbeVersion:sources"

        def config = project.getConfigurations().maybeCreate(configName)
        project.getDependencies().add(config.getName(), sbeAllArtifact)
        project.getDependencies().add(config.getName(), sbeToolArtifact)
        return config;
    }

    static FileCollection sbeClasspath(Project project,
            SbeGeneratorPluginExtension extension, Configuration config) {
        try {
            return project.files(config.getFiles())
        } catch (final Exception ex) {
            def sbeVersion = project.getProperties().get(PROJECT_PROPERTY_SBE_VERSION)
            throw new IllegalArgumentException(
                "SBE '$sbeVersion' not found", ex)
        }
    }
}
