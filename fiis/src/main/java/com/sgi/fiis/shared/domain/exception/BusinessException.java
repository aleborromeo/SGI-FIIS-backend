package com.sgi.fiis.shared.domain.exception;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class BusinessException extends RuntimeException {
    private final transient List<Object> args;

    public BusinessException(String message) {
        super(message);
        this.args = Collections.emptyList();
    }

    public BusinessException(String message, Object[] args) {
        super(message);
        if (args == null) {
            this.args = Collections.emptyList();
        } else {
            this.args = Collections.unmodifiableList(Arrays.asList(Arrays.copyOf(args, args.length)));
        }
    }

    public Object[] getArgs() {
        return args.isEmpty() ? null : args.toArray();
    }
}
