package tk.freetobuild.mcunified;

import sk.tomsik68.mclauncher.api.mods.IModdingProfile;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by liz on 6/29/16.
 */
public class UnifiedModdingProfile implements IModdingProfile {
    public List<File> injectBeforeLibs = new ArrayList<>();
    public List<File> injectAfterLibs = new ArrayList<>();
    public String mainClass = null;
    public UnifiedModdingProfile injectBeforeLib(File... f) {
        injectBeforeLibs.addAll(Arrays.asList(f));
        return this;
    }
    public UnifiedModdingProfile injectAfterLib(File... f) {
        injectAfterLibs.addAll(Arrays.asList(f));
        return this;
    }
    public UnifiedModdingProfile setMainClass(String mainClass) {
        this.mainClass = mainClass;
        return this;
    }
    @Override
    public File[] injectBeforeLibs(String separator) {
        File[] libs = new File[injectBeforeLibs.size()];
        libs = injectBeforeLibs.toArray(libs);
        return libs;
    }

    @Override
    public File[] injectAfterLibs(String separator) {
        File[] libs = new File[injectAfterLibs.size()];
        libs = injectAfterLibs.toArray(libs);
        return libs;
    }

    @Override
    public boolean isLibraryAllowed(String libraryName) {
        return true;
    }

    @Override
    public File getCustomGameJar() {
        return null;
    }

    @Override
    public String getMainClass() {
        return mainClass;
    }

    @Override
    public String[] changeMinecraftArguments(String[] minecraftArguments) {
        return minecraftArguments;
    }

    @Override
    public List<String> getLastParameters() {
        return null;
    }
}
