package me.labalityowo.quests.type;

import cn.nukkit.block.Block;

public class PlaceQuest extends BlockQuest{

    public PlaceQuest(int progress, int maxProgress, boolean rewardCollected, Block block) {
        super(progress, maxProgress, rewardCollected, block);
    }
}
