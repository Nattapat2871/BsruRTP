package com.bsru;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class ZonePlaceholders extends PlaceholderExpansion {

    private final bsruRTP plugin;

    public ZonePlaceholders(bsruRTP plugin) {
        this.plugin = plugin;
    }

    @Override
    public @NotNull String getIdentifier() {
        return "bsrurtp";
    }

    @Override
    public @NotNull String getAuthor() {
        return "YourNameHere";
    }

    @Override
    public @NotNull String getVersion() {
        return plugin.getDescription().getVersion();
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public String onPlaceholderRequest(Player player, @NotNull String params) {
        // [แก้ไข] ตรวจสอบ Placeholder รูปแบบใหม่
        if (params.startsWith("zone_secs_")) {
            // ดึงชื่อโซนออกมาจาก "zone_secs_<zonename>"
            String zoneName = params.substring("zone_secs_".length());

            // ดึงเวลาที่เหลือจาก Map โดยใช้ชื่อโซน
            Integer secondsLeft = plugin.getZoneCountdownSeconds().get(zoneName.toLowerCase());

            if (secondsLeft != null) {
                return String.valueOf(secondsLeft);
            } else {
                // ถ้าไม่มีโซนชื่อนั้น หรือยังไม่เริ่มนับ
                return "N/A";
            }
        }

        return null;
    }
}