package io.mhsilva.matecocustomersupport.model;

import android.support.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Represents a message exchanged in the chat.
 */
public abstract class Message {
    @StringDef({
            TYPE_TEXT,
            TYPE_IMAGE,
            TYPE_BILL
    })
    @Retention(RetentionPolicy.SOURCE)
    public @interface Type { }

    public static final String TYPE_TEXT = "text";
    public static final String TYPE_IMAGE = "image";
    public static final String TYPE_BILL = "bill";

    public @Type String type;
    public String sender;
    public long timestamp;
}
