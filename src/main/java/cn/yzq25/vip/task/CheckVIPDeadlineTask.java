package cn.yzq25.vip.task;

import cn.nukkit.AdventureSettings;
import cn.nukkit.Player;
import cn.nukkit.scheduler.PluginTask;
import cn.yzq25.utils.ZQUtils;
import cn.yzq25.vip.VIPMain;

import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

/**
 * Created by Yanziqing25
 */
public class CheckVIPDeadlineTask extends PluginTask<VIPMain> {

    public CheckVIPDeadlineTask(VIPMain plugin) {
        super(plugin);
    }

    @Override
    public void onRun(int currentTick) {
        if (getOwner().getAllVipPlayersSet().isEmpty()) {
            return;
        }

        Map<UUID, Player> players = getOwner().getServer().getOnlinePlayers();
        Iterator<Map.Entry<UUID, Player>> iterator = players.entrySet().iterator();
        while (iterator.hasNext()) {
            Player player = iterator.next().getValue();
            Date date = getOwner().getVipFinishTime(getOwner().getServer().getOfflinePlayer(player.getName()).getPlayer());
            if (date != null && ZQUtils.getDateTime().getTime() >= date.getTime()) {
                getOwner().removeVip(getOwner().getServer().getOfflinePlayer(player.getName()).getPlayer());
            }
        }
    }
}