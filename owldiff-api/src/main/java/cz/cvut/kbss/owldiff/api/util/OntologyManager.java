package cz.cvut.kbss.owldiff.api.util;

import cz.cvut.kbss.owldiff.OWLDiffException;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;

import java.io.InputStream;

public class OntologyManager {

    private final OWLOntologyManager ontologyManager;

    public OntologyManager() {
        this.ontologyManager = OWLManager.createConcurrentOWLOntologyManager();
    }

    public OWLOntology getOntologyByFile(InputStream fileStream) throws OWLDiffException {
        try {
            return ontologyManager.loadOntologyFromOntologyDocument(fileStream);
        } catch (OWLOntologyAlreadyExistsException e) {
            OWLOntologyID originalID = e.getOntologyID();
            return ontologyManager.getOntology(originalID);
        } catch (OWLOntologyCreationException e) {
            throw new OWLDiffException(OWLDiffException.Reason.PARSING_FAILED, "Parsing ontologies failed, check that they are valid.");
        }
    }
}
