package com.jiaxy.copycat.example.cluster;

import net.kuujo.copycat.Copycat;
import net.kuujo.copycat.CopycatClient;
import net.kuujo.copycat.CopycatServer;
import net.kuujo.copycat.cluster.ManagedCluster;
import net.kuujo.copycat.cluster.NettyCluster;
import net.kuujo.copycat.cluster.NettyMember;
import net.kuujo.copycat.cluster.NettyMembers;
import net.kuujo.copycat.raft.Raft;
import net.kuujo.copycat.raft.StateMachine;
import net.kuujo.copycat.raft.log.Log;
import net.kuujo.copycat.raft.log.StorageLevel;

import java.util.concurrent.TimeUnit;


/**
 * Title:<br>
 * Desc:<br>
 * <p>
 * </p>
 *
 * @author wutao
 *
 * @since 2015/06/13 13:15
 */
public class ClusterUtil {
    public static Raft buildRaft(int localMember, int members) {
        Raft.Builder builder = Raft.builder()
                .withCluster(buildCluster(localMember, members))
                .withStateMachine(new StateMachine() {
                })
                .withLog(Log.builder()
                        .withStorageLevel(StorageLevel.MEMORY)
                        .build());
        Raft raft = builder.build();

        return raft;

    }

    public static ManagedCluster buildCluster(int localMember, int members) {
        NettyCluster.Builder builder = NettyCluster.builder()
                .withMemberId(localMember)
                .withHost("localhost")
                .withPort(8090 + localMember);
        for (int i = 1; i <= members; i++) {
            builder.addMember(NettyMember.builder()
                    .withId(i)
                    .withHost("localhost")
                    .withPort(8090 + i)
                    .build());
        }
        return builder.build();
    }

    public static Copycat buildClientCopycat(int members) {
        CopycatClient.Builder copycatBuilder = CopycatClient.builder();
        NettyMembers.Builder membersBuilder = NettyMembers.builder();
        for (int i = 1; i <= members; i++) {
            membersBuilder.addMember(NettyMember.builder()
                    .withId(i)
                    .withHost("localhost")
                    .withPort(8090 + i)
                    .build());
        }
        copycatBuilder.withMembers(membersBuilder.build());
        return copycatBuilder.build();
    }


    public static Copycat buildCopycat(int localMember,int members){
        ManagedCluster managedCluster = buildCluster(localMember,members);

        Copycat copycat = CopycatServer.builder()
                .withCluster(managedCluster)
                /*.withHeartbeatInterval(5, TimeUnit.SECONDS)
                .withKeepAliveInterval(10,TimeUnit.SECONDS)
                .withElectionTimeout(8,TimeUnit.SECONDS)*/
//                .withSessionTimeout(60,TimeUnit.SECONDS)
                .withLog(Log.builder()
                        .withStorageLevel(StorageLevel.MEMORY)
                        .build())
                .build();
        return copycat;

    }
}