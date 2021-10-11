package me.labalityowo.events;

import me.labalityowo.quests.QuestSession;

public class QuestFinishEvent extends QuestEvent{

    public QuestFinishEvent(QuestSession questSession, String questId) {
        super(questSession, questId);
    }
}
