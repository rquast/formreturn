package com.ebstrada.formreturn.manager.log4j;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class AwtExceptionHandler {

    private static Log logger = LogFactory.getLog(AwtExceptionHandler.class);

    /**
     * WARNING: Don't change the signature of this method!
     */
    public void handle(Throwable throwable) {
        com.ebstrada.formreturn.manager.util.Misc.printStackTrace(throwable);
        logger.error("Uncaught Throwable Detected", throwable);
    }

}
