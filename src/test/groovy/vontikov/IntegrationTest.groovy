package vontikov

import static org.gradle.testkit.runner.TaskOutcome.SUCCESS
import static org.hamcrest.Matchers.*
import static org.junit.Assert.*
import static vontikov.Const.*
import static vontikov.SbeGeneratorPlugin.*

import org.gradle.testfixtures.ProjectBuilder
import org.gradle.testkit.runner.GradleRunner
import org.junit.Test

class IntegrationTest {

    static final USER_DIR = System.getProperty('user.dir')

    final PLUGIN_CLASS_PATH = getClass()
            .classLoader
            .findResource("plugin-classpath.txt")
            .readLines()
            .collect { new File(it) }

    @Test
    void shouldValidate() {
        def projectDir = new File(USER_DIR 
                + '/src/test/resources/projects/validator')
        before(projectDir)

        def result = GradleRunner.create()
                .withProjectDir(projectDir)
                .withPluginClasspath(PLUGIN_CLASS_PATH)
                .withArguments(VALIDATE_TASK)
                .forwardOutput()
                .build()

        def outcome = result.task(':' + VALIDATE_TASK).getOutcome()
        assertEquals(SUCCESS, outcome)
    }
    
    @Test
    void shouldGenerateJava() {
        def projectDir = new File(USER_DIR 
                + '/src/test/resources/projects/generator')
        before(projectDir)

        def result = GradleRunner.create()
                .withProjectDir(projectDir)
                .withPluginClasspath(PLUGIN_CLASS_PATH)
                .withArguments(GENERATE_JAVA_TASK)
                .forwardOutput()
                .build()

        def outcome = result.task(':' + GENERATE_JAVA_TASK).getOutcome()
        assertEquals(SUCCESS, outcome)

        def project = ProjectBuilder.builder().build()
        def stubsDir = new File(projectDir.absolutePath + '/'
                + DEFAULT_JAVA_GEN_DIR)
        def stubs = project.fileTree(dir: stubsDir).files.name

        assertThat(stubs, hasSize(5));
        assertThat(stubs, hasItem('MessageHeaderDecoder.java'))
        assertThat(stubs, hasItem('MessageHeaderEncoder.java'))
        assertThat(stubs, hasItem('MetaAttribute.java'))
        assertThat(stubs, hasItem('MsgDecoder.java'))
        assertThat(stubs, hasItem('MsgEncoder.java'))
    }
    
    @Test
    void shouldGenerateCpp() {
        def projectDir = new File(USER_DIR 
                + '/src/test/resources/projects/generator')
        before(projectDir)

        def result = GradleRunner.create()
                .withProjectDir(projectDir)
                .withPluginClasspath(PLUGIN_CLASS_PATH)
                .withArguments(GENERATE_CPP_TASK)
                .forwardOutput()
                .build()

        def outcome = result.task(':' + GENERATE_CPP_TASK).getOutcome()
        assertEquals(SUCCESS, outcome)
        
        def project = ProjectBuilder.builder().build()
        def stubsDir = new File(projectDir.absolutePath + '/'
                + DEFAULT_CPP_GEN_DIR)
        def stubs = project.fileTree(dir: stubsDir).files.name

        assertThat(stubs, hasSize(2));
        assertThat(stubs, hasItem('Msg.h'))
        assertThat(stubs, hasItem('MessageHeader.h'))
    }
    
    private void before(File projectDir) {
        def buildDir = new File(projectDir, 'build')
        if(buildDir.exists())  {
            buildDir.deleteDir()
        }
    }
}
