/**
 *
 */
package executors;

import java.io.*;
import java.nio.file.*;
import java.time.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.*;

/**
 * Program demonstrujący interface Callable i egzekutory.
 * @author Tomek
 *
 */
public class ExecutorDemo {

    /**
     * Liczy wystąpienia danego słowa w pliku.
     * @return liczba wystąpień danego słowa
     */
    public static long occurrences(String word, Path path) {
        try (var in = new Scanner(path)){
            int count = 0;
            while (in.hasNext())
                if(in.next().equals(word))
                    count++;
            return count;
        }
        catch (IOException ex) {
            return 0;
        }
    }

    /**Zwraca wszystkie podkatalogi danego katalogu - zobacz rozdziały 1. i 2. w tomie II
     * @param rootDir katalog główny
     * @return zbiór wszystkich podkatalogów w katalogu głównego
     */
    public static Set<Path> descendants(Path rootDir) throws IOException
    {
        try (Stream <Path> entries = Files.walk(rootDir)){
            return entries.filter(Files :: isRegularFile).collect(Collectors.toSet());
        }
    }

    /**
     * Tworzy zadanie szukujące słowa w pliku.
     * @param word szukane słowo
     * @param path plik do przeszukania
     * @return zadanie wyszukiwania, które zwraca ścierzkę w przypadku powodzenia
     */
    public static Callable<Path> searchForTask(String word, Path path){
        return () ->{
            try(var in = new Scanner(path)){
                while (in.hasNext()) {
                    if(in.next().equals(word))
                        return path;
                    if(Thread.currentThread().isInterrupted()) {
                        System.out.println("Szukanie w " + path + "anulowano.");
                        return null;
                    }
                }
                throw new NoSuchElementException();
            }
        };
    }

    public static void main(String[] args) 	throws 	InterruptedException,
            ExecutionException,
            IOException{
        try(var in = new Scanner(System.in)){
            System.out.print("Wpisz ścierzkę do katalogu podstawowego(np. /opt/jdk-9-src): ");
            String start = in.nextLine();
            System.out.print("Wpisz słowo kluczowe (np. volatile): ");
            String word = in.nextLine();

            Set <Path> files = descendants(Path.of(start));
            var tasks = new ArrayList<Callable<Long>>();
            for(Path file : files) {
                Callable<Long> task = () -> occurrences(word, file);
                tasks.add(task);
            }
            ExecutorService executor = Executors.newCachedThreadPool();
            //Użyj egzekutora wątku, aby sprawdzić,
            //czy większa liczba wątków przyśpiesza wyszukiwanie
            //ExecutorService executor = Executor.newSingleThreadExecutor();

            Instant  startTime = Instant.now();
            List<Future<Long>> results = executor.invokeAll(tasks);
            long total = 0;
            for(Future <Long> result : results)
                total += result.get();
            Instant endTime = Instant.now();
            System.out.println("Liczba wystąpień słowa " + word + ": " + total);
            System.out.println("Czas: "
                    + Duration.between(startTime, endTime).toMillis() + " ms");

            var searchTasks = new ArrayList<Callable<Path>>();
            for (Path file : files)
                searchTasks.add(searchForTask(word, file));
            Path found = executor.invokeAny(searchTasks);
            System.out.println(word + " występuje w: " + found);

            if(executor instanceof ThreadPoolExecutor) // egzekutor jednowątkowy nie jest
                System.out.println("Największy rozmiar puli: "
                        + ((ThreadPoolExecutor) executor).getLargestPoolSize());
            executor.shutdown();
        }
    }
}
