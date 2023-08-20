package org.joon1.protecthouse.Command;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
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
import org.joon1.protecthouse.Main;

import java.io.File;
import java.util.Arrays;

public class ProtectMenu implements CommandExecutor {
    private Main main;

    public ProtectMenu(Main main){
        this.main = main;
    }
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if(commandSender instanceof Player){
            Player player = (Player) commandSender;
            File rankFile = Main.playerRank;
            YamlConfiguration rf = YamlConfiguration.loadConfiguration(rankFile);
            String playerRank = rf.getString(player.getUniqueId().toString());
            if(playerRank != null && playerRank.equals("OWNER")){ // 마을 관리자 메뉴
                int upCost = main.upTwo;
                File file = new File(main.getDataFolder(), "region/" + player.getUniqueId() + ".yml");
                YamlConfiguration mFile = YamlConfiguration.loadConfiguration(file);
                int level = mFile.getInt("level");
                if(level==1) upCost = main.upTwo;
                if(level==2) upCost = main.upThree;
                if(level==3) upCost = main.upFour;

                Inventory inv = Bukkit.createInventory(player, 27, ChatColor.DARK_GRAY + player.getName() + " 님의 마을 관리자 메뉴");

                File town = new File(main.getDataFolder(), "region/" + player.getUniqueId().toString() + ".yml");
                YamlConfiguration yc = YamlConfiguration.loadConfiguration(town);
                ConfigurationSection section = yc.getConfigurationSection("Players");
                int townUser= 0;
                for(String key : section.getKeys(false)){
                    townUser++;
                }

                // 마을 정보
                ItemStack townInfo = new ItemStack(Material.ENDER_PEARL);
                ItemMeta townInfoMeta = townInfo.getItemMeta();
                townInfoMeta.setDisplayName(ChatColor.WHITE.toString() + ChatColor.BOLD + "마을 정보");
                townInfoMeta.setLore(Arrays.asList(ChatColor.WHITE + "마을 레벨 : " + mFile.getString("level"), ChatColor.WHITE + "마을 위치 (x,z): " + mFile.getInt("locX") +" , " + mFile.getInt("locZ"),
                        ChatColor.WHITE + "마을 인원 : " + townUser));
                townInfo.setItemMeta(townInfoMeta);
                inv.setItem(10, townInfo);

                // 마을 강화
                ItemStack townUpgrade = new ItemStack(Material.DIAMOND);
                ItemMeta townUpgradeMeta = townUpgrade.getItemMeta();
                townUpgradeMeta.setDisplayName(ChatColor.WHITE.toString() + ChatColor.BOLD + "마을 업그레이드");
                if(level<=3){
                    townUpgradeMeta.setLore(Arrays.asList(ChatColor.WHITE + "현재 레벨 : " + mFile.getString("level"), ChatColor.WHITE + "강화 비용 : 다이아몬드 " + upCost + " 개"));
                }else{
                    townUpgradeMeta.setLore(Arrays.asList(ChatColor.WHITE + "더 이상 강화가 불가능합니다!"));
                }
                townUpgrade.setItemMeta(townUpgradeMeta);
                inv.setItem(12, townUpgrade);

                // 마을 인원 목록
                ItemStack EditTownUser = new ItemStack(Material.PLAYER_HEAD);
                ItemMeta townUserMeta = EditTownUser.getItemMeta();
                townUserMeta.setDisplayName(ChatColor.WHITE.toString() + ChatColor.BOLD + "마을 인원 관리");
                EditTownUser.setItemMeta(townUserMeta);
                inv.setItem(14, EditTownUser);

                // 마을 기능
                ItemStack townSet = new ItemStack(Material.ANVIL);
                ItemMeta townSetMeta = townSet.getItemMeta();
                townSetMeta.setDisplayName(ChatColor.WHITE.toString() + ChatColor.BOLD + "마을 기능");
                townSet.setItemMeta(townSetMeta);
                inv.setItem(16, townSet);

                player.openInventory(inv);
            }else if(playerRank != null && playerRank.equals("NEWBIE")){
                Inventory inv = Bukkit.createInventory(player, 27, ChatColor.DARK_GRAY  + player.getName() + " 님의 마을 메뉴");
                File list = new File(main.getDataFolder(), "protectList.yml");
                YamlConfiguration lc = YamlConfiguration.loadConfiguration(list);
                String id = lc.getString(player.getUniqueId().toString());
                int UserCount = 0;

                File town = new File(main.getDataFolder(), "region/" + id + ".yml");
                YamlConfiguration yc = YamlConfiguration.loadConfiguration(town);
                ConfigurationSection section = yc.getConfigurationSection("Players");
                for(String key : section.getKeys(false)){
                    UserCount++;
                }
                int level = yc.getInt("level");
                ItemStack townInfo = new ItemStack(Material.ENDER_PEARL);
                ItemMeta townInfoMeta = townInfo.getItemMeta();
                townInfoMeta.setDisplayName(ChatColor.WHITE.toString() + ChatColor.BOLD + "마을 정보");
                townInfoMeta.setLore(Arrays.asList(ChatColor.WHITE + "마을 레벨 : " + yc.getString("level"), ChatColor.WHITE + "마을 위치 (x,z): " + yc.getInt("locX") +" , " + yc.getInt("locZ"),
                        ChatColor.WHITE + "마을 인원 : " +  UserCount));
                townInfo.setItemMeta(townInfoMeta);
                inv.setItem(10, townInfo);

                // 마을 인원 목록
                ItemStack townUser = new ItemStack(Material.PLAYER_HEAD);
                ItemMeta townUserMeta = townUser.getItemMeta();
                townUserMeta.setDisplayName(ChatColor.BOLD + ChatColor.WHITE.toString() + "마을 인원 정보 확인");
                townUser.setItemMeta(townUserMeta);
                inv.setItem(12, townUser);

                ItemStack leaveTown = new ItemStack(Material.RED_CONCRETE);
                ItemMeta leaveTownMeta = leaveTown.getItemMeta();
                leaveTownMeta.setDisplayName("마을 떠나기");
                leaveTown.setItemMeta(leaveTownMeta);
                inv.setItem(14, leaveTown);

                ItemStack checkLine = new ItemStack(Material.END_CRYSTAL);
                ItemMeta checkLineMeta = checkLine.getItemMeta();
                checkLineMeta.setDisplayName(ChatColor.WHITE + "마을 경계 확인하기");
                checkLine.setItemMeta(checkLineMeta);
                inv.setItem(16, checkLine);

                player.openInventory(inv);
            }
            else{ //마을이 없을 때
                Inventory createTown = Bukkit.createInventory(player, 27, ChatColor.WHITE.toString() + "마을 생성권 구매");
                createTown.setItem(13,Main.townTicket);
                player.openInventory(createTown);
            }
        }
        return false;
    }
}
