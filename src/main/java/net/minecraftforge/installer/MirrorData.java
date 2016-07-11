package net.minecraftforge.installer;

import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Created by liz on 6/29/16.
 */
class MirrorData {
    private static class Mirror {
        final String name;
        final String imageURL;
        final String clickURL;
        final String url;

        public Mirror(String name, String imageURL, String clickURL, String url)
        {
            this.name = name;
            this.imageURL = imageURL;
            this.clickURL = clickURL;
            this.url = url;
        }
    }

    private final List<Mirror> mirrors;
    private int chosenMirror;
    private final VersionInfo version;
    public MirrorData(VersionInfo version)
    {
        this.version = version;
        if (version.hasMirrors())
        {
            mirrors = buildMirrorList();
            if (!mirrors.isEmpty())
            {
                chosenMirror = new Random().nextInt(getAllMirrors().size());
            }
        }
        else
        {
            mirrors = Collections.emptyList();
        }
    }

    private List<Mirror> buildMirrorList()
    {
        String url = version.getMirrorListURL();
        List<Mirror> results = Lists.newArrayList();
        List<String> mirrorList = DownloadUtils.downloadList(url);
        Splitter splitter = Splitter.on('!').trimResults();
        for (String mirror : mirrorList)
        {
            String[] strings = Iterables.toArray(splitter.split(mirror),String.class);
            Mirror m = new Mirror(strings[0],strings[1],strings[2],strings[3]);
            results.add(m);
        }
        return results;
    }

    public boolean hasMirrors()
    {
        return version.hasMirrors() && mirrors != null && !mirrors.isEmpty();
    }

    private List<Mirror> getAllMirrors()
    {
        return mirrors;
    }

    private Mirror getChosen()
    {
        return getAllMirrors().get(chosenMirror);
    }

    public String getMirrorURL()
    {
        return getChosen().url;
    }
}
