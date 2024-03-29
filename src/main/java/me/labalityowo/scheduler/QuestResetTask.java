package me.labalityowo.scheduler;

import cn.nukkit.Server;
import cn.nukkit.scheduler.Task;
import me.labalityowo.Quest;

import java.util.Date;

public class QuestResetTask extends Task {
    @Override
    public void onRun(int i) {
        Server.getInstance().getOnlinePlayers().forEach((uuid, player) -> {
            Quest.getInstance().addOrUpdateSession(player, new Date());
        });
    }
}
