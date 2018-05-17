package io.mhsilva.matecocustomersupport.viewmodel;

import android.databinding.BaseObservable;
import android.databinding.ObservableField;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import io.mhsilva.matecocustomersupport.manager.ChatServerManager;
import io.mhsilva.matecocustomersupport.model.Message;
import io.mhsilva.matecocustomersupport.model.TextMessage;

public class ChatViewModel extends BaseObservable {

    private ChatViewModelListener mListener;

    public ObservableField<String> input = new ObservableField<>();
    public ObservableField<Boolean> canSend = new ObservableField<>();
    public ObservableField<Boolean> supportOnline = new ObservableField<>();

    public ChatViewModel(ChatViewModelListener listener) {
        mListener = listener;
        input.set("");
        canSend.set(false);
        supportOnline.set(false);
    }

    public void onStart() {
        ChatServerManager.getInstance().subscribe(getServerListener());
    }

    public void onStop() {
        ChatServerManager.getInstance().unsubscribe();
    }

    public void updateSupportOccupancy(int occupancy) {
        supportOnline.set(occupancy >= 2); // me & support
        notifyChange();
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
            if (actionId == EditorInfo.IME_ACTION_SEND) {
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
        input.set("");
        ChatServerManager.getInstance().sendMessage(message);
        if (mListener != null) {
            mListener.onNewMessage(message);
        }
        notifyChange();
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
                updateSupportOccupancy(occupancy);
            }
        };
    }

    // INNER LISTENER INTERFACE
    public interface ChatViewModelListener {
        void onNewMessage(Message message);
        int getMessageCount();
    }
}
