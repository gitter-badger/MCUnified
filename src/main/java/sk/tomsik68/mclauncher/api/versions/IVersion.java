package sk.tomsik68.mclauncher.api.versions;

/**
 * General interface for versions
 *
 * @author Tomsik68
 */
public interface IVersion extends Comparable<IVersion> {
    /**
     * @return Human-readable name of this version
     */
    String getDisplayName();

    /**
     * @return ID of this version, like 1.7.5
     */
    String getId();

    /**
     * @return Unique ID of this version, like s1.7.5 or r1.7.5
     */
    String getUniqueID();

    /**
     * @return Installer that can install this version
     */
    IVersionInstaller getInstaller();

    /**
     * @return Launcher that can run this version
     */
    IVersionLauncher getLauncher();

    /**
     * @return True if this version is compatible with current runtime.
     */
    boolean isCompatible();

    /**
     * @return Reason why it isn't compatible with specified runtime
     */
    String getIncompatibilityReason();

}
