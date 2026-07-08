import java.util.*;

public class PrimeNumber {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int n = sc.nextInt();
        if (n < 2) {
            System.out.println("Not Prime");
            sc.close();
            return;
        }
        int count = 0;
        for (int i = 1; i * i <= n; i++) {
            if (n % i == 0) {
                count++;
                if (n / i != i) {
                    count++;
                }
            }
        }
        if (count == 2) {
            System.out.println("Prime");
        } else {
            System.out.println("Not Prime");
        }
        sc.close();
    }
}
