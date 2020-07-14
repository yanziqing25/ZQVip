package cn.yzq25.vip.command;

import cn.nukkit.command.Command;
import cn.nukkit.command.CommandExecutor;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.PluginCommand;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.utils.TextFormat;
import cn.yzq25.vip.VIPMain;

import java.util.LinkedHashMap;

/**
 * Created by Yanziqing25
 */
public class RemoveVipCommand extends PluginCommand<VIPMain> implements CommandExecutor {

    public RemoveVipCommand() {
        super("removevip", VIPMain.getInstance());
        this.setExecutor(this);
        this.setCommandParameters(new LinkedHashMap<String, CommandParameter[]>(){{put("default", new CommandParameter[]{new CommandParameter("玩家名称", CommandParamType.STRING, false)});}});
        this.setAliases(new String[]{"rv", "移除vip"});
        this.setPermission("vip.command.removevip");
        this.setDescription("删除VIP命令");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (getPlugin().removeVip(getPlugin().getServer().getOfflinePlayer(args[0]).getPlayer())) {
            sender.sendMessage(TextFormat.GREEN + "已删除玩家[" + args[0] + "]" + "的VIP!");
            return true;
        } else {
            sender.sendMessage(TextFormat.RED + "删除玩家[" + args[0] + "]" + "VIP失败!");
            return false;
        }
    }
}
