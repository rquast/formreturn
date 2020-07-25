package com.ebstrada.formreturn.server.preferences.persistence;

import com.ebstrada.formreturn.manager.util.NoObfuscation;
import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("vacuumJobPreferences") public class VacuumJobPreferences
    extends TaskSchedulerJobPreferences implements NoObfuscation {

}
