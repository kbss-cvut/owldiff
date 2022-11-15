package cz.cvut.kbss.owldiff.cli;

import cz.cvut.kbss.owldiff.change.SyntacticAxiomChange;
import org.semanticweb.owlapi.model.IRI;

import java.util.Comparator;

public class SyntacticChangeComparator implements Comparator<SyntacticAxiomChange> {

    private IRI getIRI(SyntacticAxiomChange c) {
        final MainOwlEntityResolver resolver = new MainOwlEntityResolver();
        c.getAxiom().accept(resolver);
        return resolver.getEntity();
    }

    public int compare(final SyntacticAxiomChange c1, final SyntacticAxiomChange c2) {
        return Comparator.nullsFirst((cx1, cx2) -> {
            final SyntacticAxiomChange cc1 = (SyntacticAxiomChange) cx1;
            final SyntacticAxiomChange cc2 = (SyntacticAxiomChange) cx2;
            // Compare entities first
            int c = Comparator.nullsFirst(IRI::compareTo).compare(getIRI(cc1), getIRI(cc2));
            // ... then axioms inside these entities.
            return c != 0 ? c : cc1.getAxiom().compareTo(cc2.getAxiom());
        }).compare(c1, c2);
    }
}
