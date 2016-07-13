/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.emc.nautilus.streaming.impl;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicBoolean;

import com.emc.nautilus.logclient.SegmentOutputStream;
import com.emc.nautilus.logclient.SegmentSealedExcepetion;
import com.emc.nautilus.streaming.Serializer;
import com.google.common.base.Preconditions;

public class SegmentProducerImpl<Type> implements SegmentProducer<Type> {

    private final Serializer<Type> serializer;

    private final SegmentOutputStream out;
    private final Vector<Event<Type>> outstanding = new Vector<>();
    private final AtomicBoolean sealed = new AtomicBoolean(false);
    private final AtomicBoolean closed = new AtomicBoolean(false);

    public SegmentProducerImpl(SegmentOutputStream out, Serializer<Type> serializer) {
        Preconditions.checkNotNull(out);
        Preconditions.checkNotNull(serializer);
        this.serializer = serializer;
        this.out = out;
    }

    @Override
    public void publish(Event<Type> m) throws SegmentSealedExcepetion {
        checkSealedAndClosed();
        ByteBuffer buffer = serializer.serialize(m.getValue());
        out.write(buffer, m.getCallback());
        outstanding.add(m);
    }

    @Override
    public void flush() throws SegmentSealedExcepetion {
        checkSealedAndClosed();
        try {
            out.flush();
        } catch (SegmentSealedExcepetion e) {
            sealed.set(true);
            throw e;
        }
    }

    @Override
    public void close() throws SegmentSealedExcepetion {
        Preconditions.checkState(!sealed.get(), "Already Sealed");
        if (closed.get()) {
            return;
        }
        try {
            out.close();
        } catch (SegmentSealedExcepetion e) {
            sealed.set(true);
            throw e;
        }
    }

    private void checkSealedAndClosed() {
        Preconditions.checkState(!sealed.get(), "Already Sealed");
        Preconditions.checkState(!closed.get(), "Already Closed");
    }

    /**
     * @return All unacked events in the order in which they were published.
     */
    @Override
    public List<Event<Type>> getUnackedEvents() {
        return new ArrayList<>(outstanding);
    }

    @Override
    public boolean isAlreadySealed() {
        return sealed.get();
    }

}
