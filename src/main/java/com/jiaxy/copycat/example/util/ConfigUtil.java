package com.jiaxy.copycat.example.util;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import java.util.List;

/**
 * Title: <br>
 * <p>
 * Description: <br>
 * </p>
 *
 * @author <a href=mailto:taobaorun@gmail.com>wutao</a>
 *
 * @since 2015/06/15 09:31
 */
public class ConfigUtil {

    private static final Config config = ConfigFactory.load();

    public static int getLocalMemberID(){
        return config.getInt("cluster.local");
    }

    public static int getMemberNums(){
        return config.getInt("cluster.memberNum");
    }
}
