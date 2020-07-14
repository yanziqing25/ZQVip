package cn.yzq25.vip.task;

import cn.nukkit.AdventureSettings;
import cn.nukkit.Player;
import cn.nukkit.scheduler.PluginTask;
import cn.yzq25.vip.VIPMain;
import cn.yzq25.vip.VIPType;

import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

/**
 * Created by Yanziqing25
 */
public class CheckVIPFlightStatusTask extends PluginTask<VIPMain> {

    public CheckVIPFlightStatusTask(VIPMain plugin) {
        super(plugin);
    }

    @Override
    public void onRun(int currentTick) {
        Map<UUID, Player> players = getOwner().getServer().getOnlinePlayers();
        Iterator<Map.Entry<UUID, Player>> iterator = players.entrySet().iterator();
        while (iterator.hasNext()) {
            Player player = iterator.next().getValue();
            if (!getOwner().isVip(player, VIPType.svip)) {
                if (player.getGamemode() == 1) {
                    return;
                }
                player.getAdventureSettings().set(AdventureSettings.Type.ALLOW_FLIGHT, false);
                player.getAdventureSettings().update();
                //TODO: 马上落地
            }
        }
    }
}
