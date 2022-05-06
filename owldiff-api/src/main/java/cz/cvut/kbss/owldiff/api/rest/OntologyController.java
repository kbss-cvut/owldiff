package cz.cvut.kbss.owldiff.api.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import cz.cvut.kbss.owldiff.OWLDiffException;
import cz.cvut.kbss.owldiff.api.enums.OWLDocumentFormatEnum;
import cz.cvut.kbss.owldiff.api.service.OntologyService;
import cz.cvut.kbss.owldiff.syntax.SyntaxEnum;
import cz.cvut.kbss.owldiff.view.DiffView;
import cz.cvut.kbss.owldiff.view.DiffVisualization;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.io.IOException;

@RequestMapping("/api/ontology")
@RestController
public class OntologyController {

    @Autowired
    OntologyService ontologyService;

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces=MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> uploadAndCompareOntologies(@RequestParam("originalFile") MultipartFile originalFile,
                                                             @RequestParam("updateFile") MultipartFile updateFile,
                                                             @RequestParam(value = "sid",required = false) String sessionId,
                                                             @RequestParam(value = "diffType",required = false, defaultValue="SYNTACTIC") DiffView.DiffEnum diffType,
                                                             @RequestParam(value = "diffView",required = false, defaultValue="LIST_VIEW") DiffVisualization diffView,
                                                             @RequestParam(value = "syntax",required = false, defaultValue="MANCHESTER") SyntaxEnum syntax,
                                                             @RequestParam(value = "generateExplanation",required = false, defaultValue="false") Boolean generateExplanation,
                                                             @RequestParam(value = "showCommon",required = false, defaultValue="false") Boolean showCommon,
                                                             HttpSession session) throws IOException, OWLDiffException {
        return ontologyService.uploadAndCompareOntologies(originalFile, updateFile, sessionId, diffType, diffView, syntax, generateExplanation, showCommon, session);
    }

    @GetMapping(value = "/upload/{id}")
    public ResponseEntity<Object> getOntologies(@PathVariable String id) throws JsonProcessingException {
        return ontologyService.getOntologiesById(id);
    }

    @PostMapping(value = "/merge")
    public ResponseEntity<Object> mergeOntologies(@RequestParam(value = "sid",required = true) String sessionId,
                                                  @RequestParam(value = "filename",required = false) String fileName,
                                                  @RequestParam(value = "add",required = false, defaultValue = "") int[] addFromOriginal,
                                                  @RequestParam(value = "remove", required = false, defaultValue = "") int[] removedFromUpdate,
                                                  @RequestParam(value = "format", required = false) OWLDocumentFormatEnum format){
        return ontologyService.mergeOntologies(sessionId,fileName,addFromOriginal,removedFromUpdate,format);
    }
}
