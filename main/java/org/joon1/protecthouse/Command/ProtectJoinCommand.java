package org.joon1.protecthouse.Command;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.checkerframework.checker.units.qual.A;
import org.joon1.protecthouse.Main;
import org.joon1.protecthouse.ProtectMessage;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class ProtectJoinCommand implements CommandExecutor {
    private Main main;

    String prefix = ProtectMessage.prefix.getValue();
    static public HashMap<UUID, Boolean> passMap = new HashMap<>();
    public ProtectJoinCommand(Main main){
        this.main = main;
    }
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if(commandSender instanceof Player){
            Player player = (Player) commandSender;
            if(passMap.containsKey(player.getUniqueId())){
                passMap.remove(player.getUniqueId());
                if(args.length ==1){  // 초대 수락
                    String tu = args[0];
                    Player owner = Bukkit.getPlayer(UUID.fromString(tu));
                    player.sendMessage(prefix + ChatColor.WHITE + "초대를 수락하셨습니다!");
                    owner.sendMessage(prefix + ChatColor.WHITE + player.getName() + " 님이 초대를 수락하셨습니다!");
                    YamlConfiguration protectYml = YamlConfiguration.loadConfiguration(main.protectList);
                    protectYml.set(player.getUniqueId().toString(), owner.getUniqueId().toString());
                    try {
                        protectYml.save(main.protectList);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    File rankList = new File(main.getDataFolder(), "playerRank.yml");
                    YamlConfiguration rankYml = YamlConfiguration.loadConfiguration(rankList);
                    rankYml.set(player.getUniqueId().toString(), "NEWBIE");
                    try {
                        rankYml.save(rankList);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    File town = new File(main.getDataFolder(), "region/" + tu + ".yml");
                    YamlConfiguration yc = YamlConfiguration.loadConfiguration(town);
                    ConfigurationSection section = yc.getConfigurationSection("Players");
                    Map<String, String> map = new HashMap<>();
                    for(String key : section.getKeys(false)){
                        String uuid = key;
                        String playername = section.getString(key);
                        map.put(uuid, playername);
                    }
                    map.put(player.getUniqueId().toString(), player.getName());
                    yc.set("Players", map);

                    try {
                        yc.save(town);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }

                }
                if(args.length ==2 && args[0].equals("no")) { // 초대 거절
                    UUID tu = UUID.fromString(args[1]);
                    Player owner = Bukkit.getPlayer(tu);
                    owner.sendMessage(player.getName() +" 님이 초대를 거절하셨습니다.");
                    player.sendMessage("초대를 거절하셨습니다.");
                }
            }else{
                player.sendMessage(prefix + ChatColor.WHITE +"잘못된 접근입니다. (4000)");
            }
        }

        return false;
    }
}
