/**
 *
 */
package synch;

import java.util.Arrays;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Bank z kilkoma kontami, kontrolujący dostęp za pomocą blokad
 * @author Tomek
 *
 */
public class BankSynch {

    private final double[] accounts;
    private Lock bankLock;
    private Condition sufficientFunds;

    /**
     * Tworzy bank
     * @param n liczba kont
     * @param initialBalance saldo początkowe na każdym koncie
     */
    public BankSynch(int n, double initialBalance) {

        accounts = new double[n];
        Arrays.fill(accounts, initialBalance);
        bankLock = new ReentrantLock();
        sufficientFunds = bankLock.newCondition();
    }

    /**
     * Przelewa pieniądze pomiędzy kontami.
     * @param from konto, z którego ma nastąpić przelew
     * @param to konto, na które mają zostać przelane środki
     * @param amount kwota przelania
     */
    public void transfer(int from, int to, double amount) throws InterruptedException{

        bankLock.lock();
        try {
            while(accounts[from] < amount)
                sufficientFunds.await();
            System.out.print(Thread.currentThread());
            accounts[from] -= amount;
            System.out.printf(" %10.2f z %d na %d", amount, from ,to);
            accounts[to] += amount;
            System.out.printf(" Saldo ogólne: %10.2f%n", getTotalBalance());
            sufficientFunds.signalAll();
        }
        finally{
            bankLock.unlock();
        }
    }

    /**
     * Zwraca sumę sald wszystkich kont.
     * @return saldo ogólne
     */
    public double getTotalBalance() {
        bankLock.lock();
        try {
            double sum =0;

            for(double a : accounts)
                sum += a;
            return sum;
        }
        finally {
            bankLock.unlock();
        }
    }

    /**
     * Zwraca liczbę kont w banku.
     * @return liczba kont
     */
    public int size() {
        return accounts.length;
    }
}
