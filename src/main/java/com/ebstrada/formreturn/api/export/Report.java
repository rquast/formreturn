package com.ebstrada.formreturn.api.export;

import java.util.ArrayList;

import com.ebstrada.formreturn.api.messaging.ProcessingStatusDialog;

import net.xeoh.plugins.base.Plugin;

public interface Report extends Plugin {

    public void reportOnPublications(final ArrayList<Long> publicationIds) throws Exception;

    public void reportOnForms(final ArrayList<Long> formIds) throws Exception;

    public void setProcessingStatusDialog(ProcessingStatusDialog processingStatusDialog);

}
