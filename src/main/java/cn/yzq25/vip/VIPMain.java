package cn.yzq25.vip;

import cn.nukkit.Player;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.TextFormat;
import cn.yzq25.extension.ExtensionMain;
import cn.yzq25.extension.MySQLDatabase;
import cn.yzq25.extension.RelationalDatabase;
import cn.yzq25.extension.SQLServerDatabase;
import cn.yzq25.utils.ZQUtils;
import cn.yzq25.vip.command.*;
import cn.yzq25.vip.event.vip.AddVipEvent;
import cn.yzq25.vip.event.vip.RemoveVipEvent;
import cn.yzq25.vip.provider.Local;
import cn.yzq25.vip.task.CheckVIPDeadlineTask;
import cn.yzq25.vip.task.CheckVIPFlightStatusTask;

import java.io.File;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.*;

/**
 * Created by Yanziqing25
 */
public class VIPMain extends PluginBase {
    private static VIPMain instance;
    public String vipDataFolder;
    public String svipDataFolder;
    public List<String> allowFlightWorldsList;
    private String mode;
    private Local local;
    public RelationalDatabase database;

    public VIPMain() {
    }

    public static VIPMain getInstance() {
        return instance;
    }

    @Override
    public void onLoad() {
        instance = this;
        this.vipDataFolder = getDataFolder().getPath() + "/vip/";
        this.svipDataFolder = getDataFolder().getPath() + "/svip/";
        saveDefaultConfig();
    }

    @Override
    public void onEnable() {
        if (getConfig().getBoolean("check_update", true)) {
            ZQUtils.checkPluginUpdate(this);
        }
        this.allowFlightWorldsList = getConfig().getList("allow-flight-worlds");
        if (ExtensionMain.getDatabase() == null) {
            this.mode = "local";
            this.local = new Local();
        } else {
            this.mode = ExtensionMain.getDatabase().getName();
        }
        switch (this.mode) {
            case "MySQL":
                this.database = (MySQLDatabase) ExtensionMain.getDatabase();
                break;
            case "SQLServer":
                this.database = (SQLServerDatabase) ExtensionMain.getDatabase();
                break;
        }
        createVipTable();
        getServer().getCommandMap().register("VIP", new AddVipCommand(), "addvip");
        getServer().getCommandMap().register("VIP", new AddSVipCommand(), "addsvip");
        getServer().getCommandMap().register("VIP", new RemoveVipCommand(), "removevip");
        getServer().getCommandMap().register("VIP", new SVipFlyCommand(), "fly");
        getServer().getPluginManager().registerEvents(new EventListener(), this);
        getServer().getScheduler().scheduleRepeatingTask(new CheckVIPFlightStatusTask(this), 1200, true);
        getServer().getScheduler().scheduleRepeatingTask(new CheckVIPDeadlineTask(this), 1200, true);
        getLogger().info(TextFormat.GREEN + "插件加载成功! By:Yanziqing25");
    }

    @Override
    public void onDisable() {
        getLogger().info(TextFormat.RED + "插件已关闭!");
    }

    public Map<String, String> getVipInfo(String player) {
        ResultSet rs;
        Map<String, String> playerInfo = new HashMap<>();
        switch (this.mode) {
            case "MySQL":
                rs = this.database.executeQuery("SELECT * FROM `vip_user` INNER JOIN `user` ON `vip_user`.`user_id` = `user`.`id` AND `user`.`username` = '" + player.toLowerCase() + "';");
                try {
                    ResultSetMetaData metadatas = rs.getMetaData();
                    while (rs.next()) {
                        for (int i=1;i<=metadatas.getColumnCount();i++) {
                            playerInfo.put(metadatas.getColumnLabel(i), rs.getString(i));
                        }
                    }
                    return playerInfo;
                } catch (SQLException e) {
                    e.printStackTrace();
                    return new HashMap<>();
                }
            case "SQLServer":
                rs = this.database.executeQuery("SELECT * FROM [vip_user] WHERE [user_id] = (SELECT [id] FROM [user] WHERE [username] = '" + player.toLowerCase() + "');");
                try {
                    ResultSetMetaData metadatas = rs.getMetaData();
                    while (rs.next()) {
                        for (int i=1;i<=metadatas.getColumnCount();i++) {
                            playerInfo.put(metadatas.getColumnLabel(i), rs.getString(i));
                        }
                    }
                    return playerInfo;
                } catch (SQLException e) {
                    e.printStackTrace();
                    return new HashMap<>();
                }
            case "local":
                return local.getVipInfo(player);
            default:
                return new HashMap<>();
        }
    }

