package cz.cvut.kbss.owldiff.cli;

import cz.cvut.kbss.owldiff.change.OWLChangeType;

public class ChangeTypeUtils {

    public static String changeTypeName(OWLChangeType ct) {
        switch (ct) {
            case SYNTACTIC_ORIG_REST:
                return "-";
            case SYNTACTIC_UPD_REST:
                return "+";
            case ENTAILEXPL_INFERRED:
            case ENTAILEXPL_POSSIBLY_REMOVE:
            case CEX_ISIN_DIFFR_DIFFL:
            case CEX_ISIN_DIFFR:
            case CEX_ISIN_DIFFL:
            case HEURISTIC_SAME_SUB_AND_SUPER_CLASSES:
            case HEURISTIC_SINGLE_UNMATCHED_SIBLING:
            default:
                throw new UnsupportedOperationException();
        }
    }
}
