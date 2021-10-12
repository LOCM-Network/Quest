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

import java.util.HashMap;

public class QuestForm {

    public static void sendMain(Player player){
        SimpleForm form = new SimpleForm();
        form.setTitle("Quest");
        QuestSession session = Quest.getInstance().getSession(player.getUniqueId().toString());
        HashMap<String, QuestData> questMaps;
        if(true){
            questMaps = session.getQuestMap();
        }else{
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
            Button button = new Button(name, (player1, button1) -> {
                sendQuestForm();
            });
            form.addButton(button);
        });
        form.send(player);
    }

    public static void sendQuestForm(){
        SimpleForm form = new SimpleForm();
    }
}
