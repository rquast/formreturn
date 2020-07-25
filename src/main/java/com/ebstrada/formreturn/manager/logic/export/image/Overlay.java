package com.ebstrada.formreturn.manager.logic.export.image;

import com.ebstrada.formreturn.manager.util.NoObfuscation;
import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("overlay") public enum Overlay implements NoObfuscation {
    GRADES, FORM_SCORE, PAGE_SCORE, SOURCE_DATA, CAPTURED_DATA, INDIVIDUAL_SCORES
}
