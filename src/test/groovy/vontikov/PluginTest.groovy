package vontikov

import static org.junit.Assert.*
import static vontikov.SbeGeneratorPlugin.*

import org.gradle.api.Project
import org.gradle.api.tasks.JavaExec
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Test

class PluginTest {

    @Test
    void shouldHaveExtension() {
        Project project = ProjectBuilder.builder().build()
        project.pluginManager.apply PLUGIN_ID
        assertTrue(project.extensions.sbeGenerator
                instanceof SbeGeneratorPluginExtension)
    }

    @Test
    void shouldHaveTasks() {
        Project project = ProjectBuilder.builder().build()
        project.pluginManager.apply PLUGIN_ID

        assertTrue(project.tasks[VALIDATE_TASK] instanceof SbeValidatorTask)
        assertTrue(project.tasks[GENERATE_JAVA_TASK] instanceof JavaExec)
        assertTrue(project.tasks[GENERATE_CPP_TASK] instanceof JavaExec)
    }
}
