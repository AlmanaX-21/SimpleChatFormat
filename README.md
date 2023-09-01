# SimpleChatFormat

This plugin does what it says in the name. It will allow you to format chat according to your own needs with a simple config file. The plugin works on a seperate thread from the main server one so it does not affect server performance at all. All data is also cached so as to save even more on performance.

# [Downloads](https://hangar.papermc.io/AlmanaX21/SimpleChatFormat)

# How to use

Simply, download and drag the plugin into your plugin's folder. Thereafter run the server once and a config file will generate.
## Dependancies

- [LuckPerms](https://luckperms.net): The plugin only supports groups with luckperms for now.

## How to format

For formatting this plugin uses **[MiniMessage](https://docs.advntr.dev/minimessage/index.html)**
To test out what your formatted message would look like, you can use this amazing tool provided by the developers of MiniMessage -> [MiniMessageViewer](https://webui.advntr.dev/)

**Sample config**
```
[
  {
    "luckPermsGroup": "default",
    "messageFormat": "<prefix> <displayname><suffix><gray>:<white><message>"
  }
]
```
*This is the default generated config*
*Config is a simplistic json file*

Just input the name of the group or even the uuid of the player you want to have the formatting done for into the `luckPermsGroup` field and how you want their chat to be formatted into the `messageFormat` field
**Do note**: You need to put all the tags in your format and if they are not set, they will be replaced by an empty field

# Have problems or want to suggest something?

If you feel like you want a feature in this plugin or there is a bug you want to report just make an issue or contact me on discord at `almanax21`
