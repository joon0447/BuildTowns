package org.joon1.protecthouse;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.io.File;
import java.io.IOException;
import java.util.*;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.joon1.protecthouse.Command.ProtectDestroyCommand;
import org.joon1.protecthouse.Command.ProtectJoinCommand;
import org.joon1.protecthouse.Command.ProtectRemoveCommand;

public class ProtectMenuListener implements Listener {

    private Main main;
    HashMap<UUID, ArrayList<List>> townlineMap = new HashMap<>();
    ArrayList<UUID> lineList = new ArrayList<>();
    ArrayList<List> arrLoc = new ArrayList<>();
    ProtectMenuListener(Main main){
        this.main = main;
    }
    int cost;
    String prefix = ProtectMessage.prefix.getValue();
    @EventHandler
    public void onCLick(InventoryClickEvent e){
        if(ChatColor.translateAlternateColorCodes('&',e.getView().getTitle()).contains(" 님의 마을 관리자 메뉴") && e.getCurrentItem()!=null) {   //마을 관리자 메뉴
            e.setCancelled(true);
            Player player = (Player) e.getWhoClicked();
            switch (e.getRawSlot()){
                case 12:
                    File file = new File(main.getDataFolder(), "region/" + player.getUniqueId() + ".yml");
                    YamlConfiguration mFile = YamlConfiguration.loadConfiguration(file);
                    int level = mFile.getInt("level");
                    if(level==1) cost = main.upTwo;
                    else if(level==2) cost = main.upThree;
                    else if(level==3) cost = main.upFour;
                    else{
                        player.sendMessage(ProtectMessage.prefix.getValue() + ProtectMessage.noUpgrade.getValue());
                        player.closeInventory();
                        break;
                    }
                    Inventory playerInv = player.getInventory();
                    if(playerInv.contains(Material.DIAMOND, cost)){
                        ItemStack costDia = new ItemStack(Material.DIAMOND);
                        costDia.setAmount(cost);
                        int tLevel = level +1;
                        player.sendMessage(ProtectMessage.prefix.getValue() + ChatColor.WHITE+ "마을이 " + tLevel + ProtectMessage.completeUpgrade.getValue());
                        mFile.set("level", level+1);
                        player.closeInventory();
                        try {
                            mFile.save(file);
                        } catch (IOException ex) {
                            throw new RuntimeException(ex);
                        }
                        playerInv.removeItem(costDia);
                    }else{
                        player.sendMessage(ProtectMessage.prefix.getValue() + ProtectMessage.failUpgrade.getValue());
                        player.closeInventory();
                        break;
                    }
                    break;
                case 14:
                    Inventory userInv = Bukkit.createInventory(player, 27, ChatColor.DARK_GRAY  + "마을 인원 관리");

                    ItemStack playerHead = new ItemStack(Material.PLAYER_HEAD);
                    ItemMeta playerHeadMeta = playerHead.getItemMeta();
                    playerHeadMeta.setDisplayName(ChatColor.WHITE + "마을 인원 정보 확인");
                    playerHead.setItemMeta(playerHeadMeta);
                    userInv.setItem(11, playerHead);

                    ItemStack playerOut = new ItemStack(Material.RED_CONCRETE);
                    ItemMeta playerOutMeta = playerOut.getItemMeta();
                    playerOutMeta.setDisplayName(ChatColor.WHITE + "마을 인원 추방");
                    playerOut.setItemMeta(playerOutMeta);
                    userInv.setItem(13, playerOut);

                    ItemStack playerIn = new ItemStack(Material.GREEN_CONCRETE);
                    ItemMeta playerInMeta = playerIn.getItemMeta();
                    playerInMeta.setDisplayName(ChatColor.WHITE + "마을 인원 초대");
                    playerIn.setItemMeta(playerInMeta);
                    userInv.setItem(15, playerIn);
                    player.openInventory(userInv);
                    break;
                case 16:
                    Inventory townInv = Bukkit.createInventory(player, 27, ChatColor.DARK_GRAY  + "마을 기능");

                    ItemStack checkLine = new ItemStack(Material.END_CRYSTAL);
                    ItemMeta checkLineMeta = checkLine.getItemMeta();
                    checkLineMeta.setDisplayName(ChatColor.WHITE + "마을 경계 확인하기");
                    checkLine.setItemMeta(checkLineMeta);
                    townInv.setItem(12, checkLine);

                    ItemStack removeTown = new ItemStack(Material.BARRIER);
                    ItemMeta removeTownMeta = removeTown.getItemMeta();
                    removeTownMeta.setDisplayName(ChatColor.RED + "마을 삭제");
                    removeTown.setItemMeta(removeTownMeta);
                    townInv.setItem(14, removeTown);
                    player.openInventory(townInv);
                    break;

                default:
                    return;
            }
        }else if(ChatColor.translateAlternateColorCodes('&',e.getView().getTitle()).contains("마을 생성권 구매") && e.getCurrentItem()!=null) {
            e.setCancelled(true);
            Player player = (Player) e.getWhoClicked();
            switch (e.getRawSlot()) {
                case 13:
                    if (player.getInventory().contains(Material.DIAMOND, 5)) {
                        ItemStack createCostDia = new ItemStack(Material.DIAMOND);
                        createCostDia.setAmount(main.upCreate);
                        player.getInventory().addItem(Main.townTicket);
                        player.getInventory().removeItem(createCostDia);
                        player.closeInventory();
                        player.sendMessage(ProtectMessage.prefix.getValue() + ChatColor.WHITE + "마을 생성권을 구매하였습니다.");
                        player.closeInventory();
                    } else {
                        player.sendMessage(ProtectMessage.prefix.getValue() + ChatColor.WHITE + "다이아몬드 "+main.upCreate+"개가 필요합니다!");
                        player.closeInventory();
                    }

                }
            } else if(ChatColor.translateAlternateColorCodes('&',e.getView().getTitle()).contains("마을 인원 관리") && e.getCurrentItem()!=null){
            e.setCancelled(true);
            Player player = (Player) e.getWhoClicked();
            switch (e.getRawSlot()){
                case 11:  // 인원 정보
                    Inventory townUserInv = Bukkit.createInventory(player, 27, ChatColor.DARK_GRAY  + "마을 인원 정보");
                    File town = new File(main.getDataFolder(), "region/" + player.getUniqueId() + ".yml");
                    YamlConfiguration yc = YamlConfiguration.loadConfiguration(town);
                    ConfigurationSection section = yc.getConfigurationSection("Players");
                    int countSeat = 0;
                    for(String user : section.getKeys(false)){
                        ItemStack userHead = new ItemStack(Material.PLAYER_HEAD);
                        SkullMeta userHeadMeta = (SkullMeta) userHead.getItemMeta();
                        userHeadMeta.setOwningPlayer(Bukkit.getPlayer(UUID.fromString(user)));
                        UUID uuid = UUID.fromString(user);
                        if(Bukkit.getOfflinePlayer(uuid).isOnline()){
                            userHeadMeta.setDisplayName(ChatColor.GREEN + Bukkit.getPlayer(uuid).getName());
                            if(user.equals(yc.getString("UUID"))){
                                userHeadMeta.setLore(Arrays.asList(ChatColor.GREEN + "온라인", ChatColor.LIGHT_PURPLE + "마을 관리자"));
                            }else{
                                userHeadMeta.setLore(Arrays.asList(ChatColor.GREEN + "온라인"));
                            }
                        }else{
                            userHeadMeta.setDisplayName(ChatColor.GRAY + Bukkit.getOfflinePlayer(uuid).getName());
                            if(user.equals(yc.getString("UUID"))){
                                userHeadMeta.setLore(Arrays.asList(ChatColor.GRAY + "오프라인", ChatColor.LIGHT_PURPLE + "마을 관리자"));
                            }else{
                                userHeadMeta.setLore(Arrays.asList(ChatColor.GRAY + "오프라인"));
                            }
                        }
                        userHead.setItemMeta(userHeadMeta);
                        townUserInv.setItem(countSeat,userHead);
                        countSeat++;
                    }
                    player.openInventory(townUserInv);
                    break;
                case 13:   //츄방
                    Inventory removeUserInv = Bukkit.createInventory(player, 27, ChatColor.DARK_GRAY  + "플레이어 추방");
                    int reInvCount = 0;
                    File userFile = new File(main.getDataFolder(), "region/" + player.getUniqueId() + ".yml");
                    YamlConfiguration userYml = YamlConfiguration.loadConfiguration(userFile);
                    ConfigurationSection userSection = userYml.getConfigurationSection("Players");
                    for(String removeUser : userSection.getKeys(false)){
                        if(!removeUser.equals(player.getUniqueId().toString())){
                            ItemStack removeUserHead = new ItemStack(Material.PLAYER_HEAD);
                            SkullMeta removeUserHeadMeta = (SkullMeta) removeUserHead.getItemMeta();
                            removeUserHeadMeta.setOwningPlayer(Bukkit.getPlayer(removeUser));
                            UUID removeUUID = UUID.fromString(removeUser);
                            if(Bukkit.getOfflinePlayer(removeUUID).isOnline()){
                                removeUserHeadMeta.setDisplayName(Bukkit.getPlayer(removeUUID).getName());
                                removeUserHeadMeta.setLore(Arrays.asList(ChatColor.GREEN + "온라인"));
                            }else{
                                removeUserHeadMeta.setDisplayName(Bukkit.getOfflinePlayer(removeUUID).getName());
                                removeUserHeadMeta.setLore(Arrays.asList(ChatColor.GRAY + "오프라인"));
                            }
                            removeUserHead.setItemMeta(removeUserHeadMeta);
                            removeUserInv.setItem(reInvCount,removeUserHead);
                            reInvCount++;
                        }
                    }
                    player.openInventory(removeUserInv);
                    break;
                case 15:   //초대
                    Inventory inviteInv = Bukkit.createInventory(player, 45, ChatColor.DARK_GRAY  + "플레이어 초대");
                    int invCount = 0;
                    File rankList = new File(main.getDataFolder(), "playerRank.yml");
                    YamlConfiguration rankListYml = YamlConfiguration.loadConfiguration(rankList);
                    for(Player p : Bukkit.getOnlinePlayers()){
                        if(!rankListYml.getKeys(false).contains(p.getUniqueId().toString()) || rankListYml.getString(p.getUniqueId().toString()).equals("none")) {   //마을 가입 안한 사람만 머리 띄움.
                            ItemStack inviteHead = new ItemStack(Material.PLAYER_HEAD);
                            SkullMeta inviteHeadMeta = (SkullMeta) inviteHead.getItemMeta();
                            inviteHeadMeta.setOwningPlayer(p);
                            inviteHeadMeta.setDisplayName(p.getName());
                            inviteHead.setItemMeta(inviteHeadMeta);
                            inviteInv.setItem(invCount, inviteHead);
                            invCount++;
                        }
                    }
                    player.openInventory(inviteInv);
                    break;
                default:
                    return;
            }
        } else if(ChatColor.translateAlternateColorCodes('&',e.getView().getTitle()).contains("플레이어 초대") && e.getCurrentItem()!=null){
            e.setCancelled(true);
            Player player = (Player) e.getWhoClicked();
            Player target = Bukkit.getPlayer(e.getCurrentItem().getItemMeta().getDisplayName());
            net.md_5.bungee.api.chat.TextComponent agree = new net.md_5.bungee.api.chat.TextComponent(prefix + ChatColor.WHITE + "초대에 응답하려면 우측의 수락 / 거절 버튼을 눌러주세요.");
            TextComponent ag = new TextComponent(ChatColor.GREEN + "  [수락]");
            TextComponent de = new TextComponent(ChatColor.RED + "  [거절]");
            ag.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tjoin " + player.getUniqueId()));
            de.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,  "/tjoin no " + player.getUniqueId()));
            agree.addExtra(ag);
            agree.addExtra(de);
            ProtectJoinCommand.passMap.put(target.getUniqueId(), true);
            target.sendMessage(prefix + ChatColor.WHITE + player.getName() +" 님이 당신을 마을에 초대하셨습니다!");
            player.sendMessage(prefix + ChatColor.WHITE +target.getName() + " 님을 마을에 초대하였습니다!");
            target.spigot().sendMessage(agree);
            player.closeInventory();
        } else if(ChatColor.translateAlternateColorCodes('&',e.getView().getTitle()).contains(" 님의 마을 메뉴") && e.getCurrentItem()!=null) {   //일반 유저 마을 메뉴
            e.setCancelled(true);
            Player player = (Player) e.getWhoClicked();
            switch (e.getRawSlot()) {
                case 12:
                    Inventory townUserInv = Bukkit.createInventory(player, 27, ChatColor.DARK_GRAY  + "마을 인원 정보");
                    File list = new File(main.getDataFolder(), "protectList.yml");
                    YamlConfiguration lc = YamlConfiguration.loadConfiguration(list);
                    String id = lc.getString(player.getUniqueId().toString());
                    File town = new File(main.getDataFolder(), "region/" + id + ".yml");
                    YamlConfiguration yc = YamlConfiguration.loadConfiguration(town);
                    ConfigurationSection section = yc.getConfigurationSection("Players");
                    int countSeat = 0;
                    for(String user : section.getKeys(false)){
                        ItemStack userHead = new ItemStack(Material.PLAYER_HEAD);
                        SkullMeta userHeadMeta = (SkullMeta) userHead.getItemMeta();
                        userHeadMeta.setOwningPlayer(Bukkit.getPlayer(UUID.fromString(user)));
                        UUID uuid = UUID.fromString(user);
                        if(Bukkit.getOfflinePlayer(uuid).isOnline()){
                            userHeadMeta.setDisplayName(ChatColor.GREEN + Bukkit.getPlayer(uuid).getName());
                            if(user.equals(yc.getString("UUID"))){
                                userHeadMeta.setLore(Arrays.asList(ChatColor.GREEN + "온라인", ChatColor.LIGHT_PURPLE + "마을 관리자"));
                            }else{
                                userHeadMeta.setLore(Arrays.asList(ChatColor.GREEN + "온라인"));
                            }
                        }else{
                            userHeadMeta.setDisplayName(ChatColor.GRAY + Bukkit.getOfflinePlayer(uuid).getName());
                            if(user.equals(yc.getString("UUID"))){
                                userHeadMeta.setLore(Arrays.asList(ChatColor.GRAY + "오프라인", ChatColor.LIGHT_PURPLE + "마을 관리자"));
                            }else{
                                userHeadMeta.setLore(Arrays.asList(ChatColor.GRAY + "오프라인"));
                            }
                        }
                        userHead.setItemMeta(userHeadMeta);
                        townUserInv.setItem(countSeat,userHead);
                        countSeat++;
                    }
                    player.openInventory(townUserInv);
                    break;
                case 14:
                    net.md_5.bungee.api.chat.TextComponent check = new net.md_5.bungee.api.chat.TextComponent(prefix
                            + ChatColor.WHITE + "마을을 떠나려면 우측 확인 버튼을 눌러주세요." );
                    TextComponent ag = new TextComponent(ChatColor.GREEN + "  [확인]");
                    ProtectRemoveCommand.removeMap.put(player.getUniqueId(), true);
                    ag.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tremove " + player.getUniqueId()));
                    check.addExtra(ag);
                    player.spigot().sendMessage(check);
                    player.closeInventory();
                    break;
                case 16:
                    if(!lineList.contains(player.getUniqueId())){
                        lineList.add(player.getUniqueId());
                        YamlConfiguration lineYc = YamlConfiguration.loadConfiguration(Main.protectList);
                        File checkTown = new File(main.getDataFolder(), "region/" + lineYc.getString(player.getUniqueId().toString()) + ".yml");
                        YamlConfiguration checkTownYc = YamlConfiguration.loadConfiguration(checkTown);
                        int level = checkTownYc.getInt("level");
                        int protectBlock = 10;
                        if(level==2){
                            protectBlock = 50;
                        }else if(level==3){
                            protectBlock = 100;
                        }else if(level==4){
                            protectBlock = 150;
                        }
                        double locX = checkTownYc.getDouble("locX");
                        double locZ = checkTownYc.getDouble("locZ");
                        double locY = player.getLocation().getY();
                        for (double x = locX + protectBlock; x >= locX - protectBlock; x--) {
                            if (x == locX + protectBlock || x == locX - protectBlock) {
                                for (double z = locZ + protectBlock; z >= locZ - protectBlock; z--) {
                                    for(double y = locY; ; y++){
                                        Location loc = new Location(player.getWorld(), x, y, z);
                                        if(player.getWorld().getBlockAt(loc).getType() == Material.AIR){
                                            player.sendBlockChange(loc, Material.LIME_CONCRETE.createBlockData());   //테두리 생성
                                            arrLoc.add(Arrays.asList(x, y, z));
                                            break;
                                        }
                                    }
                                }
                            }
                            else {
                                for (double z = locZ + protectBlock; z >= locZ - protectBlock; z--) {
                                    if (z == locZ + protectBlock || z == locZ - protectBlock) {
                                        for(double y = locY; ; y++) {
                                            Location loc = new Location(player.getWorld(), x, y, z);
                                            if (player.getWorld().getBlockAt(loc).getType() == Material.AIR) {
                                                player.sendBlockChange(loc, Material.LIME_CONCRETE.createBlockData());   //테두리 생성
                                                arrLoc.add(Arrays.asList(x, y, z));
                                                break;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        townlineMap.put(player.getUniqueId(), arrLoc);
                        player.closeInventory();
                        player.sendMessage(ProtectMessage.prefix.getValue() + ChatColor.WHITE + "마을의 경계가 표시됩니다.");
                        player.sendMessage(ProtectMessage.prefix.getValue() + ChatColor.WHITE + "보호 범위는 초록색 라인을 포함하지 않습니다.");
                        Bukkit.getScheduler().runTaskLater(main, () -> {
                            for (List dlist : townlineMap.get(player.getUniqueId())) {  // 이전 블럭 초기화
                                double lx = (double) dlist.get(0);
                                double ly = (double) dlist.get(1);
                                double lz = (double) dlist.get(2);
                                Location loc = new Location(player.getWorld(), lx, ly, lz);
                                player.sendBlockChange(loc, Material.AIR.createBlockData());
                            }
                            player.sendMessage(ProtectMessage.prefix.getValue() + ChatColor.WHITE + "마을의 경계 표시가 사라집니다!");
                            townlineMap.remove(player.getUniqueId());
                            lineList.remove(player.getUniqueId());
                        }, 200);
                        break;
                    }else{
                        player.sendMessage(ProtectMessage.prefix.getValue() + "이미 경계를 표시하고 있습니다!");
                        player.closeInventory();
                        break;
                    }
                default:
                    break;
            }
        } else if(ChatColor.translateAlternateColorCodes('&',e.getView().getTitle()).contains("마을 인원 정보") && e.getCurrentItem()!=null){
            e.setCancelled(true);
        }else if(ChatColor.translateAlternateColorCodes('&',e.getView().getTitle()).contains("플레이어 추방") && e.getCurrentItem()!=null){   // 마을 인원 추방
            e.setCancelled(true);
            Player player  = (Player) e.getWhoClicked();
            Player target = Bukkit.getPlayer(e.getCurrentItem().getItemMeta().getDisplayName());
            net.md_5.bungee.api.chat.TextComponent check = new net.md_5.bungee.api.chat.TextComponent(prefix
                    + ChatColor.WHITE + target.getName() + " 님을 추방하려면 우측 확인 버튼을 눌러주세요." );
            TextComponent ag = new TextComponent(ChatColor.GREEN + "  [확인]");
            ProtectRemoveCommand.removeMap.put(player.getUniqueId(), true);
            ag.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tremove " + target.getUniqueId()));
            check.addExtra(ag);
            player.spigot().sendMessage(check);
            player.closeInventory();
        }else if(ChatColor.translateAlternateColorCodes('&',e.getView().getTitle()).contains("마을 기능") && e.getCurrentItem()!=null) {  //마을 설정
            e.setCancelled(true);
            Player player = (Player) e.getWhoClicked();
            switch (e.getRawSlot()){
                case 12:
                    if(!lineList.contains(player.getUniqueId())){
                        lineList.add(player.getUniqueId());
                        File checkTown = new File(main.getDataFolder(), "region/" + player.getUniqueId() + ".yml");
                        YamlConfiguration checkTownYc = YamlConfiguration.loadConfiguration(checkTown);
                        int level = checkTownYc.getInt("level");
                        int protectBlock = 10;
                        if(level==2){
                            protectBlock = 50;
                        }else if(level==3){
                            protectBlock = 100;
                        }else if(level==4){
                            protectBlock = 150;
                        }
                        double locX = checkTownYc.getDouble("locX");
                        double locZ = checkTownYc.getDouble("locZ");
                        double locY = player.getLocation().getY();
                        for (double x = locX + protectBlock; x >= locX - protectBlock; x--) {
                            if (x == locX + protectBlock || x == locX - protectBlock) {
                                for (double z = locZ + protectBlock; z >= locZ - protectBlock; z--) {
                                    for(double y = locY; ; y++){
                                        Location loc = new Location(player.getWorld(), x, y, z);
                                        if(player.getWorld().getBlockAt(loc).getType() == Material.AIR){
                                            player.sendBlockChange(loc, Material.LIME_CONCRETE.createBlockData());   //테두리 생성
                                            arrLoc.add(Arrays.asList(x, y, z));
                                            break;
                                        }
                                    }
                                }
                            }
                            else {
                                for (double z = locZ + protectBlock; z >= locZ - protectBlock; z--) {
                                    if (z == locZ + protectBlock || z == locZ - protectBlock) {
                                        for(double y = locY; ; y++) {
                                            Location loc = new Location(player.getWorld(), x, y, z);
                                            if (player.getWorld().getBlockAt(loc).getType() == Material.AIR) {
                                                player.sendBlockChange(loc, Material.LIME_CONCRETE.createBlockData());   //테두리 생성
                                                arrLoc.add(Arrays.asList(x, y, z));
                                                break;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        townlineMap.put(player.getUniqueId(), arrLoc);
                        player.closeInventory();
                        player.sendMessage(ProtectMessage.prefix.getValue() + ChatColor.WHITE + "마을의 경계가 표시됩니다.");
                        player.sendMessage(ProtectMessage.prefix.getValue() + ChatColor.WHITE + "보호 범위는 초록색 라인을 포함합니다.");
                        Bukkit.getScheduler().runTaskLater(main, () -> {
                            for (List list : townlineMap.get(player.getUniqueId())) {  // 이전 블럭 초기화
                                double lx = (double) list.get(0);
                                double ly = (double) list.get(1);
                                double lz = (double) list.get(2);
                                Location loc = new Location(player.getWorld(), lx, ly, lz);
                                player.sendBlockChange(loc, Material.AIR.createBlockData());
                            }
                            player.sendMessage(ProtectMessage.prefix.getValue() + ChatColor.WHITE + "마을의 경계 표시가 사라집니다!");
                            townlineMap.remove(player.getUniqueId());
                            lineList.remove(player.getUniqueId());
                        }, 200);
                        break;
                    }else{
                        player.sendMessage(ProtectMessage.prefix.getValue() + "이미 경계를 표시하고 있습니다!");
                        player.closeInventory();
                        break;
                    }

                case 14:
                    File town = new File(main.getDataFolder(), "region/" + player.getUniqueId() + ".yml");
                    YamlConfiguration yc = YamlConfiguration.loadConfiguration(town);
                    ConfigurationSection section = yc.getConfigurationSection("Players");
                    int userCount = 0;
                    for(String s : section.getKeys(false)){
                        userCount++;
                    }
                    if(userCount==1){
                        ProtectDestroyCommand.destroyMap.put(player.getUniqueId(), true);
                        player.sendMessage(prefix + ChatColor.WHITE + "마을이 삭제되면 마을로 설정된 구역이 더 이상 보호받지 못합니다.");
                        net.md_5.bungee.api.chat.TextComponent check = new net.md_5.bungee.api.chat.TextComponent(prefix
                                + ChatColor.WHITE + "마을을 삭제하려면 우측 확인 버튼을 눌러주세요." );
                        TextComponent ag = new TextComponent(ChatColor.GREEN + "  [확인]");
                        ag.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tdestroy " + player.getUniqueId()));
                        check.addExtra(ag);
                        player.spigot().sendMessage(check);
                        player.closeInventory();
                        break;
                    }else{
                        player.sendMessage(prefix + ChatColor.WHITE + "마을을 삭제하려면 마을에 소속된 다른 인원이 없어야 합니다.");
                        player.sendMessage(prefix + ChatColor.WHITE + "마을 인원을 추방한 후 다시 시도해주세요.");
                        player.closeInventory();
                        break;
                    }
            }
        }else if(ChatColor.translateAlternateColorCodes('&',e.getView().getTitle()).contains("BuildsTown 어드민 메뉴") && e.getCurrentItem()!=null){
            e.setCancelled(true);
            Player player =(Player) e.getWhoClicked();
            Player target = Bukkit.getPlayer(e.getCurrentItem().getItemMeta().getDisplayName());
            player.sendMessage(ProtectMessage.prefix.getValue() + "마을을 삭제하면 마을에 속한 유저도 모두 추방처리 됩니다.");
            net.md_5.bungee.api.chat.TextComponent check = new net.md_5.bungee.api.chat.TextComponent(prefix
                    + ChatColor.WHITE + e.getCurrentItem().getItemMeta().getDisplayName() +" 님의 마을을 삭제하시겠습니까?");
            TextComponent ag = new TextComponent(ChatColor.GREEN + "  [확인]");
            ag.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/bta remove "+ target.getUniqueId()));
            check.addExtra(ag);
            player.spigot().sendMessage(check);
            player.closeInventory();
        }
    }
}
