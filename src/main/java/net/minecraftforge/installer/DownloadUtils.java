package net.minecraftforge.installer;

import argo.jdom.JsonNode;
import com.google.common.base.Charsets;
import com.google.common.collect.Lists;
import com.google.common.hash.Hashing;
import com.google.common.io.CharStreams;
import com.google.common.io.Files;
import com.google.common.io.InputSupplier;
import org.tukaani.xz.XZInputStream;
import tk.freetobuild.mcunified.Main;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import java.util.jar.JarOutputStream;
import java.util.jar.Pack200;

class DownloadUtils {
    private static final String LIBRARIES_URL = "https://libraries.minecraft.net/";

    private static final String PACK_NAME = ".pack.xz";

    static int downloadInstalledLibraries(File librariesDir, IMonitor monitor, List<JsonNode> libraries, int progress, List<Artifact> grabbed, List<Artifact> bad, MirrorData mirrors)
    {
        for (JsonNode library : libraries)
        {
            Artifact artifact = new Artifact(library.getStringValue("name"));
            List<String> checksums = null;
            if (library.isArrayNode("checksums"))
            {
                checksums = Lists.newArrayList(Lists.transform(library.getArrayNode("checksums"), JsonNode::getText));
            }
            if (library.isBooleanValue("clientreq") && library.getBooleanValue("clientreq"))
            {
                monitor.setNote(String.format("Considering library %s", artifact.getDescriptor()));
                File libPath = artifact.getLocalPath(librariesDir);
                String libURL = LIBRARIES_URL;
                if(mirrors.hasMirrors()&library.isStringValue("url")) {
                    libURL=mirrors.getMirrorURL();
                }
                if (library.isStringValue("url"))
                {
                    libURL = library.getStringValue("url") + "/";
                }
                if (libPath.exists() && checksumValid(libPath, checksums))
                {
                    monitor.setProgress(progress++);
                    continue;
                }

                if(!libPath.getParentFile().mkdirs())
                    Main.logger.severe("Unable to create directory" + libPath.getParentFile().getPath());
                monitor.setNote(String.format("Downloading library %s", artifact.getDescriptor()));
                libURL += artifact.getPath();

                File packFile = new File(libPath.getParentFile(), libPath.getName() + PACK_NAME);
                if (!downloadFile(packFile, libURL + PACK_NAME, null))
                {
                    if (library.isStringValue("url"))
                    {
                        monitor.setNote(String.format("Trying unpacked library %s", artifact.getDescriptor()));
                    }
                    if (!downloadFile(libPath, libURL, checksums))
                    {
                        if (!libURL.startsWith(LIBRARIES_URL))
                        {
                            bad.add(artifact);
                        }
                        else
                        {
                            monitor.setNote("Unmrriored file failed, Mojang launcher should download at next run, non fatal");
                        }
                    }
                    else
                    {
                        grabbed.add(artifact);
                    }
                }
                else
                {
                    try
                    {
                        monitor.setNote(String.format("Unpacking packed file %s", packFile.getName()));
                        unpackLibrary(libPath, Files.toByteArray(packFile));
                        monitor.setNote(String.format("Successfully unpacked packed file %s",packFile.getName()));
                        if(!packFile.delete())
                            Main.logger.severe("Unable to delete "+packFile.getPath());

                        if (checksumValid(libPath, checksums))
                        {
                            grabbed.add(artifact);
                        }
                        else
                        {
                            bad.add(artifact);
                        }
                    }
                    catch (OutOfMemoryError oom)
                    {
                        oom.printStackTrace();
                        bad.add(artifact);
                        artifact.setMemo();
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                        bad.add(artifact);
                    }
                }
            }
            else
            {
                monitor.setNote(String.format("Considering library %s: Not Downloading", artifact.getDescriptor()));
            }
            monitor.setProgress(progress++);
        }
        return progress;
    }

    private static boolean checksumValid(File libPath, List<String> checksums)
    {
        try
        {
            byte[] fileData = Files.toByteArray(libPath);
            boolean valid = checksums == null || checksums.isEmpty() || checksums.contains(Hashing.sha1().hashBytes(fileData).toString());
            if (!valid && libPath.getName().endsWith(".jar"))
            {
                valid = validateJar(libPath, fileData, checksums);
            }
            return valid;
        }
        catch (IOException e)
        {
            e.printStackTrace();
            return false;
        }
    }

