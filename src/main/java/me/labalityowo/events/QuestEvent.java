package me.labalityowo.events;

import cn.nukkit.event.Event;
import cn.nukkit.event.HandlerList;
import me.labalityowo.quests.QuestData;
import me.labalityowo.quests.QuestSession;

public class QuestEvent extends Event {

    private String id;
    private QuestData questData;
    private QuestSession session;

    public static HandlerList handlerList = new HandlerList();

    public QuestEvent(QuestSession session, QuestData questData, String id){
        this.session = session;
        this.questData = questData;
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public QuestData getQuestData() {
        return questData;
    }

    public QuestSession getSession() {
        return session;
    }

    public static HandlerList getHandlers(){
        return handlerList;
    }
}
