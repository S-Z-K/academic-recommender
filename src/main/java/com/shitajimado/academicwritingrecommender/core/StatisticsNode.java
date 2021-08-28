package com.shitajimado.academicwritingrecommender.core;


public class StatisticsNode {
    private String name;
    private double[] count;
    private int[] frequency;

    public StatisticsNode(String name, double[] count) {
        this.name = name;
        this.count = count.clone();
    }

        public StatisticsNode(String name, double[] count, int[] frequency) {
        this.name = name;
        this.count = count.clone();
        this.frequency = frequency.clone();
    }

    public String getName() {
        return name;
    }

    public double[] getCount() {
        return count;
    }

    public int[] getFrequency(){
        return frequency;
    }

    @Override
    public boolean equals (Object o){
        if(o instanceof StatisticsNode){
            StatisticsNode toCompare = (StatisticsNode) o;
            return this.name.equals(toCompare.name);
        }
        return false;
    }
}
