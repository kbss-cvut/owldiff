package cz.cvut.kbss.owldiff.api.util;

import cz.cvut.kbss.owldiff.ExplanationManager;
import cz.cvut.kbss.owldiff.api.dto.NodeModelDto;
import cz.cvut.kbss.owldiff.syntax.Syntax;
import cz.cvut.kbss.owldiff.view.nodes.*;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLNamedObject;

import javax.swing.*;
import java.awt.*;

public class NodeModelDataParser implements NodeModelVisitor {

    private Syntax syntax;
    private NodeModelDto nodeModelDto;
    private ExplanationManager explanationManager;
    private WriteCallbackImpl writeCallback;

    public NodeModelDataParser(Syntax syntax, ExplanationManager expl) {
        this.syntax = syntax;
        this.nodeModelDto = new NodeModelDto();
        this.explanationManager = expl;
        writeCallback = new WriteCallbackImpl();
    }

    @Override
    public void visit(AxiomNodeModel anm) {
        final OWLAxiom ax = anm.getData();
        final StringBuilder b = new StringBuilder();
        b.append(syntax.writeAxiom(ax, false, null, true));
        nodeModelDto.setAxiom(ax);
        nodeModelDto.setId(anm.hashCode());
        nodeModelDto.setData(b.toString());
        nodeModelDto.setCommon(anm.isCommon());
        nodeModelDto.setIsAxiom(true);
        nodeModelDto.setInferred(anm.isInferred());
    }

    @Override
    public void visit(ClassNodeModel cnm) {
        Object o = cnm.getData();
        nodeModelDto.setId(cnm.hashCode());
        nodeModelDto.setData(getData(o));
        nodeModelDto.setUseCex(cnm.isInBothLandR() || cnm.isJustInL() || cnm.isJustR());
        setProperties(cnm);
    }

    @Override
    public void visit(PropertyNodeModel pnm) {
        Object o = pnm.getData();
        nodeModelDto.setId(pnm.hashCode());
        nodeModelDto.setData(getData(o));
        setProperties(pnm);
    }

    @Override
    public void visit(CategoryNodeModel cnm) {
        Object o = cnm.getData();
        nodeModelDto.setId(cnm.hashCode());
        nodeModelDto.setData(getData(o));
        setProperties(cnm);
    }

    @Override
    public void visit(AnnotationNodeModel anm) {
        final StringBuilder b = new StringBuilder();
        nodeModelDto.setId(anm.hashCode());
        b.append(syntax.write(anm.getData(), false, true));
        nodeModelDto.setData(b.toString());
    }

    @Override
    public void visit(OntologyNodeModel onm) {
        nodeModelDto.setId(onm.hashCode());
        nodeModelDto.setData(onm.getData().getOntologyID().toString());
    }

    @Override
    public void visit(IndividualNodeModel inm) {
        Object o = inm.getData();
        nodeModelDto.setId(inm.hashCode());
        nodeModelDto.setData(getData(o));
        setProperties(inm);
    }

    private String getData(Object o) {
        if (o instanceof OWLNamedObject) {
            IRI u = ((OWLNamedObject) o).getIRI();
            if (u.getFragment() == null) {
                return u.toString();
            } else {
                return u.getFragment();
            }
        } else {
            return o.toString();
        }
    }

    private void setProperties(NodeModel nm) {
        if (!(nm instanceof OntologyNodeModel)) { // not root
            if (nm.containsInferred()) {
                nodeModelDto.setInferred(true);
            } else if (nm.containsDifferent()) {
                nodeModelDto.setCommon(false);
            } else {
                nodeModelDto.setInferred(false);
                nodeModelDto.setCommon(true);
            }
        }
    }


    public void generateExplanation(NodeModel nm){
        nm.writeProperty(writeCallback, syntax, explanationManager);
        nodeModelDto.setExplanations(writeCallback.getRet());
    }

    public NodeModelDto getNodeModelDto() {
        return nodeModelDto;
    }

    public void setNodeModelDto(NodeModelDto nodeModelDto) {
        this.nodeModelDto = nodeModelDto;
    }
}
