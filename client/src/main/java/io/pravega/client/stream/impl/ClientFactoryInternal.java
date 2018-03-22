/**
 * Copyright (c) 2017 Dell Inc., or its subsidiaries. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package io.pravega.client.stream.impl;

import com.google.common.annotations.VisibleForTesting;
import io.pravega.client.ClientFactory;
import io.pravega.client.state.InitialUpdate;
import io.pravega.client.state.Revisioned;
import io.pravega.client.state.RevisionedStreamClient;
import io.pravega.client.state.StateSynchronizer;
import io.pravega.client.state.SynchronizerConfig;
import io.pravega.client.state.Update;
import io.pravega.client.stream.EventStreamWriter;
import io.pravega.client.stream.EventWriterConfig;
import io.pravega.client.stream.Serializer;
import java.util.UUID;


/**
 * Internal extension of {@link ClientFactory} to provide non-public methods.
 */
@VisibleForTesting
public interface ClientFactoryInternal extends ClientFactory {

    <T> EventStreamWriter<T> createEventWriter(UUID writerId, String streamName, Serializer<T> s,
                                               EventWriterConfig config);
    
    @Override
    default <T> EventStreamWriter<T> createEventWriter(String streamName, Serializer<T> s, EventWriterConfig config) {
        return createEventWriter(UUID.randomUUID(), streamName, s, config);
    }

    <T> RevisionedStreamClient<T> createRevisionedStreamClient(UUID writerId, String streamName,
                                                               Serializer<T> serializer, SynchronizerConfig config);
    
    @Override
    default <T> RevisionedStreamClient<T> createRevisionedStreamClient(String streamName, Serializer<T> serializer,
                                                                       SynchronizerConfig config) {
        return createRevisionedStreamClient(UUID.randomUUID(), streamName, serializer, config);
    }
    
    <StateT extends Revisioned, UpdateT extends Update<StateT>, InitT extends InitialUpdate<StateT>>
    StateSynchronizer<StateT> createStateSynchronizer(UUID writerId, String streamName,
                                                      Serializer<UpdateT> updateSerializer,
                                                      Serializer<InitT> initSerializer,
                                                      SynchronizerConfig config);
    
    @Override
    default <StateT extends Revisioned, UpdateT extends Update<StateT>, InitT extends InitialUpdate<StateT>>
    StateSynchronizer<StateT> createStateSynchronizer(String streamName,
                                                      Serializer<UpdateT> updateSerializer,
                                                      Serializer<InitT> initSerializer,
                                                      SynchronizerConfig config) {
        return createStateSynchronizer(UUID.randomUUID(), streamName, updateSerializer, initSerializer, config);
    }

}
