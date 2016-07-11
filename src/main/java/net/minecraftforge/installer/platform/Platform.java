package net.minecraftforge.installer.platform;

import java.util.HashMap;

public final class Platform {
    // macos was renamed to osx in 1.6, so I've created a map of changed OSs
    private static final HashMap<String, String> minecraftOsWrapper = new HashMap<>();

    private static final IOperatingSystem WINDOWS = new WindowsOS();
    private static final IOperatingSystem MAC = new MacintoshOS();
    private static final IOperatingSystem SOLARIS = new SolarisOS();
    private static final IOperatingSystem UNIX  = new LinuxOS();
    private static final IOperatingSystem UNKNOWN = new UnknownOS();

    private static final IOperatingSystem[] operatingSystems = new IOperatingSystem[]{ WINDOWS, MAC, SOLARIS, UNIX, UNKNOWN };

    static {
        //                    newName | oldName
        minecraftOsWrapper.put("osx", "macos");
    }

    private Platform(){
        
    }

    /** our current operating system */
    private static IOperatingSystem currentOS = null;

    /** tries to determine the current operating system
     *  @return Current operating system
     * */
    public static IOperatingSystem getCurrentPlatform() {
        if (currentOS != null)
            return currentOS;
        for (IOperatingSystem os : operatingSystems) {
            if (os.isCurrent()) {
                forcePlatform(os);
                return currentOS;
            }
        }
        forcePlatform(new UnknownOS());
        return currentOS;
    }

    /** Forces current operating system. This can be used to override the default setting
     * in case OS detection goes wrong(it doesn't go wrong very often) or for testing.
     * @param p - The operating system we want to use
     * */
    private static void forcePlatform(IOperatingSystem p) {
        currentOS = p;
    }

    /**
     * Changes "old" name of operating system to "new".
     * Old refers to names of pre-1.6 MC launcher.
     * New refers to names of post-1.6 MC Launcher
     * @param name - Old name of operating system
     * @return New name of operating system
     */
    public static String wrapName(String name) {
        if (minecraftOsWrapper.containsKey(name)) {
            name = minecraftOsWrapper.get(name);
        }
        return name;
    }

    /**
     * Finds {@link IOperatingSystem} by name
     * @param name "New" name of operating system
     * @return IOperatingSystem which suits the criteria
     */
    public static IOperatingSystem osByName(String name) {
        for (IOperatingSystem os : operatingSystems) {
            if (os.getMinecraftName().equalsIgnoreCase(name) || name.contains(os.getMinecraftName()) || os.getMinecraftName().contains(name))
                return os;
        }
        return null;
    }
}
