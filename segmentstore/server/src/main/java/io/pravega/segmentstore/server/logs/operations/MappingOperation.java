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
import java.util.Map;
import java.util.UUID;

/**
 * Defines a mapping between a StreamSegment Name and its Id.
 */
public abstract class MappingOperation extends MetadataOperation {
    /**
     * Gets a value indicating the Id of the Parent StreamSegment.
     */
    public abstract long getParentStreamSegmentId();

    /**
     * Gets a value indicating the Id of the StreamSegment.
     */
    public abstract long getStreamSegmentId();

    /**
     * Sets the StreamSegmentId for this operation.
     *
     * @param value The Id of the segment to set.
     */
    public abstract void setStreamSegmentId(long value);

    /**
     * Gets a value indicating the Name of the StreamSegment.
     */
    public abstract String getStreamSegmentName();

    /**
     * Gets a value indicating the first offset within the StreamSegment available for reading.
     */
    public abstract long getStartOffset();

    /**
     * Gets a value indicating the Length of the StreamSegment at the time of the mapping.
     */
    public abstract long getLength();

    /**
     * Gets a value indicating whether the StreamSegment is currently sealed at the time of the mapping.
     */
    public abstract boolean isSealed();

    /**
     * Gets the Attributes for the StreamSegment at the time of the mapping.
     */
    public abstract Map<UUID, Long> getAttributes();

    /**
     * Gets a value indicating whether this MappingOperation is for a new StreamSegment.
     */
    public abstract boolean isNewSegment();

    /**
     * Gets a value indicating whether this MappingOperation is for a Transaction StreamSegment.
     */
    public boolean isTransaction() {
        return getParentStreamSegmentId() != ContainerMetadata.NO_STREAM_SEGMENT_ID;
    }
}
