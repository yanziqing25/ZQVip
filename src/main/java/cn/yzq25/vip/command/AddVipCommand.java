package cn.yzq25.vip.command;

import cn.nukkit.command.Command;
import cn.nukkit.command.CommandExecutor;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.PluginCommand;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.utils.TextFormat;
import cn.yzq25.vip.VIPMain;
import cn.yzq25.vip.VIPType;

import java.util.LinkedHashMap;

/**
 * Created by Yanziqing25
 */
public class AddVipCommand extends PluginCommand<VIPMain> implements CommandExecutor {

    public AddVipCommand() {
        super("addvip", VIPMain.getInstance());
        this.setExecutor(this);
        this.setCommandParameters(new LinkedHashMap<String, CommandParameter[]>(){{put("default", new CommandParameter[]{new CommandParameter("玩家名称", CommandParamType.STRING, false), new CommandParameter("时长(天)", CommandParamType.INT, true)});}});
        this.setAliases(new String[]{"av", "添加vip"});
        this.setPermission("vip.command.addvip");
        this.setDescription("添加VIP命令");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 1) {
            if (getPlugin().addVip(getPlugin().getServer().getOfflinePlayer(args[0]).getPlayer(), VIPType.vip)) {
                sender.sendMessage(TextFormat.GREEN + "已成功开通玩家[" + args[0] + "]" + "永久VIP!");
                return true;
            } else {
                sender.sendMessage(TextFormat.RED + "开通玩家[" + args[0] + "]" + "永久VIP失败!");
                return true;
            }
        } else if (args.length == 2) {
            long time;
            try {
                time = Long.valueOf(args[1]);
            } catch (Exception e) {
                time = 1L;
                sender.sendMessage(TextFormat.RED + "天数必须为整数!现已默认为1天");
            }
            if (time == 0) {
                sender.sendMessage(TextFormat.RED + "天数不能为0!");
                return true;
            }
            if (getPlugin().addVip(getPlugin().getServer().getOfflinePlayer(args[0]).getPlayer(), time, VIPType.vip)) {
                sender.sendMessage(TextFormat.GREEN + "已成功开通玩家[" + args[0] + "]" + time + "天VIP!");
            } else {
                sender.sendMessage(TextFormat.RED + "开通玩家[" + args[0] + "]" + time + "天VIP失败!");
            }
        }
        return true;
    }
}
