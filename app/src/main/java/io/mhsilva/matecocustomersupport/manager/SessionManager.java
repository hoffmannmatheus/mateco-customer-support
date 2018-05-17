package io.mhsilva.matecocustomersupport.manager;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.UUID;

/**
 * Manages the client session, storing its token.
 */
public class SessionManager {

    private static final String PREF_SESSION_KEY = "SessionManager_session_key";
    private static SessionManager gInstance;

    private String mToken;

    /**
     * Used to lazy initialize this singleton and save/load Shared Prefs tokens.
     */
    public static void initialize(Context context) {
        if (gInstance == null) {
            gInstance = new SessionManager(context);
        }
    }

    /**
     * Returns the single instance.
     * @return The instance.
     */
    public static SessionManager getInstance() {
        return gInstance;
    }

    /**
     * Hidden constructor.
     * <p>
     * It will create a new session token (a simple UUID) or use an existing one.
     * Token is persisted using Shared Preferences, and will be erased if the app is uninstalled
     * or users clears the app data.
     * @param context The context.
     */
    private SessionManager(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_SESSION_KEY,
                Context.MODE_PRIVATE);

        if (prefs.contains(PREF_SESSION_KEY)) {
            mToken = prefs.getString(PREF_SESSION_KEY, UUID.randomUUID().toString());
        } else {
            mToken = UUID.randomUUID().toString();
            prefs.edit().putString(PREF_SESSION_KEY, mToken).apply();
        }
    }

    /**
     * Returns this unique user's session token.
     * @return The session token.
     */
    public String getSessionToken() {
        return mToken;
    }

    /**
     * Checks whether the given token matches the current session token.
     * @param givenToken The token to compare to.
     * @return Does the given token matches the current session?
     */
    public boolean matchesToken(String givenToken) {
        return mToken.equals(givenToken);
    }
}
