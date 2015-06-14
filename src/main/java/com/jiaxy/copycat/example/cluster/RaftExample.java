package com.jiaxy.copycat.example.cluster;

import net.kuujo.copycat.cluster.*;
import net.kuujo.copycat.raft.Raft;

import java.util.concurrent.CompletableFuture;

/**
 * Title:<br>
 * Desc:<br>
 * <p>
 * </p>
 *
 * @author wutao
 *
 * @since 2015/06/13 15:23
 */
public class RaftExample {

    public static void main(String[] args){
        /*ManagedCluster cluster = buildCluster(3,3);
        cluster.open().thenRun(() -> {
            System.out.println("id---->"+cluster.member().id());
            cluster.member(1).send("test","Hello").whenComplete((result,error)->{
                System.out.println("====="+result);
            });
        });
//        cluster.member().registerHandler("test",message -> {
//            return CompletableFuture.completedFuture("world!");
//        });*/

        Raft raft = ClusterUtil.buildRaft(1, 3);
        raft.open().thenRun( () -> {
            raft.cluster().addListener(new MembershipListener() {
                @Override
                public void memberJoined(Member member) {
                    System.out.println("-----------member joined "+member.info());
                }

                @Override
                public void memberLeft(int memberId) {
                    System.out.println("-----------member left "+memberId);
                }
            });
            raft.cluster().member().registerHandler("leader", message -> {
                if (message == Raft.State.LEADER) {
                    System.out.println("----------message is from leader-------------");
                } else {
                    System.out.println("----------message is from follower-------------");

                }
                return CompletableFuture.completedFuture(raft.state());
            });

            raft.cluster().member().send("leader",raft.state());
            print(raft);
        });

    }


    private static void print(Raft raft){
        System.out.println("raft isOpen--->"+raft.isOpen());
        System.out.println("raft stat ---->"+raft.state());
        System.out.println("current id---->"+raft.cluster().member().id());
        System.out.println("leader--->"+raft.leader());
        raft.state();
        raft.cluster().members().forEach(System.out::println);

    }

}
