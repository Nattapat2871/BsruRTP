package com.bsru;

import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ZoneManager {

    private final bsruRTP plugin;
    private final Map<String, Zone> zones = new HashMap<>();
    private FileConfiguration zonesConfig;
    private File zonesFile;

    public ZoneManager(bsruRTP plugin) {
        this.plugin = plugin;
        setup();
        loadZones();
    }

    public void setup() {
        zonesFile = new File(plugin.getDataFolder(), "zones.yml");
        if (!zonesFile.exists()) {
            try {
                zonesFile.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().severe("Could not create zones.yml!");
            }
        }
        zonesConfig = YamlConfiguration.loadConfiguration(zonesFile);
    }

    public void loadZones() {
        zones.clear();
        ConfigurationSection zonesSection = zonesConfig.getConfigurationSection("zones");
        if (zonesSection == null) return;

        for (String zoneName : zonesSection.getKeys(false)) {
            String world = zonesSection.getString(zoneName + ".world");
            int x = zonesSection.getInt(zoneName + ".x");
            int y = zonesSection.getInt(zoneName + ".y");
            int z = zonesSection.getInt(zoneName + ".z");
            int radius = zonesSection.getInt(zoneName + ".radius");
            String shape = zonesSection.getString(zoneName + ".shape", "SQUARE");
            String targetWorld = zonesSection.getString(zoneName + ".target-world");
            int countdown = zonesSection.getInt(zoneName + ".countdown", 30);
            String requiredBlock = zonesSection.getString(zoneName + ".required-block", null);

            Zone zone = new Zone(zoneName, world, x, y, z, radius, shape, targetWorld, countdown, requiredBlock);
            zones.put(zoneName.toLowerCase(), zone);
        }
        plugin.getLogger().info("Loaded " + zones.size() + " RTP zones.");
    }

    public void saveZones() {
        try {
            zonesConfig.save(zonesFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Could not save zones.yml!");
        }
    }

    public boolean createZone(String name, Location center, int radius, String shape, String targetWorld, int countdown, String requiredBlock) {
        if (zones.containsKey(name.toLowerCase())) {
            return false;
        }

        String path = "zones." + name;
        zonesConfig.set(path + ".world", center.getWorld().getName());
        zonesConfig.set(path + ".x", center.getBlockX());
        zonesConfig.set(path + ".y", center.getBlockY());
        zonesConfig.set(path + ".z", center.getBlockZ());
        zonesConfig.set(path + ".radius", radius);
        zonesConfig.set(path + ".shape", shape.toUpperCase());
        zonesConfig.set(path + ".target-world", targetWorld);
        zonesConfig.set(path + ".countdown", countdown);
        if (requiredBlock != null && !requiredBlock.isEmpty()) {
            zonesConfig.set(path + ".required-block", requiredBlock.toUpperCase());
        }

        saveZones();
        loadZones();
        return true;
    }

    public boolean removeZone(String name) {
        // ใช้ getZoneNames เพื่อให้ได้ชื่อที่ตรงตาม case
        String actualZoneName = getZoneNames().stream()
                .filter(zn -> zn.equalsIgnoreCase(name))
                .findFirst().orElse(null);

        if (actualZoneName == null) {
            return false;
        }
        zonesConfig.set("zones." + actualZoneName, null);
        saveZones();
        loadZones();
        return true;
    }

    // [เพิ่ม] เมธอดที่ขาดไปสำหรับดึงข้อมูลโซนเดียว
    public Zone getZone(String name) {
        return zones.get(name.toLowerCase());
    }

    public Collection<Zone> getAllZones() {
        return zones.values();
    }

    public Set<String> getZoneNames() {
        ConfigurationSection zonesSection = zonesConfig.getConfigurationSection("zones");
        if (zonesSection == null) {
            return Set.of();
        }
        return zonesSection.getKeys(false);
    }
}