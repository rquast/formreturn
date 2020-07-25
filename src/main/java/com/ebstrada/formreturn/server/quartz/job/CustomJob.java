package com.ebstrada.formreturn.server.quartz.job;

import org.apache.log4j.Logger;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.StatefulJob;

import com.ebstrada.formreturn.server.preferences.persistence.CustomJobPreferences;

public class CustomJob extends TaskSchedulerJob implements StatefulJob {

    private static final Logger logger = Logger.getLogger(CustomJob.class);

    // DO NOT REMOVE THE DEFAULT CONSTRUCTOR - IT IS REQUIRED FOR QUARTZ!
    public CustomJob() {
        super();
    }

    public CustomJob(CustomJobPreferences jobPreferences) {
        super(jobPreferences);
    }

    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        System.out.println(jobExecutionContext.getJobDetail().getFullName());
    }

}
