package com.jiaxy.copycat.example.cluster.collection;

import com.jiaxy.copycat.example.cluster.ClusterUtil;
import com.jiaxy.copycat.example.util.ConfigUtil;
import net.kuujo.copycat.Copycat;
import net.kuujo.copycat.CopycatServer;
import net.kuujo.copycat.Node;
import net.kuujo.copycat.collections.AsyncMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Title:<br>
 * Desc:<br>
 * <p>
 * </p>
 *
 * @author wutao
 *
 * @since 2015/06/14 15:15
 */
public class AsyncMapExample {

    private static Logger logger = LoggerFactory.getLogger(AsyncMapExample.class);

    public static void main(String[] args) {

        Copycat copycat1 = ClusterUtil.buildCopycat(ConfigUtil.getLocalMemberID(),ConfigUtil.getMemberNums());
        logger.info("copycat "+copycat1.getClass().getCanonicalName());
        copycat1.open().thenRun(() -> {
            try {
                Thread.sleep(10000);

                logger.info("------------------i am here------------------------" + copycat1.isOpen());
                    Node node = copycat1.create("/testMap").get();
                    AsyncMap<String, String> map = node.create(AsyncMap.class).get();
                    map.containsKey("test_key1").whenComplete((exist,t) ->{
                        CopycatServer server = null;
                        if ( copycat1 instanceof CopycatServer){
                            server= (CopycatServer) copycat1;
                            server.cluster().members().forEach(member ->logger.info(member.toString()));
                        }
                        if ( !exist){
                            if ( server != null ){
                                map.put("test_key1", "Hello World "+server.cluster().member()).thenRun( ()->{
                                    logger.info("put ok");
                                });
                            }
                        } else {
                            map.get("test_key1").thenAccept(result -> {
                                logger.info("==========================" + result);
                            });
                        }
                    });
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        });
    }

}
