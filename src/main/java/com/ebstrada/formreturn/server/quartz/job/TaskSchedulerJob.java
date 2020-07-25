package com.ebstrada.formreturn.server.quartz.job;

import java.text.ParseException;
import java.util.Date;

import org.apache.log4j.Logger;
import org.quartz.CronTrigger;
import org.quartz.JobDetail;
import org.quartz.StatefulJob;
import org.quartz.Trigger;

import com.ebstrada.formreturn.manager.gef.util.Localizer;
import com.ebstrada.formreturn.manager.util.RandomGUID;
import com.ebstrada.formreturn.server.preferences.persistence.TaskSchedulerJobPreferences;
import com.ebstrada.formreturn.server.quartz.ITriggerTypes;
import com.ebstrada.formreturn.server.quartz.PauseAwareSimpleTrigger;

public abstract class TaskSchedulerJob implements StatefulJob {

    protected JobDetail job;

    protected int state;

    protected PauseAwareSimpleTrigger simpleTrigger;

    protected CronTrigger cronTrigger;

    protected TaskSchedulerJobPreferences preferences;

    protected final String GUID = (new RandomGUID()).toString();

    protected Class<?> clazz;

    private static final Logger logger = Logger.getLogger(TaskSchedulerJob.class);

    public TaskSchedulerJob() {
    }

    public TaskSchedulerJob(TaskSchedulerJobPreferences jobPreferences) {
        this.preferences = jobPreferences;
    }

    public int getTriggerType() {
        return preferences.getTriggerType();
    }

    public JobDetail getJob() {
        return job;
    }

    public Trigger getTrigger() {
        switch (getTriggerType()) {
            case ITriggerTypes.CRON_TRIGGER:
                return cronTrigger;
            case ITriggerTypes.SIMPLE_TRIGGER:
            default:
                return simpleTrigger;
        }
    }

    public void createJob(Class<?> clazz) {

        this.clazz = clazz;

        if (job == null) {
            job = new JobDetail(GUID, GUID + "Group", clazz);
            job.setDescription(preferences.getDescription());
        }
        switch (getTriggerType()) {

            case ITriggerTypes.SIMPLE_TRIGGER:
                if (simpleTrigger == null) {
                    simpleTrigger =
                        new PauseAwareSimpleTrigger(GUID + "Trigger", GUID + "TriggerGroup");
                    long ctime = System.currentTimeMillis();
                    simpleTrigger.setJobName(job.getName());
                    simpleTrigger.setJobGroup(job.getGroup());
                    simpleTrigger.setStartTime(new Date(ctime));
                    simpleTrigger.setRepeatInterval(preferences.getInterval());
                    simpleTrigger.setRepeatCount(PauseAwareSimpleTrigger.REPEAT_INDEFINITELY);
                    simpleTrigger.setPriority(Trigger.DEFAULT_PRIORITY);
                }
                break;

            case ITriggerTypes.CRON_TRIGGER:
                if (cronTrigger == null) {
                    try {
                        cronTrigger = new CronTrigger(GUID + "Trigger", GUID + "TriggerGroup");
                        long ctime = System.currentTimeMillis();
                        cronTrigger.setJobName(job.getName());
                        cronTrigger.setJobGroup(job.getGroup());
                        cronTrigger.setStartTime(new Date(ctime));
                        cronTrigger.setPriority(Trigger.DEFAULT_PRIORITY);
                        cronTrigger.setCronExpression(preferences.getCronExpression());
                    } catch (ParseException e) {
                        logger.warn(e.getLocalizedMessage(), e);
                    }
                }
                break;
        }
    }

    public Trigger rescheduleJob(TaskSchedulerJobPreferences jobPreferences) throws ParseException {
        this.preferences = jobPreferences;
        job.setDescription(preferences.getDescription());
        long ctime = System.currentTimeMillis();
        switch (getTriggerType()) {
            case ITriggerTypes.CRON_TRIGGER:
                CronTrigger ct = new CronTrigger(GUID + "Trigger", GUID + "TriggerGroup");
                ct.setJobName(job.getName());
                ct.setJobGroup(job.getGroup());
                ct.setStartTime(new Date(ctime));
                ct.setPriority(Trigger.DEFAULT_PRIORITY);
                ct.setCronExpression(this.preferences.getCronExpression());
                return ct;
            default:
            case ITriggerTypes.SIMPLE_TRIGGER:
                PauseAwareSimpleTrigger nt =
                    new PauseAwareSimpleTrigger(GUID + "Trigger", GUID + "TriggerGroup");
                nt.setJobName(job.getName());
                nt.setJobGroup(job.getGroup());
                nt.setStartTime(new Date(ctime));
                nt.setRepeatInterval(preferences.getInterval());
                nt.setRepeatCount(PauseAwareSimpleTrigger.REPEAT_INDEFINITELY);
                nt.setPriority(Trigger.DEFAULT_PRIORITY);
                return nt;
        }
    }

    public void setInterval(int repeatInterval) {
        preferences.setInterval(repeatInterval);
    }

    public int getInterval() {
        return preferences.getInterval();
    }

    public TaskSchedulerJobPreferences getPreferences() {
        return this.preferences;
    }

    public void setTrigger(Trigger trigger) {
        if (trigger instanceof PauseAwareSimpleTrigger) {
            this.simpleTrigger = (PauseAwareSimpleTrigger) trigger;
        } else if (trigger instanceof CronTrigger) {
            this.cronTrigger = (CronTrigger) trigger;
        }
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public String getStateString(int state) {

        switch (state) {
            case Trigger.STATE_BLOCKED:
            case Trigger.STATE_NORMAL:
                return Localizer.localize("Server", "StateNormalText");

            case Trigger.STATE_PAUSED:
                return Localizer.localize("Server", "StatePausedText");

            case Trigger.STATE_ERROR:
                return Localizer.localize("Server", "StateErrorText");

            case Trigger.STATE_COMPLETE:
                return Localizer.localize("Server", "StateCompleteText");
            case Trigger.STATE_NONE:
                return Localizer.localize("Server", "StateNotRunningText");
            default:
                return Localizer.localize("Server", "StateNotRunningText");
        }

    }

    public String toString() {
        return "\"" + getJob().getDescription() + "\" - " + getStateString(this.state);
    }

    public String getGUID() {
        return GUID;
    }

}
