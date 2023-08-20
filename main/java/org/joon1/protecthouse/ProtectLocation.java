package org.joon1.protecthouse;


import org.bukkit.*;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;


import java.io.File;
import java.io.IOException;
import java.util.*;

public class ProtectLocation implements Listener {
    private Main main;
    public static HashMap<UUID, ArrayList<List>> lineLoc = new HashMap<>();    // 보호 범위 표시
    public static HashMap<UUID, Location> protectMap = new HashMap<>();  // 보호 구역 저장
    Cuboid cuboid;
    int count = 10;


    public ProtectLocation(Main main) {
        this.main = main;
    }

    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        Player player = e.getPlayer();
        if (player.getInventory().getItemInMainHand().getItemMeta() != null && player.getInventory().getItemInMainHand().getItemMeta().toString().equals(Main.townTicket.getItemMeta().toString())) {
            int px = (int) Math.floor(e.getPlayer().getLocation().getX());
            int pz = (int) Math.floor(e.getPlayer().getLocation().getZ());
            if (lineLoc.containsKey(e.getPlayer().getUniqueId())) {  // 해쉬맵에 자기것이 있을 때
                ArrayList<List> arrLoc = new ArrayList<>();
                for (List list : lineLoc.get(e.getPlayer().getUniqueId())) {  // 이전 블럭 초기화
                    int lx = (int)list.get(0);
                    int ly = (int)list.get(1);
                    int lz = (int)list.get(2);
                    Location loc = new Location(player.getWorld(), lx, ly, lz);
                    player.sendBlockChange(loc, Material.AIR.createBlockData());
                }
                arrLoc.clear(); // 이전 좌표 삭제
                if (!protectMap.containsKey(e.getPlayer().getUniqueId())) {
                    for (int x = (int) Math.floor(player.getLocation().getX()) + count; x >= (int) Math.floor(player.getLocation().getX()) - count; x--) {
                        if (x == (int) Math.floor(player.getLocation().getX()) + count || x == (int) Math.floor(player.getLocation().getX()) - count) {
                            for (int z = (int) Math.floor(player.getLocation().getZ()) + count; z >= (int) Math.floor(player.getLocation().getZ()) - count; z--) {
                                for (int y = (int) Math.floor(player.getLocation().getY()); ; y++) {
                                    Location loc = new Location(player.getWorld(), x, y, z);
                                    if (e.getPlayer().getWorld().getBlockAt(loc).getType() == Material.AIR) {
                                        player.sendBlockChange(loc, Material.LIME_CONCRETE.createBlockData());   //테두리 생성
                                        arrLoc.add(Arrays.asList(x, y, z));
                                        break;
                                    }
                                }
                            }
                        } else {
                            for (int z = (int) Math.floor(player.getLocation().getZ()) + count; z >= (int) Math.floor(player.getLocation().getZ()) - count; z--) {
                                if (z == (int) Math.floor(player.getLocation().getZ()) + count || z == (int) Math.floor(player.getLocation().getZ()) - count) {
                                    for (int y = (int) Math.floor(player.getLocation().getY()); ; y++) {
                                        Location loc = new Location(player.getWorld(), x, y, z);
                                        if (e.getPlayer().getWorld().getBlockAt(loc).getType() == Material.AIR) {  //테두리 생성
                                            player.sendBlockChange(loc, Material.LIME_CONCRETE.createBlockData());   //테두리 생성
                                            arrLoc.add(Arrays.asList(x, y, z));
                                            break;
                                        }
                                    }
                                }
                            }
                        }
                        lineLoc.put(e.getPlayer().getUniqueId(), arrLoc);
                    }
                }

            } else {    //해쉬맵에 자기것이 없을때 (최초생성)
                if (player.getInventory().getItemInMainHand().getItemMeta().toString().equals(Main.townTicket.getItemMeta().toString())) {
                    ArrayList<List> arrLoc = new ArrayList<>();
                    if (!protectMap.containsKey(e.getPlayer().getUniqueId())) {
                        for (int x =(int) Math.floor(player.getLocation().getX()) + count; x >= (int) Math.floor(player.getLocation().getX()) - count; x--) {
                            if (x == (int) Math.floor(player.getLocation().getX()) + count || x == (int) Math.floor(player.getLocation().getX()) - count) {
                                for (int z = (int) Math.floor(player.getLocation().getZ()) + count; z >= (int) Math.floor(player.getLocation().getZ()) - count; z--) {
                                    for (int y = (int) Math.floor(player.getLocation().getY()); ; y++) {
                                        Location loc = new Location(player.getWorld(), x, y, z);
                                        if (e.getPlayer().getWorld().getBlockAt(loc).getType() == Material.AIR) {  //테두리 생성
                                            player.sendBlockChange(loc, Material.LIME_CONCRETE.createBlockData());   //테두리 생성
                                            arrLoc.add(Arrays.asList(x, y, z));
                                            break;
                                        }
                                    }
                                }
                            } else {
                                for (int z = (int) Math.floor(player.getLocation().getZ()) + count; z >= (int) Math.floor(player.getLocation().getZ()) - count; z--) {
                                    if (z == (int) Math.floor(player.getLocation().getZ()) + count || z == (int) Math.floor(player.getLocation().getZ()) - count) {
                                        for (int y = (int) Math.floor(player.getLocation().getY()); ; y++) {
                                            Location loc = new Location(player.getWorld(), x, y, z);
                                            if (e.getPlayer().getWorld().getBlockAt(loc).getType() == Material.AIR) {
                                                player.sendBlockChange(loc, Material.LIME_CONCRETE.createBlockData());   //테두리 생성
                                                arrLoc.add(Arrays.asList(x, y, z));
                                                break;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    lineLoc.put(e.getPlayer().getUniqueId(), arrLoc);
                }
            }
        } else {  // 생성 아이템 이외 아이템을 들고 있을 때
            if (lineLoc.containsKey(e.getPlayer().getUniqueId())) {  // 해쉬맵에 자기것이 있을 때
                for (List list : lineLoc.get(e.getPlayer().getUniqueId())) {  // 이전 블럭 초기화
                    int lx = (int) list.get(0);
                    int ly = (int) list.get(1);
                    int lz = (int) list.get(2);
                    Location loc = new Location(player.getWorld(), lx, ly, lz);
                    player.sendBlockChange(loc, Material.AIR.createBlockData());
                }
                lineLoc.remove(e.getPlayer().getUniqueId()); //해쉬맵 삭제
            }
        }
    }

    @EventHandler
    public void onRightClick(PlayerInteractEvent e) {
        Player player = e.getPlayer();
        Location spawnPoint = Bukkit.getServer().getWorld("world").getSpawnLocation();
        int spawnX = (int) Math.floor(spawnPoint.getX());
        int spawnZ = (int) Math.floor(spawnPoint.getZ());
        YamlConfiguration protectYml = YamlConfiguration.loadConfiguration(Main.protectList);
        if (player.getInventory().getItemInMainHand().getType() != Material.AIR && (player.getInventory().getItemInMainHand().getItemMeta().toString().equals(Main.townTicket.getItemMeta().toString()))
                && (e.getAction().equals(Action.RIGHT_CLICK_AIR) || e.getAction().equals(Action.RIGHT_CLICK_BLOCK))) {
            if(!protectYml.contains(player.getUniqueId().toString()) || protectYml.getString(player.getUniqueId().toString()).equals("none")) {
                int x = (int) Math.floor(player.getLocation().getX());
                int z = (int) Math.floor(player.getLocation().getZ());
                int spawnLimit = main.spawnLimit;
                if(x>=spawnX-spawnLimit && x<=spawnX+spawnLimit && z>=spawnZ-spawnLimit && z<=spawnZ+spawnLimit){
                    player.sendMessage(ProtectMessage.prefix.getValue() + "이곳은 마을 지정이 불가능한 구역입니다!");
                    return;
                }
                int townBytown = main.townBytown;
                for (UUID uuid : protectMap.keySet()) {
                    int tx = (int) Math.floor(protectMap.get(uuid).getX());
                    int tz = (int) Math.floor(protectMap.get(uuid).getZ());
                    if ((tx - townBytown <= x && tx + townBytown >= x) && (tz - townBytown <= z && tz + townBytown >= z)) {
                        e.getPlayer().sendMessage(ProtectMessage.prefix.getValue() + ProtectMessage.createClose.getValue());
                        return;}
                }
                try {
                    cuboid = new Cuboid(
                            new Location(Bukkit.getWorld("world"), (int) Math.floor(e.getPlayer().getLocation().getX()) + count, (int) Math.floor(e.getPlayer().getLocation().getY()) + 0, (int) Math.floor(e.getPlayer().getLocation().getZ()) + count),
                            new Location(Bukkit.getWorld("world"), (int) Math.floor(e.getPlayer().getLocation().getX()) - count, (int) Math.floor(e.getPlayer().getLocation().getY()) + 0, (int) Math.floor(e.getPlayer().getLocation().getZ()) - count));
                    e.getPlayer().sendMessage(ProtectMessage.prefix.getValue() + ProtectMessage.createDone.getValue());
                    protectYml.set(player.getUniqueId().toString(), player.getUniqueId().toString());
                    protectYml.save(Main.protectList);
                    e.getPlayer().playSound(e.getPlayer(), Sound.ITEM_GOAT_HORN_SOUND_1, 1, 1);
                    player.getInventory().removeItem(Main.townTicket);
                    saveData(e.getPlayer());
                    } catch (Exception ed) {
                        e.getPlayer().sendMessage(ProtectMessage.prefix.getValue() + ProtectMessage.createError.getValue());
                    }
                }else{
                    player.sendMessage(ProtectMessage.prefix.getValue() + ChatColor.WHITE + "이미 소속된 마을이 있습니다!");
                }
        }
    }

    public void saveData(Player player){
        File tmp = new File(main.getDataFolder(), "region/" + player.getUniqueId() + ".yml");
        YamlConfiguration mFile = YamlConfiguration.loadConfiguration(tmp);
        mFile.set("Owner", player.getName());
        mFile.set("UUID", player.getUniqueId().toString());
        mFile.set("locX", player.getLocation().getX());
        mFile.set("locZ", player.getLocation().getZ());
        mFile.set("level", 1);
        Map<String, String> map = new HashMap<>();
        map.put(player.getUniqueId().toString(), player.getName());
        mFile.set("Players", map);
        try {
            mFile.save(tmp);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        File rankList = new File(main.getDataFolder(), "playerRank.yml");
        YamlConfiguration rankYml = YamlConfiguration.loadConfiguration(rankList);
        rankYml.set(player.getUniqueId().toString(), "OWNER");
        try {
            rankYml.save(rankList);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        protectMap.put(player.getUniqueId(), new Location(Bukkit.getWorld("world"), player.getLocation().getX(), 0, player.getLocation().getZ()));
    }
}




