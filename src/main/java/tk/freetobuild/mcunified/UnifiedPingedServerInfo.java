package tk.freetobuild.mcunified;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import sk.tomsik68.mclauncher.api.servers.ServerInfo;
import sk.tomsik68.mclauncher.impl.servers.PingedServerInfo;

/**
 * Created by liz on 6/28/16.
 */
public class UnifiedPingedServerInfo extends PingedServerInfo {
    private final int onlinePlayers;
    private final int maxPlayers;
    private final String message;
    private final String version;
    public UnifiedPingedServerInfo(ServerInfo server, JSONObject obj) {
        super(server.getIP(),server.getName(),server.getIcon(),server.getPort());
        this.onlinePlayers = (int)((JSONObject)obj.get("players")).get("online");
        this.maxPlayers = (int)((JSONObject)obj.get("players")).get("max");
        this.message = chatToHtml((JSONObject)obj.get("description"));
        this.version = ((JSONObject)obj.get("version")).get("name").toString();
    }

    @Override
    public int getOnlinePlayers() {
        return onlinePlayers;
    }

    @Override
    public int getMaxPlayers() {
        return maxPlayers;
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public String getVersionId() {
        return version;
    }
    private String chatToHtml(JSONObject message) {
        String text = message.get("text").toString();
        if(message.containsKey("extra")) {
            JSONArray extras = (JSONArray) message.get("extra");
            for (Object extra1 : extras) {
                JSONObject extra = (JSONObject) extra1;
                String extraText = "<span style=\"";
                if ((boolean) extra.getOrDefault("bold", false)) {
                    extraText += "font-weight: bold; ";
                }
                if ((boolean) extra.getOrDefault("italic", false)) {
                    extraText += "font-style: italic; ";
                }
                if ((boolean) extra.getOrDefault("strikethrough", false)) {
                    extraText += "decoration: line-through; ";
                }
                String color = extra.getOrDefault("color", "white").toString();
                if (color.equalsIgnoreCase("white")) {
                    extraText += "color: #FFFFFF;";
                } else if (color.equalsIgnoreCase("yellow")) {
                    extraText += "color: #FFFF55;";
                } else if (color.equalsIgnoreCase("light_purple")) {
                    extraText += "color: #FF55FF;";
                } else if (color.equalsIgnoreCase("red")) {
                    extraText += "color: #FF5555;";
                } else if (color.equalsIgnoreCase("aqua")) {
                    extraText += "color: #55FFFF;";
                } else if (color.equalsIgnoreCase("green")) {
                    extraText += "color: #55FF55;";
                } else if (color.equalsIgnoreCase("blue")) {
                    extraText += "color: #5555FF;";
                } else if (color.equalsIgnoreCase("dark_gray")) {
                    extraText += "color: #555555;";
                } else if (color.equalsIgnoreCase("gray")) {
                    extraText += "color: #AAAAAA;";
                } else if (color.equalsIgnoreCase("gold")) {
                    extraText += "color: #FFAA00;";
                } else if (color.equalsIgnoreCase("dark_purple")) {
                    extraText += "color: #AA00AA;";
                } else if (color.equalsIgnoreCase("dark_red")) {
                    extraText += "color: #AA0000;";
                } else if (color.equalsIgnoreCase("dark_aqua")) {
                    extraText += "color: #00AAAA;";
                } else if (color.equalsIgnoreCase("dark_green")) {
                    extraText += "color: #00AA00;";
                } else if (color.equalsIgnoreCase("dark_blue")) {
                    extraText += "color: #0000AA;";
                } else if (color.equalsIgnoreCase("black")) {
                    extraText += "color: #000000;";
                }
                extraText += "\">";
                extraText += extra.get("text").toString().replace("\n", "<br>");
                extraText += "</span>";
                text += extraText;
            }
        }
        return text;
    }
}
