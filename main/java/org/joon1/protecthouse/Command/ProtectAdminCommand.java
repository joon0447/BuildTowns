package org.joon1.protecthouse.Command;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.joon1.protecthouse.Main;
import org.joon1.protecthouse.ProtectMessage;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ProtectAdminCommand implements CommandExecutor {
    private Main main;
    public ProtectAdminCommand(Main main){
        this.main = main;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if(commandSender instanceof Player){
            Player player = (Player) commandSender;
            if(player.isOp()){
                if(args.length==0) {
                    Inventory btaInv = Bukkit.createInventory(player, 54, "BuildsTown 어드민 메뉴");
                    File files[] = Main.file.listFiles();
                    int count = 0;
                    for (File f : files) {
                        YamlConfiguration yc = YamlConfiguration.loadConfiguration(f);
                        String uuid = yc.getString("UUID");
                        int locX = yc.getInt("locX");
                        int locZ = yc.getInt("locZ");
                        int level = yc.getInt("level");
                        ConfigurationSection section = yc.getConfigurationSection("Players");
                        int townUser = 0;
                        for (String key : section.getKeys(false)) {
                            townUser++;
                        }
                        Player target = Bukkit.getPlayer(UUID.fromString(uuid));
                        ItemStack town = new ItemStack(Material.PLAYER_HEAD);
                        SkullMeta townMeta = (SkullMeta) town.getItemMeta();
                        townMeta.setOwningPlayer(target);
                        townMeta.setDisplayName(target.getName());
                        townMeta.setLore(Arrays.asList("좌표 : " + locX + " " + locZ, "마을 레벨 : " + level, "마을 인원 : " + townUser));
                        town.setItemMeta(townMeta);
                        btaInv.setItem(count, town);
                        count++;
                    }
                    player.openInventory(btaInv);
                }else if(args.length == 2){
                    UUID uuid = UUID.fromString(args[1]);
                    YamlConfiguration rank = YamlConfiguration.loadConfiguration(Main.playerRank);
                    YamlConfiguration protect = YamlConfiguration.loadConfiguration(Main.protectList);
                    rank.set(uuid.toString(), "none");
                    protect.set(uuid.toString(), "none");
                    File file = new File(main.getDataFolder(), "region/" + uuid +".yml");
                    YamlConfiguration yc = YamlConfiguration.loadConfiguration(file);
                    ConfigurationSection section = yc.getConfigurationSection("Players");
                    for (String key : section.getKeys(false)) {
                        Player townUser = Bukkit.getPlayer(UUID.fromString(key));
                        rank.set(key.toString(), "none");
                        protect.set(key.toString(), "none");
                        try {
                            rank.save(Main.playerRank);
                            protect.save(Main.protectList);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                        if(townUser.isOnline()){
                            townUser.sendMessage(ProtectMessage.prefix.getValue() + "서버 관리자가 마을을 강제 삭제하여 마을에서 추방되었습니다.");
                        }
                        player.sendMessage(ProtectMessage.prefix.getValue() + townUser.getName() + "님을 마을에서 추방했습니다.");
                    }
                    file.delete();
                    player.sendMessage(ProtectMessage.prefix.getValue() + "마을 삭제가 완료되었습니다.");
                }

            }else{
                player.sendMessage(ProtectMessage.prefix.getValue() + "잘못된 접근입니다. (5000)");
                System.out.println(player.getName() + "님이 관리자 명령어를 실행하였으나 차단되었습니다. (5000)");
            }
        }

        return false;
    }
}
