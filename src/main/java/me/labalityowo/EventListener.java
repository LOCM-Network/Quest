package me.labalityowo;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.block.BlockBreakEvent;
import cn.nukkit.event.block.BlockPlaceEvent;
import cn.nukkit.event.inventory.CraftItemEvent;
import cn.nukkit.event.player.PlayerJoinEvent;
import cn.nukkit.item.Item;
import cn.nukkit.utils.TextFormat;
import com.google.common.base.Strings;
import me.labalityowo.database.Database;
import me.labalityowo.events.ProgressUpdateEvent;
import me.labalityowo.events.QuestCompletedEvent;
import me.labalityowo.quests.QuestData;
import me.labalityowo.quests.QuestSession;
import me.labalityowo.quests.type.BreakQuest;
import me.labalityowo.quests.type.CraftQuest;
import me.labalityowo.quests.type.PlaceQuest;

import javax.xml.crypto.Data;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class EventListener implements Listener {

    public static long getDateDiff(Date date1, Date date2, TimeUnit timeUnit) {
        long diffInMillies = date2.getTime() - date1.getTime();
        return timeUnit.convert(diffInMillies,TimeUnit.MILLISECONDS);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event){
        Player player = event.getPlayer();
        String uuid = player.getUniqueId().toString();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date today = new Date();
        if(Quest.getInstance().getSession(uuid) == null){
            Database.execute("SELECT COUNT(*) FROM questClaimedLog WHERE uuid='" + uuid + "'", resultSet1 -> {
                while(resultSet1.next()){
                    if(resultSet1.getInt(1) == 0){
                        //Register new quest
                        Quest.getInstance().addOrUpdateSession(player, today);
                    }else{
                        Database.execute("SELECT * FROM questClaimedLog WHERE uuid='" + uuid + "'", resultSet2 -> {
                            while (resultSet2.next()){
                                try {
                                    Date claimed = format.parse(resultSet2.getString("date"));
                                    if(getDateDiff(claimed, today, TimeUnit.DAYS) >= 1){
                                        //Register new quest and update the old date
                                        Quest.getInstance().addOrUpdateSession(player, today);
                                    }else{
                                        Database.execute("SELECT * FROM questData WHERE uuid='" + uuid + "'", resultSet3 -> {
                                            HashMap<String, QuestData> dataMap = new HashMap<String, QuestData>();
                                            while (resultSet3.next()){
                                                String questExtraInformationRaw = resultSet3.getString("questExtraInformation");
                                                ArrayList<Integer> extraInformation = Quest.getIntegerArray(new ArrayList<String>(Arrays.asList(questExtraInformationRaw.split(":"))));
                                                QuestData questData = Quest.getInstance().rawQuestDataHandling(resultSet3.getString("questType"), extraInformation, resultSet3.getInt("maxProgress"), resultSet3.getBoolean("rewardsClaimed"));
                                                questData.setProgress(resultSet3.getInt("progress"));
                                                dataMap.put(resultSet3.getString("questType"), questData);
                                            }
                                            Quest.getInstance().registerSession(player, dataMap);
                                        });
                                    }
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }

                            }
                        });
                    }
                }
            });
        }
    }

    @EventHandler
    public void onProgress(ProgressUpdateEvent event){
        Player player = event.getSession().getPlayer();
        QuestData questData = event.getQuestData();
        int progressBars = (int) (30 * event.getQuestData().getPercent());
        String popup = "{type}: " + Strings.repeat("" + "&a|", progressBars) + Strings.repeat("" + "&c|", 30 - progressBars);

        if(questData instanceof BreakQuest){
            popup = popup.replace("{type}", "Đào " + questData.getMaxProgress() + " " + ((BreakQuest) questData).getBlock().getName());
        }else if(questData instanceof PlaceQuest){
            popup = popup.replace("{type}", "Đặt " + questData.getMaxProgress() + " " + ((PlaceQuest) questData).getBlock().getName());
        }else if(questData instanceof CraftQuest){
            popup = popup.replace("{type}", "Chế tạo " + questData.getMaxProgress() + " " + ((CraftQuest) questData).getItem().getName());
        }else if(event.getId().equals("diemdanh")){
            popup = popup.replace("{type}", "Điểm danh");
        }else if(event.getId().equals("online")){
            popup = popup.replace("{type}", "Hoạt động");
        }

        player.sendPopup(TextFormat.colorize(popup));
    }

    @EventHandler
    public void onComplete(QuestCompletedEvent event){
        Player player = event.getSession().getPlayer();
        QuestData questData = event.getQuestData();
        String title = "Bạn đã làm xong nhiệm vụ {type}";

        if(questData instanceof BreakQuest){
            title = title.replace("{type}", "đào " + questData.getMaxProgress() + " " + ((BreakQuest) questData).getBlock().getName());
        }else if(questData instanceof PlaceQuest){
            title = title.replace("{type}", "đặt " + questData.getMaxProgress() + " " + ((PlaceQuest) questData).getBlock().getName());
        }else if(questData instanceof CraftQuest){
            title = title.replace("{type}", "chế tạo " + questData.getMaxProgress() + " " + ((CraftQuest) questData).getItem().getName());
        }else if(event.getId().equals("diemdanh")){
            title = title.replace("{type}", "điểm danh");
        }else if(event.getId().equals("online")){
            title = title.replace("{type}", "hoạt động");
        }

        player.sendTitle(title);
    }

    @EventHandler

    public void onBreak(BlockBreakEvent event){
        if(event.isCancelled()) return;
        Player player = event.getPlayer();
        Block block = event.getBlock();
        QuestSession session = Quest.getInstance().getSession(player.getUniqueId().toString());
        if(!session.isCompletedRequirements()) return;
        BreakQuest breakQuest = (BreakQuest) session.getQuest("break");
        BreakQuest breakQuest1 = (BreakQuest) session.getQuest("break1");
        if(breakQuest != null && !breakQuest.isCompleted() && breakQuest.getBlock().getId() == block.getId() && breakQuest.getBlock().getDamage() == block.getDamage()){
            breakQuest.setProgress(breakQuest.getProgress() + 1, session, "break");
        }
        if(breakQuest1 != null && !breakQuest1.isCompleted() && breakQuest1.getBlock().getId() == block.getId() && breakQuest1.getBlock().getDamage() == block.getDamage()){
            breakQuest1.setProgress(breakQuest1.getProgress() + 1, session, "break1");
        }
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent event){
        if(event.isCancelled()) return;
        Player player = event.getPlayer();
        Block block = event.getBlock();
        QuestSession session = Quest.getInstance().getSession(player.getUniqueId().toString());
        if(!session.isCompletedRequirements()) return;
        PlaceQuest placeQuest = (PlaceQuest) session.getQuest("place");
        PlaceQuest placeQuest1 = (PlaceQuest) session.getQuest("place1");
        if(placeQuest != null && !placeQuest.isCompleted() && placeQuest.getBlock().getId() == block.getId() && placeQuest.getBlock().getDamage() == block.getDamage()){
            placeQuest.setProgress(placeQuest.getProgress() + 1, session, "place");
        }
        if(placeQuest1 != null && !placeQuest1.isCompleted() && placeQuest1.getBlock().getId() == block.getId() && placeQuest1.getBlock().getDamage() == block.getDamage()){
            placeQuest1.setProgress(placeQuest1.getProgress() + 1, session, "place1");
        }
    }

    @EventHandler
    public void onCraft(CraftItemEvent event){
        if(event.isCancelled()) return;
        Player player = event.getPlayer();
        Item item = event.getTransaction().getPrimaryOutput();
        QuestSession session = Quest.getInstance().getSession(player.getUniqueId().toString());
        if(!session.isCompletedRequirements()) return;
        CraftQuest craftQuest = (CraftQuest) session.getQuest("craft");
        CraftQuest craftQuest1 = (CraftQuest) session.getQuest("craft1");
        if(craftQuest != null && !craftQuest.isCompleted() && craftQuest.getItem().getId() == item.getId() && craftQuest.getItem().getDamage() == item.getDamage()){
            craftQuest.setProgress(craftQuest.getProgress() + 1, session, "craft");
        }
        if(craftQuest1 != null && !craftQuest1.isCompleted() && craftQuest1.getItem().getId() == item.getId() && craftQuest1.getItem().getDamage() == item.getDamage()){
            craftQuest1.setProgress(craftQuest1.getProgress() + 1, session, "craft1");
        }
    }
}
