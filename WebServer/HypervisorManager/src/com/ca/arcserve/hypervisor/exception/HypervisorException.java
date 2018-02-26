package com.ca.arcserve.hypervisor.exception;

public class HypervisorException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4494273671265478501L;
	
	private String errorCode;
	private String errorMessage;
	private String hypervisorErrorMessage;
	

    public HypervisorException(String errorCode,String errorMessage){
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
        this.hypervisorErrorMessage = null;
    }
    
    public HypervisorException(String errorCode,String errorMessage, String hypervisorErrorMessage){
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
        this.hypervisorErrorMessage = hypervisorErrorMessage;
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

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public void setHypervisorErrorMessage(String hypervisorErrorMessage) {
        this.hypervisorErrorMessage = hypervisorErrorMessage;
    }
    
    public String getHypervisorErrorMessage() {
        return hypervisorErrorMessage;
    }
}
