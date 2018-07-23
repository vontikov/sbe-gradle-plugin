package vontikov

import static org.junit.Assert.*
import static vontikov.Const.*

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
}

