package me.labalityowo.quests;

import cn.nukkit.Player;
import me.labalityowo.Quest;

import java.util.HashMap;

public class QuestSession {

    private Player player;
    private HashMap<String, QuestData> questMap;

    public QuestSession(Player player, HashMap<String, QuestData> questMap){
        this.player = player;
        this.questMap = questMap;
    }

    public HashMap<String, QuestData> getQuestMap() {
        return questMap;
    }

    public QuestData getQuest(String id){
        return questMap.get(id);
    }

    public Player getPlayer() {
        return player;
    }

    public boolean isCompletedRequirements(){
        return (questMap.get("diemdanh").isCompleted() && questMap.get("online").isCompleted());
    }
}
