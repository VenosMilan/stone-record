package cz.stones.stone.stones.service.exception;

public class StoneException extends RuntimeException {

    public StoneException(String message, Object... messageParams) {
        super(formatMessage(message, messageParams));
    }

    public StoneException(String message, Throwable cause, Object... messageParams) {
        super(formatMessage(message, messageParams), cause);
    }

    private static String formatMessage(String message, Object[] messageParams) {
        return String.format(message, messageParams);
    }
}
