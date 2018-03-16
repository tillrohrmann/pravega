/**
 * Copyright (c) 2017 Dell Inc., or its subsidiaries. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package io.pravega.segmentstore.server.logs.operations;

import io.pravega.segmentstore.server.ContainerMetadata;
import java.util.Random;
import org.junit.Assert;


/**
 * Unit tests for CreateSegmentOperation class.
 */
public class CreateSegmentOperationTests extends OperationTestsBase<CreateSegmentOperation> {
    @Override
    protected CreateSegmentOperation createOperation(Random random) {
        return new CreateSegmentOperation(random.nextLong(), super.getStreamSegmentName(random.nextLong()),
                StreamSegmentMapOperationTests.createAttributes(10));
    }

    @Override
    protected boolean isPreSerializationConfigRequired(CreateSegmentOperation operation) {
        return operation.getStreamSegmentId() == ContainerMetadata.NO_STREAM_SEGMENT_ID;
    }

    @Override
    protected void configurePreSerialization(CreateSegmentOperation operation, Random random) {
        if (operation.getStreamSegmentId() == ContainerMetadata.NO_STREAM_SEGMENT_ID) {
            operation.setStreamSegmentId(random.nextLong());
        } else if (isPreSerializationConfigRequired(operation)) {
            Assert.fail("isPreSerializationConfigRequired returned true but there is nothing to be done.");
        }
    }
}
