package cz.cvut.kbss.owldiff.cli;

import cz.cvut.kbss.owldiff.diff.syntactic.SyntacticDiff;
import cz.cvut.kbss.owldiff.diff.syntactic.SyntacticDiffOutput;
import cz.cvut.kbss.owldiff.ontology.OntologyHandler;
import cz.cvut.kbss.owldiff.syntax.ManchesterSyntax;
import lombok.extern.slf4j.Slf4j;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.io.IRIDocumentSource;
import org.semanticweb.owlapi.model.*;

import java.io.File;
import java.net.URI;
import java.text.MessageFormat;
import java.util.regex.Pattern;

import static cz.cvut.kbss.owldiff.cli.ChangeTypeUtils.changeTypeName;

@Slf4j
public class Diff {

    public void run(final String original, final String updated) throws OWLOntologyCreationException {
        final OWLOntology originalO = createFreshOntology(original);
        final OWLOntology updateO = createFreshOntology(updated);

        final SyntacticDiff d = new SyntacticDiff(new OntologyHandler() {
            @Override
            public OWLOntology getOriginalOntology() {
                return originalO;
            }

            @Override
            public OWLOntology getUpdateOntology() {
                return updateO;
            }
        });

        final SyntacticDiffOutput o = d.diff();
        final ManchesterSyntax s = new ManchesterSyntax();
        o.getOWLChanges()
                .stream().sorted(
                        new SyntacticChangeComparator())
                .forEach(c ->
                        System.out.println(MessageFormat.format("{0} {1}",
                                changeTypeName(c.getOWLChangeType()),
                                s.writeAxiom(c.getAxiom(), false, null, false)))
                );
    }

    private URI ensureFileExists(final String file) {
        final File f = new File(file);

        if (!f.exists()) {
            throw new IllegalArgumentException(MessageFormat.format("File \"{0}\" does not exist.", file));
        } else if (!f.isAbsolute()) {
            throw new IllegalArgumentException(MessageFormat.format("File \"{0}\" is not absolute.", file));
        } else {
            return f.getAbsoluteFile().toURI();
        }
    }

    private OWLOntology createFreshOntology(final String original) throws OWLOntologyCreationException {
        final OntologyConfigurator configurator = new OntologyConfigurator()
                .setMissingImportHandlingStrategy(
                        MissingImportHandlingStrategy.SILENT)
                .setReportStackTraces(true)
                .setStrict(false);
        final OWLOntologyManager originalM = OWLManager
                .createOWLOntologyManager();
        originalM.setOntologyConfigurator(configurator);
        originalM.getIRIMappers().add(createIRIMapper(original));
        return
                originalM.loadOntologyFromOntologyDocument(new IRIDocumentSource(
                        IRI.create(ensureFileExists(original))));
    }

    private OWLOntologyIRIMapper createIRIMapper(final String file) {
        final Pattern p = Pattern.compile(file);
        return (ontologyIRI) -> p.matcher(ontologyIRI.toString()).matches() ?
                ontologyIRI :
                IRI.create(String.format("file://%s", ontologyIRI));
    }
}
