package com.openfaas.function.partition;

import io.kubernetes.client.ApiException;
import io.kubernetes.client.models.V1Node;
import io.kubernetes.client.models.V1ObjectMeta;
import com.openfaas.function.kubernetesApiWrapper.KubeApi;

import java.util.LinkedList;
import java.util.List;

public class Community {

    private final static String ROLE_KEY= "role";
    private final static String COMMUNITY_KEY= "community";

    private final String name;

    private V1Node leader;

    private final List<V1Node> members;

    public Community(String name){
        this.name = name;
        leader = null;
        members = new LinkedList<>();
    }

    public void addLeader(V1Node node){
        this.leader = node;
        setRoleLabel(node, com.openfaas.function.partition.Role.LEADER);
        setCommunityLabel(node);
    }

    public void addMember(V1Node member){
        if (members.contains(member)) throw new RuntimeException("This member is already present in the community");
        members.add(member);
        setRoleLabel(member, com.openfaas.function.partition.Role.MEMBER);
        setCommunityLabel(member);
    }

    private void setRoleLabel(V1Node node, Role role){
        V1ObjectMeta metadata = node.getMetadata();
        metadata = metadata.putLabelsItem(ROLE_KEY, role.toString());
        node.setMetadata(metadata);
    }

    private void setCommunityLabel(V1Node node){
        V1ObjectMeta metadata = node.getMetadata();
        metadata = metadata.putLabelsItem(COMMUNITY_KEY, this.name);
        node.setMetadata(metadata);
    }

    public void loadOnKube() throws ApiException {
        //load leader
        KubeApi.updateNode(leader);
        //load community members
        for(V1Node member : members){
            KubeApi.updateNode(member);
        }
    }


}
