import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

public class Main {

    static final long PART1_ITERATIONS = 50_000_000L;
    static final long PART3_ITERATIONS = 100_000_000L;

    static long totalHits = 0;

    //PART 1 
    static void part1() throws InterruptedException {

        totalHits = 0;

        Thread[] threads = new Thread[4];
        long iterationsPerThread = PART1_ITERATIONS / 4;

        for (int i = 0; i < 4; i++) {
            threads[i] = new Thread(() -> {
                Random random = new Random();

                for (long j = 0; j < iterationsPerThread; j++) {
                    double x = random.nextDouble();
                    double y = random.nextDouble();

                    if (x * x + y * y <= 1.0) {
                        totalHits++;
                    }
                }
            });

            threads[i].start();
        }

        for (Thread thread : threads) {
            thread.join();
        }

        double pi = 4.0 * totalHits / PART1_ITERATIONS;

        System.out.println("Part 1:");
        System.out.println("Hits = " + totalHits);
        System.out.println("Pi = " + pi);
    }

    //PART 2
    static AtomicLong atomicHits = new AtomicLong(0);

    static long part2MultiThreaded() throws InterruptedException {

        atomicHits.set(0);

        Thread[] threads = new Thread[4];
        long iterationsPerThread = PART1_ITERATIONS / 4;

        long start = System.nanoTime();

        for (int i = 0; i < 4; i++) {
            threads[i] = new Thread(() -> {
                Random random = new Random();

                for (long j = 0; j < iterationsPerThread; j++) {
                    double x = random.nextDouble();
                    double y = random.nextDouble();

                    if (x * x + y * y <= 1.0) {
                        atomicHits.incrementAndGet();
                    }
                }
            });

            threads[i].start();
        }

        for (Thread thread : threads) {
            thread.join();
        }

        return (System.nanoTime() - start) / 1_000_000;
    }

    static long part2SingleThreaded() {

        long hits = 0;
        Random random = new Random();

        long start = System.nanoTime();

        for (long i = 0; i < PART1_ITERATIONS; i++) {
            double x = random.nextDouble();
            double y = random.nextDouble();

            if (x * x + y * y <= 1.0) {
                hits++;
            }
        }

        return (System.nanoTime() - start) / 1_000_000;
    }

    //PART 3
    static long runReduction(int threadCount) throws InterruptedException {

        Thread[] threads = new Thread[threadCount];
        long[] localHits = new long[threadCount];

        long iterationsPerThread = PART3_ITERATIONS / threadCount;

        long start = System.nanoTime();

        for (int i = 0; i < threadCount; i++) {

            final int index = i;

            threads[i] = new Thread(() -> {

                Random random = new Random();

                long hits = 0;

                for (long j = 0; j < iterationsPerThread; j++) {

                    double x = random.nextDouble();
                    double y = random.nextDouble();

                    if (x * x + y * y <= 1.0) {
                        hits++;
                    }
                }

                localHits[index] = hits;
            });

            threads[i].start();
        }

        for (Thread thread : threads) {
            thread.join();
        }

        long totalHits = 0;

        for (long hits : localHits) {
            totalHits += hits;
        }

        long runtime = (System.nanoTime() - start) / 1_000_000;

        double pi = 4.0 * totalHits / PART3_ITERATIONS;

        System.out.printf(
                "%2d threads | %6d ms | Pi = %.6f%n",
                threadCount,
                runtime,
                pi
        );

        return runtime;
    }

    // ---------------- MAIN ----------------
    public static void main(String[] args) throws Exception {

        System.out.println("========== PART 1 ==========");

        for (int i = 1; i <= 5; i++) {
            part1();
            System.out.println();
        }

        System.out.println("========== PART 2 ==========");

        long single = part2SingleThreaded();
        long multi = part2MultiThreaded();

        System.out.println("Single-threaded: " + single + " ms");
        System.out.println("AtomicLong, 4 threads: " + multi + " ms");

        System.out.println();
        System.out.println("========== PART 3 ==========");

        int[] threadCounts = {1, 2, 4, 8, 16, 32};

        long[] times = new long[threadCounts.length];

        for (int i = 0; i < threadCounts.length; i++) {
            times[i] = runReduction(threadCounts[i]);
        }

        long baseline = times[0];

        System.out.println();
        System.out.println("Threads | Runtime | Speedup | Efficiency");

        for (int i = 0; i < threadCounts.length; i++) {

            double speedup = (double) baseline / times[i];
            double efficiency = speedup / threadCounts[i] * 100;

            System.out.printf(
                    "%7d | %7d ms | %.2fx | %.2f%%%n",
                    threadCounts[i],
                    times[i],
                    speedup,
                    efficiency
            );
        }
    }
}