package sk.tomsik68.mclauncher.api.common;

import java.io.File;
import java.util.List;
import java.util.Map;

/**
 * This class holds basic settings for launching minecraft
 *
 * @author Tomsik68
 */
@SuppressWarnings({"unused", "SameReturnValue"})
public interface ILaunchSettings {
    /**
     * <B>Required</B>
     * Accepts values like 512M or 3G etc.
     *
     * @return initial heap size (-Xms argument)
     */
    String getInitHeap();

    /**
     * <B>Required</B>
     * Accepts values like 512M or 3G etc.
     *
     * @return maximal heap size (-Xmx argument)
     */
    String getHeap();

    /**
     * @return Map of custom parameters for either minecraft applet or minecraft
     * main method(depends on version). May be null.
     */
    Map<String, String> getCustomParameters();

    /**
     * @return command list to append before the minecraft launch command. Can
     * be glc-capture or other programs that need process pointer...
     */
    List<String> getCommandPrefix();

    /**
     * @return If applet should open a table with options to change(only works
     * with MCAssetsVersion). False if unsure.
     */
    boolean isModifyAppletOptions();

    /**
     * @return Java executable location (e.g. C:\Program
     * Files\java\jre\bin\java.exe). If null, default java will be used
     */
    File getJavaLocation();

    /**
     * @return Additional arguments for java process
     */
    List<String> getJavaArguments();
}
