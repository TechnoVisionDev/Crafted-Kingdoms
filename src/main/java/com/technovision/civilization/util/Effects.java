package com.technovision.civilization.util;

import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.inventory.meta.FireworkMeta;

public class Effects {

    public static final FireworkEffect blueFirework = FireworkEffect.builder().flicker(false).trail(true).with(FireworkEffect.Type.BALL).withColor(Color.AQUA).withFade(Color.BLUE).build();
    public static final FireworkEffect greenFirework = FireworkEffect.builder().flicker(false).trail(true).with(FireworkEffect.Type.BALL).withColor(Color.LIME).withFade(Color.GREEN).build();
    public static final FireworkEffect redFirework = FireworkEffect.builder().flicker(false).trail(true).with(FireworkEffect.Type.BALL).withColor(Color.RED).withFade(Color.WHITE).build();

    public static void firework(FireworkEffect fe, Location loc) {
        Firework fw = (Firework) loc.getWorld().spawnEntity(loc, EntityType.FIREWORK);
        FireworkMeta fwm = fw.getFireworkMeta();
        fwm.addEffect(fe);
        fwm.setPower(1);
        fw.setFireworkMeta(fwm);
    }
}
