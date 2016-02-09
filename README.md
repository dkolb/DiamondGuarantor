Diamond Guarantor
=================

Still under development, several changes coming soon.

Diamond Guarantor is a clean room reimplementation of the Bukkit plugin Diamond Guarantee's central feature,
injecting Diamond Ore around players who have mined sufficient stone blocks.

In simple terms each player has a DiamondScore.  When they mine a stone block (or any of it's variants) at a depth
between the `maxY` and `minY`, inclusive, they gain `stoneValue` points.  When the player breaks a stone block
that puts their score over the `diamondValue` setting, the plugin substitutes a diamond ore block on a neighboring stone
block that has just been exposed.  It only substitutes normal stone, none of the variants, preventing diamond ore from
showing up in veins of Granite, Diorisite, or Andisite.  If there are no blocks available, the plugin waits for another
stone block to be broken before trying again.

Unless the player is using an XRay mod, they should not see this.

If the player places blocks at diamond mining depth, they will lose points from their score.  As such, be careful
about setting your Y value.  Players that build at that depth will likely have their diamond scores artificially low.

Commands
========
`/diamondgaurantor setScore <player> <score>`
`/dg setScore <player> <score>`
Sets a player's score.  Only works if the player is online.

Permissions
`diamondguarantor.command.setdiamondscore.use`

`/diamondgaurantor getScore <player> <score>`
`/dg getScore <player> <score>`
Retrieves a player's score.  Only works if the player is online.

Permissions
`diamondguarantor.command.getdiamondscore.use`


Caveats, To-dos, Misc
=====================

Currently the plugin only uses a JSON file as it's back end and holds the entire file in memory.  However, alternative
data stores are completely possible and I will rework this to be more efficient.  Likely, I will add methods to the
DiamondScoreService to allow for loading and unloading individual players and breaking player data into discreet files.

As it stands, all players are loaded at start up and the entire map of scores is flushed to disk every time the world
is saved.

For a database, this would also allow for aggressive caching and not having to query the database every single time
a player breaks/places a brick or is rewarded diamond ore.

Settings
========
```hocon
# Value at which player is awarded a diamond block.
diamondValue=600
# Max depth to count mined stone at.
maxY=16
# Min depth to count mined stone at.
minY=0
# Value every mined stone block adds to player's diamond score.
stoneValue=1
```