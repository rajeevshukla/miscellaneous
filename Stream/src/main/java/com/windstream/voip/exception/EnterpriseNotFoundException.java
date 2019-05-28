package com.windstream.voip.exception;

public class EnterpriseNotFoundException extends UserNotFoundException{

	/**
	 * 
	 */
	private static final long serialVersionUID = 2301034951297049245L;

	public EnterpriseNotFoundException(String message, Throwable t) {
		
		super(message, t);
	}
	
	public EnterpriseNotFoundException(String message) {
		super(message);
	}
	
	public EnterpriseNotFoundException() {
		this("Enterprise groups details not found for the given account.");
	}

}
