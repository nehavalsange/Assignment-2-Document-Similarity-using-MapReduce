# Assignment 2: Document Similarity using MapReduce

**Name:** Neha Valsange

**Student ID:** 801393828

## Approach and Implementation
This project computes Jaccard Similarity between multiple text documents using Hadoop MapReduce. The similarity score measures how similar two documents are based on the words they contain:

Jaccard Similarity (A, B)=∣𝐴∩𝐵∣∣𝐴∪𝐵∣
Jaccard Similarity (A, B)=∣A∪B∣∣A∩B∣
Where:𝐴andB = sets of unique words from two documents
∣A∩B∣ = number of words common to both documents
∣𝐴∪𝐵∣ = total number of unique words across both documents
This project demonstrates distributed text processing and scalable computation using Hadoop.

### Mapper Design
[Explain the logic of your Mapper class. What is its input key-value pair? What does it emit as its output key-value pair? How does it help in solving the overall problem?]
Reads each document line.
Cleans text: converts to lowercase and removes punctuation.
Emits word → documentID pairs for building sets of words per document.

### Reducer Design
[Explain the logic of your Reducer class. What is its input key-value pair? How does it process the values for a given key? What does it emit as the final output? How do you calculate the Jaccard Similarity here?]

Receives all document IDs for a word.
For each word, generates document pairs containing that word.
Computes intersection counts.
Combines counts to calculate Jaccard Similarity for each document pair.

### Driver

Configures Hadoop job:
Sets Mapper, Reducer, OutputKey, OutputValue classes
Input path: /input/data
Output path: /output1 (or /output2, /output3 for different runs)

### Overall Data Flow
[Describe how data flows from the initial input files, through the Mapper, shuffle/sort phase, and the Reducer to produce the final output.]

1. Input Phase:
The input dataset consists of text files where each line represents a document.
Each line starts with a document ID followed by the document text.
These files are uploaded to HDFS (/input/data) so that Hadoop can access them in a distributed manner.

2. Mapper Phase:
Each Mapper reads a line (document) from HDFS.
It parses the line to extract the document ID and tokenizes the text into words.
Words are converted to lowercase and punctuation is removed to normalize them.
The Mapper emits key-value pairs where the key is the document ID and the value is the set of words in that document.

3. Shuffle & Sort Phase:
Hadoop automatically groups the key-value pairs by key (document ID).
All values associated with the same document ID are sent to the same Reducer.
This ensures that each Reducer has the data needed to compute similarities between document pairs.

4. Reducer Phase:
The Reducer receives sets of words for each document.
It generates all unique pairs of documents.
For each document pair, it computes the Jaccard Similarity:
𝐽(𝐴,𝐵)=∣𝐴∩𝐵∣/∣𝐴∪𝐵∣
The Reducer emits the document pair along with their similarity score.

5. Output Phase:
The final output is written to HDFS in the /output folder.
Each line of the output contains a document pair and its similarity score, rounded to two decimal places:

Document1, Document2 Similarity: 0.56
Document1, Document3 Similarity: 0.42
Document2, Document3 Similarity: 0.50

The output can then be copied from HDFS to the local machine for review and analysis.

---

## Setup and Execution

### ` Note: The below commands are the ones used for the Hands-on. You need to edit these commands appropriately towards your Assignment to avoid errors. `

### 1. **Start the Hadoop Cluster**

Run the following command to start the Hadoop cluster:

```bash
docker compose up -d
```

### 2. **Build the Code**

Build the code using Maven:

```bash
mvn clean package
```

### 4. **Copy JAR to Docker Container**

Copy the JAR file to the Hadoop ResourceManager container:

```bash
docker cp target/DocumentSimilarity-0.0.1-SNAPSHOT.jar resourcemanager:/opt/hadoop-3.2.1/share/hadoop/mapreduce/
```

### 5. **Move Dataset to Docker Container**

Copy the dataset to the Hadoop ResourceManager container:

```bash
docker cp src/input1.txt resourcemanager:/opt/hadoop-3.2.1/share/hadoop/mapreduce/
```
```bash
docker cp src/input2.txt resourcemanager:/opt/hadoop-3.2.1/share/hadoop/mapreduce/
```
```bash
docker cp src/input3.txt resourcemanager:/opt/hadoop-3.2.1/share/hadoop/mapreduce/
```

### 6. **Connect to Docker Container**

Access the Hadoop ResourceManager container:

```bash
docker exec -it resourcemanager /bin/bash
```

Navigate to the Hadoop directory:

```bash
cd /opt/hadoop-3.2.1/share/hadoop/mapreduce/
```

### 7. **Set Up HDFS**

Create a folder in HDFS for the input dataset:

```bash
hadoop fs -mkdir -p /input/data
```

Copy the input dataset to the HDFS folder:

```bash
hadoop fs -put ./input.txt /input/data
```

### 8. **Execute the MapReduce Job**

Run your MapReduce job using the following command: Here I got an error saying output already exists so I changed it to output1 instead as destination folder

```bash
hadoop jar /opt/hadoop-3.2.1/share/hadoop/mapreduce/DocumentSimilarity-0.0.1-SNAPSHOT.jar com.example.controller.Controller /input/data/input.txt /output1
```

Corrected Command:

```bash
hadoop jar DocumentSimilarity-0.0.1-SNAPSHOT.jar com.example.controller.DocumentSimilarityDriver /input/data /output1
```

### 9. **View the Output**

To view the output of your MapReduce job, use:

```bash
hadoop fs -cat /output1/*
```

