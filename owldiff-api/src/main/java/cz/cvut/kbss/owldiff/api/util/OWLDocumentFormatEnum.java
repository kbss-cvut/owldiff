package cz.cvut.kbss.owldiff.api.util;

import org.semanticweb.owlapi.formats.*;
import org.semanticweb.owlapi.model.OWLDocumentFormat;

public enum OWLDocumentFormatEnum {
    //TODO: Add more formats? Source http://robot.obolibrary.org/merge
    OBO(new OBODocumentFormat(), "obo"),
    OWL(new RDFXMLDocumentFormat(), "owl"),
    TTL(new TurtleDocumentFormat(), "ttl"),
    OWX(new OWLXMLDocumentFormat(), "owx"),
    OMN(new ManchesterSyntaxDocumentFormat(), "omn"),
    OFN(new FunctionalSyntaxDocumentFormat(), "ofn");


    private OWLDocumentFormat format;
    private String extension;
    private OWLDocumentFormatEnum(OWLDocumentFormat format, String extension){
        this.format = format;
        this.extension = extension;
    }

    public OWLDocumentFormat getFormat(){
        return format;
    }

    public String getExtension(){
        return extension;
    }
}
