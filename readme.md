# MCUnified
###### Unifying Minecraft launchers everywhere.

## Features
- [X] Multiple instances
- [X] Multiple accounts
- [X] Jar mods
- [X] Forge Mods
- [X] Install Forge
- [ ] World Management
- [ ] One-Click mod installing
- [ ] Mod pack importing
- [ ] Mod pack json installing

## One-Click mod installing
MCUnified is able to install mods simply with a click of a button by using curseforge.com. It connects to curseforge to retrieve a list of mods which is then parsed from html to java objects which then can be used to download mods and create modpacks.

## Mod Pack format
MCUnified uses json to set up Custom Modpacks which it reads and installs the mods from curse
````json
{
    "name": "Name of modpack",
    "forge": 1321,
    "mods": ["modname:12473","secondmod:1351"],
    "config": [ 
    {
        "file": "modname.txt",
        "text": "some kind of config data\ngoes here"
    }
    ]
}
````
|key|type|value|
|---|----|-----|
|name|String|The name of the modpack|
|forge|Integer|The build number of forge to install|
|mods|String array|An array of identifiers for a mod's file, this is obtained through the launcher|
|config|Object array|An array of objects that hold the file name to write and the text to write to the file|

## Building And Running
````
./gradlew build
./gradlew run
````
