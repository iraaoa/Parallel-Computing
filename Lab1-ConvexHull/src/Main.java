import java.io.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.IntStream;

public class Main {



    // ---------------- КЛАС ТОЧКИ ----------------
    // Описує точку на площині з координатами x та y
    static class Point {
        double x, y;
        Point(double x, double y) {
            this.x = x;
            this.y = y;
        }
    }


    // ---------------- РУЧНЕ ВВЕДЕННЯ З КЛАВІАТУРИ ----------------
    static void generateScanner() {
        Scanner input = new Scanner(System.in);
        A.clear();
        B.clear();

        System.out.println("\n--- Введення точок для групи A ---");
        System.out.print("Скільки точок у групі A? ");
        int n = input.nextInt();
        for (int i = 0; i < n; i++) {
            System.out.print("Точка A[" + i + "] (x y): ");
            double x = input.nextDouble();
            double y = input.nextDouble();
            A.add(new Point(x, y));
        }

        System.out.println("\n--- Введення точок для групи B ---");
        System.out.print("Скільки точок у групі B? ");
        int m = input.nextInt();
        for (int i = 0; i < m; i++) {
            System.out.print("Точка B[" + i + "] (x y): ");
            double x = input.nextDouble();
            double y = input.nextDouble();
            B.add(new Point(x, y));
        }
        System.out.println("✅ Точки успішно додані вручну.");
    }

    // Списки для зберігання вхідних точок груп A та B
    static List<Point> A = new ArrayList<>();
    static List<Point> B = new ArrayList<>();

    // Змінні для збереження кількості точок, що увійшли до фінальної оболонки
    static int hullASize = 0;
    static int hullBSize = 0;

    // ---------------- ГЕНЕРАЦІЯ ДАНИХ ----------------
    // Створює випадкові точки в діапазоні від 0 до 10000
    static void generate(int n, int m) {
        Random r = new Random();
        A.clear();
        B.clear();
        for (int i = 0; i < n; i++)
            A.add(new Point(r.nextDouble() * 10000, r.nextDouble() * 10000));
        for (int i = 0; i < m; i++)
            B.add(new Point(r.nextDouble() * 10000, r.nextDouble() * 10000));
    }


    static void generateManual() {
        A.clear();
        B.clear();

        // Група A - "Ліва" множина
        A.add(new Point(1, 1));
        A.add(new Point(1, 5));
        A.add(new Point(3, 3));
        A.add(new Point(2, 2));
        A.add(new Point(2, 3));

        // Група B - "Права" множина
        B.add(new Point(8, 8));
        B.add(new Point(8, 2));
        B.add(new Point(10, 5));
        B.add(new Point(9, 7));
        B.add(new Point(9, 4));

        System.out.println("✅ Точки задано вручну (по 5 точок).");
    }



    // ---------------- ЗБЕРЕЖЕННЯ У ФАЙЛ ----------------
    static void save(String file) throws Exception {
        PrintWriter pw = new PrintWriter(file);
        pw.println(A.size());
        for (Point p : A) pw.println(p.x + " " + p.y);
        pw.println(B.size());
        for (Point p : B) pw.println(p.x + " " + p.y);
        pw.close();
    }

