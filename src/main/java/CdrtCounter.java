import com.googlecode.totallylazy.Sequences;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static java.lang.Math.max;

public class CdrtCounter {

    public static void increment(List<AtomicInteger> counter, int nodeIndex) {
        counter.get(nodeIndex).incrementAndGet();
    }

    public static void merge(List<AtomicInteger> counter, List<AtomicInteger> counterToMergeInto) {
        Sequences.zip(counter, counterToMergeInto)
                .map(pair -> new AtomicInteger(max(pair.first().get(), pair.second().get())))
                .zipWithIndex()
                .each(indexAndMax -> counterToMergeInto.set(indexAndMax.first().intValue(), indexAndMax.second()));
    }

    public static int value(List<AtomicInteger> counter) {
        final AtomicInteger atomicInteger = Sequences.reduceLeft(counter, (count1, count2) -> {
            count1.addAndGet(count2.get());
            return count1;
        });
        return atomicInteger.intValue();
    }
}
