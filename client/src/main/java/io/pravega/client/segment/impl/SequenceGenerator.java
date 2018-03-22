/**
 * Copyright (c) 2017 Dell Inc., or its subsidiaries. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package io.pravega.client.segment.impl;

import io.pravega.common.hash.RandomFactory;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Supplier;

public class SequenceGenerator implements Supplier<Long> {
    
    private static final long INITIAL_MASK = Long.MAX_VALUE >>> 1; // Created with some headroom to grow before wrap.
    
    /**
     * Current sequence. Initialized to a random value to prevent collision via bug.
     */
    private final AtomicLong value = new AtomicLong(RandomFactory.getSeed() & INITIAL_MASK); 
    
    @Override
    public Long get() {
        return value.incrementAndGet();
    }

}
