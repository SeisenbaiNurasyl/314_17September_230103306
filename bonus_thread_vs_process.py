import time
import multiprocessing as mp
import threading

TOTAL = 25_000_000

def work(n):
    x = 0
    for i in range(n):
        x += (i % 7) * (i % 11)
    return x

def run_threads(workers):
    threads = []
    start = time.perf_counter()

    for _ in range(workers):
        t = threading.Thread(target=work, args=(TOTAL // workers,))
        threads.append(t)
        t.start()

    for t in threads:
        t.join()

    return time.perf_counter() - start

def run_processes(workers):
    start = time.perf_counter()

    with mp.Pool(workers) as pool:
        pool.map(work, [TOTAL // workers] * workers)

    return time.perf_counter() - start

if __name__ == "__main__":
    print("Workers | Threads | Processes")
    print("-" * 32)

    for w in [1, 2, 4, 8, 16, 32]:
        t = run_threads(w)
        p = run_processes(w)
        print(f"{w:7d} | {t:7.4f}s | {p:9.4f}s")