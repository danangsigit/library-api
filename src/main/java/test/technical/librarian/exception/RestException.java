package test.technical.librarian.exception;

public class RestException extends Exception {

    private static final long serialVersionUID = -1419357854115809564L;

    public RestException(String code, String message, Throwable cause) {
        super(code.concat("|").concat(message).concat(", Cause: ").concat(cause.getMessage()), cause);
    }

    public RestException(String code, String message) {
        super(code.concat("|").concat(message));
    }

    public RestException(String message) {
        super(message);
    }
}
