package com.github.vontikov

import static org.hamcrest.MatcherAssert.assertThat
import static org.hamcrest.Matchers.*

import static com.github.vontikov.Const.*

import java.lang.System

import org.gradle.testfixtures.ProjectBuilder
import org.junit.jupiter.api.Test

class UtilTest {

    static final USER_DIR = System.getProperty('user.dir')

    @Test
    void treeShouldSelectAll() {
        def project = ProjectBuilder.builder().build()
        def src = new Src(USER_DIR + '/src/test/resources/xml/util_all')

        def tree = Util.tree(project, src)
        assertThat(3, equalTo(tree.size()))
    }

    @Test
    void treeShouldIncludeExplicitly() {
        def project = ProjectBuilder.builder().build()

        def src = new Src(
                USER_DIR + '/src/test/resources/xml/util_some',
                ['*.txt', '*.lst'])
        def tree = Util.tree(project, src)
        assertThat(2, equalTo(tree.size()))
    }

    @Test
    void treeShouldExcludeExplicitly() {
        def project = ProjectBuilder.builder().build()
        def src = new Src(
                USER_DIR + '/src/test/resources/xml/util_some',
                [],
                ['*.txt', '*.lst'])
        def tree = Util.tree(project, src)
        assertThat(3, equalTo(tree.size()))
    }

    @Test
    void emptyTreeShodldThrow() {
        def project = ProjectBuilder.builder().build()
        def src = new Src(
                USER_DIR + '/src/test/resources/xml/util_some',
                ['*.no'])
        def tree = Util.tree(project, src)
        assertThat(tree.isEmpty(), is(true))
    }
}
