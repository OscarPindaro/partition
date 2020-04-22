package com.openfaas.function.partition;

import java.util.*;

public class Memory {

    // attributes
    private final Map<String, Integer> memory;    //String will contain a label and Integer will represent the number of occurrences.
    private int totLabelReceived;   // the number of all the labels received

    // constructor
    public Memory(String nodeID){

        this.totLabelReceived = 0;
        this.memory = new HashMap<String, Integer>();
        this.updateMemory(nodeID);

    }


    // getters and setters
    public int getTotLabelReceived() {
        return totLabelReceived;
    }

    private void updateTotLabelReceived() {
        this.totLabelReceived = totLabelReceived +1;
    }

    public Map<String, Integer> getMemory() { return new HashMap<String, Integer>(memory); }

    public Set<String> getDifferentReceivedLabels() { return memory.keySet(); }

    public void updateMemory(String label) {
        int previousValue;

        if (memory.containsKey(label)){
            previousValue = memory.get(label);
            memory.put(label, previousValue + 1);
        }
        else{
            memory.put(label, 1);
        }
        this.updateTotLabelReceived();
    }

    public int getOccurrences(String label) { return memory.get(label); }


    // FUNCTIONS

    // this function returns a list of all the labels occurring in the memory
    // sorted by number of occurrences and sorted in ascending order
    public List<String> returnSortedLabels() {
        List<String> sortedList;
        sortedList = new ArrayList<>(this.memory.keySet());
        sortedList.sort((a,b)->{
            if (memory.get(a) > memory.get(b)) {
                return 1;
            }
            else if(memory.get(a).equals(memory.get(b))){
                return 0;
            }
            else {
                return -1;
            }
        });
        return sortedList;
    }


/*    // debug
    public static void main(String[] args) {
        Memory memoriaProva = new Memory("fabio");
        System.out.println(memoriaProva.getMemory());
        memoriaProva.updateMemory("fabio");
        memoriaProva.updateMemory("oscar");
        memoriaProva.updateMemory("oscar");
        memoriaProva.updateMemory("oscar");
        memoriaProva.updateMemory("enka");
        System.out.println(memoriaProva.getMemory());
        System.out.println(memoriaProva.getTotLabelReceived());
        System.out.println(memoriaProva.returnSortedLabels());

    }*/
}
