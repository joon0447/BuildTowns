package org.joon1.protecthouse;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.*;
import org.joon1.protecthouse.Command.ProtectChatCommand;

import java.io.File;
import java.util.*;

public class ProtectListener implements Listener {

    private Main main;
    private Cuboid cuboid;

    public static HashMap<UUID, UUID> checkPass = new HashMap<>();  // 자기 자신 / 마을 주인
    public ProtectListener(Main main) {
        this.main = main;
    }

    @EventHandler
    public void onMove(PlayerMoveEvent e){
        int bx = (int) Math.floor(e.getPlayer().getLocation().getX());
        int bz = (int) Math.floor(e.getPlayer().getLocation().getZ());
        UUID uuid = e.getPlayer().getUniqueId();
        for (UUID id : ProtectLocation.protectMap.keySet()) {
            int checkX = (int) Math.floor(ProtectLocation.protectMap.get(id).getX());
            int checkZ = (int) Math.floor(ProtectLocation.protectMap.get(id).getZ());
            if (bx >= checkX - 160 && bx <= checkX + 160 && bz >= checkZ - 160 && bz <= checkZ + 160) {
                checkPass.put(uuid, id);
                break;
            }
        }
        if(checkPass.containsKey(uuid)){
            int checkX = (int) Math.floor(ProtectLocation.protectMap.get(checkPass.get(uuid)).getX());
            int checkZ = (int) Math.floor(ProtectLocation.protectMap.get(checkPass.get(uuid)).getZ());
            if (!(bx >= checkX - 160 && bx <= checkX + 160 && bz >= checkZ - 160 && bz <= checkZ + 160)) {
                checkPass.remove(uuid);
            }
        }
    }

