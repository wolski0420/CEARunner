package pl.edu.agh.cea.model.neighbourhood;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.uma.jmetal.util.JMetalException;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Extended2DMeshTest {
    private final static int size = 7;
    private Extended2DMesh<Integer> extended2DMesh;

    @BeforeEach
    public void init() {
        extended2DMesh = new Extended2DMesh<>(size, size);
    }

    @Test
    public void nullSolutionListGetNeighboursFilter() {
        // given

        // when

        // then
        Assertions.assertThrows(JMetalException.class, () ->
                extended2DMesh.getNeighbors(null, 0));
    }

    @Test
    public void emptySolutionListGetNeighboursFilter() {
        // given

        // when

        // then
        Assertions.assertThrows(JMetalException.class, () ->
                extended2DMesh.getNeighbors(Collections.emptyList(), 0));
    }

    @Test
    public void solutionPositionNegativeGetNeighboursFilter() {
        // given

        // when

        // then
        Assertions.assertThrows(JMetalException.class, () ->
                extended2DMesh.getNeighbors(Collections.singletonList(9), -1));
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
                extended2DMesh.getNeighbors(integers, 0));
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
                extended2DMesh.getNeighbors(integers, 50));
    }

    @Test
    public void getNeighboursByIncorrectLevel() {
        // given

        // when

        // then
        Assertions.assertThrows(JMetalException.class, () ->
                extended2DMesh.getHigherLevelNeighbours(Collections.emptyList(), 0, 0));
    }

    @Test
    public void getNeighboursByFirstLevel() {
        // given
        List<Integer> integers = IntStream.range(0, size * size)
                .boxed()
                .collect(Collectors.toList());

        // when
        List<Integer> returnedNeighbours = extended2DMesh.getHigherLevelNeighbours(integers, 24, 1);

        // then
        Assertions.assertEquals(8, returnedNeighbours.size());
        Assertions.assertEquals(8, (int) returnedNeighbours.stream()
                .distinct()
                .count());
        Assertions.assertEquals(0, (int) returnedNeighbours.stream()
                .filter(integer -> integer.equals(24))
                .count());
    }

    @Test
    public void getNeighboursByThirdLevel() {
        // given
        List<Integer> integers = IntStream.range(0, size * size)
                .boxed()
                .collect(Collectors.toList());

        // when
        List<Integer> returnedNeighbours = extended2DMesh.getHigherLevelNeighbours(integers, 24, 3);

        // then
        Assertions.assertEquals(48, returnedNeighbours.size());
        Assertions.assertEquals(48, (int) returnedNeighbours.stream()
                .distinct()
                .count());
        Assertions.assertEquals(0, (int) returnedNeighbours.stream()
                .filter(integer -> integer.equals(24))
                .count());
    }
}