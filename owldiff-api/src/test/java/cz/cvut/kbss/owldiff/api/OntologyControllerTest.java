package cz.cvut.kbss.owldiff.api;

import cz.cvut.kbss.owldiff.api.rest.OntologyController;
import cz.cvut.kbss.owldiff.api.service.OntologyService;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.io.IOException;

@WebMvcTest(OntologyController.class)
public class OntologyControllerTest {

    @MockBean
    private OntologyService serviceMocked;

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void getOntologiesById_whenValid_thenReturns200AndResponse() throws Exception {
        // when
        when(serviceMocked.getOntologiesById(any())).thenReturn(new ResponseEntity<>("Saved ontologies", HttpStatus.OK));

        // mock and assert
        mockMvc.perform(get("/api/ontology/upload/test")).andDo(print()).andExpect(status().isOk())
                .andExpect(content().string(containsString("Saved ontologies")));
    }

    @Test
    public void uploadAndCompareOntologies_whenInValid_thenReturns500() throws Exception {
        MockMultipartFile originalFile = new MockMultipartFile("originalFile", "orig", null, "bar".getBytes());
        MockMultipartFile updateFile = new MockMultipartFile("updateFile", "update", null, "bar".getBytes());

        // when
        when(serviceMocked.uploadAndCompareOntologies(any(), any(), any(), any(), any(), any(), any(), any(), any())).thenThrow(new IOException("ontologyFileError"));

        // mock and assert
        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/ontology/upload").file(originalFile).file(updateFile).content(MediaType.MULTIPART_FORM_DATA_VALUE))
                .andDo(print()).andExpect(status().is5xxServerError())
                .andExpect(content().string(containsString("ontologyFileError")));
    }
}