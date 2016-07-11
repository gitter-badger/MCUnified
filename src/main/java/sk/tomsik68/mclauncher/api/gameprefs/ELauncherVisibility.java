package sk.tomsik68.mclauncher.api.gameprefs;

import java.util.HashMap;

/**
 * This is used for compatibility with the official launcher.
 * Launcher visibility rules determine how the launcher behaves when you start game.
 *
 * @author Tomsik68
 */
public enum ELauncherVisibility {

    ;

    private static final HashMap<String, ELauncherVisibility> lookupMap = new HashMap<>();

    ELauncherVisibility(String lookupStr) {
        addLV(lookupStr);
    }

    /** addLookupValue :) */
    private void addLV(String lookupStr) {
        lookupMap.put(lookupStr, this);
    }
}
