/**
 * Copyright (c) 2017 Dell Inc., or its subsidiaries. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package io.pravega.controller.server;

import io.pravega.controller.retryable.RetryableException;
import io.pravega.shared.protocol.netty.WireCommandType;

/**
 * Wire command failed exception.
 */
public class WireCommandFailedException extends RuntimeException implements RetryableException {

    public enum Reason {
        ConnectionDropped,
        ConnectionFailed,
        UnknownHost,
        PreconditionFailed,
        AuthFailed,
    }

    private final WireCommandType type;
    private final Reason reason;

    public WireCommandFailedException(Throwable cause, WireCommandType type, Reason reason) {
        super(cause);
        this.type = type;
        this.reason = reason;
    }

    public WireCommandFailedException(WireCommandType type, Reason reason) {
        super(String.format("WireCommandFailed with type %s reason %s", type.toString(), reason.toString()));
        this.type = type;
        this.reason = reason;
    }
}
