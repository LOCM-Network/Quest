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
import me.labalityowo.quests.type.BreakQuest;
import me.labalityowo.quests.type.CraftQuest;
import me.labalityowo.quests.type.PlaceQuest;
import ru.contentforge.formconstructor.form.SimpleForm;
import ru.contentforge.formconstructor.form.element.Button;

import java.util.ArrayList;
import java.util.HashMap;

public class QuestForm {

    public static void sendMain(Player player){
        SimpleForm form = new SimpleForm();
        form.setTitle("Quest");
        QuestSession session = Quest.getInstance().getSession(player.getUniqueId().toString());
        HashMap<String, QuestData> questMaps;
        if(session.isCompletedRequirements()){
            questMaps = session.getQuestMap();
        }else{
            questMaps = new HashMap<String, QuestData>();
            session.getQuestMap().forEach((s, questData) -> {
                if(s.equals("diemdanh") || s.equals("online")){
                    questMaps.put(s, questData);
                }
            });
        }
        questMaps.forEach((s, questData) -> {
            String name = "{type}\n" + (questData.isCompleted() ? "Xong" : "Chưa xong");
            if(questData instanceof BreakQuest){
                name = name.replace("{type}", "Đào khoáng sản");
            }else if(questData instanceof PlaceQuest){
                name = name.replace("{type}", "Đặt block");
            }else if(questData instanceof CraftQuest){
                name = name.replace("{type}", "Chế tạo đồ");
            }else if(s.equals("diemdanh")){
                name = name.replace("{type}", "Điểm danh");
            }else if(s.equals("online")){
                name = name.replace("{type}", "Hoạt động");
            }
            Button button = new Button(name, (player1, button1) -> sendQuestForm(player, s, questData, session));
            form.addButton(button);
        });
        form.send(player);
    }

    public static void sendQuestForm(Player player, String id, QuestData questData, QuestSession session){
        SimpleForm form = new SimpleForm();
        form.setTitle("Quest");
        String description = "Description: {type}";
        if(questData instanceof BreakQuest){
            description = description.replace("{type}", "Đào " + questData.getMaxProgress() + " " + ((BreakQuest) questData).getBlock().getName());
        }else if(questData instanceof PlaceQuest){
            description = description.replace("{type}", "Đặt " + questData.getMaxProgress() + " " + ((PlaceQuest) questData).getBlock().getName());
        }else if(questData instanceof CraftQuest){
            description = description.replace("{type}", "Chế tạo " + questData.getMaxProgress() + " " + ((CraftQuest) questData).getItem().getName());
        }else if(id.equals("diemdanh")){
            description = description.replace("{type}", "Điểm danh");
        }else if(id.equals("online")){
            description = description.replace("{type}", "Hoạt động");
        }


        int progressBars = (int) (30 * questData.getPercent());
        description += "\nProgress: " + Strings.repeat("" + "&a|", progressBars) + Strings.repeat("" + "&c|", 30 - progressBars);
        form.setContent(TextFormat.colorize(description));
        form.setNoneHandler(player1 -> sendMain(player));

        if(questData.isCompleted() && !questData.isRewardCollected()){
            Button button = new Button("Claim", (player1, button1) -> {
                questData.setRewardCollected(true);
                QuestConfig config = Quest.getInstance().getQuestConfig(id);
                ArrayList<Item> items = new ArrayList<Item>(config.getItems().keySet());
                Item selectedItem = items.get((int)(Math.random() * items.size()));
                ArrayList<Integer> randomList = config.getItems().get(selectedItem);
                selectedItem.setCount((int) (Math.random() * (randomList.get(1) - randomList.get(0))) + randomList.get(0));
                if(!player.getInventory().canAddItem(selectedItem)){
                    player.sendMessage("Full");
                    return;
                }
                player.getInventory().addItem(selectedItem);
                String command = config.getCommands().get((int)(Math.random() * config.getCommands().size()));
                Server.getInstance().dispatchCommand(new ConsoleCommandSender(), command.replace("{player}", player.getName()));
                player.sendMessage("da nhan qua");
            });
            form.addButton(button);
        }

        if(id.equals("diemdanh") && !questData.isCompleted()){
            Button button = new Button("Complete", (player1, button1) -> {
                questData.setProgress(1, session, id);
                player.sendMessage("ban da diem danh");
            });
            form.addButton(button);
        }
        form.send(player);
    }
}
