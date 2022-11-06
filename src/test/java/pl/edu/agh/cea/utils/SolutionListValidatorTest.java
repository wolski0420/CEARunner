package pl.edu.agh.cea.utils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.uma.jmetal.util.JMetalException;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class SolutionListValidatorTest {
    private final static int size = 7;

    @Test
    public void nullSolutionListGetNeighboursFilter() {
        // given

        // when

        // then
        Assertions.assertThrows(JMetalException.class, () ->
                SolutionListValidator.checkSolutionListViolations(null, 0, 0));
    }

    @Test
    public void emptySolutionListGetNeighboursFilter() {
        // given

        // when

        // then
        Assertions.assertThrows(JMetalException.class, () ->
                SolutionListValidator.checkSolutionListViolations(Collections.emptyList(), 0, 0));
    }

    @Test
    public void solutionPositionNegativeGetNeighboursFilter() {
        // given

        // when

        // then
        Assertions.assertThrows(JMetalException.class, () ->
                SolutionListValidator.checkSolutionListViolations(Collections.singletonList(9), -1, 0));
    }

    @Test
    public void sizeNotEqualGetNeighboursFilter() {
        // given
        List<Integer> integers = IntStream.range(0, size)
                .boxed()
                .collect(Collectors.toList());

        // when

        // then
        Assertions.assertThrows(JMetalException.class, () ->
                SolutionListValidator.checkSolutionListViolations(integers, 0, 0));
    }

    @Test
    public void solutionBeyondGetNeighboursFilter() {
        // given
        List<Integer> integers = IntStream.rangeClosed(1, size * size)
                .boxed()
                .collect(Collectors.toList());

        // when

        // then
        Assertions.assertThrows(JMetalException.class, () ->
                SolutionListValidator.checkSolutionListViolations(integers, 50, size*size));
    }
}
