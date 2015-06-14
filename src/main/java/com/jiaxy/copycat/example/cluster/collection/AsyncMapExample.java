package com.jiaxy.copycat.example.cluster.collection;

import com.jiaxy.copycat.example.cluster.ClusterUtil;
import net.kuujo.copycat.Copycat;
import net.kuujo.copycat.Node;
import net.kuujo.copycat.collections.AsyncMap;

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

    private static List<Copycat> copycats = new ArrayList<>();


    public static void main(String[] args) {

        Copycat copycat1 = ClusterUtil.buildCopycat(3,3);
        copycat1.open().thenRun(() -> {
            try {
                System.out.println("------------------i am here------------------------"+copycat1.isOpen());
                Node node = copycat1.create("/testMap").get();
                AsyncMap<String, String> map = node.create(AsyncMap.class).get();
                map.put("test_key1", "Hello World").thenRun( ()->{
                    System.out.println("put ok");
                });
                map.get("test_key1").thenAccept(result -> {
                    System.out.println("==========================" + result);
                });

                System.out.println("~~~~~~~~~~~~~~"+map.get("test_key1"));
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        });
    }

}
