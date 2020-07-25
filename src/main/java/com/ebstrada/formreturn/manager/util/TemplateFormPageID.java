package com.ebstrada.formreturn.manager.util;

public class TemplateFormPageID {

    private long formPageId = 0;

    private int startPage = 1;

    public long getFormPageId() {
        return formPageId;
    }

    public void setFormPageId(long formPageId) {
        this.formPageId = formPageId;
    }

    public void setStartPage(int startPage) {
        this.startPage = startPage;
    }

    public long getStartPage() {
        return startPage;
    }

    public void setFirstFormPageId(long formPageId) {
        this.formPageId = formPageId;
    }

}
