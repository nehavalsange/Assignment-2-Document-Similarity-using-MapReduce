package com.example;

import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;
import java.util.HashSet;
import java.util.StringTokenizer;

public class DocumentSimilarityMapper extends Mapper<LongWritable, Text, Text, Text> {

    private Text word = new Text();
    private Text docID = new Text();

    @Override
    protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
        // Convert to lowercase and remove punctuation
        String line = value.toString().toLowerCase().replaceAll("[^a-z0-9 ]", " ");
        String[] parts = line.split("\\s+", 2);
        if (parts.length < 2) return;

        String documentId = parts[0];
        String content = parts[1];

        HashSet<String> uniqueWords = new HashSet<>();
        StringTokenizer itr = new StringTokenizer(content);
        while (itr.hasMoreTokens()) {
            uniqueWords.add(itr.nextToken());
        }

        for (String token : uniqueWords) {
            word.set(token);
            docID.set(documentId);
            context.write(word, docID);
        }
    }
}
