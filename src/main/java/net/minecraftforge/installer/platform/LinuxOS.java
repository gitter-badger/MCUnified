package net.minecraftforge.installer.platform;


import java.io.File;

final class LinuxOS implements IOperatingSystem {
    private File workDir;

    @Override
    public String getDisplayName() {
        return "Linux/Unix";
    }

    @Override
    public String getMinecraftName() {
        return "linux";
    }

    @Override
    public boolean isCurrent() {
        String os = System.getProperty("os.name").toLowerCase();
        return os.contains("unix") || os.contains("linux");
    }

    // Minecraft on *Nix systems will live defaultly in ~/.minecraft
    @Override
    public File getWorkingDirectory() {
        if (workDir != null)
            return workDir;
        String userHome = System.getProperty("user.home");
        workDir = new File(userHome, ".minecraft");return workDir;
    }

    @Override
    public String getArchitecture() {
        return System.getProperty("sun.arch.data.model");
    }

}
