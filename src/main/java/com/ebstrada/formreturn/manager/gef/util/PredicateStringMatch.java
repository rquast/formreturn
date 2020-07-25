package com.ebstrada.formreturn.manager.gef.util;

import java.util.StringTokenizer;

public class PredicateStringMatch implements Predicate {

    // //////////////////////////////////////////////////////////////
    // constants
    public static int MAX_PATS = 10;

    // //////////////////////////////////////////////////////////////
    // instance variables
    String _patterns[];
    int _numPats;

    // //////////////////////////////////////////////////////////////
    // constructor
    protected PredicateStringMatch(String pats[], int numPats) {
        _patterns = pats;
        _numPats = numPats;
    }

    public static Predicate create(String pat) {
        pat = pat.trim();
        String pats[] = new String[MAX_PATS];
        int numPats = 0;
        if (pat.startsWith("*")) {
            pats[numPats++] = "";
        }
        StringTokenizer st = new StringTokenizer(pat, "*");
        // needs-more-work: support ? to match one character
        while (st.hasMoreElements()) {
            String token = st.nextToken();
            pats[numPats++] = token;
        }
        if (pat.endsWith("*")) {
            pats[numPats++] = "";
        }
        if (numPats == 0) {
            return PredicateTrue.theInstance();
        }
        if (numPats == 1) {
            return new PredicateEquals(pats[0]);
        }
        return new PredicateStringMatch(pats, numPats);
    }

    public boolean predicate(Object o) {
        if (o == null) {
            return false;
        }
        String target = o.toString();
        // System.out.println("[PredicateStringMatch] predicate: " + target);
        if (!target.startsWith(_patterns[0])) {
            return false;
        }
        if (!target.endsWith(_patterns[_numPats - 1])) {
            return false;
        }
        for (int i = 0; i < _numPats; i++) {
            String p = _patterns[i];
            int index = (target + "*").indexOf(p);
            if (index == -1) {
                return false;
            }
            target = target.substring(index + p.length());
        }
        return true;
    }

    // public static void main(String args[]) {
    // if (args.length <= 1) {
    // System.out.println("Arguments: pattern targets...");
    // System.out.println("outputs targets that match pattern");
    // System.out.println("be sure to protect pattern from shell expansion");
    // return;
    // }
    // System.out.println("Pattern = " + args[0]);
    // Predicate p = PredicateStringMatch.create(args[0]);
    // for (int i = 1; i < args.length; i++) {
    // if (p.predicate(args[i])) System.out.println(args[i]);
    // }
    // }

}
