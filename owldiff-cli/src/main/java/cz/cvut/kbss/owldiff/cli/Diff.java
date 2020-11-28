package cz.cvut.kbss.owldiff.cli;

import com.github.rvesse.airline.annotations.Arguments;
import com.github.rvesse.airline.annotations.Command;
import cz.cvut.kbss.owldiff.change.OWLChangeType;
import cz.cvut.kbss.owldiff.change.SyntacticAxiomChange;
import cz.cvut.kbss.owldiff.diff.syntactic.SyntacticDiff;
import cz.cvut.kbss.owldiff.diff.syntactic.SyntacticDiffOutput;
import cz.cvut.kbss.owldiff.ontology.OntologyHandler;
import cz.cvut.kbss.owldiff.syntax.ManchesterSyntax;
import java.io.File;
import java.net.URI;
import java.text.MessageFormat;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Pattern;
import lombok.extern.slf4j.Slf4j;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.io.IRIDocumentSource;
import org.semanticweb.owlapi.io.OWLRendererException;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.MissingImportHandlingStrategy;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OntologyConfigurator;

@Slf4j
@Command(name = "diff")
public class Diff {

    @Arguments(title = "File paths", description = "Path of the oldFile and newFile")
    private List<String> paths;

    public void run() throws OWLOntologyCreationException, OWLRendererException {
        if (paths.size() != 2) {
            log.error("Exactly two files must be provided, but got {0} only", paths.size());
            return;
        }

        String oldFF = paths.get(0);
        String newFF = paths.get(1);

        URI oldF;
        URI newF;
        if (new File(oldFF).exists()) {
            oldF = new File(oldFF).getAbsoluteFile().toURI();
        } else {
            log.error("File does not exist {}");
            throw new RuntimeException();
        }

        if (new File(newFF).exists()) {
            newF = new File(newFF).getAbsoluteFile().toURI();
        } else {
            log.error("File does not exist {}");
            throw new RuntimeException();
        }

        if (oldF.isAbsolute() && newF.isAbsolute()) {
            final OWLOntologyManager originalM = OWLManager
                .createOWLOntologyManager();
            originalM.setOntologyConfigurator(
                new OntologyConfigurator()
                    .setMissingImportHandlingStrategy(MissingImportHandlingStrategy.SILENT)
                    .setReportStackTraces(true)
            .setStrict(false));
            originalM.getIRIMappers().add( (ontologyIRI) -> {
            if (Pattern.compile(oldFF).matcher(ontologyIRI.toString()).matches()) {
                return ontologyIRI;
            } else {
                return IRI.create(String.format("file://%s", ontologyIRI.toString()));
            }});
            final OWLOntology originalO =
                originalM.loadOntologyFromOntologyDocument(new IRIDocumentSource(
                    IRI.create(oldF)));

            final OWLOntologyManager updateM = OWLManager.createOWLOntologyManager();
            updateM.setOntologyConfigurator(new OntologyConfigurator().setMissingImportHandlingStrategy(
                MissingImportHandlingStrategy.SILENT)
                .setReportStackTraces(true)
                .setStrict(false)
            );
            updateM.getIRIMappers().add( (ontologyIRI) -> {
                if (Pattern.compile(newFF).matcher(ontologyIRI.toString()).matches()) {
                    return ontologyIRI;
                } else {
                    return IRI.create(String.format("file://%s", ontologyIRI.toString()));
                }});

            OWLOntology updateO =
                updateM.loadOntologyFromOntologyDocument(new IRIDocumentSource(IRI.create(newF)));

            SyntacticDiff d = new SyntacticDiff(new OntologyHandler() {
                @Override public OWLOntology getOriginalOntology() {
                    return originalO;
                }

                @Override public OWLOntology getUpdateOntology() {
                    return updateO;
                }
            });

            final SyntacticDiffOutput o = d.diff();

            ManchesterSyntax s = new ManchesterSyntax();
            o.getOWLChanges()
                .stream().sorted(
                new ChangeComparator()
                    .thenComparing(SyntacticAxiomChange::getOWLChangeType))
                .forEach(c ->
                    System.out.println(MessageFormat.format("{0} {1}", changeTypeName(c.getOWLChangeType()),
                    s.writeAxiom(c.getAxiom(), false, null, false)))
                );

        } else {
            log.error("Files are not absolute");
        }
    }

    private class ChangeComparator implements Comparator<SyntacticAxiomChange> {

        private MainOwlEntityResolver resolver;

        public ChangeComparator() {
            this.resolver = new MainOwlEntityResolver();
        }

        public int compare(SyntacticAxiomChange c1, SyntacticAxiomChange c2) {
            c1.getAxiom().accept(resolver);
            final IRI c1Iri = resolver.getEntity();

            c2.getAxiom().accept(resolver);
            final IRI c2Iri = resolver.getEntity();

            if ( c1Iri != null && c1Iri.equals(c2Iri) ) {
                return 0;
            } else if (c1Iri != null && c2Iri != null) {
                return c1Iri.compareTo(c2Iri);
            } else {
                return c1.getAxiom().compareTo(c2.getAxiom());
            }
        }
    }

    private String changeTypeName(OWLChangeType ct) {
        switch (ct) {
            case SYNTACTIC_ORIG_REST: return "-";
            case SYNTACTIC_UPD_REST: return "+";
            default: throw new UnsupportedOperationException();
        }
    }
}
