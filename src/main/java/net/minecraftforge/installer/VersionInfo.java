package net.minecraftforge.installer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URL;
import java.util.jar.JarFile;

import argo.jdom.JdomParser;
import argo.jdom.JsonNode;
import argo.jdom.JsonRootNode;

import com.google.common.base.Charsets;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.base.Throwables;
import com.google.common.collect.Iterables;
import com.google.common.io.ByteStreams;
import com.google.common.io.Files;
import com.google.common.io.OutputSupplier;

public class VersionInfo {
    private JsonRootNode versionData;
    private JarFile installerJar = null;
    public VersionInfo(URI installer)
    {
        try {
            File installerFile = File.createTempFile("forge-installer","jar");
            installerFile.delete();
            java.nio.file.Files.copy(installer.toURL().openStream(),installerFile.toPath());
            installerJar = new JarFile(installerFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(installerJar!=null) {
            InputStream installProfile = null;
            try {
                installProfile = installerJar.getInputStream(installerJar.getJarEntry("install_profile.json"));
            } catch (IOException e) {
                e.printStackTrace();
            }
            JdomParser parser = new JdomParser();

            try {
                versionData = parser.parse(new InputStreamReader(installProfile, Charsets.UTF_8));
            } catch (Exception e) {
                throw Throwables.propagate(e);
            }
        }
    }

    public String getProfileName()
    {
        return this.versionData.getStringValue("install","profileName");
    }

    public String getVersionTarget()
    {
        return this.versionData.getStringValue("install","target");
    }
    public File getLibraryPath(File root)
    {
        String path = this.versionData.getStringValue("install","path");
        String[] split = Iterables.toArray(Splitter.on(':').omitEmptyStrings().split(path), String.class);
        File dest = root;
        Iterable<String> subSplit = Splitter.on('.').omitEmptyStrings().split(split[0]);
        for (String part : subSplit)
        {
            dest = new File(dest, part);
        }
        dest = new File(new File(dest, split[1]), split[2]);
        String fileName = split[1]+"-"+split[2]+".jar";
        return new File(dest,fileName);
    }

    public boolean getStripMetaInf()
    {
        try
        {
            return this.versionData.getBooleanValue("install", "stripMeta");
        }
        catch (Exception e)
        {
            return false;
        }
    }

    public JsonNode getVersionInfo()
    {
        return this.versionData.getNode("versionInfo");
    }

    public File getMinecraftFile(File path)
    {
        return new File(new File(path, getMinecraftVersion()),getMinecraftVersion()+".jar");
    }
    public String getContainedJar()
    {
        return this.versionData.getStringValue("install","filePath");
    }
    public void extractFile(File path) throws IOException
    {
        this.doFileExtract(path);
    }

    private void doFileExtract(File path) throws IOException
    {
        if (Strings.isNullOrEmpty(getContainedJar())) return;
        InputStream inputStream = installerJar.getInputStream(installerJar.getJarEntry(getContainedJar()));
        OutputSupplier<FileOutputStream> outputSupplier = Files.newOutputStreamSupplier(path);
        ByteStreams.copy(inputStream, outputSupplier);
    }

    public String getMinecraftVersion()
    {
        return this.versionData.getStringValue("install","minecraft");
    }

    public String getMirrorListURL()
    {
        return this.versionData.getStringValue("install","mirrorList");
    }

    public boolean hasMirrors()
    {
        return this.versionData.isStringValue("install","mirrorList");
    }

    public boolean isInheritedJson()
    {
        return this.versionData.isStringValue("versionInfo", "inheritsFrom") &&
                this.versionData.isStringValue("versionInfo", "jar");
    }
}
