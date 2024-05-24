# MIRCV 2023 - Search Engine

This Search Engine was developed as the final project for Multimedia Information Retrieval and Computer Vision at UniPi.

The project’s primary objectives include indexing a large dataset, implementing efficient query execution algorithms, and providing an easy-to-use interface for query input and result retrieval.


## Overview
When the user runs the problem it will be prompted to choose one of the three actions:

**1. Indexing:** The system will start the process of indexing the documents from the MSMARCO collection. 
At the end, the user will receive the three data structures of the index (Document Index, Inverted Index, and Lexicon) as a result. 

**2. Query Processing:** The user will be prompted to write a query for the search engine developed 
 and will be asked to choose different scoring
options to rank the top k documents based on the query the user. The answers will be saved as Flags. The result includes the top k documents
(represented by their document IDs), ordered by their scores.

**3. Evaluation:** The system will start reading the TREC DL 2020 passages and using them as queries for the search engine developed. 
It will repeat the process for each possible combination of the flags. At the end, the user will have access to the files with the results from the test
collection as well as the average time it took for each query with each one of the combinations. There are six possible combinations of the three boolean flags of the
test collection and therefore six different documents.

### Flags
- DAAT or MaxScore for the processing of the posting lists;
  - If DAAT: Conjunctive of Disjunctive for the processing of the query; 
- TFDIF or BM25 for the scoring function to use; 
- Number of documents to be retrieved (value for k)