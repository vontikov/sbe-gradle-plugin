package vontikov

import static vontikov.Const.*

import javax.naming.spi.ObjectFactory

import org.gradle.api.Action
import org.gradle.api.Project

import groovy.transform.ToString

@ToString(includeFields = true, includeNames = true)
class Src {

    String dir
    Iterable includes
    Iterable excludes

    Src(String dir, Iterable includes = [], Iterable excludes = []) {
        this.dir = dir
        this.includes = includes
        this.excludes = excludes
    }
}

class SbeGeneratorPluginExtension {

    Src src = new Src(DEFAULT_SRC_DIR)

    String targetNamespace

    String javaCodecsDir = DEFAULT_JAVA_CODECS_DIR
    String javaClassesDir = DEFAULT_JAVA_CLASSES_DIR

    String javaSourceCompatibility = DEFAULT_JAVA_SOURCE_COMPATIBILITY
    String javaTargetCompatibility = DEFAULT_JAVA_TARGET_COMPATIBILITY

    String cppCodecsDir = DEFAULT_CPP_CODECS_DIR
    String cppCmakeDir = DEFAULT_CPP_CMAKE_PROJECT_DIR

    String archivesDir = DEFAULT_ARCHIVES_DIR

    SbeGeneratorPluginExtension(Project project) {
    }

    @javax.inject.Inject
    SbeGeneratorPluginExtension(ObjectFactory objectFactory) {
        src = objectFactory.newInstance(Src)
    }

    void src(Action<? super Src> action) {
        action.execute(src)
    }
}
