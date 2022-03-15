package cz.cvut.kbss.owldiff.api.service;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import cz.cvut.kbss.owldiff.ExplanationManager;
import cz.cvut.kbss.owldiff.OWLDiffException;
import cz.cvut.kbss.owldiff.api.dto.ComparisonDto;
import cz.cvut.kbss.owldiff.api.dto.NodeModelDto;
import cz.cvut.kbss.owldiff.api.dto.OntologyDataDto;
import cz.cvut.kbss.owldiff.api.util.NodeModelDataParser;
import cz.cvut.kbss.owldiff.diff.cex.CEXDiff;
import cz.cvut.kbss.owldiff.diff.cex.CEXDiffOutput;
import cz.cvut.kbss.owldiff.diff.entailments.EntailmentsExplanationsDiff;
import cz.cvut.kbss.owldiff.diff.entailments.EntailmentsExplanationsDiffOutput;
import cz.cvut.kbss.owldiff.diff.owlapi.BBOWLAPIExplanationManager;
import cz.cvut.kbss.owldiff.diff.syntactic.SyntacticDiff;
import cz.cvut.kbss.owldiff.diff.syntactic.SyntacticDiffOutput;
import cz.cvut.kbss.owldiff.ontology.OntologyHandler;
import cz.cvut.kbss.owldiff.syntax.Syntax;
import cz.cvut.kbss.owldiff.view.DiffView;
import cz.cvut.kbss.owldiff.view.DiffVisualization;
import cz.cvut.kbss.owldiff.view.OWLDiffTreeModel;
import cz.cvut.kbss.owldiff.view.nodes.NodeModel;
import cz.cvut.kbss.owldiff.view.nodes.OntologyNodeModel;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.io.InputStream;
import java.util.*;
import java.util.stream.IntStream;

@Service
public class OntologyService {

    @Value("${server.servlet.session.timeout}")
    String sessionTimerConfig;

    private OWLOntologyManager originalOntologyManager;
    private OWLOntologyManager updateOntologyManager;

    public OntologyService(){
        originalOntologyManager = OWLManager.createOWLOntologyManager();
        updateOntologyManager = OWLManager.createOWLOntologyManager();

    }

    public String compareOntologies(InputStream originalFile,
                                           InputStream updateFile,
                                           DiffView.DiffEnum diff,
                                           DiffVisualization view,
                                           Syntax syntax,
                                           Boolean generateExplanation,
                                           HttpSession httpSession
                                        ) throws OWLOntologyCreationException, JsonProcessingException, OWLDiffException {
        OWLOntology originalOntology = originalOntologyManager.loadOntologyFromOntologyDocument(originalFile);
        OWLOntology updateOntology = updateOntologyManager.loadOntologyFromOntologyDocument(updateFile);

        OntologyHandler ontologyHandler = new OntologyHandler() {
            public OWLOntology getOriginalOntology() {
                return originalOntology;
            }
            public OWLOntology getUpdateOntology() {
                return updateOntology;
            }
        };

        OWLDiffTreeModel treeModelOriginal = null;
        OWLDiffTreeModel treeModelUpdate = null;
        //Comparison switcher
        switch (diff) {
            case CEX:
                CEXDiffOutput cexDiffOutput = new CEXDiff(ontologyHandler).diff();
                if(httpSession!=null){
                    ComparisonDto ontologies = (ComparisonDto) httpSession.getAttribute("ontologies");
                    if(ontologies==null){
                        throw new OWLDiffException(OWLDiffException.Reason.INTERNAL_ERROR, "You need to run syntactic diff first");
                    }
                    OntologyDataDto originalOntologyDataDto = ontologies.getOriginal();
                    OntologyDataDto updateOntologyDataDto = ontologies.getUpdate();
                    treeModelOriginal = originalOntologyDataDto.getTreeModel();
                    treeModelUpdate = updateOntologyDataDto.getTreeModel();
                    treeModelOriginal.setCEXDiff(cexDiffOutput.getOriginalDiffR(), cexDiffOutput.getOriginalDiffL());
                    treeModelUpdate.setCEXDiff(cexDiffOutput.getUpdateDiffR(), cexDiffOutput.getUpdateDiffL());
                }
                break;
            case ENTAILMENT:
                SyntacticDiffOutput syntacticOutput = null;
                if(httpSession!=null){
                    System.out.println("SESSION" + httpSession.getId());
                    syntacticOutput = (SyntacticDiffOutput) httpSession.getAttribute("syntacticDiffOutput");
                    ComparisonDto ontologies = (ComparisonDto) httpSession.getAttribute("ontologies");
                    OntologyDataDto originalOntologyDataDto = ontologies.getOriginal();
                    OntologyDataDto updateOntologyDataDto = ontologies.getUpdate();
                    treeModelOriginal = originalOntologyDataDto.getTreeModel();
                    treeModelUpdate = updateOntologyDataDto.getTreeModel();
                }
                if(syntacticOutput == null){
                    syntacticOutput = new SyntacticDiff(ontologyHandler).diff();
                    treeModelOriginal = new OWLDiffTreeModel(view, syntacticOutput.getInOriginal(), originalOntology);
                    treeModelUpdate = new OWLDiffTreeModel(view, syntacticOutput.getInUpdate(), updateOntology);
                }
                EntailmentsExplanationsDiffOutput entailmentDiff = new EntailmentsExplanationsDiff(ontologyHandler, null ,syntacticOutput).diff();
                treeModelOriginal.setInferred(entailmentDiff.getInferred());
                treeModelUpdate.setInferred(entailmentDiff.getPossiblyRemove());
                break;
            default:
                System.out.println("SESSION" + httpSession.getId());
                SyntacticDiffOutput defaultDiff = new SyntacticDiff(ontologyHandler).diff();
                if(httpSession!=null){
                    httpSession.setAttribute("syntacticDiffOutput",defaultDiff);
                }
                treeModelOriginal = new OWLDiffTreeModel(view, defaultDiff.getInOriginal(), originalOntology);
                treeModelUpdate = new OWLDiffTreeModel(view, defaultDiff.getInUpdate(), updateOntology);
                break;
        }


        //We are setting comparsion dto object, with tree models.
        ComparisonDto ontologiesComparison = new ComparisonDto();
        //TODO: Check expl manager
        OntologyDataDto originalData = mapNodeTreeToOntology(treeModelOriginal, syntax, new BBOWLAPIExplanationManager(originalOntology), generateExplanation);
        originalData.setOntology(originalOntology);
        originalData.setTreeModel(treeModelOriginal);
        OntologyDataDto updateData = mapNodeTreeToOntology(treeModelUpdate, syntax, new BBOWLAPIExplanationManager(updateOntology), generateExplanation);
        updateData.setOntology(updateOntology);
        updateData.setTreeModel(treeModelUpdate);
        ontologiesComparison.setOriginal(originalData);
        ontologiesComparison.setUpdate(updateData);
        ontologiesComparison.setSessionId(httpSession.getId());
        ontologiesComparison.setSessionTimer(sessionTimerConfig);
        //Then set it into session
        if(httpSession!=null){
            httpSession.setAttribute("ontologies",ontologiesComparison);
        }
        //Return comparison dto object as json string for controller
        ObjectMapper mapper = new ObjectMapper();
        mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
        return mapper.writeValueAsString(ontologiesComparison);
    }
    private OntologyDataDto mapNodeTreeToOntology(OWLDiffTreeModel treeModel, Syntax syntax, ExplanationManager expl, Boolean generateExplanation){
        OntologyDataDto ret = new OntologyDataDto();
        NodeModelDataParser parser = new NodeModelDataParser(syntax, expl);
        //TODO: Handle better just test
        NodeModel model = (NodeModel) treeModel.getRoot();
        model.accept(parser);
        ret.setOntologyName(parser.getNodeModelDto().getData());
        NodeModelDto parentData = parser.getNodeModelDto();
        parser.setNodeModelDto(new NodeModelDto());
        modelChildrenIntoJson(model, parentData, parser, true, generateExplanation);
        ret.setData(parentData);
        return ret;
    }

