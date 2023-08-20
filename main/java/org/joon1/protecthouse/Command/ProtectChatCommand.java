package org.joon1.protecthouse.Command;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.joon1.protecthouse.Main;
import org.joon1.protecthouse.ProtectMessage;

import java.util.ArrayList;
import java.util.UUID;

public class ProtectChatCommand implements CommandExecutor {

    private Main main;

    public static ArrayList<UUID> chatList = new ArrayList<>();
    public ProtectChatCommand(Main main){
        this.main = main;
    }
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if(commandSender instanceof Player){
            Player player = (Player) commandSender;
            YamlConfiguration protectYml = YamlConfiguration.loadConfiguration(Main.protectList);
            if(protectYml.contains(player.getUniqueId().toString()) && !protectYml.getString(player.getUniqueId().toString()).equals("none")){
                if(!chatList.contains(player.getUniqueId())){
                    chatList.add(player.getUniqueId());
                    player.sendMessage(ProtectMessage.prefix.getValue() + ChatColor.WHITE + "마을 채팅이 활성화 되었습니다.");
                }else{
                    chatList.remove(player.getUniqueId());
                    player.sendMessage(ProtectMessage.prefix.getValue() + ChatColor.WHITE + "마을 채팅이 비활성화 되었습니다.");
                }
            }else{
                player.sendMessage(ProtectMessage.prefix.getValue() + ChatColor.WHITE + "마을에 속해있지 않습니다!");
            }
        }
        return false;
    }
}
