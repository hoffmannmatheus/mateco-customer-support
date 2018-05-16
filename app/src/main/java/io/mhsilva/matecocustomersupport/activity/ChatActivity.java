package io.mhsilva.matecocustomersupport.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.mhsilva.matecocustomersupport.R;
import io.mhsilva.matecocustomersupport.adapter.MessagesAdapter;

public class ChatActivity extends AppCompatActivity {

    @BindView(R.id.chat_recyclerView)
    RecyclerView mRecyclerView;

    @BindView(R.id.chat_empty_image)
    ImageView mEmptyImage;

    @BindView(R.id.chat_empty_text)
    TextView mEmptyText;

    private LinearLayoutManager mLayoutManager;
    private MessagesAdapter mAdapter;

    public static Intent newInstance(Context context) {
        return new Intent(context, ChatActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        ButterKnife.bind(this);

        mLayoutManager = new LinearLayoutManager(this);
        mAdapter = new MessagesAdapter();
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setAdapter(mAdapter);

        // subscribe to messages
    }

    @Override
    protected void onStart() {
        super.onStart();
        setFeedback(mAdapter.getItemCount() == 0);
    }

    @Override
    protected void onDestroy() {
        // unsubscribe from messages
        super.onDestroy();
    }

    private void setFeedback(boolean showEmptyMessage) {
        mEmptyImage.setVisibility(showEmptyMessage ? View.VISIBLE : View.GONE);
        mEmptyText.setVisibility(showEmptyMessage ? View.VISIBLE : View.GONE);
    }
}
