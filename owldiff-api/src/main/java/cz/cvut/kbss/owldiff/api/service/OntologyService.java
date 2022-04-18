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
import cz.cvut.kbss.owldiff.diff.OWLDiffConfiguration;
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
import openllet.owlapi.OWLHelper;
import openllet.owlapi.OpenlletReasonerFactory;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.reasoner.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.io.InputStream;
import java.util.*;
import java.util.stream.IntStream;

@Service
public class OntologyService {

    @Value("${server.servlet.session.timeout}")
    Integer sessionTimerConfig;

    @Value("${client.gui.url}")
    String clientGuiUrl;

    private final OWLOntologyManager originalOntologyManager;
    private final OWLOntologyManager updateOntologyManager;

    public OntologyService(){
        OWLDiffConfiguration.setReasonerProvider(new OWLDiffConfiguration.ReasonerProvider() {
            public OWLReasoner getOWLReasoner(OWLOntology o) {
                OWLHelper h = OWLHelper.createLightHelper(OpenlletReasonerFactory.getInstance().createReasoner(o));
                return h.getReasoner();
            }
        });
        originalOntologyManager = OWLManager.createConcurrentOWLOntologyManager();
        updateOntologyManager = OWLManager.createConcurrentOWLOntologyManager();
    }

    public String compareOntologies(InputStream originalFile,
                                           InputStream updateFile,
                                           DiffView.DiffEnum diff,
                                           DiffVisualization view,
                                           Syntax syntax,
                                           Boolean generateExplanation,
                                           Boolean showCommon,
                                           HttpSession httpSession
                                        ) throws OWLOntologyCreationException, JsonProcessingException, OWLDiffException {

        //Get imported ontology because when using endpoint multiple times on same ontology causes errors
        //TODO: Perhaps move functions and parts to separate file
        OWLOntology originalOntology = null;
        OWLOntology updateOntology = null;
        try {
            originalOntology = originalOntologyManager.loadOntologyFromOntologyDocument(originalFile);
        } catch(OWLOntologyAlreadyExistsException e){
            OWLOntologyID originalID = e.getOntologyID();
            originalOntology = originalOntologyManager.getOntology(originalID);
        }

        try {
            updateOntology = updateOntologyManager.loadOntologyFromOntologyDocument(updateFile);
        } catch(OWLOntologyAlreadyExistsException e){
            OWLOntologyID updateID = e.getOntologyID();
            updateOntology = updateOntologyManager.getOntology(updateID);
        }

        //Create handler for owldiff diffing classes
        OWLOntology finalUpdateOntology = updateOntology;
        OWLOntology finalOriginalOntology = originalOntology;
        OntologyHandler ontologyHandler = new OntologyHandler() {
            public OWLOntology getOriginalOntology() {
                return finalOriginalOntology;
            }
            public OWLOntology getUpdateOntology() {
                return finalUpdateOntology;
            }
        };

        //Prepare owldiff treeModels before running comparison
        OWLDiffTreeModel treeModelOriginal = null;
        OWLDiffTreeModel treeModelUpdate = null;
        ComparisonDto ontologies = (ComparisonDto) httpSession.getAttribute("ontologies");
        //Generate treeModels according to selected diff
        switch (diff) {
            case CEX -> {
                //Check if syntactic diff was already run
                if (ontologies == null) {
                    throw new OWLDiffException(OWLDiffException.Reason.INTERNAL_ERROR, "You need to run syntactic diff first");
                }
                CEXDiffOutput cexDiffOutput = new CEXDiff(ontologyHandler).diff();
                treeModelOriginal = ontologies.getOriginal().getTreeModel();
                treeModelUpdate = ontologies.getUpdate().getTreeModel();
                treeModelOriginal.setCEXDiff(cexDiffOutput.getOriginalDiffR(), cexDiffOutput.getOriginalDiffL());
                treeModelUpdate.setCEXDiff(cexDiffOutput.getUpdateDiffR(), cexDiffOutput.getUpdateDiffL());
            }
            case ENTAILMENT -> {
                SyntacticDiffOutput syntacticOutput = (SyntacticDiffOutput) httpSession.getAttribute("syntacticDiffOutput");
                if (syntacticOutput == null || ontologies == null) {
                    syntacticOutput = new SyntacticDiff(ontologyHandler).diff();
                    treeModelOriginal = new OWLDiffTreeModel(view, syntacticOutput.getInOriginal(), originalOntology);
                    treeModelUpdate = new OWLDiffTreeModel(view, syntacticOutput.getInUpdate(), updateOntology);
                } else {
                    treeModelOriginal = ontologies.getOriginal().getTreeModel();
                    treeModelUpdate = ontologies.getUpdate().getTreeModel();
                }
                EntailmentsExplanationsDiffOutput entailmentDiff = new EntailmentsExplanationsDiff(ontologyHandler, null, syntacticOutput).diff();
                treeModelOriginal.setInferred(entailmentDiff.getInferred());
                treeModelUpdate.setInferred(entailmentDiff.getPossiblyRemove());
            }
            default -> {
                //basically case SYNTACTIC
                SyntacticDiffOutput defaultDiff = new SyntacticDiff(ontologyHandler).diff();
                httpSession.setAttribute("syntacticDiffOutput", defaultDiff);
                treeModelOriginal = new OWLDiffTreeModel(view, defaultDiff.getInOriginal(), originalOntology);
                treeModelUpdate = new OWLDiffTreeModel(view, defaultDiff.getInUpdate(), updateOntology);
            }
        }



        //Map original TreeModel into OntologyDataDto object, which is jsonable
        OntologyDataDto originalData = mapNodeTreeToOntology(treeModelOriginal, syntax, new BBOWLAPIExplanationManager(originalOntology), generateExplanation, showCommon);
        originalData.setOntology(originalOntology);
        originalData.setTreeModel(treeModelOriginal);

        //Map update TreeModel into OntologyDataDto object, which is jsonable
        OntologyDataDto updateData = mapNodeTreeToOntology(treeModelUpdate, syntax, new BBOWLAPIExplanationManager(updateOntology), generateExplanation, showCommon);
        updateData.setOntology(updateOntology);
        updateData.setTreeModel(treeModelUpdate);

        //Now set all attributes to finishing ComparisonDto object
        ComparisonDto ontologiesComparison = new ComparisonDto();
        ontologiesComparison.setOriginal(originalData);
        ontologiesComparison.setUpdate(updateData);
        ontologiesComparison.setGuiUrl(clientGuiUrl + "/?sid=" + httpSession.getId());
        ontologiesComparison.setSessionId(httpSession.getId());
        ontologiesComparison.setSessionTimer(sessionTimerConfig);

        //Then set it into session
        httpSession.setAttribute("ontologies",ontologiesComparison);

        //Return comparison dto object as json string for controller
        ObjectMapper mapper = new ObjectMapper();
        mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
        return mapper.writeValueAsString(ontologiesComparison);
    }

