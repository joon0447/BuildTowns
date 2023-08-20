package org.joon1.protecthouse.Command;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.joon1.protecthouse.Main;
import org.joon1.protecthouse.ProtectMessage;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ProtectRemoveCommand implements CommandExecutor {
    private Main main;
    private ProtectMessage protectMessage;
    String prefix = protectMessage.prefix.getValue();
    public ProtectRemoveCommand(Main main){
        this.main = main;
    }

    static public HashMap<UUID, Boolean> removeMap = new HashMap<>();
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if(commandSender instanceof Player){
            Player player = (Player) commandSender;
            UUID uuid = player.getUniqueId();
            File rankList = new File(main.getDataFolder(), "playerRank.yml");
            YamlConfiguration rankListYml = YamlConfiguration.loadConfiguration(rankList);
            if(rankListYml.getString(uuid.toString()).equals("OWNER") && args.length==1 && removeMap.containsKey(player.getUniqueId())){
                UUID targetUUID = UUID.fromString(args[0]);
                Player target = Bukkit.getPlayer(targetUUID);
                File regionList = new File(main.getDataFolder(), "region/" + uuid + ".yml");
                YamlConfiguration regionListYml = YamlConfiguration.loadConfiguration(regionList);
                ConfigurationSection userSection = regionListYml.getConfigurationSection("Players");
                Map<String, String> map = new HashMap<>();
                for(String key : userSection.getKeys(false)){
                    if(!key.equals(args[0])){
                        String uuidKey= key;
                        String playername = userSection.getString(key);
                        map.put(uuidKey, playername);
                    }
                }
                map.put(player.getUniqueId().toString(), player.getName());
                regionListYml.set("Players", map);
                player.sendMessage(protectMessage.prefix.getValue() + ChatColor.WHITE + Bukkit.getPlayer(targetUUID).getName() +" 님을 마을에서 추방하였습니다!");
                if(target.isOnline()){
                    target.sendMessage(prefix + ChatColor.WHITE + "마을에서 추방되었습니다!");
                    if(ProtectChatCommand.chatList.contains(target.getUniqueId())){
                        ProtectChatCommand.chatList.remove(target.getUniqueId());
                    }
                }
                YamlConfiguration protectYml = YamlConfiguration.loadConfiguration(main.protectList);
                protectYml.set(target.getUniqueId().toString(), "none");
                try {
                    protectYml.save(main.protectList);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                try {
                    regionListYml.save(regionList);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                removeMap.remove(player.getUniqueId());
                File rankListFile = new File(main.getDataFolder(), "playerRank.yml");
                YamlConfiguration rankYml = YamlConfiguration.loadConfiguration(rankListFile);
                rankYml.set(targetUUID.toString(), "none");
                try {
                    rankYml.save(rankListFile);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }else if(rankListYml.getString(uuid.toString()).equals("NEWBIE")){
                rankListYml.set(player.getUniqueId().toString(), "none");
                try {
                    rankListYml.save(rankList);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                File protectList = new File(main.getDataFolder(), "protectList.yml");
                YamlConfiguration protectListYc = YamlConfiguration.loadConfiguration(protectList);
                String id = protectListYc.getString(player.getUniqueId().toString());
                protectListYc.set(player.getUniqueId().toString(), "none");
                try {
                    protectListYc.save(protectList);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                File regionList = new File(main.getDataFolder(), "region/" + id + ".yml");
                YamlConfiguration regionListYml = YamlConfiguration.loadConfiguration(regionList);
                ConfigurationSection userSection = regionListYml.getConfigurationSection("Players");
                Map<String, String> map = new HashMap<>();
                for(String key : userSection.getKeys(false)){
                    if(!key.equals(args[0])){
                        String uuidKey= key;
                        String playername = userSection.getString(key);
                        map.put(uuidKey, playername);
                    }
                }
                regionListYml.set("Players", map);
                try {
                    regionListYml.save(regionList);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                ProtectChatCommand.chatList.remove(player.getUniqueId());
                player.sendMessage(protectMessage.prefix.getValue() + ChatColor.WHITE + "마을을 떠났습니다.");
            }
            else{
                player.sendMessage(prefix + ChatColor.WHITE + "잘못된 접근입니다. (4001)");
            }
        }
        return false;
    }
}
