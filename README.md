# 🔌 bsruRTP Plugin

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

A simple yet powerful Random Teleport (RTP) plugin for Spigot/Paper servers. It provides an easy-to-use GUI for players to randomly teleport to different configured worlds. The plugin is highly configurable, allowing server owners to customize almost every aspect of the teleportation experience.

---
## ✨ Features

- **GUI-Based Teleport:** An intuitive menu for players to select which world to teleport to.
- **Highly Configurable:** Customize GUI appearance, all messages, sounds, and items via `config.yml`.
- **Per-World Radius:** Set a different random teleportation radius for each world.
- **Teleport Cooldown:** Prevents players from spamming the RTP command.
- **Teleport Delay:** A configurable countdown requires players to stand still before teleporting, adding a strategic element.
- **Multi-World Support:** Works perfectly with vanilla worlds and worlds managed by **Multiverse-Core**.
- **PlaceholderAPI Support:** Use placeholders in item lore within the GUI (Optional).
- **Reload Command:** Instantly apply configuration changes with `/bsrurtp reload`.

---
## 🎮 Compatibility

- **Minecraft Version:** `1.19` - `1.21.7`
- **Server Software:** Spigot, Paper, and their forks.

---
## 🛠️ Installation

1.  Download the latest `.jar` file from the [Releases](https://github.com/YourUsername/YourRepo/releases) page.
2.  Place the downloaded `.jar` file into your server's `/plugins` directory.
3.  Start or restart your server.
4.  The plugin will generate a `bsruRTP` folder containing the `config.yml` file for you to customize.

---
## 🔗 Dependencies

- **PlaceholderAPI** (Optional): If installed, you can use PAPI placeholders in the lore of the GUI items. The plugin will function perfectly without it.

---
## 📋 Commands & Permissions

| Command           | Description                       | Permission Node        |
| :---------------- | :-------------------------------- | :--------------------- |
| `/rtp`            | Opens the Random Teleport GUI.    | `bsrurtp.use`          |
| `/bsrurtp reload` | Reloads the plugin's config file. | `bsrurtp.reload` (Admin) |

---
## ⚙️ Configuration (`config.yml`)

Below is an example of the default configuration file with explanations for each option.

```yaml
# GUI Settings
gui:
  title: "&aSelect a World to RTP"
  # Define the inventory slot for each world's icon. (0-26)
  slots:
    world: 11
    world_nether: 13
    world_the_end: 15
  # Define the appearance of each world's icon.
  icons:
    world:
      material: "GRASS_BLOCK"
      name: "&a&lRTP to &f&lOverworld"
      lore:
        - "&7Click to randomly teleport in the Overworld."
        - "&7Radius: 2000 blocks"
        - "&fPlayers Online: &a%server_online%"
    world_nether:
      material: "NETHERRACK"
      name: "&c&lRTP to &f&lThe Nether"
      lore:
        - "&7Click to randomly teleport in The Nether."
        - "&7Radius: 1000 blocks"
    world_the_end:
      material: "END_STONE"
      name: "&5&lRTP to &f&lThe End"
      lore:
        - "&7Click to randomly teleport in The End."
        - "&7Radius: 5000 blocks"

# A list of worlds where RTP is enabled. Must match the world folder names.
allowWorlds:
  - "world"
  - "world_nether"
  - "world_the_end"

# Teleportation radius from the world's spawn point for each world.
radius:
  world: 2000
  world_nether: 1000
  world_the_end: 5000

# Cooldown settings
cooldown:
  enabled: true
  seconds: 60
  message: "&cPlease wait {cooldown} more seconds before teleporting again!"

# All plugin messages
messages:
  reload_success: "&aConfig reloaded successfully!"
  world_not_allowed: "&cRTP is not allowed in this world."
  already_in_rtp: "&cYou are already waiting to teleport."
  rtp_start: "&eTeleporting in 5 seconds. Don't move from your current block!"
  countdown_actionbar: "&eTeleporting in {seconds}s..."
  rtp_cancel_move: "&cTeleport canceled because you moved."
  rtp_cancel_world: "&cTeleport canceled because you changed worlds."
  teleport_fail: "&cCould not find a safe location to teleport. Please try again."
  teleport_success: "&aTeleported successfully to {location}"

# Sound effects
sounds:
  gui_click: "UI_BUTTON_CLICK"
  waiting: "ENTITY_ENDERMAN_TELEPORT"
  teleport: "ENTITY_PLAYER_LEVELUP"
  fail: "BLOCK_NOTE_BLOCK_BASS"
  cancel_on_move: "ENTITY_VILLAGER_NO"