package sk.tomsik68.mclauncher.impl.servers;

import sk.tomsik68.mclauncher.api.servers.FoundServerInfo;
import sk.tomsik68.mclauncher.api.servers.IServerFinder;

import java.util.HashMap;

/**
 * Builder class for FoundServerInfo. May be used in server finders
 */
final class FoundServerInfoBuilder {
    private String motd, ip, icon;
    private int port;
    private IServerFinder finder;
    private final HashMap<String, Object> properties = new HashMap<>();

    public void ip(String s){
        ip = s;
    }

    public FoundServerInfoBuilder motd(String s){
        motd = s;
        return this;
    }

    public void finder(IServerFinder sf){
        finder = sf;
    }

    public FoundServerInfoBuilder port(int p){
        port = p;
        return this;
    }

    /**
     *
     * @param ico - Icon image encoded as base64 string
     * @return <code>this</code> for chaining
     */
    public FoundServerInfoBuilder icon(String ico){
        icon = ico;
        return this;
    }

    public void property(Object value){
        properties.put("recvString", value);
    }

    public FoundServerInfo build(){
        return new FoundServerInfo(finder, ip, icon, port, motd, properties);
    }


}
