package cn.yzq25.vip;

import cn.nukkit.AdventureSettings;
import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockID;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.EventPriority;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerLoginEvent;
import cn.nukkit.event.player.PlayerTeleportEvent;
import cn.nukkit.level.Position;
import cn.nukkit.utils.TextFormat;
import cn.yzq25.vip.event.vip.AddVipEvent;
import cn.yzq25.vip.event.vip.RemoveVipEvent;

/**
 * Created by Yanziqing25
 */
public class EventListener implements Listener {
    private VIPMain mainclass = VIPMain.getInstance();

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onAddVip(AddVipEvent event) {
        Player player = event.getPlayer();
        if (player.isOnline()) {
            if (event.getTime() != 0) {
                player.sendMessage(TextFormat.GRAY + "您已成功开通" + event.getType().toString() + event.getTime() + "天!");
            } else {
                player.sendMessage(TextFormat.GRAY + "您已成功开通永久" + event.getType().toString() + "!");
            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onRemoveVip(RemoveVipEvent event) {
        Player player = event.getPlayer();
        if (player.isOnline()) {
            player.getAdventureSettings().set(AdventureSettings.Type.ALLOW_FLIGHT, false);
            player.getAdventureSettings().update();
            player.sendMessage(TextFormat.GRAY + "您已不是VIP!");

            //马上落地
            Position playerPosition = player.getPosition();

            Position downPosition = playerPosition.clone();
            downPosition.y = playerPosition.y - 1;
            Block downBlock = downPosition.getLevelBlock();

            int limit = 64;
            if (downBlock.getId() == BlockID.AIR) {
                while (playerPosition.getLevel().getBlock(downBlock).getId() == BlockID.AIR) {
                    if (limit == 0) break;
                    downBlock.y--;
                    limit--;
                }
                Position pPos = downBlock.clone();
                pPos.y = downBlock.y + 1;
                player.teleport(pPos);
            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        Player player = event.getPlayer();
        if (player.getGamemode() == 1) {
            return;
        }
        for (String world : mainclass.allowFlightWorldsList) {
            if (!player.getLevel().getName().equals(world)) {
                player.getAdventureSettings().set(AdventureSettings.Type.ALLOW_FLIGHT, false);
                player.getAdventureSettings().update();
                player.sendTip(TextFormat.RED + "这个世界不允许飞行!");
            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onPlayerLogin(PlayerLoginEvent event) {
        Player player = event.getPlayer();
        if (player.getGamemode() == 1) {
            return;
        }
        for (String world : mainclass.allowFlightWorldsList) {
            if (!player.getLevel().getName().equals(world)) {
                player.getAdventureSettings().set(AdventureSettings.Type.ALLOW_FLIGHT, false);
                player.getAdventureSettings().update();
                player.sendTip(TextFormat.RED + "这个世界不允许飞行!");
            }
        }
    }
}
