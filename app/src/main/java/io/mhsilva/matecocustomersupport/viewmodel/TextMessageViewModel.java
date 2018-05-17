package io.mhsilva.matecocustomersupport.viewmodel;

import android.databinding.BaseObservable;
import android.databinding.BindingAdapter;
import android.view.View;
import android.widget.FrameLayout;

import io.mhsilva.matecocustomersupport.manager.SessionManager;
import io.mhsilva.matecocustomersupport.model.TextMessage;

public class TextMessageViewModel extends BaseObservable {

    private TextMessage mTextMessage;

    public TextMessageViewModel(TextMessage message) {
        mTextMessage = message;
    }

    public String getText() {
        return mTextMessage != null ? mTextMessage.text : "";
    }

    public boolean isMessageFromSelf() {
        return mTextMessage != null
                && SessionManager.getInstance().matchesToken(mTextMessage.sender);
    }

    @BindingAdapter("android:layout_gravity")
    public static void setLayoutWidth(View view, int gravity) {
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams)view.getLayoutParams();
        layoutParams.gravity = gravity;
        view.setLayoutParams(layoutParams);
    }
}
