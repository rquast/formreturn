package com.ebstrada.formreturn.manager.ui.exception.dialog;

public class InvalidResellerContactFormDetailsException extends Exception {

    private static final long serialVersionUID = 1L;

    private boolean invalidFirstName;
    private boolean invalidLastName;
    private boolean invalidEmailAddress;
    private boolean invalidPhoneNumber;
    private boolean invalidJobTitle;
    private boolean invalidOrganization;
    private boolean invalidPostCode;
    private boolean invalidCountry;
    private boolean invalidContactTime;
    private boolean invalidRequirements;

    private int invalidCount = 0;

    public boolean isInvalidFirstName() {
        return invalidFirstName;
    }

    public void setInvalidFirstName(boolean invalidFirstName) {
        ++invalidCount;
        this.invalidFirstName = invalidFirstName;
    }

    public boolean isInvalidLastName() {
        return invalidLastName;
    }

    public void setInvalidLastName(boolean invalidLastName) {
        ++invalidCount;
        this.invalidLastName = invalidLastName;
    }

    public boolean isInvalidEmailAddress() {
        return invalidEmailAddress;
    }

    public void setInvalidEmailAddress(boolean invalidEmailAddress) {
        ++invalidCount;
        this.invalidEmailAddress = invalidEmailAddress;
    }

    public boolean isInvalidPhoneNumber() {
        return invalidPhoneNumber;
    }

    public void setInvalidPhoneNumber(boolean invalidPhoneNumber) {
        ++invalidCount;
        this.invalidPhoneNumber = invalidPhoneNumber;
    }

    public boolean isInvalidJobTitle() {
        return invalidJobTitle;
    }

    public void setInvalidJobTitle(boolean invalidJobTitle) {
        ++invalidCount;
        this.invalidJobTitle = invalidJobTitle;
    }

    public boolean isInvalidOrganization() {
        return invalidOrganization;
    }

    public void setInvalidOrganization(boolean invalidOrganization) {
        ++invalidCount;
        this.invalidOrganization = invalidOrganization;
    }

    public boolean isInvalidPostCode() {
        return invalidPostCode;
    }

    public void setInvalidPostCode(boolean invalidPostCode) {
        ++invalidCount;
        this.invalidPostCode = invalidPostCode;
    }

    public boolean isInvalidCountry() {
        return invalidCountry;
    }

    public void setInvalidCountry(boolean invalidCountry) {
        ++invalidCount;
        this.invalidCountry = invalidCountry;
    }

    public boolean isInvalidContactTime() {
        return invalidContactTime;
    }

    public void setInvalidContactTime(boolean invalidContactTime) {
        ++invalidCount;
        this.invalidContactTime = invalidContactTime;
    }

    public boolean isInvalidRequirements() {
        return invalidRequirements;
    }

    public void setInvalidRequirements(boolean invalidRequirements) {
        ++invalidCount;
        this.invalidRequirements = invalidRequirements;
    }

    public int getInvalidCount() {
        return invalidCount;
    }

    public void setInvalidCount(int invalidCount) {
        this.invalidCount = invalidCount;
    }

}
