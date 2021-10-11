package me.labalityowo.form;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.command.ConsoleCommandSender;
import cn.nukkit.item.Item;
import cn.nukkit.utils.TextFormat;
import com.google.common.base.Strings;
import me.labalityowo.Quest;
import me.labalityowo.config.QuestConfig;
import me.labalityowo.quests.QuestData;
import me.labalityowo.quests.QuestSession;
import ru.contentforge.formconstructor.form.SimpleForm;
import ru.contentforge.formconstructor.form.element.Button;

public class QuestForm {

    public static void sendMain(Player player){
        SimpleForm form = new SimpleForm();
        form.setTitle("Quest");
        form.setContent("Quest list");
        QuestSession session = Quest.getInstance().getSession(player.getUniqueId().toString());
        session.getQuests().forEach((id, questData) -> {
            QuestConfig questConfig = Quest.getInstance().getQuestById(id);
            form.addButton(new Button(questConfig.getName() + "\n" + (questData.isComplete() ? "XONG" : ""), (p, button) -> sendQuestForm(session, id, questConfig, questData)));
        });
        form.send(player);
    }

    public static void sendQuestForm(QuestSession session, String id, QuestConfig questConfig, QuestData questData){
        SimpleForm form = new SimpleForm();
        form.setTitle(questConfig.getName());
        String description = TextFormat.colorize("Description: " + questConfig.getDescription().replace("{progress}", String.valueOf(questData.getMaxProgress())) + "\nProgress: ");

        if(questData.getProgress() == 0){
            description += TextFormat.colorize(Strings.repeat("&c|", 30));
        }else{
            int progressBars = (int) (30 / (questData.getProgress() / questData.getMaxProgress()));
            System.out.println(progressBars);
            description += TextFormat.colorize(Strings.repeat("&a|", progressBars) + Strings.repeat("&c|", 30 - progressBars));
        }

        form.setContent(description);
        if(!questData.isComplete() && id.equals("diemdanh")){
            form.addButton(new Button("Điểm danh", (sender, button) -> questData.setProgress(1, session, id)));
        }

        if(questData.isComplete() && !questData.isRewardClaimed()){
            form.addButton(new Button("Nhận quà", (sender, button) -> {
                for (Item item : questConfig.getItemsReward()) {
                    if(!sender.getInventory().canAddItem(item)){
                        sender.sendMessage("Ko đủ chỗ, thử lại lần sau");
                        return;
                    }
                }
                questConfig.getItemsReward().forEach(item -> sender.getInventory().addItem(item));
                questData.setRewardClaimed(true);
                for (String command : questConfig.getCommandsReward()) {
                    Server.getInstance().dispatchCommand(new ConsoleCommandSender(), command.replace("{player}", sender.getName()));
                }
                sender.sendMessage("Nhận quà");
            }));
        }

        form.send(session.getPlayer());
    }
}
