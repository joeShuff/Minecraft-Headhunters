# Headhunters Minecraft Plugin

## Description
The **Headhunters** plugin adds a thrilling new challenge to your Minecraft server. Players or teams must collect the heads of every mob in the game, with optional support for mob variants like sheep colors or rabbit types. Compete with others to complete your collection, track your progress, and showcase your hunting prowess with custom skull textures and lore!

## How to Install
1. Navigate to the [Releases](https://github.com/joeShuff/Minecraft-Headhunters/releases) page on this repository.
2. Download the latest `.jar` file for the plugin.
3. Place the downloaded `.jar` file into your server's `plugins` folder.
4. Restart or reload your server to activate the plugin.

## How to Get Started
1. Create a team using `/createteam <team_name>` (team names can include spaces).
2. Invite others to your team if desired.
3. Start hunting mobs! The plugin will automatically track your progress.

## Commands
Here’s a list of commands available in the Headhunters plugin:

### Team Management
- **`/createteam <team_name>`**: Create a new team.
- **`/teamname <new_team_name>`**: Rename your team.
- **`/invite <player_name>`**: Invite a player to your team.
- **`/leave`**: Leave your current team.
- **`/join <team ID> <player>`**: (Admin) Add a player to a given team ID.
- **`/teams`**: (Admin) view the list of teams and their IDs

### Shrine Management
- **`/setshrinespawn`**: Set your team’s shrine location. Requires confirmation if a shrine is already set.
- **`/setshrinespawn confirm`**: Confirm the new shrine location.

### Progress Tracking
- **`/progress`**: View your team’s collection progress.
- **`/missing`**: List the mobs your team still needs to collect.
- **`/globalprogress`**: See other teams' overall progress (does not show specific heads).

### Skull Management
- **`/resummon <entity_type>`**: Resummon a skull for your team, if it has already been earned.
- **`/earn <entity_type> [team_name]`**: (Admin) Mark a skull as earned for a team.

## Thanks
Special thanks to:
- The **Vanilla Tweaks** team for providing some of the skull textures used in this plugin from their *More Mob Heads* datapack.
- **ChatGPT** for assisting with the development of this plugin and writing much of the base code.

Enjoy the hunt!