    private static void unpackLibrary(File output, byte[] data) throws IOException
    {
        if (output.exists())
            if (!output.delete())
                Main.logger.severe("Unable to delete " + output.getPath());

        byte[] decompressed = DownloadUtils.readFully(new XZInputStream(new ByteArrayInputStream(data)));

        //Snag the checksum signature
        String end = new String(decompressed, decompressed.length - 4, 4);
        if (!end.equals("SIGN"))
        {
            System.out.println("Unpacking failed, signature missing " + end);
            return;
        }

        int x = decompressed.length;
        int len =
                ((decompressed[x - 8] & 0xFF)      ) |
                ((decompressed[x - 7] & 0xFF) << 8 ) |
                ((decompressed[x - 6] & 0xFF) << 16) |
                ((decompressed[x - 5] & 0xFF) << 24);

        File temp = File.createTempFile("art", ".pack");
        System.out.println("  Signed");
        System.out.println("  Checksum Length: " + len);
        System.out.println("  Total Length:    " + (decompressed.length - len - 8));
        System.out.println("  Temp File:       " + temp.getAbsolutePath());

        byte[] checksums = Arrays.copyOfRange(decompressed, decompressed.length - len - 8, decompressed.length - 8);

        //As Pack200 copies all the data from the input, this creates duplicate data in memory.
        //Which on some systems triggers a OutOfMemoryError, to counter this, we write the data
        //to a temporary file, force GC to run {I know, eww} and then unpack.
        //This is a tradeoff of disk IO for memory.
        //Should help mac users who have a lower standard max memory then the rest of the world (-.-)
        OutputStream out = new FileOutputStream(temp);
        out.write(decompressed, 0, decompressed.length - len - 8);
        out.close();
        System.gc();

        FileOutputStream jarBytes = new FileOutputStream(output);
        JarOutputStream jos = new JarOutputStream(jarBytes);

        Pack200.newUnpacker().unpack(temp, jos);

        JarEntry checksumsFile = new JarEntry("checksums.sha1");
        checksumsFile.setTime(0);
        jos.putNextEntry(checksumsFile);
        jos.write(checksums);
        jos.closeEntry();

        jos.close();
        jarBytes.close();
        if(!temp.delete())
            Main.logger.severe("Unable to delete "+temp.getPath());
    }

    private static boolean validateJar(File libPath, byte[] data, List<String> checksums) throws IOException
    {
        System.out.println("Checking \"" + libPath.getAbsolutePath() + "\" internal checksums");

        HashMap<String, String> files = new HashMap<>();
        String[] hashes = null;
        JarInputStream jar = new JarInputStream(new ByteArrayInputStream(data));
        JarEntry entry = jar.getNextJarEntry();
        while (entry != null)
        {
            byte[] eData = readFully(jar);

            if (entry.getName().equals("checksums.sha1"))
            {
                hashes = new String(eData, Charset.forName("UTF-8")).split("\n");
            }

            if (!entry.isDirectory())
            {
                files.put(entry.getName(), Hashing.sha1().hashBytes(eData).toString());
            }
            entry = jar.getNextJarEntry();
        }
        jar.close();

        if (hashes != null)
        {
            boolean failed = !checksums.contains(files.get("checksums.sha1"));
            if (failed)
            {
                System.out.println("    checksums.sha1 failed validation");
            }
            else
            {
                System.out.println("    checksums.sha1 validated successfully");
                for (String hash : hashes)
                {
                    if (hash.trim().equals("") || !hash.contains(" ")) continue;
                    String[] e = hash.split(" ");
                    String validChecksum = e[0];
                    String target = hash.substring(validChecksum.length() + 1);
                    String checksum = files.get(target);

                    if (!files.containsKey(target) || checksum == null)
                    {
                        System.out.println("    " + target + " : missing");
                        failed = true;
                    }
                    else if (!checksum.equals(validChecksum))
                    {
                        System.out.println("    " + target + " : failed (" + checksum + ", " + validChecksum + ")");
                        failed = true;
                    }
                }
            }

            if (!failed)
            {
                System.out.println("    Jar contents validated successfully");
            }

            return !failed;
        }
        else
        {
            System.out.println("    checksums.sha1 was not found, validation failed");
            return false; //Missing checksums
        }
    }

    static List<String> downloadList(String libURL)
    {
        try
        {
            URL url = new URL(libURL);
            URLConnection connection = url.openConnection();
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);
            InputSupplier<InputStream> urlSupplier = new URLISSupplier(connection);
            return CharStreams.readLines(CharStreams.newReaderSupplier(urlSupplier, Charsets.UTF_8));
        }
        catch (Exception e)
        {
            return Collections.emptyList();
        }

    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    private static boolean downloadFile(File libPath, String libURL, List<String> checksums)
    {
        try
        {
            URL url = new URL(libURL);
            URLConnection connection = url.openConnection();
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);
            InputSupplier<InputStream> urlSupplier = new URLISSupplier(connection);
            Files.copy(urlSupplier, libPath);
            return checksumValid(libPath, checksums);
        }
        catch (FileNotFoundException fnf)
        {
            if (!libURL.endsWith(PACK_NAME))
                fnf.printStackTrace();
            return false;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return false;
        }
    }

    private static byte[] readFully(InputStream stream) throws IOException
    {
        byte[] data = new byte[4096];
        ByteArrayOutputStream entryBuffer = new ByteArrayOutputStream();
        int len;
        do
        {
            len = stream.read(data);
            if (len > 0)
            {
                entryBuffer.write(data, 0, len);
            }
        } while (len != -1);

        return entryBuffer.toByteArray();
    }

    private static class URLISSupplier implements InputSupplier<InputStream>
    {
        private final URLConnection connection;

        private URLISSupplier(URLConnection connection)
        {
            this.connection = connection;
        }

        @Override
        public InputStream getInput() throws IOException
        {
            return connection.getInputStream();
        }
    }
}
