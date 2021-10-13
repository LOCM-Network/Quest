package me.labalityowo.events;

import me.labalityowo.quests.QuestData;
import me.labalityowo.quests.QuestSession;

public class ProgressUpdateEvent extends QuestEvent{

    public ProgressUpdateEvent(QuestSession session, QuestData questData, String id) {
        super(session, questData, id);
    }
}
