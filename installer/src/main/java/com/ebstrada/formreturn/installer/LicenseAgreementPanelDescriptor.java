package com.ebstrada.formreturn.installer;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;

import javax.swing.JScrollBar;

import com.ebstrada.formreturn.wizard.WizardPanelDescriptor;

public class LicenseAgreementPanelDescriptor extends WizardPanelDescriptor implements
    ActionListener, AdjustmentListener {

  public static final String IDENTIFIER = "LICENSE_AGREEMENT_PANEL";

  public boolean hasAgreementBeenRead = false;

  private LicenseAgreementPanel licenseAgreementPanel;

  public LicenseAgreementPanelDescriptor() {

    licenseAgreementPanel = new LicenseAgreementPanel();
    licenseAgreementPanel.addCheckBoxActionListener(this);
    licenseAgreementPanel.addScrollBarAdjustmentListener(this);

    setPanelDescriptorIdentifier(LicenseAgreementPanelDescriptor.IDENTIFIER);
    setPanelComponent(licenseAgreementPanel);

  }

  public void adjustmentValueChanged(AdjustmentEvent e) {
    if (e.getSource() instanceof JScrollBar && licenseAgreementPanel.isVisible()) {
      JScrollBar scrollBar = (JScrollBar) e.getSource();
      int scrollBarValue = scrollBar.getValue();
      int extentValue = scrollBar.getModel().getMaximum() - scrollBar.getModel().getExtent();
      if (scrollBarValue >= (extentValue - 10) && hasAgreementBeenRead == false) {
        hasAgreementBeenRead = true;
        licenseAgreementPanel.removeScrollDownBlock();
      }
    }
  }

  @Override
  public Object getNextPanelDescriptor() {
    return SetupOptionsPanelDescriptor.IDENTIFIER;
  }

  @Override
  public Object getBackPanelDescriptor() {
    return WelcomePanelDescriptor.IDENTIFIER;
  }

  @Override
  public void aboutToDisplayPanel() {
    setNextButtonAccordingToCheckBox();
  }

  public void actionPerformed(ActionEvent e) {
    setNextButtonAccordingToCheckBox();
  }

  private void setNextButtonAccordingToCheckBox() {
    if (licenseAgreementPanel.isCheckBoxSelected()) {
      getWizard().setNextFinishButtonEnabled(true);
    } else {
      getWizard().setNextFinishButtonEnabled(false);
    }

  }
}
