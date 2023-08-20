package org.joon1.protecthouse.Command;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.joon1.protecthouse.Main;
import org.joon1.protecthouse.ProtectListener;
import org.joon1.protecthouse.ProtectLocation;
import org.joon1.protecthouse.ProtectMessage;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;

public class ProtectDestroyCommand implements CommandExecutor {
    private Main main;

    String prefix = ProtectMessage.prefix.getValue();
    static public HashMap<UUID, Boolean> destroyMap = new HashMap<>();
    public ProtectDestroyCommand(Main main){
        this.main = main;
    }
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if(commandSender instanceof Player){
            Player player = (Player) commandSender;
            if(destroyMap.containsKey(player.getUniqueId())){
                destroyMap.remove(player.getUniqueId());
                File town = new File(main.getDataFolder(), "region/" + player.getUniqueId() + ".yml");
                town.delete();
                YamlConfiguration yc = YamlConfiguration.loadConfiguration(Main.playerRank);
                YamlConfiguration ycc = YamlConfiguration.loadConfiguration(Main.protectList);
                yc.set(player.getUniqueId().toString(), "none");
                ycc.set(player.getUniqueId().toString(), "none");
                ProtectChatCommand.chatList.remove(player.getUniqueId());
                try {
                    yc.save(Main.playerRank);
                    ycc.save(Main.protectList);
                    player.sendMessage(prefix + ChatColor.WHITE + "마을 삭제가 완료되었습니다.");
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                ProtectLocation.protectMap.remove(player.getUniqueId());
                ProtectListener.checkPass.remove(player.getUniqueId());
            }else{
                player.sendMessage(prefix + ChatColor.WHITE + "잘못된 접근입니다. (4002)");
            }

        }



        return false;
    }
}
