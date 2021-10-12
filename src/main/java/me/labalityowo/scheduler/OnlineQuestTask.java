package me.labalityowo.scheduler;

import cn.nukkit.Server;
import cn.nukkit.scheduler.Task;
import me.labalityowo.Quest;
import me.labalityowo.quests.QuestData;
import me.labalityowo.quests.QuestSession;

public class OnlineQuestTask extends Task {
    @Override
    public void onRun(int i) {
        Server.getInstance().getOnlinePlayers().forEach((uuid, player) -> {
            QuestSession session = Quest.getInstance().getSession(uuid.toString());
            QuestData quest = session.getQuestMap().get("online");
            if(quest.isCompleted()){
                return;
            }
            quest.setProgress(quest.getProgress() + 1);
        });
    }
}
