package cz.cvut.kbss.owldiff.api.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.semanticweb.owlapi.model.OWLAxiom;

import java.util.List;

public class NodeModelDto {

    private int id;

    private String data;

    private String explanations;

    private boolean common;
    private boolean inferred;
    private boolean useCex;

    private List<NodeModelDto> children;

    @JsonIgnore
    private OWLAxiom axiom;

    public OWLAxiom getAxiom() {
        return axiom;
    }

    public void setAxiom(OWLAxiom axiom) {
        this.axiom = axiom;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getExplanations() {
        return explanations;
    }

    public void setExplanations(String explanations) {
        this.explanations = explanations;
    }

    public boolean isCommon() {
        return common;
    }

    public void setCommon(boolean common) {
        common = common;
    }

    public boolean isInferred() {
        return inferred;
    }

    public void setInferred(boolean inferred) {
        inferred = inferred;
    }

    public boolean isUseCex() {
        return useCex;
    }

    public void setUseCex(boolean useCex) {
        this.useCex = useCex;
    }

    public List<NodeModelDto> getChildren() {
        return children;
    }

    public void setChildren(List<NodeModelDto> children) {
        this.children = children;
    }
}
