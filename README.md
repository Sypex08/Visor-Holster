# Visor addon template (multi-version: Fabric + Forge + NeoForge)
It is a Visor addon template to start from, built for many Minecraft versions in a single branch

## Start From this template

1. Change the values in `Mod` block of `stonecutter.properties.toml`
2. Rename the `your.mod.template` package everywhere. Don't forget to update the entrypoint in
   `mod-fabric/src/main/resources/fabric.mod.json` and the package in `mod.mixins.json`
3. Work on one version at a time: `gradlew "Set active project to 26.3"`, back to the committed one with
   `gradlew "Reset active project"`

`gradlew build-1.21.1` builds every loader of a version,
`gradlew buildAll` every version, both into `build/libs`.

Once you finished with that, delete or rewrite README.md
