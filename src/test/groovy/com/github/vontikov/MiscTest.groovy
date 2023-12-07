package com.github.vontikov

import static org.hamcrest.MatcherAssert.assertThat
import static org.hamcrest.Matchers.*

import static com.github.vontikov.Const.*

import java.lang.System

import org.gradle.testfixtures.ProjectBuilder
import org.junit.jupiter.api.Test

class MiscTest {

    @Test
    void existingPropsShouldNotBeOverridden() {
        def props = [
            'sbe.target.language': 'Java',
            'sbe.output.dir': 'some_folder',
        ]

        def opts = [
            'sbe.target.language': 'Groovy',
            'sbe.output.dir': 'another_folder',
            'k0': 'v0',
            'k1': 'v2',
            'k3': 'v3',
        ]

        opts.each { key, value -> props.putIfAbsent(key, value) }

        assertThat(opts.size(), equalTo(props.size()))
        assertThat(props.get('sbe.target.language'), equalTo('Java'))
        assertThat(props.get('sbe.output.dir'), equalTo('some_folder'))
    }
}
