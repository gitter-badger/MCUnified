package net.minecraftforge.installer.platform;

import java.io.File;

final class WindowsOS implements IOperatingSystem {
    private File workDir; // cached working directory

    @Override
    public String getDisplayName() {
        return "Microsoft Windows";
    }

    @Override
    public String getMinecraftName() {
        return "windows";
    }

    @Override
    public boolean isCurrent() {
        return System.getProperty("os.name").toLowerCase().contains("win");
    }

    @Override
    public File getWorkingDirectory() {
        if (workDir != null)
            return workDir;
        String appData = System.getenv("APPDATA");
        if (appData != null) {
            workDir = new File(appData, ".minecraft");
        } else {
            workDir = new File(System.getProperty("user.home"), ".minecraft");
        }
        return workDir;
    }

    @Override
    public String getArchitecture() {
        return System.getProperty("sun.arch.data.model");
    }
}
