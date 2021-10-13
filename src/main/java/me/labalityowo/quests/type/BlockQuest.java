package me.labalityowo.quests.type;

import cn.nukkit.block.Block;
import me.labalityowo.quests.QuestData;

public class BlockQuest extends QuestData {

    private Block block;

    public BlockQuest(int progress, int maxProgress, boolean rewardCollected, Block block) {
        super(progress, maxProgress, rewardCollected);
        this.block = block;
    }

    public Block getBlock() {
        return block;
    }
}
