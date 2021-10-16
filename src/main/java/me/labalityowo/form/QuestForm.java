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
        form.setTitle("§a§lＬＯＣＭ §6ＱＵＥＳＴ");
        QuestSession session = Quest.getInstance().getSession(player.getUniqueId().toString());
        HashMap<String, QuestData> questMaps;
        if(session.isCompletedRequirements()){
            questMaps = session.getQuestMap();
        }else{
            questMaps = new HashMap<>();
            questMaps.put("online", session.getQuest("online"));
            questMaps.put("diemdanh", session.getQuest("diemdanh"));
        }
        questMaps.forEach((s, questData) -> {
            String name = "§l§f•§0 {type} §f•\n" + (questData.isCompleted() ? "§l§aĐã Hoàn Thành" : "§l§cChưa Hoàn Thành");
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
            }else if(s.equals("kiemxu")){
                name = name.replace("{type}", "Kiếm xu");
            }else if(s.equals("tieuxu")){
                name = name.replace("{type}", "Tiêu xu");
            }else if(s.equals("tieulcoin")){
                name = name.replace("{type}", "Tiêu Lcoin");
            }else if(s.equals("naplcoin")){
                name = name.replace("{type}", "Nạp Lcoin");
            }

            Button button = new Button(name, (player1, button1) -> sendQuestForm(player, s, questData, session));
            form.addButton(button);
        });
        form.send(player);
    }

    public static void sendQuestForm(Player player, String id, QuestData questData, QuestSession session){
        SimpleForm form = new SimpleForm();
        form.setTitle("§a§lＬＯＣＭ §6ＱＵＥＳＴ");
        String description = "&l&f⋗&l&c Nhiệm Vụ: &e: {type}\n";
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
        }else if(id.equals("kiemxu")){
            description = description.replace("{type}", "Kiếm " + questData.getMaxProgress() + " xu");
        }else if(id.equals("tieuxu")){
            description = description.replace("{type}", "Tiêu " + questData.getMaxProgress() + " xu");
        }else if(id.equals("tieulcoin")){
            description = description.replace("{type}", "Tiêu " + questData.getMaxProgress() + " Lcoin");
        }else if(id.equals("naplcoin")){
            description = description.replace("{type}", "Nạp "+ questData.getMaxProgress() + " Lcoin");
        }

        description += "&l&f⋗&l&c Tiến Độ: &e" + questData.getProgress() + "&f/&e" + questData.getMaxProgress() + "\n";

        int progressBars;

        if(questData.getMaxProgress() == 1){
            progressBars = 0;
        }else{
            progressBars = (int) (30 * questData.getPercent());
        }

        description += Strings.repeat("" + "&a|", progressBars) + Strings.repeat("" + "&c|", 30 - progressBars);
        form.setContent(TextFormat.colorize(description));
        form.setNoneHandler(player1 -> sendMain(player));

        if(questData.isCompleted() && !questData.isRewardCollected()){
            Button button = new Button("Claim", (player1, button1) -> {
                questData.setRewardCollected(true);
                QuestConfig config = Quest.getInstance().getQuestConfig(id);
                ArrayList<Item> items = new ArrayList<>(config.getItems().keySet());
                Item selectedItem = items.get((int)(Math.random() * items.size()));
                ArrayList<Integer> randomList = config.getItems().get(selectedItem);
                selectedItem.setCount((int) (Math.random() * (randomList.get(1) - randomList.get(0))) + randomList.get(0));
                if(!player.getInventory().canAddItem(selectedItem)){
                    player.sendMessage("§l§cKho đã đầy!");
                    return;
                }
                player.getInventory().addItem(selectedItem);
                String command = config.getCommands().get((int)(Math.random() * config.getCommands().size()));
                Server.getInstance().dispatchCommand(new ConsoleCommandSender(), command.replace("{player}", player.getName()));
                player.sendMessage("§l§aBạn đã nhận quà");
            });
            form.addButton(button);
        }

        if(id.equals("diemdanh") && !questData.isCompleted()){
            Button button = new Button("Complete", (player1, button1) -> {
                questData.setProgress(1, session, id);
                player.sendMessage("§l§aBạn đã điểm danh");
            });
            form.addButton(button);
        }
        form.send(player);
    }
}
