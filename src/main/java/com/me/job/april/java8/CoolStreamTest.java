package com.me.job.april.java8;

import org.junit.Test;

import java.awt.geom.Point2D;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertTrue;

public class CoolStreamTest {
    private Map<Long, Product> cache = new ConcurrentHashMap<>();

    private static double calc(List<Integer> ints) {
        List<Point2D> point2DList = new ArrayList<>();
        for (Integer i : ints) {
            point2DList.add(new Point2D.Double((double) i % 3, (double) i / 3));
        }

        double total = 0;
        int count = 0;

        for (Point2D point2D : point2DList) {
            if (point2D.getY() > 1) {
                double distance = point2D.distance(0, 0);
                total += distance;
                count++;
            }
        }
        return count > 0 ? total / count : 0;
    }

    @Test
    public void stream() {
        List<Integer> ints = IntStream.rangeClosed(1, 8).boxed().collect(Collectors.toList());
        double average = calc(ints);

        double streamResult = ints.stream()
                .map(i -> new Point2D.Double((double) i % 3, (double) i / 3))
                .filter(point -> point.getY() > 1)
                .mapToDouble(point -> point.distance(0, 0))
                .average()
                .orElse(0);

        assertThat(average, is(streamResult));
    }

    @Test
    public void coolCache() {
        getProductAndCacheCool(1L);
        getProductAndCacheCool(100L);

        System.out.println(cache);
        assertThat(cache.size(), is(1));
        assertTrue(cache.containsKey(1L));
    }

    @Test
    public void notCoolCache() {
        getProductAndCache(1L);
        // ???????????????????????????
        getProductAndCache(100L);
        System.out.println(cache);

        assertThat(cache.size(), is(1));
        assertTrue(cache.containsKey(1L));
    }

    /**
     * ??????????????????cache???????????????
     */
    private Product getProductAndCacheCool(Long id) {
        return cache.computeIfAbsent(id, i -> Product.getData().stream()
                .filter(p -> p.getId().equals(i))
                .findFirst()
                .orElse(null));
    }

    private Product getProductAndCache(Long id) {
        Product product = null;
        if (cache.containsKey(id)) {
            product = cache.get(id);
        } else {
            // Product.getData()????????????????????????
            for (Product p : Product.getData()) {
                if (p.getId().equals(id)) {
                    product = p;
                    break;
                }
            }
            if (product != null) {
                cache.put(id, product);
            }
        }
        return product;
    }

    @Test
    public void filesExample() throws IOException {
        try(Stream<Path> pathStream = Files.walk(Paths.get("."))) {
            pathStream.filter(Files::isRegularFile)//??????????????????
                    .filter(FileSystems.getDefault().getPathMatcher("glob:**/*.java")::matches)//??????java????????????
                    .flatMap(ThrowingFunction.unchecked(path ->
                            Files.readAllLines(path).stream()
                                    .filter(line -> Pattern.compile("public class").matcher(line).find())//????????????????????????public class??????
                                    .map(line -> path.getFileName() + " >> " + line)))//???????????????????????????????????????+???
                    .forEach(System.out::println);
        }
    }

    /**
     * Files.readAllLines?????????????????????????????????IOException???????????????????????????????????????????????????
     * ThrowingFunction?????????????????????????????????????????????????????????????????????????????????
     */
    @FunctionalInterface
    public interface ThrowingFunction<T, R, E extends Throwable> {
        static <T, R, E extends Throwable> Function<T, R> unchecked(ThrowingFunction<T, R, E> f) {
            return t -> {
                try {
                    return f.apply(t);
                } catch (Throwable e) {
                    throw new RuntimeException(e);
                }
            };
        }

        R apply(T t) throws E;
    }

    @Test
    public void fibonacci() {
        Stream.iterate(new BigInteger[]{BigInteger.ONE, BigInteger.ONE},
                        p -> new BigInteger[]{p[1], p[0].add(p[1])})
                .limit(100)
                .forEach(p -> System.out.println(p[0]));
    }
}
