package io.mhsilva.matecocustomersupport.manager;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.pubnub.api.PNConfiguration;
import com.pubnub.api.PubNub;
import com.pubnub.api.callbacks.SubscribeCallback;
import com.pubnub.api.enums.PNReconnectionPolicy;
import com.pubnub.api.models.consumer.PNStatus;
import com.pubnub.api.models.consumer.pubsub.PNMessageResult;
import com.pubnub.api.models.consumer.pubsub.PNPresenceEventResult;

import io.mhsilva.matecocustomersupport.R;
import io.mhsilva.matecocustomersupport.model.Message;
import io.mhsilva.matecocustomersupport.model.TextMessage;

/**
 * Manages external communication with pubnub.
 */
public class ChatServerManager {
    private static final String TAG = "ChatServerManager";
    private static final int PRESENCE_TIMEOUT = 60; // seconds
    private static final String UUID_PREFIX = "user_";

    private static ChatServerManager gInstance;
    private PubNub mPubNubClient;

    /**
     * Used to initialize this singleton and configure PubNub client.
     */
    public static void initialize(Context context) {
        if (gInstance != null) {
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
     * Gets the UUID used by pubnub to identify the current user.
     * @return The UUID.
     */
    private String getUserId() {
        String token = SessionManager.getInstance().getSessionToken();
        return UUID_PREFIX + token;
    }

    // NESTED CLASSES
    /**
     * Implementation of the Pub Nub callbacks
     */
    private class PubNubCallback extends SubscribeCallback {
        private static final String TAG = "PubNubCallback";

        private static final String JSON_TYPE = "type";
        private static final String JSON_CONTENT = "content";

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
            // todo setup/call listeners to send message
        }

        @Override
        public void presence(PubNub pubnub, PNPresenceEventResult presence) {
            Log.d(TAG, "UNHANDLED: " + presence.getChannel() + " - presence: "
                    + presence.getEvent() + "    "  + presence.getUuid() +  "     "
                    + presence.getUserMetadata());
            // todo send presence update (just occupancy?)
        }
    }
}
