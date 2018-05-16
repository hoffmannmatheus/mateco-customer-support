package io.mhsilva.matecocustomersupport.activity;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.databinding.OnRebindCallback;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.view.ViewGroup;

import butterknife.ButterKnife;
import io.mhsilva.matecocustomersupport.R;
import io.mhsilva.matecocustomersupport.adapter.MessagesAdapter;
import io.mhsilva.matecocustomersupport.databinding.ActivityChatBinding;
import io.mhsilva.matecocustomersupport.model.Message;
import io.mhsilva.matecocustomersupport.viewmodel.ChatViewModel;

public class ChatActivity extends AppCompatActivity implements ChatViewModel.ChatViewModelListener {

    private LinearLayoutManager mLayoutManager;
    private MessagesAdapter mAdapter;

    public static Intent newInstance(Context context) {
        return new Intent(context, ChatActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityChatBinding binding = DataBindingUtil.setContentView(this,
                R.layout.activity_chat);
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        binding.setViewModel(new ChatViewModel(this));
        binding.addOnRebindCallback(new OnRebindCallback() {
            @Override
            public boolean onPreBind(ViewDataBinding binding) {
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                    AutoTransition transition = new AutoTransition();
                    transition.setDuration(120);
                    TransitionManager.beginDelayedTransition((ViewGroup) binding.getRoot(),
                            transition);
                }
                return super.onPreBind(binding);
            }
        });
        ButterKnife.bind(this);

        if (binding.chatMessagesLayout != null) {
            setupRecyclerView(binding.chatMessagesLayout.chatRecyclerView);
        }
        // subscribe to messages
    }

    @Override
    protected void onDestroy() {
        // unsubscribe from messages
        super.onDestroy();
    }

    @Override
    public void onNewMessage(Message message) {

    }

    @Override
    public int getMessageCount() {
        return mAdapter != null ? mAdapter.getItemCount() : 0;
    }

    private void setupRecyclerView(RecyclerView recyclerView) {
        mLayoutManager = new LinearLayoutManager(this);
        mAdapter = new MessagesAdapter();
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(mAdapter);
    }
}
