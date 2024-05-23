package unipi.it.mircv.common.dataStructures;

public class TopDocumentsComparator implements java.util.Comparator<TopDocuments> {
    @Override
    public int compare(TopDocuments pair1, TopDocuments pair2) {
        // descending order of the double value
        //return Double.compare(pair2.getScore(), pair1.getScore());
        // ascending order of the double value
        return Double.compare(pair1.getScore(), pair2.getScore());
    }
}
