package io.mhsilva.matecocustomersupport.viewmodel;

import android.databinding.BaseObservable;
import android.databinding.ObservableField;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import io.mhsilva.matecocustomersupport.manager.ChatServerManager;
import io.mhsilva.matecocustomersupport.model.Message;
import io.mhsilva.matecocustomersupport.model.TextMessage;

public class ChatViewModel extends BaseObservable {

    private ChatViewModelListener mListener;
    private int mChatPresence = 0;
    private boolean canSend = false;

    public ObservableField<String> input = new ObservableField<>();

    public ChatViewModel(ChatViewModelListener listener) {
        mListener = listener;
        input.set("");
    }

    public void onStart() {
        ChatServerManager.getInstance().subscribe(getServerListener());
        ChatServerManager.getInstance().checkSupportPresence();
    }

    public void onStop() {
        ChatServerManager.getInstance().unsubscribe();
    }

    public boolean isSupportOnline() {
        return mChatPresence >= 2; // me & support
    }

    public boolean isEmpty() {
        return mListener == null || mListener.getMessageCount() == 0;
    }

    public boolean canSend() {
        return canSend;
    }

    public TextWatcher inputWatcher = new TextWatcher() {
        public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
        public void onTextChanged(CharSequence s, int start, int before, int count) {  }
        public void afterTextChanged(Editable editable) {
            String text = input.get();
            if (text != null && !text.equals(editable.toString())) {
                input.set(editable.toString());
                canSend = !TextUtils.isEmpty(input.get());
                notifyChange();
            }
        }
    };

    public TextView.OnEditorActionListener inputEditorAction
            = new TextView.OnEditorActionListener() {
        @Override
        public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                sendMessage(textView);
                textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                return true;
            }
            return false;
        }
    };

    public void sendMessage(View view) {
        String text = input.get();
        if (text == null || text.trim().isEmpty()) {
            return;
        }
        Message message = new TextMessage(text.trim());
        ChatServerManager.getInstance().sendMessage(message);
        input.set("");
        canSend = false;
        notifyChange();
    }

    public void sendImage(View view) {

    }

    public void voiceInput(View view) {

    }

    private ChatServerManager.ChatServerListener getServerListener() {
        return new ChatServerManager.ChatServerListener() {
            @Override
            public void onNewMessage(Message message) {
                if (mListener != null) {
                    mListener.onNewMessage(message);
                    notifyChange();
                }
            }

            @Override
            public void onPresenceUpdate(int occupancy) {
                mChatPresence = occupancy;
                notifyChange();
            }
        };
    }

    // INNER LISTENER INTERFACE
    public interface ChatViewModelListener {
        void onNewMessage(Message message);
        int getMessageCount();
    }
}
