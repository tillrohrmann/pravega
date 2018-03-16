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

import com.google.common.base.Preconditions;
import io.pravega.common.Exceptions;
import io.pravega.common.io.serialization.RevisionDataInput;
import io.pravega.common.io.serialization.RevisionDataOutput;
import io.pravega.segmentstore.server.ContainerMetadata;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;

/**
 * Log Operation that represents the creation of a new StreamSegment or Transaction Segment.
 */
public class CreateSegmentOperation extends MetadataOperation {
    //region Members

    private long streamSegmentId;
    private long parentStreamSegmentId;
    private String streamSegmentName;
    private Map<UUID, Long> attributes;

    //endregion

    //region Constructor

    /**
     * Creates a new instance of the CreateSegmentOperation class for a non-transaction Segment.
     *
     * @param streamSegmentName Name of the StreamSegment to create.
     * @param attributes        A Map of initial Attributes to set on this StreamSegment.
     */
    public CreateSegmentOperation(String streamSegmentName, Map<UUID, Long> attributes) {
        this(ContainerMetadata.NO_STREAM_SEGMENT_ID, streamSegmentName, attributes);
    }

    /**
     * Creates a new instance of the CreateSegmentOperation class.
     *
     * @param parentStreamSegmentId The Id of the Parent StreamSegment. If this is not a transaction, this should be set
     *                              to ContainerMetadata.NO_STREAM_SEGMENT_ID.
     * @param streamSegmentName     Name of the StreamSegment to create.
     * @param attributes            A Map of initial Attributes to set on this StreamSegment.
     */
    public CreateSegmentOperation(long parentStreamSegmentId, String streamSegmentName, Map<UUID, Long> attributes) {
        this.streamSegmentId = ContainerMetadata.NO_STREAM_SEGMENT_ID;
        this.parentStreamSegmentId = parentStreamSegmentId;
        this.streamSegmentName = Exceptions.checkNotNullOrEmpty(streamSegmentName, "streamSegmentName");
        this.attributes = Preconditions.checkNotNull(attributes, "attributes");
    }

    /**
     * Deserialization constructor.
     */
    private CreateSegmentOperation() {
    }

    //endregion

    //region Properties

    /**
     * Gets a value indicating the Name of the StreamSegment.
     */
    public String getStreamSegmentName() {
        return this.streamSegmentName;
    }

    /**
     * Gets a value indicating the Id of the StreamSegment.
     */
    public long getStreamSegmentId() {
        return this.streamSegmentId;
    }

    /**
     * Sets the StreamSegmentId for this operation.
     *
     * @param value The Id of the segment to set.
     */
    public void setStreamSegmentId(long value) {
        Preconditions.checkState(this.streamSegmentId == ContainerMetadata.NO_STREAM_SEGMENT_ID,
                "StreamSegmentId has already been assigned for this operation.");
        Preconditions.checkArgument(value != ContainerMetadata.NO_STREAM_SEGMENT_ID, "Invalid StreamSegmentId");
        this.streamSegmentId = value;
    }

    /**
     * Gets the Attributes for the StreamSegment at the time of the mapping.
     */
    public Map<UUID, Long> getAttributes() {
        return this.attributes;
    }

    /**
     * Gets a value indicating the Id of the Parent StreamSegment.
     */
    public long getParentStreamSegmentId() {
        return this.parentStreamSegmentId;
    }

    public boolean isTransaction() {
        return this.parentStreamSegmentId != ContainerMetadata.NO_STREAM_SEGMENT_ID;
    }

    @Override
    public String toString() {
        return String.format(
                "%s, Id = %s%s, Name = %s",
                super.toString(),
                toString(getStreamSegmentId(), ContainerMetadata.NO_STREAM_SEGMENT_ID),
                isTransaction() ? String.format(", ParentId = %s", getParentStreamSegmentId()) : "",
                getStreamSegmentName());
    }

    //endregion

    static class Serializer extends OperationSerializer<CreateSegmentOperation> {
        private static final int STATIC_LENGTH = 3 * Long.BYTES;

        @Override
        protected OperationBuilder<CreateSegmentOperation> newBuilder() {
            return new OperationBuilder<>(new CreateSegmentOperation());
        }

        @Override
        protected byte getWriteVersion() {
            return 0;
        }

        @Override
        protected void declareVersions() {
            version(0).revision(0, this::write00, this::read00);
        }

        @Override
        protected void beforeSerialization(CreateSegmentOperation o) {
            super.beforeSerialization(o);
            Preconditions.checkState(o.streamSegmentId != ContainerMetadata.NO_STREAM_SEGMENT_ID, "StreamSegment Id has not been assigned.");
        }

        private void write00(CreateSegmentOperation o, RevisionDataOutput target) throws IOException {
            target.length(STATIC_LENGTH + target.getUTFLength(o.streamSegmentName)
                    + target.getMapLength(o.attributes.size(), RevisionDataOutput.UUID_BYTES, Long.BYTES));
            target.writeLong(o.getSequenceNumber());
            target.writeLong(o.streamSegmentId);
            target.writeLong(o.parentStreamSegmentId);
            target.writeUTF(o.streamSegmentName);
            target.writeMap(o.attributes, RevisionDataOutput::writeUUID, RevisionDataOutput::writeLong);
        }

        private void read00(RevisionDataInput source, OperationBuilder<CreateSegmentOperation> b) throws IOException {
            b.instance.setSequenceNumber(source.readLong());
            b.instance.streamSegmentId = source.readLong();
            b.instance.parentStreamSegmentId = source.readLong();
            b.instance.streamSegmentName = source.readUTF();
            b.instance.attributes = source.readMap(RevisionDataInput::readUUID, RevisionDataInput::readLong);
        }
    }
}
