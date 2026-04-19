package com.mate.cloud.googlecheck.exception;

public class GoogleAuthenticatorException extends AbstractGoogleAuthenticatorException{

    public GoogleAuthenticatorException() {
        super();
    }

    public GoogleAuthenticatorException(String code) {
        super(code);
    }

    public GoogleAuthenticatorException(String code, String message) {
        super(code, message);
    }

    public GoogleAuthenticatorException(Throwable cause) {
        super(cause);
    }
}