    // ---------------- ЗЧИТУВАННЯ З ФАЙЛУ ----------------
    static List<Point> readPoints(Scanner sc, int n) {
        List<Point> list = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            list.add(new Point(sc.nextDouble(), sc.nextDouble()));
        }
        return list;
    }

    static void load(String file) throws Exception {
        Scanner sc = new Scanner(new File(file)).useLocale(Locale.US);
        int n = sc.nextInt();
        A = readPoints(sc, n);
        int m = sc.nextInt();
        B = readPoints(sc, m);
        sc.close();
        System.out.println("✅ Дані зчитано. Група A: " + A.size() + ", Група B: " + B.size());
    }






    // ---------------- ГЕОМЕТРИЧНІ ОБЧИСЛЕННЯ ----------------
    // Обчислення векторного добутку (визначає поворот: наліво, направо або прямо)
    static double cross(Point o, Point a, Point b) {

        return (a.x - o.x) * (b.y - o.y) - (a.y - o.y) * (b.x - o.x);
    }
    //векторний добуток.
    // > 0: Поворот наліво (проти годинникової стрілки).
    // <=0 : Поворот направо або пряма лінія (за годинниковою стрілкою).


    // ---------- ПЕРЕВІРКА ПЕРЕТИНУ ВІДРІЗКІВ ----------
    static boolean segmentsIntersect(Point a, Point b, Point c, Point d) {
        double d1 = cross(a, b, c);
        double d2 = cross(a, b, d);
        double d3 = cross(c, d, a);
        double d4 = cross(c, d, b);
        // Якщо кінці одного відрізка лежать по різні боки від іншого — вони перетинаються
        return (d1 * d2 < 0) && (d3 * d4 < 0);
    }







    // ---------------- ПОСЛІДОВНІ ОБЧИСЛЕННЯ ----------------

    // Алгоритм "сканування Монотонного ланцюга" (Monotone Chain)
    static List<Point> buildHull(List<Point> pts) {
        List<Point> hull = new ArrayList<>();

        // Побудова нижньої частини оболонки
        for (Point p : pts) {
            while (hull.size() >= 2 && cross(hull.get(hull.size()-2), hull.get(hull.size()-1), p) <= 0)
                hull.remove(hull.size()-1);
            hull.add(p);
        }

        // Побудова верхньої частини оболонки
        int t = hull.size() + 1;
        for (int i = pts.size() - 1; i >= 0; i--) {
            Point p = pts.get(i);
            while (hull.size() >= t && cross(hull.get(hull.size()-2), hull.get(hull.size()-1), p) <= 0)
                hull.remove(hull.size()-1);
            hull.add(p);
        }
        hull.remove(hull.size()-1); // Видаляємо дублікат останньої точки
        return hull;
    }



    // Послідовна оболонка
    static List<Point> convexHullSequential(List<Point> points) {
        List<Point> pts = new ArrayList<>(points);
        pts.sort((p1, p2) -> p1.x != p2.x ? Double.compare(p1.x, p2.x) : Double.compare(p1.y, p2.y));
        return buildHull(pts);
    }



    static boolean sequential() {
        List<Point> hA = convexHullSequential(A);
        List<Point> hB = convexHullSequential(B);
        hullASize = hA.size();
        hullBSize = hB.size();
        // Перевірка перетину
        for (int i = 0; i < hA.size(); i++) {
            for (int j = 0; j < hB.size(); j++) {
                if (segmentsIntersect(hA.get(i), hA.get((i+1)%hA.size()), hB.get(j), hB.get((j+1)%hB.size()))) return false;
            }
        }
        return true;
    }








    // ---------------- ПАРАЛЕЛЬНІ ОБЧИСЛЕННЯ ----------------

    static boolean polygonsIntersectParallel(List<Point> P, List<Point> Q) {
        return IntStream.range(0, P.size()).parallel().anyMatch(i -> {
            Point a1 = P.get(i);
            Point a2 = P.get((i + 1) % P.size());
            for (int j = 0; j < Q.size(); j++) {
                if (segmentsIntersect(a1, a2, Q.get(j), Q.get((j + 1) % Q.size()))) return true;
            }
            return false;
        });
    }


    // Паралельна оболонка (використовує parallelStream для сортування)
    static List<Point> convexHullParallel(List<Point> points, ForkJoinPool pool) throws Exception {

        List<Point> pts = pool.submit(() ->
                points.parallelStream()
                        .sorted((p1, p2) -> p1.x != p2.x ?
                                Double.compare(p1.x, p2.x) :
                                Double.compare(p1.y, p2.y))
                        .toList()
        ).get();

        return buildHullParallel(pts, pool);
    }


    // Паралельна побудова Monotone Chain
    static List<Point> buildHullParallel(List<Point> pts, ForkJoinPool pool) throws Exception {
// Запускаємо два потоки: один для низу, один для верху
        // Нижня частина
        CompletableFuture<List<Point>> lowerFuture =
                CompletableFuture.supplyAsync(() -> {
                    List<Point> lower = new ArrayList<>();
                    for (Point p : pts) {
                        while (lower.size() >= 2 &&
                                cross(lower.get(lower.size() - 2),
                                        lower.get(lower.size() - 1), p) <= 0) {
                            lower.remove(lower.size() - 1);
                        }
                        lower.add(p);
                    }
                    return lower;
                }, pool);

        // Верхня частина (в зворотному порядку)
        CompletableFuture<List<Point>> upperFuture =
                CompletableFuture.supplyAsync(() -> {
                    List<Point> upper = new ArrayList<>();
                    for (int i = pts.size() - 1; i >= 0; i--) {
                        Point p = pts.get(i);
                        while (upper.size() >= 2 &&
                                cross(upper.get(upper.size() - 2),
                                        upper.get(upper.size() - 1), p) <= 0) {
                            upper.remove(upper.size() - 1);
                        }
                        upper.add(p);
                    }
                    return upper;
                }, pool);

        List<Point> lower = lowerFuture.join();
        List<Point> upper = upperFuture.join();

        // Об'єднуємо (видаляємо дублікати крайніх точок)
        lower.remove(lower.size() - 1);
        upper.remove(upper.size() - 1);

        lower.addAll(upper);
        return lower;
    }




    static boolean parallel(int customParallelism) throws Exception {

        // СТВОРЕННЯ МАЙДАНЧИКА (ПУЛУ ПОТОКІВ)
        ForkJoinPool customPool = new ForkJoinPool(customParallelism);

        try {
            return customPool.submit(() -> {

                // Паралельно будуємо оболонки A та B
                CompletableFuture<List<Point>> futureA =
                        CompletableFuture.supplyAsync(() -> {
                            try {
                                return convexHullParallel(A, customPool);
                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            }
                        }, customPool);

                CompletableFuture<List<Point>> futureB =
                        CompletableFuture.supplyAsync(() -> {
                            try {
                                return convexHullParallel(B, customPool);
                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            }
                        }, customPool);

                List<Point> hA = futureA.join();
                List<Point> hB = futureB.join();

                hullASize = hA.size();
                hullBSize = hB.size();

                // Паралельна перевірка перетину
                return !polygonsIntersectParallel(hA, hB);

            }).get();
        } finally {
            customPool.shutdown();
        }
    }










    // ---------------- MAIN ----------------
    public static void main(String[] args) throws Exception {
        Scanner sc = new Scanner(System.in);
        System.out.println("Виберіть режим введення:");
        System.out.println("1 - Автоматична генерація (для тестів швидкості)");
        System.out.println("2 - Введення вручну з консолі");
        System.out.println("3 - Використати заздалегідь прописані точки (Manual)");
        int choice = sc.nextInt();

        if (choice == 1) {
            System.out.print("Кількість точок (напр. 500000): ");
            int n = sc.nextInt();
            generate(n, n);
        } else if (choice == 2) {
            generateScanner();
        } else {
            generateManual();
        }
        save("input1.txt");
        load("input1.txt");

        System.out.println("\n--- ТЕСТУВАННЯ ---");

        // 1. Послідовний запуск
        long tStartSeq = System.nanoTime();
        boolean resSeq = sequential();
        long tEndSeq = System.nanoTime();
        double sTime = (tEndSeq - tStartSeq) / 1e6;
        System.out.printf("Sequential: %.2f ms (Роздільні: %b)\n", sTime, resSeq);

        // 2. Цикл по кількості потоків
        System.out.println("\nThreads | Time (ms) | Speedup | Efficiency");
        System.out.println("------------------------------------------");

        int[] threadCounts = {1, 2, 4, 8, 16, 32};
        for (int threads : threadCounts) {
            long tStartPar = System.nanoTime();
            boolean resPar = parallel(threads);
            long tEndPar = System.nanoTime();

            double pTime = (tEndPar - tStartPar) / 1e6;
            double speedup = sTime / pTime;
            double efficiency = (speedup / threads) * 100;

            System.out.printf("%7d | %9.2f | %7.2fx | %9.1f%%\n",
                    threads, pTime, speedup, efficiency);
        }

        sc.close();
    }
}




