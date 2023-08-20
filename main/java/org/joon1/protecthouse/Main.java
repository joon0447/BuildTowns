package org.joon1.protecthouse;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.joon1.protecthouse.Command.*;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.UUID;


public final class Main extends JavaPlugin implements Listener {

    static public File file;
    static public File playerRank;
    static public File protectList;
    static public File config;
    static public ItemStack townTicket;

    final public int townBytown = getConfig().getInt("마을과의 최소 거리");
    final public int spawnLimit = getConfig().getInt("스폰과의 최소 거리");
    final public int upCreate = getConfig().getInt("마을 생성 비용");
    final public int upTwo = getConfig().getInt("마을 2단계 비용");
    final public int upThree = getConfig().getInt("마을 3단계 비용");
    final public int upFour = getConfig().getInt("마을 4단계 비용");
    @Override
    public void onEnable() {
        createFile();
        loadFile();
        ticketStack();
        getCommand("마을").setExecutor(new ProtectMenu(this));
        getCommand("town").setExecutor(new ProtectMenu(this));
        getCommand("TOWN").setExecutor(new ProtectMenu(this));
        getCommand("tjoin").setExecutor(new ProtectJoinCommand(this));
        getCommand("tremove").setExecutor(new ProtectRemoveCommand(this));
        getCommand("tdestroy").setExecutor(new ProtectDestroyCommand(this));
        getCommand("tc").setExecutor(new ProtectChatCommand(this));
        getCommand("TC").setExecutor(new ProtectChatCommand(this));
        getCommand("마을채팅").setExecutor(new ProtectChatCommand(this));
        getCommand("bta").setExecutor(new ProtectAdminCommand(this));
        getCommand("BTA").setExecutor(new ProtectAdminCommand(this));

        Bukkit.getPluginManager().registerEvents(new ProtectLocation(this), this);
        Bukkit.getPluginManager().registerEvents(new ProtectListener(this), this);
        Bukkit.getPluginManager().registerEvents(new ProtectMenuListener(this),this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }



    public void loadFile(){
        File files[] = file.listFiles();
        for(File f : files){   // 보호 구역 불러오기
            YamlConfiguration yc = YamlConfiguration.loadConfiguration(f);
            UUID uuid = UUID.fromString(yc.getString("UUID"));
            double x = yc.getDouble("locX");
            double z = yc.getDouble("locZ");
            ProtectLocation.protectMap.put(uuid, new Location(Bukkit.getWorld("world"),x,0,z));
        }
        System.out.println("[BuildTowns] 보호 구역을 성공적으로 불러왔습니다!");
    }

    public void createFile(){
        file = new File(getDataFolder(), "region");
        if(!file.exists()){
            file.mkdirs();
        }

        playerRank = new File(getDataFolder(), "playerRank.yml");
        YamlConfiguration rankYml = YamlConfiguration.loadConfiguration(playerRank);
        try{
            rankYml.save(playerRank);
        }catch (IOException e){
            throw new RuntimeException(e);
        }

        protectList = new File(getDataFolder(), "protectList.yml");
        YamlConfiguration protectYml = YamlConfiguration.loadConfiguration(protectList);
        try{
            protectYml.save(protectList);
        }catch (IOException e){
            throw new RuntimeException(e);
        }

        getConfig().options().copyDefaults();
        saveDefaultConfig();
    }
    public void ticketStack(){
        townTicket = new ItemStack(Material.PAPER);
        ItemMeta townTicketMeta = townTicket.getItemMeta();
        townTicketMeta.setDisplayName(ChatColor.BOLD + "마을 생성권");
        townTicketMeta.setLore(Arrays.asList(ChatColor.WHITE + "손에 들면 마을의 범위가 표시되며, 우클릭 시 마을이 생성됩니다.", ChatColor.WHITE + "주변에 마을이 있으면 생성이 불가능합니다."));
        townTicket.setItemMeta(townTicketMeta);
    }
}

