package sk.tomsik68.mclauncher.api.mods;


import java.io.File;
import java.util.List;

/**
 * Describes current set of mods that will be injected into JAR file
 */
@SuppressWarnings("SameReturnValue")
public interface IModdingProfile {
    /**
     * Returns <code>separator</code>-separated list of absolute paths to JAR files that will be injected before libraries
     * @return Null for none.
     */
    File[] injectBeforeLibs();

    /**
     * Returns <code>separator</code>-separated list of absolute paths to JAR files that will be injected after libraries
     * @return Null for none.
     */
    File[] injectAfterLibs();

    /**
     * Checks if this library should be loaded with our mods.
     * @return True if specified library may be injected along with all vanilla libraries
     */
    boolean isLibraryAllowed();

    /**
     *
     * @return Custom game JAR file to use. If you don't want to change it, return null
     */
    File getCustomGameJar();

    /**
     *
     * @return Name of main class to use while launching Minecraft.
     */
    String getMainClass();

    /**
     * Minecraft arguments are arguments that will be available in minecraft's main method.
     * These contain mostly user information, but also assets path, saves path etc, which might be useful...
     * @param minecraftArguments Array of minecraft arguments created by launcher
     * @return Array of string which is formatted in the same way as the input array. If you don't want to make any changes, return null or <code>minecraftArguments</code>
     */
    String[] changeMinecraftArguments(String[] minecraftArguments);

    /**
     *
     * @return List of parameters that will be appended after all parameters to launch the JAR. These most likely won't influence the launching process, but you may find it useful...
     */
    List<String> getLastParameters();
}
