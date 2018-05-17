package io.mhsilva.matecocustomersupport.model;

public class TimestampMessage extends Message {
    public TimestampMessage(long timestamp) {
        this.type = Message.TYPE_TIMESTAMP;
        this.timestamp = timestamp;
    }
}