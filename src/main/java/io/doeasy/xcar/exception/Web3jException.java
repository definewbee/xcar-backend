package io.doeasy.xcar.exception;

/**
 * @author kris.wang
 */
public class Web3jException extends RuntimeException {

    public Web3jException(String message, Throwable cause) {
        super(message, cause);
    }

    public Web3jException(String message) {
        super(message);
    }
}
