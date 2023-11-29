package unipi.it.mircv.common;

import java.util.*;

public class InvertedIndex {
    private HashMap<String, ArrayList<Posting>> invertedIndex;

    public HashMap<String, ArrayList<Posting>> getInvertedIndex(){ return invertedIndex; }
    public InvertedIndex() {
        this.invertedIndex = new HashMap<>();
    }

    public void addPosting(String term, int docId, int freq){
        //add Posting to the PostingList
        if (!invertedIndex.containsKey(term)){
            invertedIndex.put(term, new ArrayList<>());
        }
        invertedIndex.get(term).add(new Posting(docId, freq));
    }
    public void sortPostingList() {
        // Iterate through each term in the inverted index
        for (String term : invertedIndex.keySet()) {
            // Get the posting list for the current term
            ArrayList<Posting> postingList = invertedIndex.get(term);

            // Sort the posting list using a custom comparator
            Collections.sort(postingList, new Comparator<Posting>() {
                @Override
                public int compare(Posting p1, Posting p2) {
                    // Compare based on the frequency (you can change this based on your sorting criteria)
                    return Integer.compare(p1.getFreq(), p2.getFreq());
                }
            });

            // Update the inverted index with the sorted posting list
            invertedIndex.put(term, postingList);
        }
    }
    public ArrayList<String> sortInvertedIndexByTerm() {
        ArrayList<String>sortedDocId = new ArrayList<String>(invertedIndex.keySet());
        Collections.sort(sortedDocId);
        return sortedDocId;
    }
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        for (Map.Entry<String, ArrayList<Posting>> entry : invertedIndex.entrySet()) {
            String term = entry.getKey();
            ArrayList<Posting> postings = entry.getValue();

            sb.append(term).append(": ");

            for (Posting posting : postings) {
                sb.append(posting).append(", ");
            }

            // Remove the trailing comma and space
            if (!postings.isEmpty()) {
                sb.setLength(sb.length() - 2);
            }

            sb.append("\n");
        }

        String result = sb.toString();

        // Add debugging information
        System.out.println("Generated String: " + result);

        return result;
    }


}

