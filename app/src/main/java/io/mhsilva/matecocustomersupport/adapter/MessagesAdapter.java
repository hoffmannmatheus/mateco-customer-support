package io.mhsilva.matecocustomersupport.adapter;

import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import io.mhsilva.matecocustomersupport.R;
import io.mhsilva.matecocustomersupport.databinding.ViewItemMessageTextBinding;
import io.mhsilva.matecocustomersupport.databinding.ViewItemMessageTimestampBinding;
import io.mhsilva.matecocustomersupport.model.Message;
import io.mhsilva.matecocustomersupport.model.TextMessage;
import io.mhsilva.matecocustomersupport.model.TimestampMessage;
import io.mhsilva.matecocustomersupport.viewmodel.TextMessageViewModel;
import io.mhsilva.matecocustomersupport.viewmodel.TimestampMessageViewModel;

public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.BindingHolder> {

    private static final int TYPE_TEXT = 0;
    private static final int TYPE_IMAGE = 1;
    private static final int TYPE_BILL = 2;
    private static final int TYPE_TIMESTAMP = 3;

    private List<Message> mMessages;

    public MessagesAdapter() {
        mMessages = new ArrayList<>();
    }

    @NonNull @Override
    public BindingHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case TYPE_IMAGE:
                // todo

            case TYPE_BILL:
                // todo

            case TYPE_TIMESTAMP:
                ViewItemMessageTimestampBinding timeBinding = DataBindingUtil.inflate(
                        LayoutInflater.from(parent.getContext()),
                        R.layout.view_item_message_timestamp,
                        parent,
                        false);
                return new BindingHolder(timeBinding);

            case TYPE_TEXT:
            default:
                ViewItemMessageTextBinding textBinding = DataBindingUtil.inflate(
                    LayoutInflater.from(parent.getContext()),
                    R.layout.view_item_message_text,
                    parent,
                    false);
                return new BindingHolder(textBinding);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull BindingHolder holder, int position) {
        Message message = mMessages.get(position);
        switch (message.type) {
            case Message.TYPE_IMAGE:
                // todo
                break;

            case Message.TYPE_BILL:
                // todo
                break;

            case Message.TYPE_TIMESTAMP:
                ViewItemMessageTimestampBinding timeBinding = holder.timeBinding;
                timeBinding.setViewModel(new TimestampMessageViewModel(message.timestamp));
                break;

            case Message.TYPE_TEXT:
                ViewItemMessageTextBinding textBinding = holder.textBinding;
                textBinding.setViewModel(new TextMessageViewModel((TextMessage) message));
                break;
        }
    }

    @Override
    public int getItemViewType(int position) {
        Message message = mMessages.get(position);
        switch (message.type) {
            case Message.TYPE_IMAGE:
                return TYPE_IMAGE;

            case Message.TYPE_BILL:
                return TYPE_BILL;

            case Message.TYPE_TIMESTAMP:
                return TYPE_TIMESTAMP;

            case Message.TYPE_TEXT:
            default:
                return TYPE_TEXT;
        }
    }

    @Override
    public int getItemCount() {
        return mMessages.size();
    }

    /**
     * Adds a new message to the adapter.
     * <p>
     * This method will also add the Timestamp Message if needed.
     * The only current rule is: if there's no previous messages, add the timestamp.
     * @param message The message.
     */
    public int add(Message message) {
        if (message == null) {
            return -1;
        }
        int index = mMessages.size();
        // check timestamp
        if (index == 0) {
            mMessages.add(new TimestampMessage(message.timestamp));
            index++;
        }
        // todo: check if previous message is (eg) 24h+ of difference. If so, add timestamp.
        mMessages.add(message);
        notifyItemInserted(index);
        return index;
    }

    class BindingHolder extends RecyclerView.ViewHolder {
        private ViewItemMessageTextBinding textBinding;
        private ViewItemMessageTimestampBinding timeBinding;

        BindingHolder(ViewItemMessageTextBinding binding) {
            super(binding.textMessageRoot);
            this.textBinding = binding;
        }

        BindingHolder(ViewItemMessageTimestampBinding binding) {
            super(binding.timestampMessageRoot);
            this.timeBinding = binding;
        }
    }
}