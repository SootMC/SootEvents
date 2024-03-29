package net.sootmc.sootevents;

import dev.jamieisgeek.CommandRegisterer;
import dev.jamieisgeek.EventRegisterer;
import net.sootmc.sootevents.EventTools.EventToolsCommand;
import net.sootmc.sootevents.EventTools.EventToolsListener;
import net.sootmc.sootevents.Utils.BossbarUtils;
import net.sootmc.sootevents.Utils.ChatUtils;
import net.sootmc.sootevents.Utils.ScoreboardUtils;
import net.sootmc.sootevents.Utils.WaterRisingUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class SootEvents extends JavaPlugin {

    public static SootEvents instance;
    public static String PREFIX = ChatColor.GOLD + "Soot" + ChatColor.RED + "Events" + ChatColor.RESET + " | ";
    public static Map<UUID, ItemStack[]> storedInventories;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();

        whitelistCheck();

        new ScoreboardUtils();
        new ChatUtils();
        new WaterRisingUtils();
        new BossbarUtils();

        ScoreboardUtils.getScoreboardUtils().setScoreboardEnabled(this.getConfig().getBoolean("toggled"));
        ChatUtils.getChatUtils().setChatEnabled(this.getConfig().getBoolean("chat.toggled"));

        storedInventories = new HashMap<>();
        this.getCommand("eventtools").setExecutor(new EventToolsCommand());
        this.getServer().getPluginManager().registerEvents(new EventToolsListener(), this);
        try {
            new CommandRegisterer(this, getClass().getPackage().getName(), "Commands").registerCommands();
            new EventRegisterer(this, getClass().getPackage().getName(), "Listeners").registerEvents();
        } catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }

        getLogger().info("SootEvents has been enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("SootEvents has been disabled!");
    }

    private void whitelistCheck() {
        new BukkitRunnable() {
            @Override
            public void run() {
                if(getServer().hasWhitelist()) {
                    Bukkit.getOnlinePlayers().forEach(player -> {
                        if(!player.isWhitelisted()) {
                            player.kickPlayer("You are not whitelisted on this server.");
                        }
                    });
                }
            }
        }.runTaskTimer(this, 0, 20L);
    }
}
