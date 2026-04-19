package com.mate.cloud.googlecheck.exception;

import com.mate.cloud.common.exception.BusinessRuntimeException;

public abstract class AbstractGoogleAuthenticatorException extends BusinessRuntimeException {

    public AbstractGoogleAuthenticatorException() {
        super();
    }

    public AbstractGoogleAuthenticatorException(String code) {
        super(code);
    }

    public AbstractGoogleAuthenticatorException(String code, String message) {
        super(code, message);
    }

    public AbstractGoogleAuthenticatorException(Throwable cause) {
        super(cause);
    }
}
