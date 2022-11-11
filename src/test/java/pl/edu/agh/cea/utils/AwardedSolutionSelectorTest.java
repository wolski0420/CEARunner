package pl.edu.agh.cea.utils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class AwardedSolutionSelectorTest {
    private AwardedSolutionSelector<Integer> awardedSolutionSelector;

    @Test
    public void getOnlyBest() {
        // given
        awardedSolutionSelector = new AwardedSolutionSelector<>(Integer::compareTo, 0.2, 0, 0);
        List<Integer> solutions = IntStream.range(0, 10)
                .boxed()
                .collect(Collectors.toList());

        // when
        Set<Integer> iterAwarded = awardedSolutionSelector.getAllAwarded(solutions);

        // then
        Assertions.assertEquals(2, iterAwarded.size());
        Assertions.assertTrue(iterAwarded.contains(9));
        Assertions.assertTrue(iterAwarded.contains(8));
    }

    @Test
    public void getOnlyWorst() {
        // given
        awardedSolutionSelector = new AwardedSolutionSelector<>(Integer::compareTo, 0, 0.2, 0);
        List<Integer> solutions = IntStream.range(0, 10)
                .boxed()
                .collect(Collectors.toList());

        // when
        Set<Integer> iterAwarded = awardedSolutionSelector.getAllAwarded(solutions);

        // then
        Assertions.assertEquals(2, iterAwarded.size());
        Assertions.assertTrue(iterAwarded.contains(0));
        Assertions.assertTrue(iterAwarded.contains(1));
    }

    @Test
    public void getOnlyRandom() {
        // given
        awardedSolutionSelector = new AwardedSolutionSelector<>(Integer::compareTo, 0, 0, 0.2);
        List<Integer> solutions = IntStream.range(0, 10)
                .boxed()
                .collect(Collectors.toList());

        // when
        Set<Integer> iterAwarded = awardedSolutionSelector.getAllAwarded(solutions);

        // then
        Assertions.assertEquals(2, iterAwarded.size());
    }

    @Test
    public void getAllAwarded() {
        // given
        awardedSolutionSelector = new AwardedSolutionSelector<>(Integer::compareTo, 0.2, 0.2, 0.1);
        List<Integer> solutions = IntStream.range(0, 10)
                .boxed()
                .collect(Collectors.toList());

        // when
        Set<Integer> iterAwarded = awardedSolutionSelector.getAllAwarded(solutions);

        // then
        Assertions.assertTrue(iterAwarded.contains(0));
        Assertions.assertTrue(iterAwarded.contains(1));
        Assertions.assertTrue(iterAwarded.contains(8));
        Assertions.assertTrue(iterAwarded.contains(9));
    }
}
