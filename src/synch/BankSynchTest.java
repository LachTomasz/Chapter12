/**
 *
 */
package synch;

/**
 * @author Tomek
 *
 */
public class BankSynchTest {

    public static final int NACCOUNTS = 100;
    public static final double INITIAL_BALANCE = 1000;
    public static final double MAX_AMOUNT = 1000;
    public static final int DELAY = 10;

    public static void main (String[] args) {

        BankSynch bank = new BankSynch(NACCOUNTS, INITIAL_BALANCE);
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