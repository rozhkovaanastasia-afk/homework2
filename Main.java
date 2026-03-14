import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

public class Main {

    static long[] N_values = {
            1_000_000L,
            10_000_000L,
            100_000_000L,
            1_000_000_000L,
            10_000_000_000L,
            100_000_000_000L
    };

    static int[] M_values = {
            1,2,4,8,16,32,64,128
    };

    public static void main(String[] args) throws Exception {

        new File("results").mkdirs();

        FileWriter writer = new FileWriter("results/pi_monte_carlo_parallel_results.csv");

        writer.append("N/M,");

        for (int m : M_values)
            writer.append(m + ",");

        writer.append("\n");

        for (long N : N_values) {

            writer.append(N + ",");

            for (int M : M_values) {

                double time = runMonteCarlo(N, M);

                writer.append(time + ",");

                System.out.println("N=" + N + " M=" + M + " time=" + time);
            }

            writer.append("\n");
        }

        writer.close();

        System.out.println("Results saved.");
    }

    static double runMonteCarlo(long N, int M) throws Exception {

        long start = System.nanoTime();

        Thread[] threads = new Thread[M];
        Worker[] workers = new Worker[M];

        long batch = N / M;

        for (int i = 0; i < M; i++) {

            workers[i] = new Worker(batch);

            threads[i] = new Thread(workers[i]);

            threads[i].start();
        }

        long inside = 0;

        for (int i = 0; i < M; i++) {

            threads[i].join();

            inside += workers[i].inside;
        }

        long end = System.nanoTime();

        double time = (end - start) / 1_000_000_000.0;

        return time;
    }

    static class Worker implements Runnable {

        long points;
        long inside = 0;
        Random random = new Random();

        Worker(long points) {
            this.points = points;
        }

        public void run() {

            for (long i = 0; i < points; i++) {

                double x = random.nextDouble();
                double y = random.nextDouble();

                if (x*x + y*y <= 1)
                    inside++;
            }
        }
    }
}