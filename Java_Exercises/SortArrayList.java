import java.util.*;

public class SortArrayList {
    public static void main(String[] args) {
        ArrayList<Integer> list = new ArrayList<>();
        list.add(50);
        list.add(10);
        list.add(30);
        list.add(20);
        Collections.sort(list);
        System.out.println(list);
    }
}
