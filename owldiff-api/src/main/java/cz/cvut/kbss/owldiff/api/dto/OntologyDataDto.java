package cz.cvut.kbss.owldiff.api.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import cz.cvut.kbss.owldiff.view.OWLDiffTreeModel;
import org.semanticweb.owlapi.model.OWLOntology;

import java.io.InputStream;
import java.util.List;

public class OntologyDataDto {

    private String ontologyName;

    private NodeModelDto data;

    @JsonIgnore
    private OWLOntology ontology;

    @JsonIgnore
    private OWLDiffTreeModel treeModel;

    public OWLDiffTreeModel getTreeModel() {
        return treeModel;
    }

    public void setTreeModel(OWLDiffTreeModel treeModel) {
        this.treeModel = treeModel;
    }

    public OWLOntology getOntology() {
        return ontology;
    }

    public void setOntology(OWLOntology ontology) {
        this.ontology = ontology;
    }

    public String getOntologyName() {
        return ontologyName;
    }

    public void setOntologyName(String ontologyName) {
        this.ontologyName = ontologyName;
    }

    public NodeModelDto getData() {
        return data;
    }

    public void setData(NodeModelDto data) {
        this.data = data;
    }
}