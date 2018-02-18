package vontikov

import static vontikov.Const.*

import javax.naming.spi.ObjectFactory

import org.gradle.api.Action
import org.gradle.api.Project

import groovy.transform.ToString

@ToString(includeFields = true, includeNames = true)
class Src {

    final String dir 
    final Iterable includes
    final Iterable excludes
    
    Src(String dir, Iterable includes = [], Iterable excludes = []) {
        this.dir = dir
        this.includes = includes
        this.excludes = excludes
    }
}

class SbeGeneratorPluginExtension {
    
    String sbeVersion = DEFAULT_SBE_VERSION
    String xsd = DEFAULT_XSD_URL  
    Src src = new Src(DEFAULT_SRC_DIR)
    String targetNamespace
    String javaGenDir = DEFAULT_JAVA_GEN_DIR 
    String cppGenDir = DEFAULT_CPP_GEN_DIR 

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
