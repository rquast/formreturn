package com.ebstrada.formreturn.manager.persistence.viewer;

import com.ebstrada.formreturn.manager.ui.component.TableFilterPanel;

public interface GenericDataViewer {

    public void refresh();

    public void refresh(boolean refreshPageModel, TableFilterPanel tableFilterPanel);

}
