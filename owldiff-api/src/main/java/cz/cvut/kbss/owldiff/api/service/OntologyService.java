package cz.cvut.kbss.owldiff.api.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import cz.cvut.kbss.owldiff.OWLDiffException;
import cz.cvut.kbss.owldiff.api.config.HttpSessionConfig;
import cz.cvut.kbss.owldiff.api.dto.ComparisonDto;
import cz.cvut.kbss.owldiff.api.enums.OWLDocumentFormatEnum;
import cz.cvut.kbss.owldiff.syntax.SyntaxEnum;
import cz.cvut.kbss.owldiff.view.DiffView;
import cz.cvut.kbss.owldiff.view.DiffVisualization;
import org.semanticweb.owlapi.formats.RDFXMLDocumentFormat;
import org.semanticweb.owlapi.model.OWLDocumentFormat;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpSession;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

@Service
public class OntologyService {

    @Autowired
    HttpSessionConfig httpSessionConfig;

    @Autowired
    OntologyServiceHandler handler;

    public ResponseEntity<Object> uploadAndCompareOntologies(MultipartFile originalFile,
                                                             MultipartFile updateFile,
                                                             String sessionId,
                                                             DiffView.DiffEnum diffType,
                                                             DiffVisualization diffView,
                                                             SyntaxEnum syntax,
                                                             Boolean generateExplanation,
                                                             Boolean showCommon,
                                                             HttpSession session) throws IOException, OWLDiffException {
        //If session already exist, use that one
        if(sessionId!=null && httpSessionConfig.getSessionById(sessionId)!=null){
            session = httpSessionConfig.getSessionById(sessionId);
        }
        InputStream originalStream;
        InputStream updateStream;
        originalStream = originalFile.getInputStream();
        updateStream = updateFile.getInputStream();
        String ontologyMapped = handler.compareOntologies(originalStream, updateStream, diffType,diffView, syntax.getSyntax(), generateExplanation, showCommon, session);
        //Save update fileName for merge ontology
        String fileName = updateFile.getOriginalFilename();
        session.setAttribute("updateFilename",fileName.substring(0, fileName.lastIndexOf('.')));
        return new ResponseEntity<>(ontologyMapped, HttpStatus.OK);

    }

    public ResponseEntity<Object> getOntologiesById(String id) throws JsonProcessingException {
        HttpSession session = httpSessionConfig.getSessionById(id);
        if(session==null){
            //If session is timeout return not found
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Provided session was not found or is timeout");
        }
        ComparisonDto comparison = (ComparisonDto) session.getAttribute("ontologies");
        String ret = handler.getComparisonBySession(comparison);
        return new ResponseEntity<>(ret,HttpStatus.OK);
    }

    public ResponseEntity<Object> mergeOntologies(String sessionId,
                                                  String fileName,
                                                  int[] addFromOriginal,
                                                  int[] removedFromUpdate,
                                                  OWLDocumentFormatEnum format){
        HttpSession session = httpSessionConfig.getSessionById(sessionId);
        if(session==null){
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Provided session was not found or is timeout");
        }
        OWLOntology retOntology = handler.mergeOntologies(session,addFromOriginal,removedFromUpdate);
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
