package me.labalityowo.events;

import me.labalityowo.quests.QuestData;
import me.labalityowo.quests.QuestSession;

public class QuestCompletedEvent extends QuestEvent{
    public QuestCompletedEvent(QuestSession session, QuestData questData, String id) {
        super(session, questData, id);
    }
}
