package com.foros.rs.provider;

import com.foros.jaxb.adapters.LocalizedParseException;
import com.foros.rs.schema.GeneratedModelBuilder;
import com.foros.rs.schema.InvalidOperationException;
import com.foros.rs.schema.InvalidTagException;
import com.foros.rs.schema.ModelInspector;
import com.foros.rs.schema.ModelNode;
import com.foros.rs.schema.ParseErrorByType;
import com.foros.rs.schema.PathNode;
import com.foros.rs.schema.UnexpectedCollectionException;
import com.foros.util.StringUtil;
import com.foros.validation.code.InputErrors;
import com.foros.validation.code.ForosError;
import com.foros.validation.constraint.violation.ConstraintViolation;
import com.foros.validation.constraint.violation.parsing.ParseErrorConstraintViolation;

import java.util.LinkedHashSet;
import java.util.Set;

import javax.xml.bind.ValidationEvent;
import javax.xml.bind.ValidationEventHandler;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLFilterImpl;

public class ErrorHandlingFilter extends XMLFilterImpl implements ValidationEventHandler {

    private static final String PATH_DELIMITER = ".";

    private static final ModelNode MODEL = new GeneratedModelBuilder().build();

    private StringBuilder elementText;
    private ModelInspector modelInspector;
    private Set<ConstraintViolation> errors = new LinkedHashSet<ConstraintViolation>();

    public ErrorHandlingFilter(XMLReader reader) {
        super(reader);
        elementText = new StringBuilder();
        modelInspector = new ModelInspector(MODEL);
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        try {
            modelInspector.stepNext(qName, attributes);
        } catch (InvalidTagException e) {
            String message = StringUtil.getLocalizedString("xml.parseError.invalidTag");
            addError(message, null, InputErrors.XML_WRONG_TAG);
        } catch (UnexpectedCollectionException e) {
            PathNode pathNode = modelInspector.getPathNode();
            String parentPath = pathNode.getParent().fullPath(PATH_DELIMITER);
            String message = StringUtil.getLocalizedString("xml.modelValidation.noCollectionExpected", pathNode.getName(), parentPath);
            addError(message, null, InputErrors.XML_UNEXPECTED_COLLECTION);
        } catch (InvalidOperationException e){
            String message = StringUtil.getLocalizedString("xml.parseError.invalidOperation");
            addError(message, null, InputErrors.XML_ENUM_PARSE_ERROR);
        }

        if (!modelInspector.defective()) {
            super.startElement(uri, localName, qName, attributes);
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        if (!modelInspector.defective()) {
            super.endElement(uri, localName, qName);
        }
        modelInspector.stepBack(qName);
        elementText = new StringBuilder();
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        super.characters(ch, start, length);
        elementText.append(ch, start, length);
    }


    @Override
    public boolean handleEvent(ValidationEvent event) {
        String message = event.getMessage();
        ForosError error = null;
        String value = elementText.toString();
        boolean handled = true;

        if (event.getSeverity() == ValidationEvent.FATAL_ERROR) {
            error = InputErrors.XML_ILL_FORMED;
            handled = false;
        } else {
            @SuppressWarnings({"ThrowableResultOfMethodCallIgnored"})
            Throwable linkedException = event.getLinkedException();
            if (linkedException != null) {
                if (linkedException.getCause() instanceof LocalizedParseException) {
                    LocalizedParseException lpe = (LocalizedParseException) linkedException.getCause();
                    message = lpe.getMessage();
                    error = lpe.getError();
                }
            }
        }

        if (error == null) {
            error = ParseErrorByType.resolveError(modelInspector.getNodeTypeName());
        }

        if (error != null && message == null) {
            // try to find default value
            message = StringUtil.getLocalizedStringWithDefault("errors." + error.name(), null);
        }

        addError(message, value, error);

        return handled;
    }

    public Set<ConstraintViolation> getErrors() {
        return errors;
    }

    private void addError(String message, String value, ForosError error) {
        String path = modelInspector.getPathNode().fullPath(PATH_DELIMITER);
        value = StringUtil.trimProperty(value);
        errors.add(new ParseErrorConstraintViolation(message, path, value, error));
    }
}
