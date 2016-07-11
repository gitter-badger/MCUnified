package sk.tomsik68.mclauncher.api.login;

import java.util.List;

/**
 * Session is what is obtained in login process.
 *
 * @author Tomsik68
 */
public interface ISession {
    /**
     * @return Username of this session
     */
    String getUsername();

    /**
     * @return ID of the session
     */
    String getSessionID();

    /**
     * @return Player's UUID
     */
    String getUUID();

    /**
     * @return Type of this session
     */
    ESessionType getType();

    /**
     * @return User properties tied with this session
     */
    List<Prop> getProperties();

    /** A simple class for user properties. A property is a name-value pair. */
    final class Prop {
        public String name, value;
    }

}
