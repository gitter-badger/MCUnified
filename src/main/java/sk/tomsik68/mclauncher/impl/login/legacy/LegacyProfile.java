package sk.tomsik68.mclauncher.impl.login.legacy;

import sk.tomsik68.mclauncher.api.login.IProfile;

public final class LegacyProfile implements IProfile {

    private final String pass;
    private final String name;

    public LegacyProfile(String username, String password) {
        name = username;
        pass = password;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getPassword() {
        return pass;
    }

    public boolean isRemember() {
        return pass.length() > 0;
    }

    @Override
    public String getSkinURL() {
        String SKINS_ROOT = "http://skins.minecraft.net/MinecraftSkins/";
        StringBuilder url = new StringBuilder(SKINS_ROOT);
        url = url.append(getName());
        url = url.append(".png");
        return url.toString();
    }

}
