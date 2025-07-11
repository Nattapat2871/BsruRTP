# 🔌 bsruRTP Plugin

<div align="center">
  
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![GitHub Repo stars](https://img.shields.io/github/stars/Nattapat2871/BsruRTP?style=flat-square)](https://github.com/Nattapat2871/BsruRTP/stargazers)
![Visitor Badge](https://api.visitorbadge.io/api/VisitorHit?user=Nattapat2871&repo=BsruRTP&countColor=%237B1E7A&style=flat-square)

</div>

<p align= "center">
        <b>English</b>　<a href="/README_TH.md">ภาษาไทย</a>
        

A powerful and versatile Random Teleport (RTP) plugin for Spigot/Paper servers. This plugin offers two distinct RTP modes: a classic GUI-based teleport for individual players and a unique, event-driven ZoneRTP system for group teleports. It is highly configurable and designed for modern Minecraft servers.

---
## ✨ Features

### Standard RTP (/rtp)
- **GUI-Based Teleport:** An intuitive menu for players to select which world to teleport to.
- **Stand-Still Countdown:** A short countdown requires players to stand still before teleporting.
- **Cooldown System:** Prevents players from spamming the RTP command.
- **Safe Location Finding:** Advanced algorithm to find safe teleport spots, including checks to avoid the Nether roof and lava oceans.

### ZoneRTP
- **Persistent Timers:** Create RTP zones with looping countdowns that run continuously, creating server-wide events.
- **Group Teleport:** All players inside a zone when the timer hits zero are teleported to the same random location.
- **Zone Requirements:** Configure zones to require players to stand on a specific block type and within a specific vertical height.
- **Global Visibility:** Use PlaceholderAPI to display zone countdowns anywhere on the server.

### General Features
- **Highly Configurable:** Customize GUI, all messages, sounds, radiuses, and timings.
- **Full Admin Control:** A robust set of commands to create, remove, and manage all plugin features.
- **Multi-World Support:** Works perfectly with vanilla worlds and worlds managed by **Multiverse-Core**.
- **PlaceholderAPI Support:** Deep integration for displaying dynamic information.

---
## 🎮 Compatibility

- **Minecraft Version:** `1.17` - `1.21+`
- **Server Software:** Spigot, Paper, and their forks.

---
## 🛠️ Installation

1.  Download the latest `.jar` file from the [Modrinth](https://modrinth.com/plugin/bsrurtp) or [Releases](https://github.com/Nattapat2871/BsruRTP/releases) page.
2.  Place the downloaded `.jar` file into your server's `/plugins` directory.
3.  (Optional) Install [PlaceholderAPI](https://www.spigotmc.org/resources/placeholderapi.624/) for placeholder support.
4.  Start or restart your server.
5.  Configure the generated `config.yml` and `zones.yml` files to your liking.

---
## 📋 Commands & Permissions

| Command | Description | Permission Node | Default |
| :--- | :--- | :--- | :--- |
| `/rtp` | Opens the Random Teleport GUI. | `bsrurtp.use` | `true` |
| `/bsrurtp` | Shows plugin information. | `bsrurtp.admin` | `op` |
| `/bsrurtp help` | Shows all plugin commands. | `bsrurtp.admin` | `op` |
| `/bsrurtp reload` | Reloads all plugin configs. | `bsrurtp.reload` | `op` |
| `/bsrurtp status` | Shows the plugin's current status. | `bsrurtp.status` | `op` |
| `/bsrurtp tpzone <zone>` | Teleports you to a zone's center. | `bsrurtp.tpzone` | `op` |
| `/zonertp create ...` | Creates a new RTP zone. | `bsrurtp.admin.zone` | `op` |
| `/zonertp remove <zone>` | Removes an RTP zone. | `bsrurtp.admin.zone` | `op` |

---
## 🔌 Placeholders (PlaceholderAPI)

- `%bsrurtp_zone_secs_<zonename>%`
  - Displays the remaining seconds for a specific looping RTP Zone.
  - **Example:** `%bsrurtp_zone_secs_spawn-event%` will show the countdown for the "spawn-event" zone.
  - Shows "N/A" if the zone does not exist or has not started its timer.

---
## ⚙️ Configuration

The plugin uses two main configuration files:

### `config.yml`
This file controls the standard `/rtp` command, global messages, sounds, and ZoneRTP behavior.

```yaml
# Height for the vertical check in ZoneRTP (from the zone's center Y).
# For example, if set to 4 and the zone's Y is 65, it will check for players between Y 65 and 69.
zone-vertical-check-height: 4

# Worlds where /rtp is allowed.
allowWorlds:
  - world
  - world_nether
  - world_the_end

# Teleportation radius from the world's spawn point for each world.
radius:
  world: 2000
  world_nether: 2000
  world_the_end: 2000

# Cooldown settings for the /rtp command.
cooldown:
  enabled: true
  seconds: 5
  message: "&cPlease wait {cooldown} more seconds before teleporting again!"

# Settings for the /rtp GUI.
gui:
  title: "&aSelect a World to RTP"
  slots:
    world: 11
    world_nether: 13
    world_the_end: 15
  icons:
    world:
      material: GRASS_BLOCK
      name: "&aRTP to Overworld"
      lore:
        - "&7Online: %server_online%"
        - "&7Click to randomly teleport."
    world_nether:
      material: NETHERRACK
      name: "&cRTP to Nether"
      lore:
        - "&7Click to randomly teleport."
    world_the_end:
      material: END_STONE
      name: "&dRTP to The End"
      lore:
        - "&7Click to randomly teleport."

# Sound effects for various actions.
sounds:
  gui_click: "ui.button.click"
  waiting: "entity.enderman.teleport"
  teleport: "entity.player.levelup"
  fail: "block.note_block.bass"
  cancel_on_move: "ENTITY_VILLAGER_NO"
  zone_countdown_tick: "BLOCK_NOTE_BLOCK_HAT"
  zone_teleport: "ENTITY_ENDERMAN_TELEPORT"

# All plugin messages.
messages:
  no_permission: "&cYou don't have permission to use this command."
  reload_success: "&aConfig reloaded successfully!"
  reload_failed: "&cFailed to reload config!"
  world_not_allowed: "&cRTP is not allowed in this world."
  already_in_rtp: "&cYou are already waiting to teleport."
  world_not_found: "&cCould not find this world."
  teleport_fail: "&cCould not find a safe location. Please try again."
  teleport_success: "&aTeleported successfully to {location}"
  rtp_start: "&eTeleporting in {seconds} seconds. Do not move!"
  countdown_actionbar: "&eTeleporting in {seconds}s..."
  rtp_cancel_move: "&cTeleport canceled because you moved."
  rtp_cancel_world: "&cTeleport canceled because you changed worlds."
  # Messages for ZoneRTP
  zone_countdown_title: "&a&lRTP ZONE"
  zone_countdown_subtitle: "&fTeleporting in &e{seconds} &fseconds!"
  zone_teleport_success: "&bYou have been teleported by an RTP Zone!"
