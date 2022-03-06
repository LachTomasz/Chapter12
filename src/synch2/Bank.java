package synch2;

import java.util.Arrays;

/**
 * Bank z kolkoma kontami wykorzystujący synchronizację
 * @author Tomek
 *
 */

public class Bank {

    private final double[] accounts;

    /**
     * @param n liczba kont
     * @param initialBalance saldo początkowe na każdym koncie
     */
    public Bank (int n, double initialBalance) {
        accounts = new double[n];
        Arrays.fill(accounts, initialBalance);
    }

    /**
     * Przelewa pieniądze pomiędzy kontami.
     * @param from konto, z którego ma nastąpić przelew
     * @param to konto, na które mają zostać przelane środki
     * @param amount kwota do przelania
     */
    public synchronized void transfer(int from, int to, double amount)
            throws InterruptedException{

        while(accounts[from] < amount)
            wait();
        accounts[from] -= amount;
        System.out.printf(" %10.2f z konta %d na konto %d", amount, from, to);
        accounts[to] += amount;
        System.out.printf(" Saldo ogólne: %10.2f%n", getTotalBalance());
        notifyAll();
    }

    /**
     * Zwraca sumę sald wszystkich kont
     * @return saldo ogólne
     */
    public synchronized double getTotalBalance() {
        double sum = 0;
        for (double a : accounts)
            sum += a;
        return sum;
    }

    /**
     * Zwraca liczbę kont w banku.
     * @return liczba kont
     */
    public int size() {
        return accounts.length;
    }
}
