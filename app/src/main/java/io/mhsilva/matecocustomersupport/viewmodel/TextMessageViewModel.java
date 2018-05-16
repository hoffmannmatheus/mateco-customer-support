package io.mhsilva.matecocustomersupport.viewmodel;

import android.databinding.BaseObservable;

import io.mhsilva.matecocustomersupport.model.TextMessage;

public class TextMessageViewModel extends BaseObservable {

    private TextMessage mTextMessage;

    public TextMessageViewModel(TextMessage message) {
        mTextMessage = message;
    }

    public String getText() {
        return mTextMessage != null ? mTextMessage.text : "";
    }
}
