package com.github.vontikov

import static javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI

import static com.github.vontikov.Const.*
import static com.github.vontikov.Util.*

import javax.xml.transform.stream.StreamSource
import javax.xml.validation.SchemaFactory

import org.gradle.api.DefaultTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.file.FileCollection
import org.gradle.api.tasks.JavaExec
import org.gradle.api.tasks.bundling.Compression
import org.gradle.api.tasks.bundling.Jar
import org.gradle.api.tasks.bundling.Tar
import org.gradle.api.tasks.compile.JavaCompile

class SbeGeneratorPlugin implements Plugin<Project> {

    Set<File> input
    FileCollection sbeClasspath

    @Override
    void apply(Project project) {
        prepareRepositories(project)

        def ext = project.extensions.create(EXTENSION_NAME,
                SbeGeneratorPluginExtension, project)

        project.afterEvaluate {
            def cfg = addSbeDependency(project, ext, CONFIGURATION_NAME)
            sbeClasspath = sbeClasspath(project, ext, cfg)
            input = tree(project, ext.src).files.absolutePath.toSet()

            def shouldValidate = ext.properties.get('shouldValidate')
            if (shouldValidate) {
                validateTask(project)
            }

            genJavaTask(project, ext)
            compileJavaTask(project, ext)
            packJavaTask(project, ext)

            genCppTask(project, ext)
            cmakeCppTask(project, ext)
            packCppTask(project, ext)
        }
    }

    void validateTask(Project project) {
        project.task(VALIDATE_TASK, type: DefaultTask) {
            group = PLUGIN_GROUP
            description = VALIDATE_TASK_DESCRIPTION

            def logger = project.logger
            def ext = project.extensions.sbeGenerator

            // extract sbe.xsd from sbe-all.jar
            def sbeAllJar = sbeClasspath.filter {
                it.name.startsWith('sbe-all')
            }.singleFile
            def xsdFile = project.zipTree(sbeAllJar).matching {
                include '**/sbe.xsd'
            }.singleFile
            if (!xsdFile) {
                throw new IllegalStateException('SBE schema not found')
            }

            def validator = SchemaFactory.newInstance(W3C_XML_SCHEMA_NS_URI)
                    .newSchema(xsdFile)
                    .newValidator()

            tree(project, ext.src).visit {
                def f = it.file
                logger.info("Validating: $f")
                validator.validate(new StreamSource(f))
            }
        }
    }

    void genJavaTask(Project project, SbeGeneratorPluginExtension ext) {
        project.task(GENERATE_JAVA_TASK, type: JavaExec) {
            group = PLUGIN_GROUP
            description = GENERATE_JAVA_TASK_DESCRIPTION

            def props = [
                'sbe.target.language': 'Java',
                'sbe.output.dir': ext.javaCodecsDir,
            ]

            def opts = ext.properties.get('javaOptions')
            opts.each { key, value -> props.putIfAbsent(key, value) }

            systemProperties = props
            main = 'uk.co.real_logic.sbe.SbeTool'
            args = input
            classpath = sbeClasspath
        }
    }

    void compileJavaTask(Project project, SbeGeneratorPluginExtension ext) {
        project.task(COMPILE_JAVA_TASK, type: JavaCompile,
                dependsOn: GENERATE_JAVA_TASK) {
            group = PLUGIN_GROUP
            description = COMPILE_JAVA_DESCRIPTION

            source = project.fileTree(dir: ext.javaCodecsDir, include: '**/*.java')
            destinationDirectory = project.file(ext.javaClassesDir)
            sourceCompatibility = ext.javaSourceCompatibility
            targetCompatibility = ext.javaTargetCompatibility
            classpath = sbeClasspath
        }
    }

    void packJavaTask(Project project, SbeGeneratorPluginExtension ext) {
        project.task(PACK_JAVA_TASK, type: Jar,
                dependsOn: COMPILE_JAVA_TASK) {
            group = PLUGIN_GROUP
            description = PACK_JAVA_TASK_DESCRIPTION

            from {
                project.files(ext.javaClassesDir, ext.javaCodecsDir)
            }

            destinationDirectory = project.file(ext.archivesDir)
            archiveFileName = "${project.name}-${project.version}.jar"

            manifest {
                attributes(
                    'Manifest-Version': '1.0',
                    'Implementation-Title': project.name,
                    'Implementation-Version': project.version
                )
            }
        }
    }

    void genCppTask(Project project, SbeGeneratorPluginExtension ext) {
        project.task(GENERATE_CPP_TASK, type: JavaExec) {
            group = PLUGIN_GROUP
            description = GENERATE_CPP_TASK_DESCRIPTION

            def props = [
                'sbe.target.language': 'CPP',
                'sbe.output.dir': ext.cppCodecsDir,
            ]

            def opts = ext.properties.get('cppOptions')
            opts.every { key, value -> props.putIfAbsent(key, value) }

            systemProperties = props
            main = 'uk.co.real_logic.sbe.SbeTool'
            args = input
            classpath = sbeClasspath
        }
    }

    void cmakeCppTask(Project project, SbeGeneratorPluginExtension ext) {
        project.task(CMAKE_CPP_TASK, type: DefaultTask,
                dependsOn: GENERATE_CPP_TASK) {
            group = PLUGIN_GROUP
            description = CMAKE_CPP_TASK_DESCRIPTION

            doLast {
                // normalize project name
                def nm = project.name.replaceAll('-|\\.', '_')

                // stubs
                project.copy {
                    into(project.file("${ext.cppCmakeDir}/include/"))
                    from(project.file(ext.cppCodecsDir))
                }

                // copy scripts
                final def cmakeScript = 'CMakeLists.txt'
                final def cmakeTemplate =  'Config.cmake.in'

                copyCmakeResourceToTmp(project, cmakeScript)
                copyCmakeResourceToTmp(project, cmakeTemplate)

                project.copy {
                    into(ext.cppCmakeDir)
                    from(project.file(TMP_DIR + '/' + cmakeScript))
                    filter {
                        it.startsWith('@') ?
                                "project(${nm} VERSION ${project.version})"
                                : it
                    }
                }

                project.copy {
                    into(ext.cppCmakeDir)
                    from(project.file(TMP_DIR + '/' + cmakeTemplate))
                    rename { 'cmake/' + it  }
                }
            }
        }
    }

    void packCppTask(Project project, SbeGeneratorPluginExtension ext) {
        project.task(PACK_CPP_TASK, type: Tar, dependsOn: CMAKE_CPP_TASK) {
            group = PLUGIN_GROUP
            description = 'Packs generated C++ codecs'

            from {
                project.files(ext.cppCmakeDir)
            }

            destinationDirectory = project.file(ext.archivesDir)
            archiveFileName = "${project.name}-${project.version}.tar.gz"

            compression = Compression.GZIP
        }
    }

    void copyCmakeResourceToTmp(Project project, String fn) {
        project.file(TMP_DIR).mkdirs()
        def is = getClass().getClassLoader().getResourceAsStream('cmake/' + fn)
        def os = new FileOutputStream(project.file(TMP_DIR + '/' + fn))

        int read
        byte[] bytes = new byte[4096]
        while ((read = is.read(bytes)) != -1) {
            os.write(bytes, 0, read)
        }
        is.close()
        os.close()
    }
}
