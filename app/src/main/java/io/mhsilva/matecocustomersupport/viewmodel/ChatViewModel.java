package io.mhsilva.matecocustomersupport.viewmodel;

import android.databinding.BaseObservable;
import android.databinding.ObservableField;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import java.util.Objects;

import io.mhsilva.matecocustomersupport.model.Message;
import io.mhsilva.matecocustomersupport.model.TextMessage;

public class ChatViewModel extends BaseObservable {

    private ChatViewModelListener mListener;

    public ObservableField<String> input = new ObservableField<>();
    public ObservableField<Boolean> canSend = new ObservableField<>();

    public ChatViewModel(ChatViewModelListener listener) {
        mListener = listener;
        input.set("");
        canSend.set(false);
    }

    public boolean isEmpty() {
        return mListener == null || mListener.getMessageCount() == 0;
    }

    public TextWatcher inputWatcher = new TextWatcher() {
        public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
        public void onTextChanged(CharSequence s, int start, int before, int count) {  }
        public void afterTextChanged(Editable editable) {
            String text = input.get();
            if (text != null && !text.equals(editable.toString())) {
                input.set(editable.toString());
                canSend.set(!TextUtils.isEmpty(input.get()));
                notifyChange();
            }
        }
    };

    public TextView.OnEditorActionListener inputEditorAction
            = new TextView.OnEditorActionListener() {
        @Override
        public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
            if (actionId == EditorInfo.IME_ACTION_GO) {
                sendMessage();
                return true;
            }
            return false;
        }
    };

    private void sendMessage() {
        String text = input.get();
        if (text == null || text.trim().isEmpty()) {
            return;
        }
        Message message = new TextMessage(text.trim());
        //send message
    }


    // INNER LISTENER INTERFACE
    public interface ChatViewModelListener {
        void onNewMessage(Message message);
        int getMessageCount();
    }
}
