package org.jd.core.v1.model.message;

import org.jd.core.v1.service.converter.classfiletojavasyntax.util.TypeMaker;

public class CompileConfiguration {

    private boolean dumpOpcode;
    private TypeMaker typeMaker;
    private boolean realignLineNumbers;
    private Boolean containsByteCode;
    private Boolean showBridgeAndSynthetic;

    public boolean isDumpOpcode() {
        return dumpOpcode;
    }

    public CompileConfiguration setDumpOpcode(boolean dumpOpcode) {
        this.dumpOpcode = dumpOpcode;
        return this;
    }

    public TypeMaker getTypeMaker() {
        return typeMaker;
    }

    public CompileConfiguration setTypeMaker(TypeMaker typeMaker) {
        this.typeMaker = typeMaker;
        return this;
    }

    public boolean isRealignLineNumbers() {
        return realignLineNumbers;
    }

    public CompileConfiguration setRealignLineNumbers(boolean realignLineNumbers) {
        this.realignLineNumbers = realignLineNumbers;
        return this;
    }

    public Boolean getContainsByteCode() {
        return containsByteCode;
    }

    public Boolean getContainsByteCode(Boolean defVal) {
        return containsByteCode != null ? containsByteCode : defVal;
    }

    public void setContainsByteCode(Boolean containsByteCode) {
        this.containsByteCode = containsByteCode;
    }

    public Boolean getShowBridgeAndSynthetic() {
        return showBridgeAndSynthetic;
    }

    public Boolean getShowBridgeAndSynthetic(Boolean defVal) {
        return showBridgeAndSynthetic != null ? showBridgeAndSynthetic : defVal;
    }

    public void setShowBridgeAndSynthetic(Boolean showBridgeAndSynthetic) {
        this.showBridgeAndSynthetic = showBridgeAndSynthetic;
    }

}
