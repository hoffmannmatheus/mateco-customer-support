package io.mhsilva.matecocustomersupport.manager;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.pubnub.api.PNConfiguration;
import com.pubnub.api.PubNub;
import com.pubnub.api.callbacks.PNCallback;
import com.pubnub.api.callbacks.SubscribeCallback;
import com.pubnub.api.enums.PNReconnectionPolicy;
import com.pubnub.api.models.consumer.PNPublishResult;
import com.pubnub.api.models.consumer.PNStatus;
import com.pubnub.api.models.consumer.pubsub.PNMessageResult;
import com.pubnub.api.models.consumer.pubsub.PNPresenceEventResult;

import java.util.Collections;

import io.mhsilva.matecocustomersupport.R;
import io.mhsilva.matecocustomersupport.model.Message;
import io.mhsilva.matecocustomersupport.model.TextMessage;

/**
 * Manages external communication with pubnub.
 */
public class ChatServerManager {

    private static final String TAG = "ChatServerManager";

    /**
     * The connection timeout PubNub setting, used for presence tracking and heartbeat.
     */
    private static final int PRESENCE_TIMEOUT = 60; // seconds

    /**
     * The string prefix used to identify users in PubNub.
     */
    private static final String UUID_PREFIX = "user.";

    /**
     * The string prefix used to create support chat channels in PubNub.
     */
    private static final String CHANNEL_PREFIX = "chat.";

    /**
     * PubNub Message type key.
     */
    private static final String JSON_TYPE = "type";

    /**
     * PubNub Message content key.
     */
    private static final String JSON_CONTENT = "content";

    private static ChatServerManager gInstance;

    private PubNub mPubNubClient;
    private ChatServerListener mListener;

    /**
     * Used to initialize this singleton and configure PubNub client.
     */
    public static void initialize(Context context) {
        if (gInstance == null) {
            gInstance = new ChatServerManager(context);
        }
    }

    /**
     * Returns the single instance.
     * @return The instance.
     */
    public static ChatServerManager getInstance() {
        return gInstance;
    }

    /**
     * Hidden constructor.
     */
    private ChatServerManager(Context context) {
        PNConfiguration config = new PNConfiguration();
        config.setPublishKey(context.getString(R.string.pn_pub_key));
        config.setSubscribeKey(context.getString(R.string.pn_sub_key));

        config.setPresenceTimeout(PRESENCE_TIMEOUT);
        config.setReconnectionPolicy(PNReconnectionPolicy.LINEAR);
        config.setUuid(getUserId());

        mPubNubClient = new PubNub(config);
        mPubNubClient.addListener(new PubNubCallback());
    }

    /**
     * Subscribe to the MateCo chat support channel.
     * @param listener The listener, used to be notified of new messages and presence updates.
     */
    public void subscribe(ChatServerListener listener) {
        Log.d(TAG, "(subscribe) called");
        mListener = listener;
        mPubNubClient.subscribe()
                .channels(Collections.singletonList(getSupportChannel()))
                .withPresence()
                .execute();
    }

    /**
     * Removes the chat subscription and deletes the internal callback listener.
     */
    public void unsubscribe() {
        Log.d(TAG, "(unsubscribe) called");
        mListener = null;
        mPubNubClient.unsubscribe()
                .channels(Collections.singletonList(getSupportChannel()))
                .execute();
    }

    public void sendMessage(Message message) {
        if (message == null) {
            return;
        }
        message.sender = getUserId();
        message.timestamp = System.currentTimeMillis();

        JsonObject json = new JsonObject();
        json.addProperty(JSON_TYPE, message.type);
        json.add(JSON_CONTENT, new Gson().toJsonTree(message));

        mPubNubClient
                .publish()
                .message(json)
                .channel(getSupportChannel())
                .async(new PNCallback<PNPublishResult>() {
                    @Override
                    public void onResponse(PNPublishResult result, PNStatus status) {
                        if (status.isError()) {
                            Log.d(TAG, "(sendMessage) error:" + status.toString());
                        } else {
                            Log.d(TAG, "(sendMessage) OK");
                        }
                    }
                });
    }

    /**
     * Gets the UUID used by pubnub to identify the current user.
     * <p>
     * This id should be unique for each user.
     * @return The UUID.
     */
    private String getUserId() {
        String token = SessionManager.getInstance().getSessionToken();
        return UUID_PREFIX + token;
    }

    /**
     * Gets the channel name for this client's support channel.
     * <p>
     * This channel should be unique for each user.
     * @return The support channel name.
     */
    private String getSupportChannel() {
        String token = SessionManager.getInstance().getSessionToken();
        return CHANNEL_PREFIX + token;
    }

    // NESTED CLASSES
    /**
     * Implementation of the Pub Nub callbacks
     */
    private class PubNubCallback extends SubscribeCallback {
        private static final String TAG = "PubNubCallback";

        // INTERFACE IMPLEMENTATION
        @Override
        public void status(PubNub pubnub, PNStatus status) {
            switch (status.getCategory()) {
                case PNConnectedCategory:
                    Log.d(TAG, "PNConnectedCategory...." + status.getOperation());
                    break;

                case PNDisconnectedCategory:
                    Log.d(TAG, "PNDisconnectedCategory...." + status.getOperation());
                    break;

                case PNAcknowledgmentCategory:
                    Log.d(TAG, "PNAcknowledgmentCategory...." + status.getOperation());
                    break;

                case PNUnexpectedDisconnectCategory:
                    Log.d(TAG, "PNUnexpectedDisconnectCategory....");
                    break;

                case PNReconnectedCategory:
                    Log.d(TAG, "PNReconnectedCategory");
                    break;

                case PNDecryptionErrorCategory:
                    Log.d(TAG, "PNReconnectedCategory");
                    break;
            }
        }

        @Override
        public void message(PubNub pubnub, PNMessageResult result) {
            if (!getSupportChannel().equals(result.getChannel())) {
                return;
            }
            JsonElement json = result.getMessage();
            if (json == null || !json.isJsonObject()) {
                return; // wut
            }
            JsonObject jsonObject = json.getAsJsonObject();
            if (jsonObject == null || !jsonObject.has(JSON_TYPE) || !jsonObject.has(JSON_CONTENT)) {
                return;
            }
            Log.d(TAG, result.getPublisher() + " - message: " + jsonObject.toString());

            Message message;
            switch (jsonObject.get(JSON_TYPE).getAsString()) {
                case Message.TYPE_TEXT:
                    message = new Gson().fromJson(jsonObject.get(JSON_CONTENT), TextMessage.class);
                    break;
                case Message.TYPE_IMAGE:
                    // TODO
                case Message.TYPE_BILL:
                    // TODO
                default:
                    return;
            }
            message.sender = result.getPublisher();
            message.timestamp = result.getTimetoken();

            if (mListener != null) {
                mListener.onNewMessage(message);
            }
        }

        @Override
        public void presence(PubNub pubnub, PNPresenceEventResult presence) {
            Log.d(TAG, "UNHANDLED: " + presence.getChannel() + " - presence: "
                    + presence.getEvent() + "    "  + presence.getUuid() +  "     "
                    + presence.getUserMetadata());
            if (getSupportChannel().equals(presence.getChannel()) && mListener != null) {
                mListener.onPresenceUpdate(presence.getOccupancy());
            }
        }
    }


    public interface ChatServerListener {
        void onNewMessage(Message message);
        void onPresenceUpdate(int occupancy);
    }
}
