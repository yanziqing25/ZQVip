package cn.yzq25.vip.provider;

import cn.nukkit.Player;
import cn.nukkit.utils.Config;
import cn.yzq25.utils.ZQUtils;
import cn.yzq25.vip.VIPMain;

import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by Yanziqing25
 */
public class Local {
    private String getVipPath(String player) {
        return VIPMain.getInstance().vipDataFolder + player.toLowerCase() + ".json";
    }

    private String getSVipPath(String player) {
        return VIPMain.getInstance().svipDataFolder + player.toLowerCase() + ".json";
    }

    private boolean saveVipInfo(String player, Map<String, String> playerinfo) {
        return new Config(getVipPath(player), Config.JSON){
            @Override
            public boolean save() {
                setAll(new LinkedHashMap<String, Object>(){
                    {
                        putAll(playerinfo);
                    }
                });
                return super.save();
            }
        }.save();
    }

    private boolean saveSVipInfo(String player, Map<String, String> playerinfo) {
        return new Config(getSVipPath(player), Config.JSON){
            @Override
            public boolean save() {
                setAll(new LinkedHashMap<String, Object>(){
                    {
                        putAll(playerinfo);
                    }
                });
                return super.save();
            }
        }.save();
    }

    public Map<String, String> getVipInfo(String player) {
        Map<String, String> playerinfo = new HashMap<>();
        new Config(getVipPath(player), Config.JSON).getAll().forEach((key, value) -> playerinfo.put(key, value.toString()));
        return playerinfo;
    }

    public boolean isVip(String player) {
        return new File(getVipPath(player)).exists();
    }

    public boolean isSVip(String player) {
        return new File(getSVipPath(player)).exists();
    }

    public boolean addVip(String player, Date time) {
        if (isVip(player) || isSVip(player)) {
            return false;
        }
        Map<String, String> playerinfo = new HashMap<>();
        Date finishtime = new Date(ZQUtils.getDateTime().getTime() + 24L * 60 * 60 * 1000 * time.getTime());
        playerinfo.put("start_time", ZQUtils.transformDateTime(ZQUtils.getDateTime()));
        playerinfo.put("finish_time", ZQUtils.transformDateTime(finishtime));
        return saveVipInfo(player, playerinfo);
    }

    public boolean addSVip(String player, Date time) {
        if (isVip(player) || isSVip(player)) {
            return false;
        }
        Map<String, String> playerinfo = new HashMap<>();
        Date finishtime = new Date(ZQUtils.getDateTime().getTime() + 24L * 60 * 60 * 1000 * time.getTime());
        playerinfo.put("start_time", ZQUtils.transformDateTime(ZQUtils.getDateTime()));
        playerinfo.put("finish_time", ZQUtils.transformDateTime(finishtime));
        return saveSVipInfo(player, playerinfo);
    }

    public boolean removeVip(String player) {
        if (isVip(player)) {
            return new File(getVipPath(player)).delete();
        } else if (isSVip(player)) {
            return new File(getSVipPath(player)).delete();
        }
        return false;
    }
}
