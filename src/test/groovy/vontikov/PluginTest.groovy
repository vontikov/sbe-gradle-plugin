package vontikov

import static org.gradle.testkit.runner.TaskOutcome.*
import static org.hamcrest.Matchers.*
import static org.junit.Assert.*
import static vontikov.Const.*

import org.gradle.testfixtures.ProjectBuilder
import org.gradle.testkit.runner.GradleRunner
import org.junit.Test

class PluginTest {

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
        assertThat(outcome, anyOf(is(SUCCESS), is(UP_TO_DATE)))
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
        assertThat(outcome, anyOf(is(SUCCESS), is(UP_TO_DATE)))

        def project = ProjectBuilder.builder().build()
        def stubsDir = new File(projectDir.absolutePath
                + '/' + DEFAULT_JAVA_CODECS_DIR)
        def stubs = project.fileTree(dir: stubsDir).files.name.toSet()

        assertThat(stubs, hasSize(6));
        assertThat(stubs, hasItem('package-info.java'))
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
        assertThat(outcome, anyOf(is(SUCCESS), is(UP_TO_DATE)))

        def project = ProjectBuilder.builder().build()
        def stubsDir = new File(projectDir.absolutePath
                + '/' + DEFAULT_CPP_CODECS_DIR)
        def stubs = project.fileTree(dir: stubsDir).files.name.toSet()

        assertThat(stubs, hasSize(2));
        assertThat(stubs, hasItem('Msg.h'))
        assertThat(stubs, hasItem('MessageHeader.h'))
    }

    @Test
    void shouldCompileJava() {
        def projectDir = new File(USER_DIR
                + '/src/test/resources/projects/generator')
        before(projectDir)

        def result = GradleRunner.create()
                .withProjectDir(projectDir)
                .withPluginClasspath(PLUGIN_CLASS_PATH)
                .withArguments(COMPILE_JAVA_TASK)
                .forwardOutput()
                .build()

        def outcome = result.task(':' + COMPILE_JAVA_TASK).getOutcome()
        assertThat(outcome, anyOf(is(SUCCESS), is(UP_TO_DATE)))

        def project = ProjectBuilder.builder().build()
        def classesDir = new File(projectDir.absolutePath
                + '/' + DEFAULT_JAVA_CLASSES_DIR)
        def classes = project.fileTree(dir: classesDir,
                include: '**/*', exclude: '**/*$*.class').files.name.toSet()

        assertThat(classes, hasSize(5));
        assertThat(classes, hasItem('MessageHeaderDecoder.class'))
        assertThat(classes, hasItem('MessageHeaderEncoder.class'))
        assertThat(classes, hasItem('MetaAttribute.class'))
        assertThat(classes, hasItem('MsgDecoder.class'))
        assertThat(classes, hasItem('MsgEncoder.class'))
    }

    @Test
    void shouldPackJava() {
        def projectDir = new File(USER_DIR
                + '/src/test/resources/projects/generator')
        before(projectDir)

        def result = GradleRunner.create()
                .withProjectDir(projectDir)
                .withPluginClasspath(PLUGIN_CLASS_PATH)
                .withArguments(PACK_JAVA_TASK)
                .forwardOutput()
                .build()

        def outcome = result.task(':' + PACK_JAVA_TASK).getOutcome()
        assertThat(outcome, is(SUCCESS))

        def project = ProjectBuilder.builder().build()
        def archiveName = new File(projectDir.absolutePath
                + '/' + DEFAULT_ARCHIVES_DIR
                + '/test-name-1.2.3.jar')
        def classes = project.zipTree(archiveName).files.name.toSet()
        classes.removeAll { it.contains('$') }

        assertThat(classes, hasSize(12));
        assertThat(classes, hasItem('MANIFEST.MF'))
        assertThat(classes, hasItem('package-info.java'))

        assertThat(classes, hasItem('MessageHeaderDecoder.java'))
        assertThat(classes, hasItem('MessageHeaderEncoder.java'))
        assertThat(classes, hasItem('MetaAttribute.java'))
        assertThat(classes, hasItem('MsgDecoder.java'))
        assertThat(classes, hasItem('MsgEncoder.java'))

        assertThat(classes, hasItem('MessageHeaderDecoder.class'))
        assertThat(classes, hasItem('MessageHeaderEncoder.class'))
        assertThat(classes, hasItem('MetaAttribute.class'))
        assertThat(classes, hasItem('MsgDecoder.class'))
        assertThat(classes, hasItem('MsgEncoder.class'))
    }

    @Test
    void shouldAppendCmakeScripts() {
        def projectDir = new File(USER_DIR
                + '/src/test/resources/projects/generator')
        before(projectDir)

        def result = GradleRunner.create()
                .withProjectDir(projectDir)
                .withPluginClasspath(PLUGIN_CLASS_PATH)
                .withArguments(CMAKE_CPP_TASK)
                .forwardOutput()
                .build()

        def outcome = result.task(':' + CMAKE_CPP_TASK).getOutcome()
        assertThat(outcome, is(SUCCESS))
    }

    @Test
    void shouldPackCppCmakeProject() {
        def projectDir = new File(USER_DIR
                + '/src/test/resources/projects/generator')
        before(projectDir)

        def result = GradleRunner.create()
                .withProjectDir(projectDir)
                .withPluginClasspath(PLUGIN_CLASS_PATH)
                .withArguments(PACK_CPP_TASK)
                .forwardOutput()
                .build()

        def outcome = result.task(':' + PACK_CPP_TASK).getOutcome()
        assertThat(outcome, is(SUCCESS))
    }

    private void before(File projectDir) {
        new File(projectDir, 'build').deleteDir()
    }
}