    @EventHandler
    public void onBreak(BlockBreakEvent e) {
        if (checkPass.containsKey(e.getPlayer().getUniqueId()) && (e.getPlayer().getWorld().getName().equals("world"))) {
            YamlConfiguration protectYml = YamlConfiguration.loadConfiguration(Main.protectList);

            if(!protectYml.contains(e.getPlayer().getUniqueId().toString()) || !protectYml.getString(e.getPlayer().getUniqueId().toString()).equals(checkPass.get(e.getPlayer().getUniqueId()).toString())){
                int bx = (int) Math.floor(e.getBlock().getX());
                int bz = (int) Math.floor(e.getBlock().getZ());
                UUID myUUID = e.getPlayer().getUniqueId();
                UUID ownerUUID = checkPass.get(myUUID);
                Location l = ProtectLocation.protectMap.get(ownerUUID);
                int lx = (int) Math.floor(l.getX());
                int lz = (int) Math.floor(l.getZ());
                File protectCheck = new File(main.getDataFolder(), "region/" + ownerUUID + ".yml");
                YamlConfiguration yc = YamlConfiguration.loadConfiguration(protectCheck);
                int level = yc.getInt("level");
                int protectBlock = 10;
                if (level == 2) {
                    protectBlock = 50;
                } else if (level == 3) {
                    protectBlock = 100;
                } else if (level == 4) {
                    protectBlock = 150;
                }
                if ((protectYml.getString(e.getPlayer().getUniqueId().toString()) == null && ((bx >= lx - protectBlock && bx <= lx + protectBlock) && (bz >= lz - protectBlock && bz <= lz + protectBlock))
                        || ((bx >= lx - protectBlock && bx <= lx + protectBlock) && (bz >= lz - protectBlock && bz <= lz + protectBlock)))) {
                    String owner = Bukkit.getPlayer(ownerUUID).getName();
                    Player player = e.getPlayer();
                    player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(owner + "의 마을"));
                    e.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent e) {
        if (checkPass.containsKey(e.getPlayer().getUniqueId()) && (e.getPlayer().getWorld().getName().equals("world"))) {
            YamlConfiguration protectYml = YamlConfiguration.loadConfiguration(Main.protectList);
            if(!protectYml.contains(e.getPlayer().getUniqueId().toString()) || !protectYml.getString(e.getPlayer().getUniqueId().toString()).equals(checkPass.get(e.getPlayer().getUniqueId()).toString())){
                int bx = (int) Math.floor(e.getBlock().getX());
                int bz = (int) Math.floor(e.getBlock().getZ());
                UUID myUUID = e.getPlayer().getUniqueId();
                UUID ownerUUID = checkPass.get(myUUID);
                Location l = ProtectLocation.protectMap.get(ownerUUID);
                int lx = (int) Math.floor(l.getX());
                int lz = (int) Math.floor(l.getZ());
                File protectCheck = new File(main.getDataFolder(), "region/" + ownerUUID + ".yml");
                YamlConfiguration yc = YamlConfiguration.loadConfiguration(protectCheck);
                int level = yc.getInt("level");
                int protectBlock = 10;
                if (level == 2) {
                    protectBlock = 50;
                } else if (level == 3) {
                    protectBlock = 100;
                } else if (level == 4) {
                    protectBlock = 150;
                }
                if ((protectYml.getString(e.getPlayer().getUniqueId().toString()) == null && ((bx >= lx - protectBlock && bx <= lx + protectBlock) && (bz >= lz - protectBlock && bz <= lz + protectBlock))
                        || ((bx >= lx - protectBlock && bx <= lx + protectBlock) && (bz >= lz - protectBlock && bz <= lz + protectBlock)))) {
                    String owner = Bukkit.getPlayer(ownerUUID).getName();
                    Player player = e.getPlayer();
                    player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(owner + "의 마을"));
                    e.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onRightClick(PlayerInteractEvent e) {
        if (checkPass.containsKey(e.getPlayer().getUniqueId()) && (e.getPlayer().getWorld().getName().equals("world"))) {
            YamlConfiguration protectYml = YamlConfiguration.loadConfiguration(Main.protectList);
            if(!protectYml.contains(e.getPlayer().getUniqueId().toString()) || !protectYml.getString(e.getPlayer().getUniqueId().toString()).equals(checkPass.get(e.getPlayer().getUniqueId()).toString())){
                if (e.getClickedBlock() != null && (e.getClickedBlock().getType().toString().contains("CHEST")|| e.getClickedBlock().getType() == Material.ANVIL
                        || e.getClickedBlock().getType()==Material.BARREL || e.getClickedBlock().getType().toString().contains("SHULKER_BOX")
                        || e.getClickedBlock().getType().toString().contains("FURNACE") || e.getClickedBlock().getType().toString().contains("DROPPER")
                        || e.getClickedBlock().getType().toString().contains("DISPENSER"))){
                    int bx = (int) Math.floor(e.getClickedBlock().getX());
                    int bz = (int) Math.floor(e.getClickedBlock().getZ());
                    UUID myUUID = e.getPlayer().getUniqueId();
                    UUID ownerUUID = checkPass.get(myUUID);
                    Location l = ProtectLocation.protectMap.get(ownerUUID);
                    int lx = (int) Math.floor(l.getX());
                    int lz = (int) Math.floor(l.getZ());
                    File protectCheck = new File(main.getDataFolder(), "region/" + ownerUUID + ".yml");
                    YamlConfiguration yc = YamlConfiguration.loadConfiguration(protectCheck);
                    int level = yc.getInt("level");
                    int protectBlock = 10;
                    if (level == 2) {
                        protectBlock = 50;
                    } else if (level == 3) {
                        protectBlock = 100;
                    } else if (level == 4) {
                        protectBlock = 150;
                    }
                    if ((protectYml.getString(e.getPlayer().getUniqueId().toString()) == null && ((bx >= lx - protectBlock && bx <= lx + protectBlock) && (bz >= lz - protectBlock && bz <= lz + protectBlock))
                            || ((bx >= lx - protectBlock && bx <= lx + protectBlock) && (bz >= lz - protectBlock && bz <= lz + protectBlock)
                    ))) {
                        String owner = Bukkit.getPlayer(ownerUUID).getName();
                        Player player = e.getPlayer();
                        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(owner + "의 마을"));
                        e.setCancelled(true);
                    }
                }
            }
        }
    }


    @EventHandler
    public void onChat(AsyncPlayerChatEvent e) {
        if (ProtectChatCommand.chatList.contains(e.getPlayer().getUniqueId())) {
            String msg = e.getMessage();
            Player player = e.getPlayer();
            e.setCancelled(true);
            YamlConfiguration protectYml = YamlConfiguration.loadConfiguration(Main.protectList);
            String myTown = protectYml.getString(e.getPlayer().getUniqueId().toString());
            for (String s : protectYml.getKeys(false)) {
                Player target = Bukkit.getPlayer(UUID.fromString(s));
                if (protectYml.getString(s).equals(myTown) && target.isOnline()) {
                    target.sendMessage(ProtectMessage.townPrefix.getValue() + e.getPlayer().getName() + " : " + ChatColor.GREEN + msg);
                }
            }
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        Player player = e.getPlayer();
        if (ProtectChatCommand.chatList.contains(player.getUniqueId())) {
            ProtectChatCommand.chatList.remove(player.getUniqueId());
        }
    }
}




