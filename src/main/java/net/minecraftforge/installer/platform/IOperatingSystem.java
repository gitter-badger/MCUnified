package net.minecraftforge.installer.platform;

import java.io.File;

/**
 * Interface for Operating Systems
 *
 * @author Tomsik68
 */
public interface IOperatingSystem {
    /**
     * @return Human-readable name of the operating system e.g. "Windows XP"
     */
    String getDisplayName();

    /**
     * @return Minecraft's name for this os (win/linux/osx/...)
     */
    String getMinecraftName();

    /**
     * @return If this pc's operating system matches this interface, return
     * true.
     */
    boolean isCurrent();

    /**
     * @return Minecraft working directory on this OS
     */
    File getWorkingDirectory();

    /**
     * @return Architecture of this system
     */
    String getArchitecture();

}
