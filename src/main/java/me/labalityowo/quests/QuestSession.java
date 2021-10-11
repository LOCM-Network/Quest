package me.labalityowo.quests;

import cn.nukkit.Player;

import java.util.Date;
import java.util.HashMap;

public class QuestSession {



    private Player player;
    private HashMap<String, QuestData> quests;
    private Date date;

    public QuestSession(Player player, HashMap<String, QuestData> quests, Date date){
        this.player = player;
        this.quests = quests;
        this.date = date;
    }

    public Date getDate() {
        return date;
    }

    public HashMap<String, QuestData> getQuests() {
        return quests;
    }

    public Player getPlayer() {
        return player;
    }

}
