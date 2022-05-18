package cz.cvut.kbss.owldiff.api;

import cz.cvut.kbss.owldiff.api.config.HttpSessionConfig;
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

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionContext;
import java.io.IOException;
import java.util.Enumeration;

@WebMvcTest(OntologyController.class)
public class OntologyControllerTest {

    @MockBean
    private OntologyService serviceMocked;

    @MockBean
    private HttpSessionConfig httpSessionConfig;

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void getOntologiesById_whenValid_thenReturns200AndResponse() throws Exception {
        HttpSession tmp = new HttpSession() {
            @Override
            public long getCreationTime() {
                return 0;
            }

            @Override
            public String getId() {
                return null;
            }

            @Override
            public long getLastAccessedTime() {
                return 0;
            }

            @Override
            public ServletContext getServletContext() {
                return null;
            }

            @Override
            public void setMaxInactiveInterval(int interval) {

            }

            @Override
            public int getMaxInactiveInterval() {
                return 0;
            }

            @Override
            public HttpSessionContext getSessionContext() {
                return null;
            }

            @Override
            public Object getAttribute(String name) {
                return null;
            }

            @Override
            public Object getValue(String name) {
                return null;
            }

            @Override
            public Enumeration<String> getAttributeNames() {
                return null;
            }

            @Override
            public String[] getValueNames() {
                return new String[0];
            }

            @Override
            public void setAttribute(String name, Object value) {

            }

            @Override
            public void putValue(String name, Object value) {

            }

            @Override
            public void removeAttribute(String name) {

            }

            @Override
            public void removeValue(String name) {

            }

            @Override
            public void invalidate() {

            }

            @Override
            public boolean isNew() {
                return false;
            }
        };
        // when
        when(httpSessionConfig.getSessionById(any())).thenReturn(tmp);
        when(serviceMocked.getOntologiesById(tmp)).thenReturn(new ResponseEntity<>("Saved ontologies", HttpStatus.OK));

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