![](https://imgur.com/kMXE3jo.png)

<p align="center">
  <img src="https://img.shields.io/badge/dynamic/json?color=success&label=DOWNLOADS&query=response.resource.downloads&url=https%3A%2F%2Fapi.polymart.org%2Fv1%2FgetResourceInfo%2F%3Fpretty_print_result%3D1%26resource_id%3D2551&style=for-the-badge">
  <img src="https://img.shields.io/bstats/servers/14788?color=success&style=for-the-badge">
  <img src="https://img.shields.io/bstats/players/14788?color=success&style=for-the-badge">
  <img src="https://img.shields.io/jitpack/v/github/cubecrafter/woolwars?color=success&style=for-the-badge">
</p>
<p align="center">
  <img src="https://img.shields.io/github/license/cubecrafter/woolwars?color=blue&style=for-the-badge">
  <img src="https://img.shields.io/github/issues/cubecrafter/woolwars?color=blue&style=for-the-badge">
  <img src="https://img.shields.io/codefactor/grade/github/CubeCrafter/WoolWars?color=blue&style=for-the-badge">
  <a href="https://discord.gg/ehjkwp5Fn4"><img src="https://img.shields.io/discord/821278914965405698?color=blue&label=DISCORD&style=for-the-badge"></a>
</p>
<br>

> Notice: Support will only be provided to **buyers**. Buy the plugin on Polymart: [Link](https://polymart.org/r/2551)

> Contributors: [@Ixf1nity](https://github.com/Ixf1nity), [@Teru](https://github.com/TeruHUB)

<br>

## Developer API

### Maven
```xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>
```
```xml
<dependency>
    <groupId>com.github.CubeCrafter</groupId>
    <artifactId>WoolWars</artifactId>
    <version>Tag</version>
    <scope>provided</scope>
</dependency>
```
### Gradle
```groovy
repositories {
    maven { url 'https://jitpack.io' }
}
```
```groovy
dependencies {
    compileOnly 'com.github.CubeCrafter:WoolWars:Tag'
}
```

## Events and Methods
```
GameEndEvent
GameStartEvent
GameStateChangeEvent
RoundEndEvent
RoundStartEvent
PlayerCollectPowerUpEvent
PlayerJoinArenaEvent
PlayerLeaveArenaEvent
PlayerKillEvent
PlayerSelectKitEvent
PlayerUseAbilityEvent
```
```
WoolWarsAPI#getLobbyLocation()
WoolWarsAPI#getArenas()
WoolWarsAPI#getArenaByPlayer(Player)
WoolWarsAPI#getArenaById(String)
WoolWarsAPI#getArenasByGroup(String)
WoolWarsAPI#getGroups()
WoolWarsAPI#getKitById(String)
WoolWarsAPI#getKits()
WoolWarsAPI#getPlayerData(Player)
WoolWarsAPI#isPlaying(Player)
WoolWarsAPI#joinRandomArena(Player)
WoolWarsAPI#joinRandomArena(Player, String)
```
