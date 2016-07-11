package net.minecraftforge.installer;

import java.io.File;

import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;

class Artifact
{
    private final String path;
    private final String descriptor;
    private String memo;

    public Artifact(String descriptor)
    {
        this.descriptor = descriptor;

        String[] pts = Iterables.toArray(Splitter.on(':').split(descriptor), String.class);
        String domain = pts[0];
        String name = pts[1];

        int last = pts.length - 1;
        int idx = pts[last].indexOf('@');
        String ext = "jar";
        if (idx != -1)
        {
            ext = pts[last].substring(idx + 1);
            pts[last] = pts[last].substring(0, idx);
        }

        String version = pts[2];
        String classifier = null;
        if (pts.length > 3)
        {
            classifier = pts[3];
        }

        String file = name + '-' + version;
        if (classifier != null) file += '-' + classifier;
        file += '.' + ext;

        path = domain.replace('.', '/') + '/' + name + '/' + version + '/' + file;
    }

    public File getLocalPath(File base)
    {
        return new File(base, path.replace('/', File.separatorChar));
    }

    public String getDescriptor(){ return descriptor; }
    public String getPath()      { return path;       }
    private String getMemo()      { return memo;       }
    public void setMemo(){ memo = "Out of Memory: Try restarting installer with JVM Argument: -Xmx1G";          }
    @Override
    public String toString()
    {
        if (getMemo() != null)
            return getDescriptor() + "\n    " + getMemo();
        return getDescriptor();
    }
}
