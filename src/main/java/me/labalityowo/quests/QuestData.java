package me.labalityowo.quests;

import cn.nukkit.Server;
import me.labalityowo.events.ProgressUpdateEvent;
import me.labalityowo.events.QuestCompletedEvent;

public class QuestData{

    private int progress;
    private int maxProgress;

    private boolean rewardCollected;

    public QuestData(int progress, int maxProgress, boolean rewardCollected){
        this.progress = progress;
        this.maxProgress = maxProgress;
        this.rewardCollected = rewardCollected;
    }

    public int getProgress() {
        return progress;
    }

    public int getMaxProgress() {
        return maxProgress;
    }

    public boolean isRewardCollected() {
        return rewardCollected;
    }

    public float getPercent(){
       return (float) (getProgress() == 0 ? 1 : getProgress()) / getMaxProgress();
    }

    public boolean isCompleted(){
        return getProgress() >= getMaxProgress();
    }

    public void setProgress(int progress){
        this.progress = progress;
    }

    public void setProgress(int progress, QuestSession session, String id){
        this.progress = progress;
        Server.getInstance().getPluginManager().callEvent(new ProgressUpdateEvent(session, this, id));
        if(isCompleted()){
            Server.getInstance().getPluginManager().callEvent(new QuestCompletedEvent(session, this, id));
        }
    }

    public void setRewardCollected(boolean claimed){
        this.rewardCollected = claimed;
    }
}
