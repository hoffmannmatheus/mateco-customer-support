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

import butterknife.BindView;
import butterknife.ButterKnife;
import io.mhsilva.matecocustomersupport.R;
import io.mhsilva.matecocustomersupport.adapter.MessagesAdapter;
import io.mhsilva.matecocustomersupport.databinding.ActivityChatBinding;
import io.mhsilva.matecocustomersupport.model.Message;
import io.mhsilva.matecocustomersupport.viewmodel.ChatViewModel;

public class ChatActivity extends AppCompatActivity implements ChatViewModel.ChatViewModelListener {

    @BindView(R.id.chat_recyclerView)
    RecyclerView mRecyclerView;

    private ChatViewModel mViewModel;
    private MessagesAdapter mAdapter;
    private LinearLayoutManager mLayoutManager;

    public static Intent newInstance(Context context) {
        return new Intent(context, ChatActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityChatBinding binding = DataBindingUtil.setContentView(this,
                R.layout.activity_chat);
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        mViewModel = new ChatViewModel(this);
        binding.setViewModel(mViewModel);
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
        setupRecyclerView();
        // subscribe to messages
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mViewModel != null) {
            mViewModel.onStart();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mViewModel != null) {
            mViewModel.onStop();
        }
    }

    @Override
    protected void onDestroy() {
        // unsubscribe from messages
        super.onDestroy();
    }

    @Override
    public void onNewMessage(Message message) {
        if (mAdapter != null) {
            mAdapter.add(message);
        }
    }

    @Override
    public int getMessageCount() {
        return mAdapter != null ? mAdapter.getItemCount() : 0;
    }

    private void setupRecyclerView() {
        mLayoutManager = new LinearLayoutManager(this);
        mAdapter = new MessagesAdapter();
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setAdapter(mAdapter);
    }
}
