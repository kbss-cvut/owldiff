package cz.cvut.kbss.owldiff.api.rest;

import cz.cvut.kbss.owldiff.OWLDiffException;
import cz.cvut.kbss.owldiff.api.config.HttpSessionConfig;
import cz.cvut.kbss.owldiff.api.enums.OWLDocumentFormatEnum;
import cz.cvut.kbss.owldiff.api.service.OntologyService;
import cz.cvut.kbss.owldiff.syntax.SyntaxEnum;
import cz.cvut.kbss.owldiff.view.DiffView;
import cz.cvut.kbss.owldiff.view.DiffVisualization;
import org.semanticweb.owlapi.formats.RDFXMLDocumentFormat;
import org.semanticweb.owlapi.model.OWLDocumentFormat;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpSession;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

@RequestMapping("/api/ontology")
@RestController
public class OntologyController {

    @Autowired
    OntologyService ontologyService;

    @Autowired
    HttpSessionConfig httpSessionConfig;

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces=MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> uploadAndCompareOntologies(@RequestParam("originalFile") MultipartFile originalFile,
                                                             @RequestParam("updateFile") MultipartFile updateFile,
                                                             @RequestParam(value = "sid",required = false) String sessionId,
                                                             @RequestParam(value = "diffType",required = false, defaultValue="SYNTACTIC") DiffView.DiffEnum diffType,
                                                             @RequestParam(value = "diffView",required = false, defaultValue="CLASSIFIED_FRAME_VIEW") DiffVisualization diffView,
                                                             @RequestParam(value = "syntax",required = false, defaultValue="MANCHESTER") SyntaxEnum syntax,
                                                             @RequestParam(value = "generateExplanation",required = false, defaultValue="false") Boolean generateExplanation,
                                                             @RequestParam(value = "showCommon",required = false, defaultValue="false") Boolean showCommon,
                                                             HttpSession session){
        //If session already exist, use that one
        if(sessionId!=null && httpSessionConfig.getSessionById(sessionId)!=null){
            session = httpSessionConfig.getSessionById(sessionId);
        }
        InputStream originalStream;
        InputStream updateStream;
        try {
            originalStream = originalFile.getInputStream();
            updateStream = updateFile.getInputStream();
            String ontologyMapped = ontologyService.compareOntologies(originalStream, updateStream, diffType,diffView, syntax.getSyntax(), generateExplanation, showCommon, session);
            //Save returning json string into session for /upload/{id} endpoint
            session.setAttribute("ontologiesMapped",ontologyMapped);
            //Save update fileName for merge ontology
            String fileName = updateFile.getOriginalFilename();
            session.setAttribute("updateFilename",fileName.substring(0, fileName.lastIndexOf('.')));
            return new ResponseEntity<>(ontologyMapped,HttpStatus.OK);
        } catch (IOException | OWLOntologyCreationException | OWLDiffException e) {
            e.printStackTrace();
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, e.getMessage(), e);
        }
    }

    @GetMapping(value = "/upload/{id}")
    public ResponseEntity<Object> getOntologies(@PathVariable String id){
        HttpSession session = httpSessionConfig.getSessionById(id);
        if(session==null){
            //If session is timeout return not found
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Provided session was not found or is timeout");
        }
        String ret = (String) session.getAttribute("ontologiesMapped");
        return new ResponseEntity<>(ret,HttpStatus.OK);
    }

    @PostMapping(value = "/merge")
    public ResponseEntity<Object> mergeOntologies(@RequestParam(value = "sid",required = true) String sessionId,
                                                  @RequestParam(value = "filename",required = false) String fileName,
                                                  @RequestParam(value = "add",required = false, defaultValue = "") int[] addFromOriginal,
                                                  @RequestParam(value = "remove", required = false, defaultValue = "") int[] removedFromUpdate,
                                                  @RequestParam(value = "format", required = false) OWLDocumentFormatEnum format){

        HttpSession session = httpSessionConfig.getSessionById(sessionId);
        if(session==null){
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Provided session was not found or is timeout");
        }
        OWLOntology retOntology = ontologyService.mergeOntologies(session,addFromOriginal,removedFromUpdate);
        //If custom filename not provided, use saved from update file
        if(fileName==null){
            fileName = (String) session.getAttribute("updateFilename");
        }
        //If format not defined use RDF/XML, else use the one defined in OWLDocumentFormatEnum
        OWLDocumentFormat owlDocumentFormat;
        String fileExtension;
        if(format==null){
            owlDocumentFormat = new RDFXMLDocumentFormat();
            fileExtension = "owl";
        }else{
            owlDocumentFormat = format.getFormat();
            fileExtension = format.getExtension();
        }

        //Try to save ontology to bytestream
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            retOntology.getOWLOntologyManager().saveOntology(retOntology, owlDocumentFormat, outputStream);
        } catch (OWLOntologyStorageException e) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, e.getMessage());
        }
        //Set headers needed for file definition
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.set(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=" + fileName + "." + fileExtension);
        headers.setContentLength(outputStream.toByteArray().length);
        headers.set(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, HttpHeaders.CONTENT_DISPOSITION);
        return new ResponseEntity<>(outputStream.toByteArray(), headers, HttpStatus.OK);
    }
}
