package com.bsru;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.StringUtil;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class bsruRTP extends JavaPlugin implements Listener, TabExecutor {

    // Maps for standard /rtp
    private final Map<UUID, Long> rtpCooldown = new ConcurrentHashMap<>();
    private final Map<UUID, BukkitTask> countdownTasks = new ConcurrentHashMap<>();
    private final Map<UUID, Location> waitingPlayers = new ConcurrentHashMap<>();
    private String guiTitle;

    // ZoneRTP components
    private ZoneManager zoneManager;
    private final Map<String, BukkitTask> zoneCountdownTasks = new HashMap<>();
    private final Map<String, Integer> zoneCountdownSeconds = new ConcurrentHashMap<>();
    // [เพิ่ม] Map สำหรับติดตามว่าผู้เล่นคนไหนอยู่ในโซนไหน
    private final Map<UUID, String> playerCurrentZone = new ConcurrentHashMap<>();


    // Getters for PlaceholderAPI expansion
    public ZoneManager getZoneManager() { return zoneManager; }
    public Map<String, Integer> getZoneCountdownSeconds() { return zoneCountdownSeconds; }

    @Override
    public void onEnable() {
        saveDefaultConfig();
        this.zoneManager = new ZoneManager(this);

        reloadTitle();
        getServer().getPluginManager().registerEvents(this, this);

        Objects.requireNonNull(getCommand("rtp")).setExecutor(this);
        Objects.requireNonNull(getCommand("bsrurtp")).setExecutor(this);
        Objects.requireNonNull(getCommand("zonertp")).setExecutor(this);

        // Hook into PlaceholderAPI
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new ZonePlaceholders(this).register();
            getLogger().info("Successfully hooked into PlaceholderAPI!");
        }

        startAllZoneTimers();
    }

    @Override
    public void onDisable() {
        for (BukkitTask task : zoneCountdownTasks.values()) {
            task.cancel();
        }
        zoneCountdownTasks.clear();
        getLogger().info("All ZoneRTP tasks have been cancelled.");
    }

    private void startAllZoneTimers() {
        for (BukkitTask task : zoneCountdownTasks.values()) {
            task.cancel();
        }
        zoneCountdownTasks.clear();
        zoneCountdownSeconds.clear();

        for (Zone zone : zoneManager.getAllZones()) {
            startZoneCountdownLoop(zone);
        }
        getLogger().info("Started " + zoneCountdownTasks.size() + " looping ZoneRTP timers.");
    }

    private void reloadTitle() {
        guiTitle = color(getConfig().getString("gui.title", "&aเลือกโลกสุ่มวาร์ป"));
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player p)) {
            sender.sendMessage("This command can only be run by a player.");
            return true;
        }

        if (cmd.getName().equalsIgnoreCase("rtp")) {
            openRTPGUI(p);
            return true;
        }

        if (cmd.getName().equalsIgnoreCase("bsrurtp")) {
            if (args.length == 0) {
                p.sendMessage(color("&8&m----------------------------------"));
                p.sendMessage(color("&b&l         bsruRTP Plugin"));
                p.sendMessage(color(""));
                p.sendMessage(color("&e  Created by: &fNattapat2871"));
                p.sendMessage(color("&e  GitHub: &fgithub.com/Nattapat2871/BsruRTP"));
                p.sendMessage(color(""));
                p.sendMessage(color("&7  Use &a/bsrurtp help &7for a list of commands."));
                p.sendMessage(color("&8&m----------------------------------"));
                return true;
            }

            String subCommand = args[0].toLowerCase();
            switch (subCommand) {
                case "reload":
                    if (!p.hasPermission("bsrurtp.reload")) {
                        p.sendMessage(ChatColor.RED + "You don't have permission.");
                        return true;
                    }
                    reloadConfig();
                    zoneManager.loadZones();
                    reloadTitle();
                    startAllZoneTimers();
                    p.sendMessage(color(getConfig().getString("messages.reload_success", "&aConfig reloaded successfully!")));
                    break;

                case "help":
                    p.sendMessage(color("&8&m---------- &b&lBsruRTP Help &8&m----------"));
                    p.sendMessage(color("&e/rtp &7- Opens the Random Teleport GUI."));
                    p.sendMessage(color("&e/bsrurtp help &7- Shows this help message."));
                    p.sendMessage(color("&e/bsrurtp reload &7- Reloads the plugin's config."));
                    p.sendMessage(color("&e/bsrurtp status &7- Shows the plugin's status."));
                    p.sendMessage(color("&e/bsrurtp tpzone <zone> &7- Teleports you to a zone's center."));
                    p.sendMessage(color("&e/zonertp create ... &7- Creates a new RTP zone."));
                    p.sendMessage(color("&e/zonertp remove <zone> &7- Removes an RTP zone."));
                    p.sendMessage(color("&8&m------------------------------------"));
                    break;

                case "status":
                    if (!p.hasPermission("bsrurtp.status")) {
                        p.sendMessage(ChatColor.RED + "You don't have permission.");
                        return true;
                    }
                    boolean papiHook = Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null;
                    p.sendMessage(color("&8&m---------- &b&lBsruRTP Status &8&m---------"));
                    p.sendMessage(color("&eLoaded Zones: &f" + zoneManager.getAllZones().size()));
                    p.sendMessage(color("&eActive Timers: &f" + zoneCountdownTasks.size()));
                    p.sendMessage(color("&ePlaceholderAPI Hook: " + (papiHook ? "&aConnected" : "&cNot Found")));
                    p.sendMessage(color("&8&m------------------------------------"));
                    break;

                case "tpzone":
                    if (!p.hasPermission("bsrurtp.tpzone")) {
                        p.sendMessage(ChatColor.RED + "You don't have permission.");
                        return true;
                    }
                    if (args.length < 2) {
                        p.sendMessage(ChatColor.RED + "Usage: /bsrurtp tpzone <zone_name>");
                        return true;
                    }
                    Zone zoneToTp = zoneManager.getZone(args[1]);
                    if (zoneToTp == null) {
                        p.sendMessage(ChatColor.RED + "Zone '" + args[1] + "' not found.");
                        return true;
                    }
                    Location center = zoneToTp.getCenter();
                    if (center != null) {
                        p.teleport(center);
                        p.sendMessage(ChatColor.GREEN + "Teleported to the center of zone '" + args[1] + "'.");
                    } else {
                        p.sendMessage(ChatColor.RED + "Could not find the location for zone '" + args[1] + "'.");
                    }
                    break;

                default:
                    p.sendMessage(ChatColor.RED + "Unknown command. Use /bsrurtp help.");
                    break;
            }
            return true;
        }

        if (cmd.getName().equalsIgnoreCase("zonertp")) {
            if (!p.hasPermission("bsrurtp.admin.zone")) {
                p.sendMessage(ChatColor.RED + "You don't have permission to use this command.");
                return true;
            }

            if (args.length == 0) {
                p.sendMessage(ChatColor.GOLD + "Usage: /zonertp <create|remove>");
                return true;
            }

            if (args[0].equalsIgnoreCase("create")) {
                if (args.length < 4) {
                    p.sendMessage(ChatColor.RED + "Usage: /zonertp create <name> <radius> <target_world> [shape] [block]");
                    return true;
                }
                String name = args[1];
                int radius;
                try {
                    radius = Integer.parseInt(args[2]);
                } catch (NumberFormatException e) {
                    p.sendMessage(ChatColor.RED + "Radius must be a number.");
                    return true;
                }
                String targetWorld = args[3];
                String shape = (args.length > 4) ? args[4] : "SQUARE";
                String requiredBlock = (args.length > 5) ? args[5] : null;

                if (Bukkit.getWorld(targetWorld) == null) {
                    p.sendMessage(ChatColor.RED + "World '" + targetWorld + "' not found!");
                    return true;
                }

                if (requiredBlock != null && Material.getMaterial(requiredBlock.toUpperCase()) == null) {
                    p.sendMessage(ChatColor.RED + "Block '" + requiredBlock + "' not found!");
                    return true;
                }

                boolean success = zoneManager.createZone(name, p.getLocation(), radius, shape, targetWorld, 30, requiredBlock);
                if (success) {
                    p.sendMessage(ChatColor.GREEN + "Successfully created RTP zone '" + name + "'! Reloading timers...");
                    startAllZoneTimers();
                } else {
                    p.sendMessage(ChatColor.RED + "A zone with that name already exists.");
                }

            } else if (args[0].equalsIgnoreCase("remove")) {
                if (args.length < 2) {
                    p.sendMessage(ChatColor.RED + "Usage: /zonertp remove <name>");
                    return true;
                }
                String name = args[1];
                boolean success = zoneManager.removeZone(name);
                if (success) {
                    p.sendMessage(ChatColor.GREEN + "Successfully removed RTP zone '" + name + "'! Reloading timers...");
                    startAllZoneTimers();
                } else {
                    p.sendMessage(ChatColor.RED + "Zone '" + name + "' not found.");
                }
            } else {
                p.sendMessage(ChatColor.GOLD + "Usage: /zonertp <create|remove>");
            }
            return true;
        }
        return false;
    }

    private void openRTPGUI(Player p) {
        try {
            Inventory gui = Bukkit.createInventory(null, 27, guiTitle);
            ConfigurationSection slots = getConfig().getConfigurationSection("gui.slots");
            ConfigurationSection icons = getConfig().getConfigurationSection("gui.icons");
            if (slots == null || icons == null) {
                p.sendMessage(color("&c[bsruRTP] config.yml ผิดพลาด!"));
                return;
            }
            for (String worldId : slots.getKeys(false)) {
                int slot = slots.getInt(worldId, -1);
                if (slot < 0 || slot > 26) continue;
                if (!icons.contains(worldId)) continue;
                ConfigurationSection icon = icons.getConfigurationSection(worldId);
                String materialName = icon.getString("material", "GRASS_BLOCK");
                Material material = Material.getMaterial(materialName.toUpperCase(Locale.ROOT));
                if (material == null) material = Material.BEDROCK;

                ItemStack item = new ItemStack(material);
                ItemMeta meta = item.getItemMeta();
                String name = icon.getString("name", "&aสุ่มวาร์ป " + worldId);
                meta.setDisplayName(color(name));

                List<String> loreList = icon.getStringList("lore");
                List<String> lore = new ArrayList<>();
                for (String l : loreList) {
                    lore.add(applyPAPI(p, color(l)));
                }
                meta.setLore(lore);
                item.setItemMeta(meta);
                gui.setItem(slot, item);
            }
            p.openInventory(gui);
        } catch (Exception e) {
            getLogger().severe("An error occurred while opening the RTP GUI!");
            e.printStackTrace();
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (!(e.getWhoClicked() instanceof Player p)) return;
        if (e.getView().getTitle().equals(guiTitle)) {
            e.setCancelled(true);
            if (e.getRawSlot() < 0 || e.getRawSlot() > 26) return;

            playSound(p, getConfig().getString("sounds.gui_click", "UI_BUTTON_CLICK"));

            ConfigurationSection slots = getConfig().getConfigurationSection("gui.slots");
            if (slots == null) return;
            String selectedWorld = null;
            for (String key : slots.getKeys(false)) {
                if (slots.getInt(key, -1) == e.getRawSlot()) {
                    selectedWorld = key;
                    break;
                }
            }
            if (selectedWorld == null) return;

            p.closeInventory();
            rtpStart(p, selectedWorld);
        }
    }

    private void rtpStart(Player p, String worldId) {
        if (getConfig().getBoolean("cooldown.enabled", true)) {
            long cd = getConfig().getLong("cooldown.seconds", 60);
            long now = System.currentTimeMillis() / 1000L;
            long last = rtpCooldown.getOrDefault(p.getUniqueId(), 0L);
            if (now - last < cd) {
                long left = cd - (now - last);
                p.sendMessage(color(getConfig().getString("cooldown.message", "&cกรุณารออีก {cooldown} วินาทีก่อนสุ่มวาร์ปอีกครั้ง!").replace("{cooldown}", "" + left)));
                return;
            }
        }

        List<String> allowed = getConfig().getStringList("allowWorlds");
        if (!allowed.contains(worldId)) {
            p.sendMessage(color(getConfig().getString("messages.world_not_allowed")));
            return;
        }

        if (countdownTasks.containsKey(p.getUniqueId())) {
            p.sendMessage(color(getConfig().getString("messages.already_in_rtp")));
            return;
        }

        int waitSeconds = 5;
        Location startLoc = p.getLocation().getBlock().getLocation();
        waitingPlayers.put(p.getUniqueId(), startLoc);

        p.sendMessage(color(getConfig().getString("messages.rtp_start", "&eสุ่มวาร์ปในอีก {seconds} วินาที ห้ามออกจากบล็อคที่ยืนอยู่!").replace("{seconds}", "" + waitSeconds)));

        BukkitRunnable runnable = new BukkitRunnable() {
            int sec = waitSeconds;
            @Override
            public void run() {
                if (!p.isOnline() || !waitingPlayers.containsKey(p.getUniqueId())) {
                    this.cancel();
                    return;
                }
                if (sec <= 0) {
                    teleportRandom(p, worldId);
                    this.cancel();
                    return;
                }
                p.sendActionBar(color(getConfig().getString("messages.countdown_actionbar", "&eสุ่มวาร์ปในอีก {seconds} วิ").replace("{seconds}", "" + sec)));
                playSound(p, getConfig().getString("sounds.waiting", "ENTITY_ENDERMAN_TELEPORT"));
                sec--;
            }
        };
        BukkitTask task = runnable.runTaskTimer(this, 0L, 20L);
        countdownTasks.put(p.getUniqueId(), task);
    }

    // [แก้ไข] ยกเครื่อง onPlayerMove ใหม่ทั้งหมด
    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e) {
        Player p = e.getPlayer();
        Location to = e.getTo();
        Location from = e.getFrom();

        // --- ส่วนที่ 1: ตรวจสอบการยกเลิกของ /rtp ปกติ ---
        if (waitingPlayers.containsKey(p.getUniqueId())) {
            Location oldLoc = waitingPlayers.get(p.getUniqueId());
            if (oldLoc != null && (oldLoc.getBlockX() != to.getBlockX() || oldLoc.getBlockY() != to.getBlockY() || oldLoc.getBlockZ() != to.getBlockZ())) {
                playSound(p, getConfig().getString("sounds.cancel_on_move", "ENTITY_VILLAGER_NO"));
                cancelRTP(p, getConfig().getString("messages.rtp_cancel_move", "&cยกเลิกวาร์ปเพราะคุณออกจากบล็อคเดิม"));
            }
        }

        // --- ส่วนที่ 2: ตรวจสอบการเข้า/ออก ZoneRTP ---
        // เช็คเมื่อมีการขยับข้ามบล็อกเท่านั้น เพื่อประสิทธิภาพ
        if (from.getBlockX() == to.getBlockX() && from.getBlockY() == to.getBlockY() && from.getBlockZ() == to.getBlockZ()) {
            return;
        }

        String previousZoneName = playerCurrentZone.get(p.getUniqueId());
        String currentZoneName = null;

        // หาว่าตอนนี้ผู้เล่นอยู่ในโซนไหน
        for (Zone zone : zoneManager.getAllZones()) {
            if (isPlayerInZone(p, zone)) {
                currentZoneName = zone.getName();
                break;
            }
        }

        // เปรียบเทียบโซนเก่ากับโซนใหม่
        if (currentZoneName != null && !currentZoneName.equalsIgnoreCase(previousZoneName)) {
            // ผู้เล่นเดินเข้าโซนใหม่
            playerCurrentZone.put(p.getUniqueId(), currentZoneName);
            playSound(p, getConfig().getString("sounds.zone_enter", "BLOCK_BEACON_POWER_SELECT"));
        } else if (currentZoneName == null && previousZoneName != null) {
            // ผู้เล่นเดินออกจากโซนเดิม
            playerCurrentZone.remove(p.getUniqueId());
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        Player p = e.getPlayer();
        // ยกเลิก /rtp ปกติ
        cancelRTP(p, null);
        // [เพิ่ม] ลบผู้เล่นออกจาก Map ของโซนด้วย
        playerCurrentZone.remove(p.getUniqueId());
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent e) {
    }

    @EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent e) {
        if (waitingPlayers.containsKey(e.getPlayer().getUniqueId())) {
            if (e.getCause() != PlayerTeleportEvent.TeleportCause.PLUGIN) {
                cancelRTP(e.getPlayer(), getConfig().getString("messages.rtp_cancel_world", "&cยกเลิกวาร์ปเพราะคุณเปลี่ยนโลก"));
            }
        }
    }

    private void cancelRTP(Player p, String msg) {
        BukkitTask task = countdownTasks.remove(p.getUniqueId());
        if (task != null) {
            task.cancel();
        }
        waitingPlayers.remove(p.getUniqueId());

        if (msg != null && p.isOnline()) {
            p.sendMessage(color(msg));
        }
        playSound(p, getConfig().getString("sounds.fail", "BLOCK_NOTE_BLOCK_BASS"));
    }

    private void teleportRandom(Player p, String worldId) {
        countdownTasks.remove(p.getUniqueId());
        waitingPlayers.remove(p.getUniqueId());

        Location randomLoc = findRandomLocationForZone(worldId);

        if (randomLoc == null) {
            p.sendMessage(color(getConfig().getString("messages.teleport_fail")));
            playSound(p, getConfig().getString("sounds.fail", "BLOCK_NOTE_BLOCK_BASS"));
            return;
        }

        rtpCooldown.put(p.getUniqueId(), System.currentTimeMillis() / 1000L);
        p.teleport(randomLoc);
        playSound(p, getConfig().getString("sounds.teleport", "ENTITY_PLAYER_LEVELUP"));
        p.sendMessage(color(getConfig().getString("messages.teleport_success", "&aวาร์ปสำเร็จ! ไปยัง {location}")
                .replace("{location}", String.format("%.1f, %.1f, %.1f", randomLoc.getX(), randomLoc.getY(), randomLoc.getZ()))));
    }

    private void startZoneCountdownLoop(Zone zone) {
        BukkitRunnable runnable = new BukkitRunnable() {
            int timeRemaining = zone.getCountdown();

            @Override
            public void run() {
                zoneCountdownSeconds.put(zone.getName().toLowerCase(), timeRemaining);

                List<Player> playersInZone = Bukkit.getOnlinePlayers().stream()
                        .filter(player -> isPlayerInZone(player, zone))
                        .collect(Collectors.toList());

                if (!playersInZone.isEmpty()) {
                    String title = color(getConfig().getString("messages.zone_countdown_title", "&a&lRTP ZONE"));
                    String subtitle = color(getConfig().getString("messages.zone_countdown_subtitle", "&fวาร์ปในอีก &e{seconds} &fวินาที!").replace("{seconds}", String.valueOf(timeRemaining)));
                    String sound = getConfig().getString("sounds.zone_countdown_tick", "BLOCK_NOTE_BLOCK_HAT");

                    for (Player player : playersInZone) {
                        player.sendTitle(title, subtitle, 0, 25, 5);
                        playSound(player, sound);
                    }
                }

                if (timeRemaining <= 0) {
                    // เช็คผู้เล่นอีกครั้ง ณ วินาทีสุดท้าย
                    List<Player> playersToTeleport = Bukkit.getOnlinePlayers().stream()
                            .filter(player -> isPlayerInZone(player, zone))
                            .collect(Collectors.toList());

                    if (!playersToTeleport.isEmpty()) {
                        Location targetLocation = findRandomLocationForZone(zone.getTargetWorld());
                        if (targetLocation != null) {
                            String successMsg = color(getConfig().getString("messages.zone_teleport_success", "&bคุณถูกวาร์ปโดย RTP Zone!"));
                            String sound = getConfig().getString("sounds.zone_teleport", "ENTITY_ENDERMAN_TELEPORT");
                            for (Player player : playersToTeleport) {
                                player.teleport(targetLocation);
                                player.sendMessage(successMsg);
                                playSound(player, sound);
                            }
                        }
                    }
                    // รีเซ็ตเวลาเพื่อเริ่มนับใหม่
                    timeRemaining = zone.getCountdown();
                } else {
                    timeRemaining--;
                }
            }
        };
        BukkitTask task = runnable.runTaskTimer(this, 0L, 20L);
        zoneCountdownTasks.put(zone.getName().toLowerCase(), task);
    }

    public boolean isPlayerInZone(Player player, Zone zone) {
        if (!player.getWorld().getName().equals(zone.getWorldName())) {
            return false;
        }

        Location playerLoc = player.getLocation();
        Location zoneCenter = zone.getCenter();
        if (zoneCenter == null) return false;

        int zoneY = zoneCenter.getBlockY();
        int playerY = playerLoc.getBlockY();
        int checkHeight = getConfig().getInt("zone-vertical-check-height", 4);
        if (playerY < zoneY || playerY > zoneY + checkHeight) {
            return false;
        }

        if (zone.getRequiredBlock() != null) {
            Block blockStandingOn = player.getLocation().subtract(0, 1, 0).getBlock();
            if (blockStandingOn.getType() != zone.getRequiredBlock()) {
                return false;
            }
        }

        int radius = zone.getRadius();
        if (zone.getShape().equalsIgnoreCase("CIRCLE")) {
            return playerLoc.distanceSquared(zoneCenter) <= (double) (radius * radius);
        } else { // SQUARE
            int playerX = playerLoc.getBlockX();
            int playerZ = playerLoc.getBlockZ();
            return Math.abs(playerX - zoneCenter.getBlockX()) <= radius &&
                    Math.abs(playerZ - zoneCenter.getBlockZ()) <= radius;
        }
    }

    private Location findRandomLocationForZone(String worldId) {
        World world = Bukkit.getWorld(worldId);
        if (world == null) return null;

        int radius = getConfig().getInt("radius." + worldId, 2000);
        Location center = world.getSpawnLocation();
        Location randomLoc = null;

        for (int i = 0; i < 50; i++) {
            double angle = Math.random() * Math.PI * 2;
            double dist = Math.random() * radius;
            int x = (int) (center.getBlockX() + Math.cos(angle) * dist);
            int z = (int) (center.getBlockZ() + Math.sin(angle) * dist);

            if (world.getEnvironment() == World.Environment.NETHER) {
                for (int y = 120; y > 32; y--) {
                    Block floor = world.getBlockAt(x, y - 1, z);
                    Block feet = world.getBlockAt(x, y, z);
                    Block head = world.getBlockAt(x, y + 1, z);
                    if (floor.getType().isSolid() && floor.getType() != Material.LAVA && !feet.getType().isSolid() && !feet.isLiquid() && !head.getType().isSolid() && !head.isLiquid()) {
                        randomLoc = new Location(world, x + 0.5, y, z + 0.5);
                        break;
                    }
                }
            } else {
                int y = world.getHighestBlockYAt(x, z);
                Block floor = world.getBlockAt(x, y, z);
                if (!floor.getType().isSolid() || floor.isLiquid() || floor.getType() == Material.CACTUS) {
                    continue;
                }
                randomLoc = new Location(world, x + 0.5, y + 1, z + 0.5);
            }

            if (randomLoc != null) return randomLoc;
        }
        return null;
    }

    private void playSound(Player p, String sound) {
        if (sound == null || sound.isEmpty()) return;
        try {
            // Sound names in 1.20+ are lowercase and don't use the legacy names
            // This tries to normalize it, but using modern keys like "ui.button.click" is best
            String soundKey = sound.toLowerCase(Locale.ROOT);
            Sound s = Sound.valueOf(sound.toUpperCase(Locale.ROOT)); // Keep legacy support for now
            p.playSound(p.getLocation(), s, SoundCategory.MASTER, 1.0f, 1.0f);
        } catch (IllegalArgumentException e) {
            getLogger().warning("Invalid sound name in config: " + sound);
        }
    }

    private String color(String s) {
        if (s == null) return "";
        return s.replace("&", "§");
    }

    private String applyPAPI(Player p, String s) {
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            try {
                return PlaceholderAPI.setPlaceholders(p, s);
            } catch (Exception ignore) {}
        }
        return s;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        final List<String> suggestions = new ArrayList<>();

        if (command.getName().equalsIgnoreCase("bsrurtp")) {
            if (args.length == 1) {
                suggestions.add("reload");
                suggestions.add("help");
                suggestions.add("status");
                suggestions.add("tpzone");
            }
            else if (args.length == 2 && args[0].equalsIgnoreCase("tpzone")) {
                suggestions.addAll(zoneManager.getZoneNames());
            }
        } else if (command.getName().equalsIgnoreCase("zonertp")) {
            if (args.length == 1) {
                suggestions.add("create");
                suggestions.add("remove");
            }
            else if (args.length == 2 && args[0].equalsIgnoreCase("remove")) {
                suggestions.addAll(zoneManager.getZoneNames());
            }
            else if (args.length == 5 && args[0].equalsIgnoreCase("create")) {
                suggestions.add("SQUARE");
                suggestions.add("CIRCLE");
            }
            else if (args.length == 6 && args[0].equalsIgnoreCase("create")) {
                for (Material mat : Material.values()) {
                    if (mat.isBlock()) {
                        suggestions.add(mat.name());
                    }
                }
            }
        }

        final List<String> completions = new ArrayList<>();
        StringUtil.copyPartialMatches(args[args.length - 1], suggestions, completions);
        Collections.sort(completions);
        return completions;
    }
}