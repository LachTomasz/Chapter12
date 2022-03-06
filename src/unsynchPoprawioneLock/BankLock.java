/**
 *
 */
package unsynchPoprawioneLock;

import java.util.*;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Bank z pewną liczbą kont.
 * @author Tomek
 *
 */
public class BankLock {

    //definjuje blokade
    private ReentrantLock bankLock = new ReentrantLock(); //Klasa ReentrantLock implementuje
    //interface Lock

    private final double[] accounts;

    /**
     * Tworzy bank
     * @param n liczba kont
     * @param initialBalance początkowe saldo każdego konta
     */
    public BankLock(int n, double initialBalance) {

        accounts = new double[n];
        Arrays.fill(accounts, initialBalance);
    }

    /**
     * Przelewa pieniądze z jednego konta na inne
     * @param from konto zródłowe
     * @param to konto docelowe
     * @param amount kwota przelewu
     */
    public void transfer(int from, int to, double amount) {

        //Tu stosuje blokade do ochrony operacji
        bankLock.lock();
        try {

            if(accounts[from] < amount) return;
            System.out.print(Thread.currentThread());
            accounts[from] -= amount;
            System.out.printf(" %10.2f z %d na %d",amount, from, to);
            accounts[to] += amount;
            System.out.printf(" Saldo całkowite %10.2f%n", getTotalBalance());
        }
        finally {
            bankLock.unlock();
        }

    }

    /**
     * Oblicz saldo całkowite
     * @return saldo całkowite
     */
    public double getTotalBalance() {

        double sum = 0;

        for (double a : accounts)sum += a;

        return sum;
    }

    /**
     * Sprawdza liczbę kont w banku
     * @return liczba kont
     */
    public int size() {

        return accounts.length;
    }
}

