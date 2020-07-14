package cn.yzq25.vip.event.vip;

import cn.nukkit.Player;
import cn.nukkit.event.Cancellable;
import cn.nukkit.event.HandlerList;
import cn.yzq25.vip.VIPType;
import cn.yzq25.vip.event.VIPPluginEvent;

/**
 * Created by Yanziqing25
 */
public class AddVipEvent extends VIPPluginEvent implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private Player player;
    private long time;
    private VIPType type;

    public AddVipEvent (Player player, VIPType type) {
        this.player = player;
        this.time = 0;
        this.type = type;
    }

    public AddVipEvent (Player player, long time, VIPType type) {
        this.player = player;
        this.time = time;
        this.type = type;
    }

    public static HandlerList getHandlers() {
        return handlers;
    }

    public Player getPlayer() {
        return this.player;
    }

    public long getTime() {
        return this.time;
    }

    public VIPType getType() {
        return this.type;
    }
}
