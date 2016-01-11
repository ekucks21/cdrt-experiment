import org.junit.Test;

import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class CdrtCounterTest {
    private final ExecutorService executorService = Executors.newCachedThreadPool();
    private final Random random = new Random();

    @Test
    public void testCounterStaysInSync() throws Exception {
        //given
        List<AtomicInteger> counter1 = asList(new AtomicInteger(0), new AtomicInteger(0));
        List<AtomicInteger> counter2 = asList(new AtomicInteger(0), new AtomicInteger(0));

        //when
        final Future<?> future1 = incrementAndMerge(counter1, counter2, 0);
        final Future<?> future2 = incrementAndMerge(counter2, counter1, 1);

        //then
        future1.get();
        future2.get();
        assertThat(CdrtCounter.value(counter1), equalTo(40));
        assertThat(CdrtCounter.value(counter2), equalTo(40));
    }
    
    private Future<?> incrementAndMerge(List<AtomicInteger> counterToIncrement, List<AtomicInteger> counterToMergeInto, int nodeIndex) {
        return executorService.submit((Runnable) () -> {
            for (int i = 0; i < 20; i++) {
                CdrtCounter.increment(counterToIncrement, nodeIndex);
                sleep();
                CdrtCounter.merge(counterToIncrement, counterToMergeInto);
            }
        });
    }

    private void sleep() {
        try {
            Thread.sleep(random.nextInt(100));
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}