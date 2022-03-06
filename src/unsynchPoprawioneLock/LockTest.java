/**
 *
 */
package unsynchPoprawioneLock;

/** Program demonstrujący zniszczenie danych 
 * spowodowane dostępem kilku wątków do struktury danych.
 * problem rozwiązany zastosowaniem blokady lock
 * korzystam z poprawionej wersji klasy ktora jest w tej samej paczce
 * @author Tomek
 *
 */
public class LockTest {

    public static final int NACCOUNTS = 100;
    public static final double INITIAL_BALANCE = 1000;
    public static final double MAX_AMOUNT = 1000;
    public static final int DELAY = 10;

    public static void main (String[] args) {

        var bank = new BankLock(NACCOUNTS, INITIAL_BALANCE);
        for(int i = 0; i < NACCOUNTS; i++) {
            int fromAccount = i;
            Runnable task = ()-> {
                try {
                    while(true) {
                        int toAccount = (int) (bank.size() * Math.random());
                        double amount = MAX_AMOUNT * Math.random();
                        bank.transfer (fromAccount, toAccount, amount);
                        Thread.sleep((int) (DELAY * Math.random()));
                    }
                }
                catch (InterruptedException e) {
                    //
                }
            };
            var t = new Thread(task);
            t.setName("Watek nr" + i);
            t.start();
        }
    }
}