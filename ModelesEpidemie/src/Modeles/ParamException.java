package Modeles;

public class ParamException extends RuntimeException {
	public ParamException(String message, Throwable err) {
		super(message, err);
	}
	public ParamException(String message) {
		super(message);
	}
}
