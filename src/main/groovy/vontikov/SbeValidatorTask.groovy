package vontikov

import static javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI
import static vontikov.Util.*;

import javax.xml.transform.stream.StreamSource
import javax.xml.validation.SchemaFactory

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

class SbeValidatorTask extends DefaultTask {

    @TaskAction
    void action() {
        def logger = project.logger
        def ext = project.extensions.sbeGenerator
        def validator = SchemaFactory.newInstance(W3C_XML_SCHEMA_NS_URI)
                .newSchema(new URL(ext.xsd))
                .newValidator();
        tree(project, ext.src).visit {
            def f = it.file
            logger.info("Validating: $f")
            validator.validate(new StreamSource(f))
        }
    }
}