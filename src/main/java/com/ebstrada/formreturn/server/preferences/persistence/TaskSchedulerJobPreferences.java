package com.ebstrada.formreturn.server.preferences.persistence;

import com.ebstrada.formreturn.manager.util.NoObfuscation;
import com.ebstrada.formreturn.server.quartz.ITriggerTypes;

public abstract class TaskSchedulerJobPreferences implements NoObfuscation {

    private int interval = 86400000;

    private boolean autoStart = true;

    private String description = "Task Description";

    private String cronExpression = "0 10 1 ? * *";

    private int triggerType = ITriggerTypes.SIMPLE_TRIGGER;

    public int getInterval() {
        return interval;
    }

    public void setInterval(int interval) {
        this.interval = interval;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isAutoStart() {
        return autoStart;
    }

    public void setAutoStart(boolean autoStart) {
        this.autoStart = autoStart;
    }

    public int getTriggerType() {
        return triggerType;
    }

    public void setTriggerType(int triggerType) {
        this.triggerType = triggerType;
    }

    public String getCronExpression() {
        return cronExpression;
    }

    public void setCronExpression(String cronExpression) {
        this.cronExpression = cronExpression;
    }

}
