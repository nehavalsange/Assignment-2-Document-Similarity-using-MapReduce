package com.example;

import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;
import java.util.*;

public class DocumentSimilarityReducer extends Reducer<Text, Text, Text, DoubleWritable> {

    private Map<String, Set<String>> docWordMap = new HashMap<>();

    @Override
    protected void reduce(Text key, Iterable<Text> values, Context context) {
        // Collect documents for this word
        Set<String> docs = new HashSet<>();
        for (Text val : values) {
            docs.add(val.toString());
        }

        // Add word into each document's word set
        for (String doc : docs) {
            docWordMap.putIfAbsent(doc, new HashSet<>());
            docWordMap.get(doc).add(key.toString());
        }
    }

    @Override
    protected void cleanup(Context context) throws IOException, InterruptedException {
        List<String> docs = new ArrayList<>(docWordMap.keySet());

        for (int i = 0; i < docs.size(); i++) {
            for (int j = i + 1; j < docs.size(); j++) {
                String d1 = docs.get(i);
                String d2 = docs.get(j);

                Set<String> words1 = docWordMap.get(d1);
                Set<String> words2 = docWordMap.get(d2);

                Set<String> intersection = new HashSet<>(words1);
                intersection.retainAll(words2);

                Set<String> union = new HashSet<>(words1);
                union.addAll(words2);

                double similarity = (double) intersection.size() / union.size();

                similarity = Math.round(similarity * 100.0) / 100.0; // round to 2 decimals

                context.write(new Text(d1 + ", " + d2 + " Similarity:"), new DoubleWritable(similarity));
            }
        }
    }
}
