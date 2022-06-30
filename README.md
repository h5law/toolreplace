# ToolReplace

This is a simple plugin for a Spigot/Paper Minecraft server that will
automatically replace a tool when it breaks with one found in the players
inventory. It will use the most damaged tool first in the case there are
multiple replacements available.

Commands can be used to toggle debug messages and the auto replacement feature
itself:

```
/togglereplace <OPTION> <ARGUMENT>
/tr <OPTION> <ARGUMENT>
```

Options:
 - help (Shows usage)
 - debug 
   - true
   - false (Default) 
   - NO ARG will toggle
 - replace 
   - true (Default)
   - false 
   - NO ARG will toggle

## TODO

 - ~~Add commands to opt-in/opt-out of replacement feature~~
 - ~~Add commands to enable/disable debug messages~~
 - Add support to replace with same tool of different tiers

## Dependencies

 - Minecraft v1.19
 - Paper v1.19-40

## Bugs

I have only tested this locally on a PaperMC v1.19-40 server. If you find any
bugs please reach out to me and I'll be more than happy to fix them.

If there are any features you want to add reach out to me as well and I can
try to implement them.


## License

[BSD-3](https://github.com/h5law/toolreplace/blob/main/LICENSE.txt)
