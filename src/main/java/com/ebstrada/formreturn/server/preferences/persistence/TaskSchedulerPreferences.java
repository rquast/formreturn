package com.ebstrada.formreturn.server.preferences.persistence;

import java.util.ArrayList;

import com.ebstrada.formreturn.manager.util.NoObfuscation;
import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("taskSchedulerPreferences") public class TaskSchedulerPreferences
    implements NoObfuscation {

    private ArrayList<TaskSchedulerJobPreferences> jobPreferences =
        new ArrayList<TaskSchedulerJobPreferences>();

    public ArrayList<TaskSchedulerJobPreferences> getJobPreferences() {
        if (jobPreferences == null) {
            jobPreferences = new ArrayList<TaskSchedulerJobPreferences>();
        }
        return jobPreferences;
    }

    public void setJobPreferences(ArrayList<TaskSchedulerJobPreferences> jobPreferences) {
        this.jobPreferences = jobPreferences;
    }

}
