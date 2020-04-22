package com.openfaas.function.partition;

import io.kubernetes.client.models.V1Node;

import java.util.*;

public class SLPA_Node {

    // attributes
    private final V1Node kubeNode;  //kubernetes related object
    private final String nodeID;
    private String labelToSpread;   //label that the node is going to spread, set up checking the memory
    private final Memory memory;    //used to save all the labels received by other nodes
    private final List<SLPA_Node> nearbyNodes;    //store all the connected nodes, used to set speakers

    // constructor
    public SLPA_Node(V1Node node) {

        this.kubeNode = node;
        this.nodeID = kubeNode.getMetadata().getName();
        this.labelToSpread = kubeNode.getMetadata().getName();
        this.memory = new Memory(kubeNode.getMetadata().getName());
        this.nearbyNodes = new LinkedList<>();

    }


    // getter and setters
    public V1Node getKubeNode() { return kubeNode; }

    public String getNodeID() { return nodeID; }

    public String getLabelToSpread() { return labelToSpread; }


    public float computeOccurrenceProbability(String label) {
        return ((float)memory.getOccurrences(label)/memory.getTotLabelReceived());
    }


    // set the label that the node will spread according to the node memory
    public void setLabelToSpread() {

        List<String> sortedLabels = memory.returnSortedLabels();
        int selectionValue = new Random().nextInt(memory.getTotLabelReceived());
        int sum = 0;
        int pos = 0;
        while( sum < selectionValue){
            pos++;
            sum += memory.getOccurrences(sortedLabels.get(pos-1));
        }

        this.labelToSpread = sortedLabels.get(pos-1);
    }

    public Memory getMemory() { return memory; }

    public void addToMemory(String labelToAdd) {
        this.memory.updateMemory(labelToAdd);
    }

    public List<SLPA_Node> getNearbyNodes() { return new LinkedList<SLPA_Node>(nearbyNodes); }

    public void addNearbyNode(SLPA_Node nearbyNode) { this.nearbyNodes.add(nearbyNode); }



    // functions
    // the speaking policy selects a label to spread from the memory according to a probability distribution
    // of the occurrences
    public String speak() { return labelToSpread; }

    // the listening policy selects the most popular label received from all the nearby nodes
    public static String listen(List<String> receivedLabels) {

        Map<String,Integer> map = new HashMap<>();

        for (String label : receivedLabels) {
            Integer occurrences = map.get(label);
            map.put(label, occurrences == null ? 1 : occurrences + 1);
        }

        Map.Entry<String, Integer> max = null;

        for (Map.Entry<String,Integer> e : map.entrySet()) {
            if (max == null || e.getValue() > max.getValue()) {
                max = e;
            }
        }

        return max.getKey();
    }


/*  // debug
    public static void main(String[] args) {
        List<String> list = new LinkedList<>();
        list.add("fabio");
        list.add("oscar");
        list.add("fabio");
        list.add("fabio");
        list.add("oscar");
        list.add("enka");
        list.add("oscar");
        System.out.println(list);
        System.out.println(listen(list));
    }
*/

}
