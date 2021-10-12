package me.labalityowo;

import cn.nukkit.Player;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerJoinEvent;
import com.google.common.base.Strings;
import me.labalityowo.database.Database;
import me.labalityowo.quests.QuestData;
import me.labalityowo.quests.QuestSession;

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
}
