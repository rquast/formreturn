package com.ebstrada.formreturn.server.quartz.job;

import org.apache.log4j.Logger;

import com.ebstrada.formreturn.api.messaging.MessageNotification;

public class QuartzMessageNotification implements MessageNotification {

    private static final Logger logger = Logger.getLogger(QuartzMessageNotification.class);

    private boolean interrupted = false;

    private Exception exception;

    @Override public void setMessage(String message) {
        logger.info(message);
    }

    @Override public Exception getException() {
        return exception;
    }

    @Override public void setException(Exception exception) {
        this.exception = exception;
    }

    @Override public void addAbortListener() {
    }

    @Override public boolean isInterrupted() {
        return interrupted;
    }

    @Override public void setInterrupted(boolean interrupted) {
        this.interrupted = interrupted;
    }

}
