package com.shitajimado.academicwritingrecommender.core;

import com.shitajimado.academicwritingrecommender.entities.Document;
import com.shitajimado.academicwritingrecommender.entities.Text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Stream;

public class Statistics {
    private List<StatisticsNode> annotations;

    public Statistics(List<StatisticsNode> annotations) {
        this.annotations = annotations;
    }

    public static Statistics fromTextStream(Stream<Text> texts){ 
        List<Map<String, Long>> list = new ArrayList<>();
        SortedSet<String> annotations = new TreeSet();
        
        texts.map(Text::getAnnotationList).forEach(
            annotationList->{
            Map<String, Long> counts = new HashMap<>();
            for(var annotation: annotationList){
                annotations.add(annotation.getName());
                counts.putIfAbsent(annotation.getName(), 1L);
                counts.computeIfPresent(annotation.getName(), (key, value) -> value + 1);
            }
            list.add(counts);
        }
        );

        Map<String, double[]> input_data = groupAnnotations(list, annotations);
        
        return StatisticalMethods.Calculation(input_data);     
    }

    private static Map<String, double[]> groupAnnotations(List<Map<String, Long>> list, SortedSet<String> annotations){
        List<StatisticsNode> nodes = new ArrayList<>();

        for (String str: annotations){
            nodes.add(new StatisticsNode(str, new double [0]));
        }
        
        int num = 0;
        for(Map<String,Long> map : list){   
            for(Map.Entry<String, Long> entry : map.entrySet()) {
                StatisticsNode sn = new StatisticsNode(entry.getKey(), new double [] {entry.getValue()});
                if(nodes.contains(sn)){
 
                    double[] counts = Arrays.copyOf(nodes.get(nodes.indexOf(sn)).getCount(), 
                                                                nodes.get(nodes.indexOf(sn)).getCount().length+1);
                    counts[counts.length-1] = sn.getCount()[0];
                    nodes.set(nodes.indexOf(sn), new StatisticsNode(sn.getName(), counts));
                }
            }  

            num++;
            for (StatisticsNode sn : nodes){
                if(sn.getCount().length < num){
                    double[] counts = Arrays.copyOf(nodes.get(nodes.indexOf(sn)).getCount(), 
                                                            nodes.get(nodes.indexOf(sn)).getCount().length+1);
                    counts[counts.length-1] = 0;
                    nodes.set(nodes.indexOf(sn), new StatisticsNode(sn.getName(), counts));
                }
            }
        }

        Map<String, double[]> input_data = new HashMap<>();

        
        for(StatisticsNode sn : nodes){
            input_data.put(sn.getName(), Arrays.copyOf(sn.getCount(), list.size()));
        }
        
        return input_data;
    }

    public List<StatisticsNode> getAnnotations() {
        return annotations;
    }


    public static List<String> getDocumentsNames(Stream<Document> documents){ 
        List<String> listNames = new ArrayList<>();

        documents.map(Document::getName).forEach(
            documentName->{
                listNames.add(documentName);
        }
        );

        return listNames;
    }
}
