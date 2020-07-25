package com.ebstrada.formreturn.manager.logic.export.image;

import com.ebstrada.formreturn.manager.util.NoObfuscation;
import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("collation") public enum Collation implements NoObfuscation {
    ALL_IMAGES_TOGETHER, FORM_IMAGES_TOGETHER, IMAGES_ONLY
}
