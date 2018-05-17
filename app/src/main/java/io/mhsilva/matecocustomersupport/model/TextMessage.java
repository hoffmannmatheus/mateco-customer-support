package io.mhsilva.matecocustomersupport.model;

public class TextMessage extends Message {
    public String text;

    public TextMessage() {
        this.type = Message.TYPE_TEXT;
    }

    public TextMessage(String text) {
        this.type = Message.TYPE_TEXT;
        this.text = text;
    }
}