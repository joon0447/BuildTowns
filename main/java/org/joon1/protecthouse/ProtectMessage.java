package org.joon1.protecthouse;

import org.bukkit.ChatColor;

public enum ProtectMessage {

    prefix(ChatColor.YELLOW + "[시스템] " + ChatColor.WHITE ),

    townPrefix(ChatColor.YELLOW + "[마을] " + ChatColor.WHITE),
    createClose(ChatColor.WHITE + "주변에 이미 생성된 마을이 있습니다!"),
    createDone(ChatColor.WHITE + "마을 생성이 완료되었습니다!"),
    createError(ChatColor.WHITE + "알 수 없는 이유로 마을 생성에 실패하였습니다."),
    noTown(ChatColor.WHITE + "가입된 마을이 없습니다!"),
    noUpgrade(ChatColor.WHITE + "더 이상 업그레이드가 불가능합니다!"),
    completeUpgrade(ChatColor.WHITE + " 단계로 업그레이드 되었습니다."),
    failUpgrade(ChatColor.WHITE + "다이아몬드가 부족합니다!");





    private final String value;
    ProtectMessage(String value){
        this.value = value;
    }

    public String getValue(){
        return value;
    }
}