### 10. **Copy Output from HDFS to Local OS**

To copy the output from HDFS to your local machine:

1. Use the following command to copy from HDFS:
    ```bash
    hdfs dfs -get /output1 /opt/hadoop-3.2.1/share/hadoop/mapreduce/
    ```

2. use Docker to copy from the container to your local machine:
   ```bash
   exit 
   ```
    ```bash
    docker cp resourcemanager:/opt/hadoop-3.2.1/share/hadoop/mapreduce/output1/ shared-folder/output/
    ```
3. Commit and push to your repo so that we can able to see your output


---

## Challenges and Solutions

[Describe any challenges you faced during this assignment. This could be related to the algorithm design (e.g., how to generate pairs), implementation details (e.g., data structures, debugging in Hadoop), or environmental issues. Explain how you overcame these challenges.]
1. Problem: Old Docker containers from previous runs caused conflicts.
Solution: Removed all old containers using docker rm -f and restarted the cluster.

2. Problem: input.txt not found in container.
Solution: Used docker cp with the exact host path to copy files into the container.

3. Problem: Output folder already existed.
Solution: Used new output folder names (/output1, /output2, /output3) for each run.

4. Problem: Java files were not in the correct folder structure for Hadoop MapReduce.
Solution: Created proper directories and moved Java files using git mv, then rebuilt the JAR using Maven (mvn -q -DskipTests clean package).

---
## Sample Input

**Input from `small_dataset.txt`**
```
Document1 This is a sample document containing words
Document2 Another document that also has words
Document3 Sample text with different words
```
## Sample Output

**Output from `small_dataset.txt`**
```
"Document1, Document2 Similarity: 0.56"
"Document1, Document3 Similarity: 0.42"
"Document2, Document3 Similarity: 0.50"
```
## Obtained Output: (Place your obtained output here.)

1. Output with 3 data nodes:-

document8, document7 Similarity:        1.0
document8, document9 Similarity:        1.0
document8, document4 Similarity:        1.0
document8, document3 Similarity:        1.0
document8, document10 Similarity:       1.0
document8, document6 Similarity:        1.0
document8, document5 Similarity:        1.0
document8, document2 Similarity:        1.0
document8, document1 Similarity:        1.0
document7, document9 Similarity:        1.0
document7, document4 Similarity:        1.0
document7, document3 Similarity:        1.0
document7, document10 Similarity:       1.0
document7, document6 Similarity:        1.0
document7, document5 Similarity:        1.0
document7, document2 Similarity:        1.0
document7, document1 Similarity:        1.0
document9, document4 Similarity:        1.0
document9, document3 Similarity:        1.0
document9, document10 Similarity:       1.0
document9, document6 Similarity:        1.0
document9, document5 Similarity:        1.0
document9, document2 Similarity:        1.0
document9, document1 Similarity:        1.0
document4, document3 Similarity:        1.0
document4, document10 Similarity:       1.0
document4, document6 Similarity:        1.0
document4, document5 Similarity:        1.0
document4, document2 Similarity:        1.0
document4, document1 Similarity:        1.0
document3, document10 Similarity:       1.0
document3, document6 Similarity:        1.0
document3, document5 Similarity:        1.0
document3, document2 Similarity:        1.0
document3, document1 Similarity:        1.0
document10, document6 Similarity:       1.0
document10, document5 Similarity:       1.0
document10, document2 Similarity:       1.0
document10, document1 Similarity:       1.0
document6, document5 Similarity:        1.0
document6, document2 Similarity:        1.0
document6, document1 Similarity:        1.0
document5, document2 Similarity:        1.0
document5, document1 Similarity:        1.0
document2, document1 Similarity:        1.0

2. Output with one data node

document8, document7 Similarity:	0.4
document8, document9 Similarity:	0.52
document8, document4 Similarity:	0.52
document8, document3 Similarity:	0.44
document8, document6 Similarity:	0.47
document8, document10 Similarity:	0.44
document8, document5 Similarity:	0.53
document8, document2 Similarity:	0.41
document8, document1 Similarity:	0.4
document7, document9 Similarity:	0.46
document7, document4 Similarity:	0.44
document7, document3 Similarity:	0.42
document7, document6 Similarity:	0.45
document7, document10 Similarity:	0.43
document7, document5 Similarity:	0.39
document7, document2 Similarity:	0.47
document7, document1 Similarity:	0.49
document9, document4 Similarity:	0.52
document9, document3 Similarity:	0.46
document9, document6 Similarity:	0.47
document9, document10 Similarity:	0.51
document9, document5 Similarity:	0.48
document9, document2 Similarity:	0.39
document9, document1 Similarity:	0.46
document4, document3 Similarity:	0.47
document4, document6 Similarity:	0.42
document4, document10 Similarity:	0.45
document4, document5 Similarity:	0.51
document4, document2 Similarity:	0.39
document4, document1 Similarity:	0.54
document3, document6 Similarity:	0.39
document3, document10 Similarity:	0.44
document3, document5 Similarity:	0.52
document3, document2 Similarity:	0.36
document3, document1 Similarity:	0.45
document6, document10 Similarity:	0.47
document6, document5 Similarity:	0.46
document6, document2 Similarity:	0.48
document6, document1 Similarity:	0.53
document10, document5 Similarity:	0.46
document10, document2 Similarity:	0.46
document10, document1 Similarity:	0.4
document5, document2 Similarity:	0.35
document5, document1 Similarity:	0.5
document2, document1 Similarity:	0.43



