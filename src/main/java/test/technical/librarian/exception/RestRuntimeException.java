package test.technical.librarian.exception;

public class RestRuntimeException extends RuntimeException {

    private static final long serialVersionUID = 6222500448452390697L;

    public RestRuntimeException(String code, String message, Throwable cause) {
        super(code.concat("|").concat(message).concat(", Cause: ").concat(cause.getMessage()), cause);
    }

    public RestRuntimeException(String code, String message) {
        super(code.concat("|").concat(message));
    }

    public RestRuntimeException(String message) {
        super(message);
    }

}
