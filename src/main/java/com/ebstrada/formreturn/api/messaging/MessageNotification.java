package com.ebstrada.formreturn.api.messaging;

public interface MessageNotification {

    public abstract void setMessage(String message);

    public abstract Exception getException();

    public abstract void setException(Exception exception);

    public abstract void addAbortListener();

    public abstract boolean isInterrupted();

    public abstract void setInterrupted(boolean interrupted);

}