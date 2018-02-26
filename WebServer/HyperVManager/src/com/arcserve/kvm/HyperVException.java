package com.arcserve.kvm;

import org.apache.log4j.Logger;

public class HyperVException extends Exception {
    private static Logger logger = Logger.getLogger(HyperVException.class);
    
    public static final String ERROR_INVALID_HOST = "ERROR_INVALID_HOST";
    public static final String ERROR_INVALID_CREDENTIAL = "ERROR_INVALID_CREDENTIAL";
    public static final String ERROR_NO_DISK_SPACE = "ERROR_NO_DISK_SPACE";
    public static final String ERROR_FILE_TOO_LARGE = "ERROR_FILE_TOO_LARGE";
    public static final String ERROR_INVALID_STORAGE = "ERROR_INVALID_STORAGE";
    public static final String ERROR_START_VM = "ERROR_START_VM";
    public static final String ERROR_STOP_VM = "ERROR_STOP_VM";
    public static final String ERROR_CONFIG_NIC = "ERROR_CONFIG_NIC";
    public static final String ERROR_CREATE_NETWORK = "ERROR_CREATE_NETWORK";
    public static final String ERROR_CHANGE_CD_ROM = "ERROR_CHANGE_CD_ROM";
    public static final String ERROR_CREATE_VM = "ERROR_CREATE_VM";
    public static final String ERROR_DELETE_VM = "ERROR_DELETE_VM";
    
    public static final String ERROR_PUT_FILE = "ERROR_PUT_FILE";
    public static final String ERROR_DELETE_FILE = "ERROR_DELETE_FILE";

    public static final String ERROR_NET_UTIL_NOT_FOUND = "ERROR_NET_UTIL_NOT_FOUND";
    public static final String ERROR_NET_UTIL_FAILED = "ERROR_NET_UTIL_FAILED";
    public static final String ERROR_CONFIG_HV_SERVER = "ERROR_CONFIG_HV_SERVER";

    /**
     *  Auto-generated ID
     */
    private static final long serialVersionUID = -478199779694328759L;

    private String errorCode;
    private String errorMessage;
    private String hvSysErrMsg;
    
    private void init(String errorCode,String errorMessage, String hvSysErrMsg) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
        this.hvSysErrMsg = hvSysErrMsg;
        HyperVException.logger.info(errorMessage);
    }

    public HyperVException(String errorCode,String errorMessage) {
        init(errorCode, errorMessage, null);
    }
    
    public HyperVException(String errorCode,String errorMessage, String hvSysErrMsg) {
        init(errorCode, errorMessage, hvSysErrMsg);
    }


    public String getErrorCode() {
        return errorCode;
    }


    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }


    public String getErrorMessage() {
        return errorMessage;
    }

    public void setHyperVSystemErrorMessage(String hvSysErrMsg) {
        this.hvSysErrMsg = hvSysErrMsg;
    }

    public String getHyperVSystemErrorMessage() {
        return hvSysErrMsg;
    }
    
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
        HyperVException.logger.info(errorMessage);
    }
}