    private void modelChildrenIntoJson(NodeModel nodeModel, NodeModelDto parent, NodeModelDataParser parser, Boolean showCommon, Boolean generateExplanation){
        if(nodeModel.getCount(showCommon)!=0){
            for(int i = 0; i < nodeModel.getCount(showCommon); i++){
                NodeModel<?> childNode = nodeModel.getChild(i, showCommon);
                childNode.accept(parser);
                if(generateExplanation) {
                    parser.generateExplanation(childNode);
                }
                NodeModelDto child = parser.getNodeModelDto();
                parser.setNodeModelDto(new NodeModelDto());
                modelChildrenIntoJson(childNode, child, parser, showCommon,generateExplanation);
                List<NodeModelDto> parentChildren = parent.getChildren();
                if(parentChildren==null) parentChildren = new ArrayList<NodeModelDto>();
                parentChildren.add(child);
                parent.setChildren(parentChildren);
            }
        }
    }

    public OWLOntology mergeOntologies(OWLOntology originalOWL, OWLOntology updateOWL, int[] toAdd, int[] toDelete){
        //TODO: Do we want merge ontologies from existing file?
        return null;
    }

    public OWLOntology mergeOntologies(HttpSession session, int[] toAdd, int[] toDelete){
        ComparisonDto ontologies = (ComparisonDto) session.getAttribute("ontologies");
        OntologyDataDto originalOntologyDataDto = ontologies.getOriginal();
        OntologyDataDto updateOntologyDataDto = ontologies.getUpdate();

        OWLOntology updateOntology = updateOntologyDataDto.getOntology();

        updateOntology.addAxioms(axiomsFromNodeModel(originalOntologyDataDto.getData(),new HashSet<OWLAxiom>(),toAdd));
        updateOntology.removeAxioms(axiomsFromNodeModel(updateOntologyDataDto.getData(),new HashSet<OWLAxiom>(),toDelete));

        return updateOntology;
    }

    private Set<OWLAxiom> axiomsFromNodeModel(NodeModelDto data, Set<OWLAxiom> axioms, int[] values){
        OWLAxiom axiom = data.getAxiom();
        if(axiom!=null){
            if(IntStream.of(values).anyMatch(val -> val == data.getId())){
                axioms.add(axiom);
            }
        }
        if(data.getChildren()!=null && data.getChildren().size()!=0){
            for(int i = 0; i < data.getChildren().size(); i++){
                axiomsFromNodeModel(data.getChildren().get(i),axioms,values);
            }
        }
        return axioms;
    }

}
