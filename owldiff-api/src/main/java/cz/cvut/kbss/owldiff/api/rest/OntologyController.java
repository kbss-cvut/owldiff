package cz.cvut.kbss.owldiff.api.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import cz.cvut.kbss.owldiff.OWLDiffException;
import cz.cvut.kbss.owldiff.api.config.HttpSessionConfig;
import cz.cvut.kbss.owldiff.api.enums.OWLDocumentFormatEnum;
import cz.cvut.kbss.owldiff.api.service.OntologyService;
import cz.cvut.kbss.owldiff.syntax.SyntaxEnum;
import cz.cvut.kbss.owldiff.view.DiffView;
import cz.cvut.kbss.owldiff.view.DiffVisualization;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpSession;
import java.io.IOException;

@RequestMapping("/api/ontology")
@RestController
public class OntologyController {

    @Autowired
    OntologyService ontologyService;

    @Autowired
    HttpSessionConfig httpSessionConfig;

    /**
     * This endpoint accepts two files, one file being the original ontology and another
     * is the update ontology. Files can be any type that is supported by OWLDiff (owl, obo, ttl, owx, omn, ofn).
     * Endpoint returns current session and comparison, meaning two arrays of different axioms.
     * One describing what is different in original ontology, another describing what is different
     * in update ontology. Other optional parameters will be show or hide common axioms, usage of
     * CEX algorithm, syntax of axioms and selection of displayed view (hierarchy tree or lists).
     *
     * @param originalFile        - original ontology file in binary
     * @param updateFile          - update ontology file in binary
     * @param sessionId           - algorithm to use (syntactic, entailment or cex)
     * @param diffType            - id of session (if the comparison has already been run)
     * @param diffView            - how the axioms should be shown and ordered (axiom list, classified view, frame view)
     * @param syntax              - in what syntax should the axioms be written (manchester or DL)
     * @param generateExplanation - boolean, if set to true show explanations to each axiom
     * @param showCommon          - boolean, if set to true it will also show common axioms, not only different
     * @return result of comparison, and session information
     * @throws IOException      - error while parsing files
     * @throws OWLDiffException - error from OWLDiff saying the computation went wrong with message explaining the error
     */
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> uploadAndCompareOntologies(@RequestParam("originalFile") MultipartFile originalFile,
                                                             @RequestParam("updateFile") MultipartFile updateFile,
                                                             @RequestParam(value = "sid", required = false) String sessionId,
                                                             @RequestParam(value = "diffType", required = false, defaultValue = "SYNTACTIC") DiffView.DiffEnum diffType,
                                                             @RequestParam(value = "diffView", required = false, defaultValue = "LIST_VIEW") DiffVisualization diffView,
                                                             @RequestParam(value = "syntax", required = false, defaultValue = "MANCHESTER") SyntaxEnum syntax,
                                                             @RequestParam(value = "generateExplanation", required = false, defaultValue = "false") Boolean generateExplanation,
                                                             @RequestParam(value = "showCommon", required = false, defaultValue = "false") Boolean showCommon,
                                                             HttpSession session) throws IOException, OWLDiffException {
        if (sessionId != null && httpSessionConfig.getSessionById(sessionId) != null) {
            session = httpSessionConfig.getSessionById(sessionId);
        }
        return ontologyService.uploadAndCompareOntologies(originalFile, updateFile, sessionId, diffType, diffView, syntax, generateExplanation, showCommon, session);
    }

    /**
     * Simple endpoint that expects uploadAndCompareOntologies to be run first.
     * Accepts parameter with session identifier and returns comparison that was computed
     * using uploadAndCompareOntologies endpoint. There is one main reason for this endpoint.
     * If the user uses API to compute the differences without a user interface, but then he
     * wants to view the differences in clear visualized form inside the user interface,
     * the user interface should be able to connect to his session and use the computed comparison
     * instead of computing it again. (for large ontologies computation can take a long time)
     *
     * @param id - id of session
     * @return result of comparison for specified seesion
     * @throws JsonProcessingException
     */
    @GetMapping(value = "/upload/{id}")
    public ResponseEntity<Object> getOntologies(@PathVariable String id) throws JsonProcessingException {
        HttpSession session = httpSessionConfig.getSessionById(id);
        if (session == null) {
            //If session is timeout return not found
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Provided session was not found or is timeout");
        }
        return ontologyService.getOntologiesById(session);
    }

    /**
     * This endpoint expects uploadAndCompareOntologies endpoint to be run first as well,
     * because it needs the comparison of ontologies to make the merge. It accepts two
     * arrays, the first being array of axioms that should be added from the original ontology
     * and the second being array of axioms that should be removed from update ontology. Returns
     * a merged ontology. Optional parameters are file type and file name of merged ontology.
     *
     * @param sessionId         - id of session
     * @param fileName          - optional specification of file name for merged ontology, default is file name of update ontology
     * @param addFromOriginal   - array of axioms to add to merged ontology from original ontology
     * @param removedFromUpdate - array of axioms to delete from update ontology which is the base for merged ontology
     * @param format-           optional file type format for merged ontology, default is file type of update ontology
     * @return file of merged ontology based on comparison from specific session
     */
    @PostMapping(value = "/merge")
    public ResponseEntity<Object> mergeOntologies(@RequestParam(value = "sid", required = true) String sessionId,
                                                  @RequestParam(value = "filename", required = false) String fileName,
                                                  @RequestParam(value = "add", required = false, defaultValue = "") int[] addFromOriginal,
                                                  @RequestParam(value = "remove", required = false, defaultValue = "") int[] removedFromUpdate,
                                                  @RequestParam(value = "format", required = false) OWLDocumentFormatEnum format) {
        HttpSession session = httpSessionConfig.getSessionById(sessionId);
        if (session == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Provided session was not found or is timeout");
        }
        return ontologyService.mergeOntologies(sessionId, fileName, addFromOriginal, removedFromUpdate, format, session);
    }
}
