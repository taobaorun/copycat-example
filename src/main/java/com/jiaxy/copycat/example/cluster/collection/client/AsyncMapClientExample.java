package com.jiaxy.copycat.example.cluster.collection.client;

import com.jiaxy.copycat.example.cluster.ClusterUtil;
import net.kuujo.copycat.Copycat;
import net.kuujo.copycat.Mode;
import net.kuujo.copycat.Node;
import net.kuujo.copycat.collections.AsyncMap;

import java.lang.management.ManagementFactory;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * Title: <br>
 * <p>
 * Description: <br>
 * </p>
 *
 * @author <a href=mailto:taobaorun@gmail.com>wutao</a>
 *
 * @since 2015/06/23 11:03
 */
public class AsyncMapClientExample {

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        Copycat copycat = ClusterUtil.buildClientCopycat(3);
        copycat.open().get();
        Node node = null;
        AsyncMap<String, String> map = null;
        if ( copycat.exists("/testMap").get() ){
            node = copycat.node("/testMap");
            map = node.get(AsyncMap.class).get();
        } else {
            node = copycat.create("/testMap").get();
            map = node.create(AsyncMap.class).get();
        }
        map.put("test", "put from client:" + getPid()).get();

        // AsyncMap get 默认采用LINEARIZABLE_LEASE 策略读取
        System.out.println("current pid:"+getPid()+"==get test value ="+map.get("test").get());
        expireKey(map);
        //ephemeralKey(copycat,map);
        printEphemeralKey(copycat,map);
    }


    private static void expireKey(AsyncMap map) throws ExecutionException, InterruptedException {
        map.putIfAbsent("expireKey", "Hello Copycat", 2, TimeUnit.SECONDS).get();
        System.out.println("current pid:" + getPid() + "==get expireKey value =" + map.get("expireKey").get());
        Thread.sleep(3 * 1000);
        map.get("expireKey").thenAccept(value ->{
            if ( value != null){
                throw new RuntimeException("value is not null");
            }
        });
        System.out.println("current pid:" + getPid() + "==after 10 s get expireKey value =" + map.get("expireKey").get());
    }


    private static void ephemeralKey(Copycat copycat,AsyncMap map) throws ExecutionException, InterruptedException {
        map.put("ephemeral_key", "Hello World", Mode.EPHEMERAL).get();
        System.out.println("put:ephemeral_key " + map.get("ephemeralKey").get());
        copycat.close().get();
    }

    private static void printEphemeralKey(Copycat copycat,AsyncMap map) throws ExecutionException, InterruptedException {
        if ( copycat.isOpen() ){
            System.out.println("print:ephemeral_key " + map.get("ephemeral_key").get());
        }
    }



    public static int getPid(){
        String name = ManagementFactory.getRuntimeMXBean().getName();
        String pid = name.split("@")[0];
        return Integer.valueOf(pid);
    }
}
