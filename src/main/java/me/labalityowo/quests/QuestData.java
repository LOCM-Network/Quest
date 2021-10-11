package me.labalityowo.quests;

import cn.nukkit.Server;
import me.labalityowo.events.QuestFinishEvent;

public class QuestData{

    private int progress;

    private int maxProgress;

    private boolean rewardClaimed;

    public QuestData(int progress, int maxProgress, boolean rewardClaimed){
        this.progress = progress;
        this.maxProgress = maxProgress;
        this.rewardClaimed = rewardClaimed;
    }

    public QuestData(int progress, int maxProgress){
        this.progress = progress;
        this.maxProgress = maxProgress;
        this.rewardClaimed = false;
    }

    public boolean isRewardClaimed() {
        return rewardClaimed;
    }

    public int getProgress() {
        return progress;
    }

    public boolean isComplete() {
        return getProgress() >= getMaxProgress();
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public void setProgress(int progress, QuestSession session, String id) {
        setProgress(progress);
        if(isComplete()) Server.getInstance().getPluginManager().callEvent(new QuestFinishEvent(session, id));
    }

    public int getMaxProgress() {
        return maxProgress;
    }

    public void setRewardClaimed(boolean rewardClaimed) {
        this.rewardClaimed = rewardClaimed;
    }
}
