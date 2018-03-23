/**
 * Copyright (c) 2017 Dell Inc., or its subsidiaries. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package io.pravega.segmentstore.server.attributes;

import com.google.common.base.Preconditions;
import io.pravega.common.concurrent.Futures;
import io.pravega.segmentstore.contracts.StreamSegmentNotExistsException;
import io.pravega.segmentstore.server.AttributeIndex;
import io.pravega.segmentstore.server.ContainerMetadata;
import io.pravega.segmentstore.server.OperationLog;
import io.pravega.segmentstore.server.SegmentMetadata;
import io.pravega.segmentstore.storage.Storage;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ScheduledExecutorService;

/**
 * Attribute Index manager for all Segments within a SegmentContainer.
 */
public class ContainerAttributeIndex {
    //region Members

    private final ContainerMetadata containerMetadata;
    private final Storage storage;
    private final OperationLog operationLog;
    private final AttributeIndexConfig config;
    private final ScheduledExecutorService executor;

    //endregion

    //region Constructor

    /**
     * Creates a new instance of the ContainerAttributeIndex class.
     *
     * @param containerMetadata The Segment Container's Metadata.
     * @param storage           A Storage adapter which can be used to access the Attribute Segment.
     * @param operationLog      An OperationLog that can be used to atomically update attributes for the main Segment.
     * @param config            Attribute Index Configuration.
     * @param executor          An Executor to run async tasks.
     */
    ContainerAttributeIndex(ContainerMetadata containerMetadata, Storage storage, OperationLog operationLog,
                            AttributeIndexConfig config, ScheduledExecutorService executor) {
        this.containerMetadata = Preconditions.checkNotNull(containerMetadata, "containerMetadata");
        this.storage = Preconditions.checkNotNull(storage, "storage");
        this.operationLog = Preconditions.checkNotNull(operationLog, "operationLog");
        this.config = Preconditions.checkNotNull(config, "config");
        this.executor = Preconditions.checkNotNull(executor, "executor");
    }

    //endregion

    //region Operations

    /**
     * Creates a new AttributeIndex instance and initializes it.
     *
     * @param streamSegmentId The Id of the Segment to create the AttributeIndex for.
     * @param timeout         Timeout for the operation.
     * @return A CompletableFuture that, when completed, will contain the requested. AttributeIndex.
     */
    CompletableFuture<AttributeIndex> forSegment(long streamSegmentId, Duration timeout) {
        SegmentMetadata sm = this.containerMetadata.getStreamSegmentMetadata(streamSegmentId);
        if (sm.isDeleted() || sm.isMerged()) {
            return Futures.failedFuture(new StreamSegmentNotExistsException(sm.getName()));
        }

        SegmentAttributeIndex index = new SegmentAttributeIndex(sm, this.storage, this.operationLog, this.config, this.executor);
        return index.initialize(timeout)
                    .thenApply(v -> index);
    }

    //endregion
}
