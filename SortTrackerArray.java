import java.util.ArrayList;

public class SortTrackerArray{

private int length;
ArrayList<Tracker> array = new ArrayList<Tracker>();

public void sort(ArrayList<Tracker> inputArr){

    if (inputArr == null || inputArr.size() == 0) {
            return;
        }
        this.array = inputArr;
        length = inputArr.size();
        quickSort(0, length - 1);
    }



   private void quickSort(int lowerIndex, int higherIndex) {
         
        int i = lowerIndex;
        int j = higherIndex;
        // calculate pivot number, I am taking pivot as middle index number
        double pivot = array.get(lowerIndex+(higherIndex-lowerIndex)/2).time;
        // Divide into two arrays
        while (i <= j) {
           //Comparing values to pivot number, and switching if needed
           
            while (array.get(i).time < pivot) {
                i++;
            }
            while (array.get(j).time > pivot) {
                j--;
            }
            if (i <= j) {
                exchangeNumbers(i, j);
                //move index to next position on both sides
                i++;
                j--;
            }
        }
        // call quickSort() method recursively
        if (lowerIndex < j)
            quickSort(lowerIndex, j);
        if (i < higherIndex)
            quickSort(i, higherIndex);
    }


private void exchangeNumbers(int i, int j) {
        ArrayList<Tracker> temp = new ArrayList<Tracker>();
        temp.add(array.get(i));
        array.set(i, array.get(j));
        array.set(j, temp.get(0));
    }


}