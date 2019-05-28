package com.windstream.voip.exception;

public class VoIPServiceException extends RuntimeException {
	private static final long serialVersionUID = 603376478753726386L;

	public VoIPServiceException(String message, Throwable t) {
		super(message, t);
	}

	public VoIPServiceException(String message) {
		super(message);
	}
	
	public VoIPServiceException(Throwable t)  {
		super(t);
	}
}