    public Date getVipFinishTime(Player player) {
        if (!isVip(player)) {
            return new Date(0);
        }
        ResultSet rs;
        switch (this.mode) {
            case "MySQL":
                rs = this.database.executeQuery("SELECT `finish_time` FROM `vip_user` INNER JOIN `user` ON `vip_user`.`user_id` = `user`.`id` AND `user`.`username` = '" + player.getName().toLowerCase() + "';");
                try {
                    if (rs.next()) {
                        return rs.getDate("finish_time");
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                    return null;
                }
            case "SQLServer":
                rs = this.database.executeQuery("SELECT [finish_time] FROM [vip_user] WHERE [user_id] = (SELECT [id] FROM [user] WHERE [username] = '" + player.getName().toLowerCase() + "');");
                try {
                    if (rs.next()) {
                        return rs.getDate("finish_time");
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                    return null;
                }
            case "local":
                return null;
            default:
                return null;
        }
    }

    public boolean isVip(Player player) {
        if (player == null) {
            return false;
        }
        ResultSet rs;
        switch (this.mode) {
            case "MySQL":
                rs = this.database.executeQuery("SELECT * FROM `vip_user` INNER JOIN `user` ON `vip_user`.`user_id` = `user`.`id` AND `user`.`username` = '" + player.getName().toLowerCase() + "';");
                try {
                    return rs.next();
                } catch (Exception e) {
                    e.printStackTrace();
                    return false;
                }
            case "SQLServer":
                rs = this.database.executeQuery("SELECT * FROM [vip_user] WHERE [user_id] = (SELECT [id] FROM [user] WHERE [username] = '" + player.getName().toLowerCase() + "');");
                try {
                    return rs.next();
                } catch (Exception e) {
                    e.printStackTrace();
                    return false;
                }
            case "local":
                return local.isVip(player.getName().toLowerCase());
            default:
                return false;
        }
    }

    public boolean isVip(Player player, VIPType type) {
        if (player == null) {
            return false;
        }
        ResultSet rs;
        switch (this.mode) {
            case "MySQL":
                rs = this.database.executeQuery("SELECT * FROM `vip_user` INNER JOIN `user` ON `vip_user`.`user_id` = `user`.`id` AND `user`.`username` = '" + player.getName().toLowerCase() + "' AND `vip_user`.`type` = '" + type.toString() + "';");
                try {
                    return rs.next();
                } catch (Exception e) {
                    e.printStackTrace();
                    return false;
                }
            case "SQLServer":
                rs = this.database.executeQuery("SELECT * FROM [vip_user] WHERE [user_id] = (SELECT [id] FROM [user] WHERE [username] = '" + player.getName().toLowerCase() + "') AND [type] = '" + type.toString() + "';");
                try {
                    return rs.next();
                } catch (Exception e) {
                    e.printStackTrace();
                    return false;
                }
            case "local":
                return false;
            default:
                return false;
        }
    }

    public boolean  addVip(Player player, VIPType type) {
        if (player == null) {
            return false;
        }
        if (isVip(player)) {
            return false;
        }
        AddVipEvent addVipEvent = new AddVipEvent(player, type);
        getServer().getPluginManager().callEvent(addVipEvent);
        if (addVipEvent.isCancelled()) {
            return false;
        }
        return addVip(player.getName(), type);
    }

    public boolean addVip(Player player, long time, VIPType type) {
        if (player == null) {
            return false;
        }
        if (isVip(player)) {
            return false;
        }
        AddVipEvent addVipEvent = new AddVipEvent(player, time, type);
        getServer().getPluginManager().callEvent(addVipEvent);
        if (addVipEvent.isCancelled()) {
            return false;
        }
        return addVip(player.getName(), time, type);
    }

    private synchronized boolean addVip(String player, VIPType type) {
        switch (this.mode) {
            case "MySQL":
                database.executeSQL("INSERT INTO `vip_user` (`user_id`, `start_time`, `type`, `status`) VALUES ((SELECT `id` FROM `user` WHERE `username` = '" + player.toLowerCase() + "'), '" + ZQUtils.transformDateTime(ZQUtils.getDateTime()) + "', '" + type.toString() + "', 'permanent');");
                return true;
            case "SQLServer":
                this.database.executeSQL("INSERT INTO [vip_user] ([user_id], [start_time], [type], [status]) VALUES ((SELECT [id] FROM [user] WHERE [username] = '" + player.toLowerCase() + "'), '" + ZQUtils.transformDateTime(ZQUtils.getDateTime()) + "', '" + type.toString() + "', 'permanent');");
                return true;
            case "local":
                return false;
            default:
                return false;
        }
    }

    private synchronized boolean addVip(String player, long time, VIPType type) {
        switch (this.mode) {
            case "MySQL":
                this.database.executeSQL("INSERT INTO `vip_user` (`user_id`, `start_time`, `finish_time`, `type`) VALUES ((SELECT `id` FROM `user` WHERE `username` = '" + player.toLowerCase() + "'), '" + ZQUtils.transformDateTime(ZQUtils.getDateTime()) + "', '" + ZQUtils.transformDateTime(new Date(ZQUtils.getDateTime().getTime() + 24L * 60 * 60 * 1000 * time)) +"', '" + type.toString() + "');");
                return true;
            case "SQLServer":
                this.database.executeSQL("INSERT INTO [vip_user] ([user_id], [start_time], [finish_time], [type]) VALUES ((SELECT [id] FROM [user] WHERE [username] = '" + player.toLowerCase() + "'), '" + ZQUtils.transformDateTime(ZQUtils.getDateTime()) + "', '" + ZQUtils.transformDateTime(new Date(ZQUtils.getDateTime().getTime() + 24L * 60 * 60 * 1000 * time)) +"', '" + type.toString() + "');");
                return true;
            case "local":
                return local.addVip(player, new Date(time));
            default:
                return false;
        }
    }

    public boolean removeVip(Player player) {
        if (player == null) {
            return false;
        }
        if (!isVip(player)) {
            return false;
        }
        RemoveVipEvent removeVipEvent = new RemoveVipEvent(player);
        getServer().getPluginManager().callEvent(removeVipEvent);
        if (removeVipEvent.isCancelled()) {
            return false;
        }
        return removeVip(player.getName());
    }

    private synchronized boolean removeVip(String player) {
        switch (this.mode) {
            case "MySQL":
                return this.database.executeSQL("DELETE FROM `vip_user` WHERE `user_id` = (SELECT `id` FROM `user` WHERE `username` = '" + player.toLowerCase() + "');");
            case "SQLServer":
                return this.database.executeSQL("DELETE FROM [vip_user] WHERE [user_id] = (SELECT [id] FROM [user] WHERE [username] = '" + player.toLowerCase() + "');");
            case "local":
                return local.removeVip(player);
            default:
                return false;
        }
    }

    public Set<String> getAllVipPlayersSet() {
        ResultSet rs;
        Set<String> vipSet = new HashSet<>();
        switch (this.mode) {
            case "MySQL":
                rs = this.database.executeQuery("SELECT `username` FROM `user` INNER JOIN `vip_user` ON `user`.`id` = `vip_user`.`user_id`;");
                try {
                    while (rs.next()) {
                        vipSet.add(rs.getString("username"));
                    }
                    return vipSet;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            case "SQLServer":
                rs = this.database.executeQuery("SELECT [username] FROM [user] WHERE [id] =  ANY ( SELECT [user_id] FROM [vip_user]) ;");
                try {
                    if (rs == null) {
                        return new HashSet<>();
                    }
                    while (rs.next()) {
                        vipSet.add(rs.getString("username"));
                    }
                    return vipSet;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            case "local":
                return vipSet;
            default:
                return vipSet;
        }
    }

    private boolean createVipTable() {
        switch (this.mode) {
            case "MySQL":
                return this.database.executeSQL("CREATE TABLE IF NOT EXISTS `vip_user` (" +
                        "`user_id` INT (11) UNSIGNED NOT NULL COMMENT '用户编号'," +
                        "`start_time` datetime NOT NULL COMMENT '开始时间'," +
                        "`finish_time` datetime NULL COMMENT '结束时间'," +
                        "`type` VARCHAR (255) NOT NULL COMMENT 'VIP类型'," +
                        "`status` VARCHAR (255) NULL DEFAULT 'normal' COMMENT 'VIP状态'," +
                        "PRIMARY KEY (`user_id`)," +
                        "FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE" +
                        ") ENGINE = INNODB COMMENT = 'VIP用户信息表';");
            case "SQLServer":
                return this.database.executeSQL("IF NOT EXISTS ( SELECT * FROM sysobjects WHERE name = 'vip_user' ) CREATE TABLE [vip_user] (" +
                        "[user_id] INT NOT NULL," +
                        "[start_time] datetime2 NOT NULL," +
                        "[finish_time] datetime2 NULL," +
                        "[type] VARCHAR ( 255 ) NOT NULL," +
                        "[status] VARCHAR ( 255 ) NULL DEFAULT 'normal'," +
                        "PRIMARY KEY CLUSTERED ( [user_id] ) WITH ( PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON )," +
                        "FOREIGN KEY ( [user_id] ) REFERENCES [user] ( [id] ) ON DELETE CASCADE ON UPDATE CASCADE " +
                        ")");
            case "local":
                return new File(vipDataFolder).mkdirs() && new File(svipDataFolder).mkdirs();
            default:
                return new File(vipDataFolder).mkdirs() && new File(svipDataFolder).mkdirs();
        }
    }
}