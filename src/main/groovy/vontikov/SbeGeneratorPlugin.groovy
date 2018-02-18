package vontikov

import static vontikov.Util.*

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.JavaExec

class SbeGeneratorPlugin implements Plugin<Project> {

    static final PLUGIN_ID = 'sbe-generator-plugin'
    static final PLUGIN_GROUP = 'sbeGenerator'
    static final EXTENSION_NAME = 'sbeGenerator'
    static final CONFIGURATION_NAME = 'sbeGenerator'

    static final VALIDATE_TASK = 'sbeValidate'
    static final GENERATE_JAVA_TASK = 'sbeGenerateJava'
    static final GENERATE_CPP_TASK = 'sbeGenerateCpp'

    @Override
    void apply(Project project) {
        def extension = project.extensions.create(EXTENSION_NAME,
                SbeGeneratorPluginExtension, project)
        
        prepareRepositories(project)
        def config = sbeDependency(project, extension, CONFIGURATION_NAME)

        project.task(VALIDATE_TASK, type: SbeValidatorTask) {
            group = PLUGIN_GROUP
            description = 'Validates definitions'
        }

        def genJavaTask = project.task(GENERATE_JAVA_TASK, type: JavaExec) {
            group = PLUGIN_GROUP
            description = 'Generates Java stubs'

            def props = [
                'sbe.target.language': 'Java',
                'sbe.output.dir': extension.javaGenDir,
            ]
            if (extension.targetNamespace) {
                props.put('sbe.target.namespace', extension.targetNamespace)
            }
            
            systemProperties = props
            main = 'uk.co.real_logic.sbe.SbeTool'
        }

        def genCppTask = project.task(GENERATE_CPP_TASK, type: JavaExec) {
            group = PLUGIN_GROUP
            description = 'Generates CPP stubs'

            def props = [
                'sbe.target.language': 'CPP',
                'sbe.cpp.namespaces.collapse': 'true',
                'sbe.output.dir': extension.cppGenDir,
            ]
            if (extension.targetNamespace) {
                props.put('sbe.target.namespace', extension.targetNamespace)
            }
            
            systemProperties = props
            main = 'uk.co.real_logic.sbe.SbeTool'
        }

        project.afterEvaluate {
            def args = tree(project, extension.src).files.absolutePath as Set
            if (args.isEmpty()) {
                throw new IllegalStateException('No input')
            }
            genJavaTask.setArgs(args)
            genCppTask.setArgs(args)

            def classPath = sbeClasspath(project, extension, config)
            genJavaTask.setClasspath(classPath)
            genCppTask.setClasspath(classPath)
        }
    }
}
