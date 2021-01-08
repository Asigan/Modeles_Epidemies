package Modeles;

public class ParamException extends RuntimeException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public ParamException(String message, Throwable err) {
		super(message, err);
	}
	public ParamException(String message) {
		super(message);
	}
}
