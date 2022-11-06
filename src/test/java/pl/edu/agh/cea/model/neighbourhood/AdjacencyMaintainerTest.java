package pl.edu.agh.cea.model.neighbourhood;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.uma.jmetal.solution.doublesolution.impl.DefaultDoubleSolution;
import org.uma.jmetal.util.JMetalException;
import pl.edu.agh.cea.model.solution.AdjacencySolution;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class AdjacencyMaintainerTest {
    private final static int size = 5;
    private AdjacencyMaintainer<AdjacencySolutionTestImpl> maintainer;

    @BeforeEach
    public void init() {
        maintainer = new AdjacencyMaintainer<>(size, size);
    }

    @Test
    public void getNeighboursByIncorrectLevel() {
        // given

        // when

        // then
        Assertions.assertThrows(JMetalException.class, () ->
                maintainer.getHigherLevelNeighbours(Collections.emptyList(), 0, 0));
    }

    @Test
    public void getNeighboursByFirstLevel() {
        // given
        AdjacencySolutionTestImpl sample1 = Mockito.mock(AdjacencySolutionTestImpl.class);
        AdjacencySolutionTestImpl sample2 = Mockito.mock(AdjacencySolutionTestImpl.class);
        AdjacencySolutionTestImpl sample3 = Mockito.mock(AdjacencySolutionTestImpl.class);
        List<AdjacencySolutionTestImpl> samples = Arrays.asList(sample1, sample2, sample3);

        // when
        Mockito.when(sample2.getNeighbours()).thenReturn(Arrays.asList(sample1, sample3));
        Mockito.when(sample1.getNeighbours()).thenReturn(Collections.singletonList(sample2));
        Mockito.when(sample3.getNeighbours()).thenReturn(Collections.singletonList(sample2));
        List<AdjacencySolutionTestImpl> returnedNeighbours = maintainer.getHigherLevelNeighbours(samples, 1, 1);

        // then
        Assertions.assertEquals(2, returnedNeighbours.size());
        Assertions.assertEquals(2, (int) returnedNeighbours.stream()
                .distinct()
                .count());
        Assertions.assertEquals(0, returnedNeighbours.stream()
                .filter(solution -> solution.equals(sample2))
                .count());
    }

    @Test
    public void getNeighboursBySecondLevel() {
        // given
        AdjacencySolutionTestImpl sample1 = Mockito.mock(AdjacencySolutionTestImpl.class);
        AdjacencySolutionTestImpl sample2 = Mockito.mock(AdjacencySolutionTestImpl.class);
        AdjacencySolutionTestImpl sample3 = Mockito.mock(AdjacencySolutionTestImpl.class);
        AdjacencySolutionTestImpl sample4 = Mockito.mock(AdjacencySolutionTestImpl.class);
        AdjacencySolutionTestImpl sample5 = Mockito.mock(AdjacencySolutionTestImpl.class);
        List<AdjacencySolutionTestImpl> samples = Arrays.asList(sample1, sample2, sample3, sample4, sample5);

        // when
        Mockito.when(sample2.getNeighbours()).thenReturn(Arrays.asList(sample1, sample3));
        Mockito.when(sample3.getNeighbours()).thenReturn(Arrays.asList(sample2, sample4));
        Mockito.when(sample4.getNeighbours()).thenReturn(Arrays.asList(sample3, sample5));
        Mockito.when(sample1.getNeighbours()).thenReturn(Collections.singletonList(sample2));
        Mockito.when(sample5.getNeighbours()).thenReturn(Collections.singletonList(sample4));
        List<AdjacencySolutionTestImpl> returnedNeighbours = maintainer.getHigherLevelNeighbours(samples, 2, 2);

        // then
        Assertions.assertEquals(4, returnedNeighbours.size());
        Assertions.assertEquals(4, returnedNeighbours.stream()
                .distinct()
                .count());
        Assertions.assertEquals(0, returnedNeighbours.stream()
                .filter(solution -> solution.equals(sample3))
                .count());
    }

    @Test
    public void replaceSpecificSolutionWithTheNewOne() {
        // given
        AdjacencySolutionTestImpl sample1 = Mockito.mock(AdjacencySolutionTestImpl.class);
        AdjacencySolutionTestImpl sample2 = Mockito.mock(AdjacencySolutionTestImpl.class);
        AdjacencySolutionTestImpl sample3 = Mockito.mock(AdjacencySolutionTestImpl.class);
        AdjacencySolutionTestImpl newSample = Mockito.mock(AdjacencySolutionTestImpl.class);
        List<AdjacencySolutionTestImpl> samples = new ArrayList<>(Arrays.asList(sample1, sample2, sample3));

        // when
        Mockito.when(sample2.getNeighbours()).thenReturn(new ArrayList<>(Arrays.asList(sample1, sample3)));
        Mockito.when(sample1.getNeighbours()).thenReturn(new ArrayList<>(Collections.singletonList(sample2)));
        Mockito.when(sample3.getNeighbours()).thenReturn(new ArrayList<>(Collections.singletonList(sample2)));
        Mockito.when(newSample.getNeighbours()).thenReturn(new ArrayList<>());
        maintainer.replaceWithNew(samples, 2, newSample);

        // then
        Assertions.assertEquals(3, samples.size());
        Assertions.assertEquals(newSample, samples.get(2));
        Assertions.assertEquals(Arrays.asList(sample1, newSample), samples.get(1).getNeighbours());
        Assertions.assertEquals(Collections.singletonList(sample2), samples.get(2).getNeighbours());
    }
}

class AdjacencySolutionTestImpl extends DefaultDoubleSolution implements AdjacencySolution<AdjacencySolutionTestImpl, Double>{

    public AdjacencySolutionTestImpl(DefaultDoubleSolution solution) {
        super(solution);
    }

    @Override
    public List<AdjacencySolutionTestImpl> getNeighbours() {
        return null;
    }
}
