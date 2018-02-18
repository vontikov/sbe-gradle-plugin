package vontikov

import static vontikov.SbeGeneratorPlugin.*
import static org.junit.Assert.*

import org.gradle.testfixtures.ProjectBuilder
import org.junit.Test

class UtilTest {

    static final USER_DIR = System.getProperty('user.dir')

    @Test
    void treeShouldSelectAll() {
        def project = ProjectBuilder.builder().build()
        def src = new Src(USER_DIR + '/src/test/resources/xml/util_all')

        def tree = Util.tree(project, src)
        assertEquals(3, tree.size())
    }

    @Test
    void treeShouldIncludeExplicitly() {
        def project = ProjectBuilder.builder().build()

        def src = new Src(
                USER_DIR + '/src/test/resources/xml/util_some',
                ['*.txt', '*.lst'])
        def tree = Util.tree(project, src)
        assertEquals(2, tree.size())
    }

    @Test
    void treeShouldExcludeExplicitly() {
        def project = ProjectBuilder.builder().build()
        def src = new Src(
                USER_DIR + '/src/test/resources/xml/util_some',
                [],
                ['*.txt', '*.lst'])
        def tree = Util.tree(project, src)
        assertEquals(3, tree.size())
    }

    @Test
    void emptyTreeShodldThrow() {
        def project = ProjectBuilder.builder().build()
        def src = new Src(
                USER_DIR + '/src/test/resources/xml/util_some',
                ['*.no'])
        def tree = Util.tree(project, src)
        assertTrue(tree.isEmpty())
    }
    
    @Test
    void shouldResolveDefaultSbeVersion() {
        def project = ProjectBuilder.builder().build()
        def extension = project.extensions.create(EXTENSION_NAME,
                SbeGeneratorPluginExtension, project)

        Util.prepareRepositories(project)
        def config = Util.sbeDependency(project, extension, CONFIGURATION_NAME)
        assertNotNull(Util.sbeClasspath(project, extension, config))
    }

    @Test
    void shouldResolveSpecificSbeVersion() {
        def project = ProjectBuilder.builder().build()
        def extension = project.extensions.create(EXTENSION_NAME,
                SbeGeneratorPluginExtension, project)
        extension.sbeVersion = '1.7.5'

        Util.prepareRepositories(project)
        def config = Util.sbeDependency(project, extension, CONFIGURATION_NAME)
        assertNotNull(Util.sbeClasspath(project, extension, config))
    }

    @Test(expected = IllegalArgumentException.class)
    void shouldNotResolveIncorrectSbeVersion() {
        def project = ProjectBuilder.builder().build()
        def extension = project.extensions.create(EXTENSION_NAME,
                SbeGeneratorPluginExtension, project)
        extension.sbeVersion = '1.7'

        Util.prepareRepositories(project)
        def config = Util.sbeDependency(project, extension, CONFIGURATION_NAME)
        Util.sbeClasspath(project, extension, config)
    }
}

