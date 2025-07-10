package com.bsru;

import me.clip.placeholderapi.PlaceholderAPI; // [เพิ่ม] แก้ปัญหา Cannot resolve symbol 'me'
import org.bukkit.*; // [เพิ่ม] Import คลาสหลักๆทั้งหมด
import org.bukkit.block.Block; // [เพิ่ม] แก้ปัญหา Cannot resolve symbol 'Block'
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

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class bsruRTP extends JavaPlugin implements Listener, TabExecutor {

    private final Map<UUID, Long> rtpCooldown = new ConcurrentHashMap<>();
    private final Map<UUID, BukkitTask> countdownTasks = new ConcurrentHashMap<>();
    private final Map<UUID, Location> waitingPlayers = new ConcurrentHashMap<>();
    private String guiTitle;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        reloadTitle();
        getServer().getPluginManager().registerEvents(this, this);

        Objects.requireNonNull(getCommand("rtp")).setExecutor(this);
        Objects.requireNonNull(getCommand("bsrurtp")).setExecutor(this);
    }

    private void reloadTitle() {
        guiTitle = color(getConfig().getString("gui.title", "&aเลือกโลกสุ่มวาร์ป"));
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player p)) return false;

        if (cmd.getName().equalsIgnoreCase("rtp")) {
            openRTPGUI(p);
            return true;
        }

        if (cmd.getName().equalsIgnoreCase("bsrurtp")) {
            if (args.length > 0 && args[0].equalsIgnoreCase("reload")) {
                reloadConfig();
                reloadTitle();
                p.sendMessage(color(getConfig().getString("messages.reload_success", "&aรีโหลด config สำเร็จ!")));
            } else {
                p.sendMessage(color("&8&m----------------------------------"));
                p.sendMessage(color("&b&l         bsruRTP Plugin"));
                p.sendMessage(color(""));
                p.sendMessage(color("&e  สร้างโดย: &fNattapat2871"));
                p.sendMessage(color("&e  ความสามารถ: &fปลั๊กอินสุ่มวาร์ปผ่าน GUI"));
                p.sendMessage(color("&e  GitHub: &fhttps://github.com/Nattapat2871/BsruRTP"));
                p.sendMessage(color(""));
                p.sendMessage(color("&7  ใช้ &a/bsrurtp reload &7เพื่อรีโหลดปลั๊กอิน"));
                p.sendMessage(color("&8&m----------------------------------"));
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
            p.sendMessage(color("&c[bsruRTP] config.yml ผิดพลาด (GUI)!"));
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

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e) {
        Player p = e.getPlayer();
        if (!waitingPlayers.containsKey(p.getUniqueId())) return;
        Location oldLoc = waitingPlayers.get(p.getUniqueId());
        Location newLoc = p.getLocation();

        if (oldLoc != null && (
                oldLoc.getBlockX() != newLoc.getBlockX() ||
                        oldLoc.getBlockY() != newLoc.getBlockY() ||
                        oldLoc.getBlockZ() != newLoc.getBlockZ())) {
            playSound(p, getConfig().getString("sounds.cancel_on_move", "ENTITY_VILLAGER_NO"));
            cancelRTP(p, getConfig().getString("messages.rtp_cancel_move", "&cยกเลิกวาร์ปเพราะคุณออกจากบล็อคเดิม"));
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        cancelRTP(e.getPlayer(), null);
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

        World world = Bukkit.getWorld(worldId);
        if (world == null) {
            p.sendMessage(color("&c[Error] ไม่พบโลก '" + worldId + "' !"));
            return;
        }

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

                    if (floor.getType().isSolid() && floor.getType() != Material.LAVA &&
                            !feet.getType().isSolid() && !feet.isLiquid() &&
                            !head.getType().isSolid() && !head.isLiquid())
                    {
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

            if (randomLoc != null) {
                break;
            }
        }

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

    private void playSound(Player p, String sound) {
        if (sound == null || sound.isEmpty()) return;
        try {
            String soundKey = sound.toUpperCase(Locale.ROOT).replace("MINECRAFT:", "");
            Sound s = Sound.valueOf(soundKey);
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
        if (command.getName().equalsIgnoreCase("bsrurtp")) {
            if (args.length == 1) return Collections.singletonList("reload");
        }
        return Collections.emptyList();
    }
}