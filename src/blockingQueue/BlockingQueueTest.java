/**
 *
 */
package blockingQueue;

import java.io.*;
import java.nio.charset.*;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.*;

/**
 * @author Tomek
 *
 */
public class BlockingQueueTest {

    private static final int FILE_QUEUE_SIZE = 10;
    private static final int SEARCH_THREADS = 100;
    private static final Path DUMMY = Path.of("");
    private static BlockingQueue<Path> queue = new ArrayBlockingQueue <>(FILE_QUEUE_SIZE);

    public static void main(String[] args) {

        try (var in = new Scanner(System.in)) {

            System.out.print("Enter base directory (e.g. /opt/jdk-9-src): ");
            String directory = in.nextLine();
            System.out.print("Wpisz słowo kluczowe (np. volatile): ");
            String keyword = in.nextLine();

            Runnable enumerator = () -> {
                try {
                    enumerate(Path.of(directory));
                    queue.put(DUMMY);
                }
                catch(IOException e) {

                    e.printStackTrace();
                }
                catch(InterruptedException e) {

                }
            };

            new Thread(enumerator).start();

            for(int i = 1; i <= SEARCH_THREADS; i++) {
                Runnable searcher = () ->{
                    try {
                        boolean done = false;
                        while(!done) {

                            Path file = queue.take();
                            if (file == DUMMY) {//DUMMY to taki marker ze juz wszystko jest przeszukane
                                queue.put(file);
                                done = true;
                            }
                            else
                                search(file, keyword);
                        }
                    }
                    catch(IOException e) {
                        e.printStackTrace();
                    }
                    catch(InterruptedException e) {
                    }
                };
                new Thread(searcher).start();
            }
        }
    }

    /**
     * Rekurencyjnie przegląda wszystkie pliki w danym katalogu i jego podkatalogach.
     * Opis operacji na strumieniach i plikach znajduje się w rozdziałach 1. i 2. tomu II.
     * @param directory katalog początkowy
     */
    public static void enumerate(Path directory) throws IOException, InterruptedException{
        try (Stream<Path> children = Files.list(directory)){

            for (Path child : children.collect(Collectors.toUnmodifiableList())) {
                if(Files.isDirectory(child))
                    enumerate(child);
                else
                    queue.put(child);
            }
        }
    }

    /**
     * Szuka słowa kluczowego w pliku i drukuje wszystkie zawierające je wiersze tekstu.
     * @param file plik do przeszukania
     * @param keyword słowo do znalezienia
     */
    public static void search(Path file, String keywords) throws IOException{
        try(var in = new Scanner(file, StandardCharsets.UTF_8)){

            int lineNumber = 0;
            while(in.hasNextLine()) {
                lineNumber++;
                String line = in.nextLine();
                if(line.contains(keywords))
                    System.out.printf("%s:%d:%s%n",  file, lineNumber, line);
            }
        }
    }
}
