package me.labalityowo.quests;

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
       return (float) getProgress() / getMaxProgress();
    }

    public boolean isCompleted(){
        return getProgress() >= getMaxProgress();
    }

    public void setProgress(int progress){
        this.progress = progress;
    }
}
