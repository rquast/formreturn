package com.ebstrada.formreturn.installer;

import com.ebstrada.formreturn.wizard.WizardPanelDescriptor;

public class SetupOptionsPanelDescriptor extends WizardPanelDescriptor {

  public static final String IDENTIFIER = "SETUP_OPTIONS_PANEL";

  SetupOptionsPanel setupOptionsPanel;

  public SetupOptionsPanelDescriptor() {

    setupOptionsPanel = new SetupOptionsPanel();

    setPanelDescriptorIdentifier(SetupOptionsPanelDescriptor.IDENTIFIER);
    setPanelComponent(setupOptionsPanel);

  }

  @Override
  public Object getNextPanelDescriptor() {
    return InstallerPanelDescriptor.IDENTIFIER;
  }

  @Override
  public Object getBackPanelDescriptor() {
    return LicenseAgreementPanelDescriptor.IDENTIFIER;
  }

  @Override
  public void aboutToDisplayPanel() {
    Main mainInstance = Main.getInstance();
    mainInstance.jarFileName = mainInstance.getInstallerJar();
    setupOptionsPanel.setDefaultInstallationFolder();
  }

  @Override
  public boolean aboutToHidePanel() {

    boolean isDirWritable = setupOptionsPanel.checkOutputDir();

    if (!isDirWritable) {
      // TODO: set the current panel error
      return false;
    }

    return true;

  }

}
