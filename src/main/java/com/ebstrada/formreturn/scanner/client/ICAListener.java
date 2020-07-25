package com.ebstrada.formreturn.scanner.client;

import com.google.gson.internal.LinkedTreeMap;

public interface ICAListener {

    public void update(ICAScannerMetadata.Type type, ICAScannerMetadata metadata) throws Exception;

    public LinkedTreeMap getSettings();

}
