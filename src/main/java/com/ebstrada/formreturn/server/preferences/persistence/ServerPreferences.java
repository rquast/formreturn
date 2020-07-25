package com.ebstrada.formreturn.server.preferences.persistence;

import java.util.Locale;

import com.ebstrada.formreturn.manager.gef.font.FontLocaleUtil;
import com.ebstrada.formreturn.manager.ui.Main;
import com.ebstrada.formreturn.manager.util.NoObfuscation;
import com.ebstrada.formreturn.manager.util.preferences.persistence.FolderMonitorPreferences;
import com.ebstrada.formreturn.manager.util.preferences.persistence.FormProcessorPreferences;
import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("serverPreferences") public class ServerPreferences implements NoObfuscation {

    @XStreamAlias("version") private String version = Main.VERSION;

    @XStreamAlias("databaseServer") private DatabaseServerPreferences databaseServer =
        new DatabaseServerPreferences();

    @XStreamAlias("folderMonitorPreferences") private FolderMonitorPreferences
        folderMonitorPreferences = new FolderMonitorPreferences();

    @XStreamAlias("formProcessorPreferences") private FormProcessorPreferences
        formProcessorPreferences = new FormProcessorPreferences();

    @XStreamAlias("taskSchedulerPreferences") private TaskSchedulerPreferences
        taskSchedulerPreferences = new TaskSchedulerPreferences();

    @XStreamAlias("formProcessingDatabaseName") private String formProcessingDatabaseName = "FRDB";

    @XStreamAlias("locale") private String locale;

    public DatabaseServerPreferences getDatabaseServer() {
        if (databaseServer == null) {
            databaseServer = new DatabaseServerPreferences();
        }
        return databaseServer;
    }

    public void setDatabaseServer(DatabaseServerPreferences databaseServer) {
        this.databaseServer = databaseServer;
    }

    public String getFormProcessingDatabaseName() {
        if (formProcessingDatabaseName == null) {
            formProcessingDatabaseName = "FRDB";
        }
        return formProcessingDatabaseName;
    }

    public void setFormProcessingDatabaseName(String formProcessingDatabaseName) {
        this.formProcessingDatabaseName = formProcessingDatabaseName;
    }

    public FolderMonitorPreferences getFolderMonitorPreferences() {
        if (folderMonitorPreferences == null) {
            folderMonitorPreferences = new FolderMonitorPreferences();
        }
        return folderMonitorPreferences;
    }

    public FormProcessorPreferences getFormProcessorPreferences() {
        if (formProcessorPreferences == null) {
            formProcessorPreferences = new FormProcessorPreferences();
        }
        return formProcessorPreferences;
    }

    public TaskSchedulerPreferences getTaskSchedulerPreferences() {
        if (this.taskSchedulerPreferences == null) {
            this.taskSchedulerPreferences = new TaskSchedulerPreferences();
        }
        return this.taskSchedulerPreferences;
    }

    public void setTaskSchedulerPreferences(TaskSchedulerPreferences taskSchedulerPreferences) {
        this.taskSchedulerPreferences = taskSchedulerPreferences;
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

}
