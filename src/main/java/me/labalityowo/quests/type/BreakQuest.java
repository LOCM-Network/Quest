package me.labalityowo.quests.type;

import cn.nukkit.block.Block;

public class BreakQuest extends BlockQuest{
    public BreakQuest(int progress, int maxProgress, boolean rewardCollected, Block block) {
        super(progress, maxProgress, rewardCollected, block);
    }
}
