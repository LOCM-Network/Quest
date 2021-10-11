package me.labalityowo.events;

import cn.nukkit.event.Event;
import cn.nukkit.event.HandlerList;
import me.labalityowo.quests.QuestSession;

abstract public class QuestEvent extends Event{

    public static HandlerList handlerList = new HandlerList();

    private String  quest;

    private QuestSession questSession;

    public QuestEvent(QuestSession questSession, String questId){
        this.questSession = questSession;
        this.quest = questId;
    }

    public String getQuestId() {
        return quest;
    }

    public QuestSession getQuestSession() {
        return questSession;
    }

    public static HandlerList getHandlers(){
        return handlerList;
    }
}