    //Helper function returning ontologyDataDto with mapped treeModel
    private OntologyDataDto mapNodeTreeToOntology(OWLDiffTreeModel treeModel, Syntax syntax, ExplanationManager expl, Boolean generateExplanation, Boolean showCommon){
        OntologyDataDto ret = new OntologyDataDto();
        NodeModelDataParser parser = new NodeModelDataParser(syntax, expl);
        NodeModel model = (NodeModel) treeModel.getRoot();
        model.accept(parser);
        ret.setOntologyName(parser.getNodeModelDto().getData());
        NodeModelDto parentData = parser.getNodeModelDto();
        parser.setNodeModelDto(new NodeModelDto());
        modelChildrenIntoJson(model, parentData, parser, showCommon, generateExplanation);
        ret.setData(parentData);
        return ret;
    }

    //Recursive function for mapping all children
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

    public OWLOntology mergeOntologies(HttpSession session, int[] toAdd, int[] toDelete){
        //Get ontologies from session
        ComparisonDto ontologies = (ComparisonDto) session.getAttribute("ontologies");
        OntologyDataDto originalOntologyDataDto = ontologies.getOriginal();
        OntologyDataDto updateOntologyDataDto = ontologies.getUpdate();

        OWLOntology updateOntology = updateOntologyDataDto.getOntology();

        //Add or remove axioms from update
        updateOntology.addAxioms(axiomsFromNodeModel(originalOntologyDataDto.getData(),new HashSet<OWLAxiom>(),toAdd));
        updateOntology.removeAxioms(axiomsFromNodeModel(updateOntologyDataDto.getData(),new HashSet<OWLAxiom>(),toDelete));

        return updateOntology;
    }

    //Helper recursive function for merge to get axioms from saved treeModel
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
