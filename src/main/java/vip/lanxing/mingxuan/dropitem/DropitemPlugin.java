package vip.lanxing.mingxuan.dropitem;

import org.bukkit.Bukkit;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import io.papermc.paper.threadedregions.scheduler.RegionScheduler;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public class DropitemPlugin extends JavaPlugin implements Listener {

    private BukkitTask bukkitTask;
    private boolean isFolia = false;
    private boolean showAmount = true;
    private String version;
    private final Set<UUID> processedItems = ConcurrentHashMap.newKeySet(); // 用于跟踪已处理的物品

    @Override
    public void onEnable() {
        detectFolia();
        loadVersion();
        initConfig();
        registerCommands();
        registerEvents();
        startDensityCheck();
        logLoadMessage();
    }

    @Override
    public void onDisable() {
        cancelTask();
        logUnloadMessage();
    }

    private void detectFolia() {
        try {
            Class.forName("io.papermc.paper.threadedregions.RegionizedServer");
            isFolia = true;
        } catch (ClassNotFoundException ignored) {}
    }

    private void loadVersion() {
        try (InputStream input = getClassLoader().getResourceAsStream("plugin.yml")) {
            Properties prop = new Properties();
            prop.load(input);
            version = prop.getProperty("version", "unknown");
        } catch (Exception e) {
            version = "unknown";
            getLogger().warning("无法加载 plugin.yml 文件，使用默认版本号");
        }
    }

    private void initConfig() {
        saveDefaultConfig();
        reloadConfiguration();
    }

    private void reloadConfiguration() {
        reloadConfig();
        showAmount = getConfig().getBoolean("show-amount", true);
    }

    private void registerCommands() {
        Optional.ofNullable(getCommand("dropitem")).ifPresentOrElse(
                command -> command.setExecutor((sender, command1, label, args) -> {
                    if (!sender.hasPermission("dropitem.reload")) {
                        sender.sendMessage(Component.text("你没有权限执行此命令！", TextColor.color(0xFF5555)));
                        return true;
                    }

                    reloadConfiguration();
                    sender.sendMessage(Component.text("配置已重载（当前数量显示: ")
                            .append(Component.text(showAmount ? "启用" : "禁用",
                                    showAmount ? TextColor.color(0x55FF55) : TextColor.color(0xFF5555)))
                            .append(Component.text("）")));
                    return true;
                }),
                () -> getLogger().warning("未在plugin.yml中找到dropitem命令，请检查配置！")
        );
    }

    private void registerEvents() {
        Bukkit.getPluginManager().registerEvents(this, this);
    }

    @EventHandler
    public void onItemSpawn(ItemSpawnEvent e) {
        updateNewItem(e.getEntity());
    }

    private void updateNewItem(Item item) {
        Component displayName = getDisplayName(item);
        applyNameWithAmount(item, displayName);
        processedItems.add(item.getUniqueId()); // 标记为新处理的物品
    }

    private Component getDisplayName(Item item) {
        return Optional.ofNullable(item.getItemStack().getItemMeta())
                .filter(ItemMeta::hasDisplayName)
                .map(ItemMeta::displayName)
                .orElseGet(() -> Component.text(
                        item.getItemStack().getType().getKey().getKey().replace('_', ' ')));
    }

    private void applyNameWithAmount(Item item, Component name) {
        Component finalName = showAmount ?
                Component.text()
                        .append(name)
                        .append(Component.text(" ×" + item.getItemStack().getAmount(), TextColor.color(0xAAAAAA)))
                        .build() :
                name;

        item.customName(finalName);
        item.setCustomNameVisible(true);
    }

    private void startDensityCheck() {
        Runnable checker = () -> Bukkit.getWorlds().stream().findFirst().ifPresent(world -> {
            Collection<Item> items = world.getEntitiesByClass(Item.class);
            items.parallelStream().forEach(item -> {
                if (!processedItems.contains(item.getUniqueId())) { // 仅处理未标记的物品
                    long density = calculateDensity(world, item);
                    boolean shouldShow = density <= getConfig().getInt("max-density", 6);
                    Bukkit.getScheduler().runTask(this, () -> item.setCustomNameVisible(shouldShow));
                    processedItems.add(item.getUniqueId()); // 标记为已处理
                }
            });
        });

        long interval = 20L * getConfig().getLong("check-interval", 3);

        if (isFolia) {
            RegionScheduler scheduler = Bukkit.getRegionScheduler();
            Consumer<ScheduledTask> task = t -> checker.run();
            Bukkit.getWorlds().stream().findFirst().ifPresent(world ->
                    scheduler.runAtFixedRate(this, world.getSpawnLocation(), task, interval, interval)
            );
        } else {
            bukkitTask = Bukkit.getScheduler().runTaskTimer(this, checker, interval, interval);
        }
    }

    private long calculateDensity(org.bukkit.World world, Item item) {
        return world.getNearbyEntities(
                        item.getLocation(),
                        getConfig().getDouble("check-radius", 2.5),
                        getConfig().getDouble("check-radius", 2.5),
                        getConfig().getDouble("check-radius", 2.5)
                ).stream()
                .filter(Item.class::isInstance)
                .filter(e -> e.getType() == item.getType())
                .count();
    }

    private void cancelTask() {
        if (bukkitTask != null) {
            bukkitTask.cancel();
        }
    }

    private void logLoadMessage() {
        getLogger().info("§a插件已加载 - 版本: " + version);
        getLogger().info("§a运行模式: " + (isFolia ? "Folia" : "Paper"));
    }

    private void logUnloadMessage() {
        getLogger().info("§c插件已卸载");
    }
}