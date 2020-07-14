package cn.yzq25.vip.event.vip;

import cn.nukkit.Player;
import cn.nukkit.event.Cancellable;
import cn.nukkit.event.HandlerList;
import cn.yzq25.vip.event.VIPPluginEvent;

/**
 * Created by Yanziqing25
 */
public class RemoveVipEvent extends VIPPluginEvent implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private Player player;

    public RemoveVipEvent (Player player) {
        this.player = player;
    }

    public static HandlerList getHandlers() {
        return handlers;
    }

    public Player getPlayer() {
        return this.player;
    }
}
