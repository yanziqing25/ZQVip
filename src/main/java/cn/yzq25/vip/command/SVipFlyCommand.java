package cn.yzq25.vip.command;

import cn.nukkit.AdventureSettings;
import cn.nukkit.Player;
import cn.nukkit.command.*;
import cn.nukkit.utils.TextFormat;
import cn.yzq25.vip.VIPMain;
import cn.yzq25.vip.VIPType;

import java.util.HashMap;

/**
 * Created by Yanziqing25
 */
public class SVipFlyCommand extends PluginCommand<VIPMain> implements CommandExecutor {

    public SVipFlyCommand() {
        super("fly", VIPMain.getInstance());
        this.setExecutor(this);
        this.setCommandParameters(new HashMap());
        this.setAliases(new String[]{"飞行"});
        this.setPermission("vip.command.svip.fly");
        this.setDescription("SVIP飞行命令");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof ConsoleCommandSender) {
            sender.sendMessage(TextFormat.RED + "请在游戏中使用此命令!");
            return false;
        }
        Player player = (Player) sender;
        if (!getPlugin().isVip(player, VIPType.svip)) {
            player.sendTip(TextFormat.RED + "您还不是SVIP!");
            return true;
        }
        for (String world : getPlugin().allowFlightWorldsList) {
            if (player.getLevel().getName().equals(world)) {
                player.getAdventureSettings().set(AdventureSettings.Type.ALLOW_FLIGHT, true);
                player.getAdventureSettings().update();
                return true;
            } else {
                player.sendTip(TextFormat.RED + "这个世界不允许飞行!");
                return true;
            }
        }
        return false;
    }
}
