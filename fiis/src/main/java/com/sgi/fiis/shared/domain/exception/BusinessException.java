package com.sgi.fiis.shared.domain.exception;

import java.util.Arrays;

public class BusinessException extends RuntimeException {
    private final Object[] args;

    public BusinessException(String message) {
        super(message);
        this.args = null;
    }

    public BusinessException(String message, Object[] args) {
        super(message);
        this.args = args != null ? Arrays.copyOf(args, args.length) : null;
    }

    public Object[] getArgs() {
        return args != null ? Arrays.copyOf(args, args.length) : null;
    }
}
