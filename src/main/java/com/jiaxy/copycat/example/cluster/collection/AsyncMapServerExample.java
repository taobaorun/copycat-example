package com.jiaxy.copycat.example.cluster.collection;

import com.jiaxy.copycat.example.cluster.ClusterUtil;
import com.jiaxy.copycat.example.util.ConfigUtil;
import net.kuujo.copycat.Copycat;
import net.kuujo.copycat.CopycatServer;
import net.kuujo.copycat.Node;
import net.kuujo.copycat.collections.AsyncMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
public class AsyncMapServerExample {

    private static Logger logger = LoggerFactory.getLogger(AsyncMapServerExample.class);

    public static void main(String[] args) {

        Copycat copycat = ClusterUtil.buildCopycat(ConfigUtil.getLocalMemberID(),ConfigUtil.getMemberNums());
        logger.info("copycat " + copycat.getClass().getCanonicalName());
        copycat.open().thenRun(() -> {
            try {
                Thread.sleep(10000);

                logger.info("------------------i am here------------------------" + copycat.isOpen());
                    Node node = copycat.create("/testMap").get();
                    AsyncMap<String, String> map = node.create(AsyncMap.class).get();
                    map.containsKey("test_key1").whenComplete((exist,t) ->{
                        CopycatServer server = null;
                        if ( copycat instanceof CopycatServer){
                            server= (CopycatServer) copycat;
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
