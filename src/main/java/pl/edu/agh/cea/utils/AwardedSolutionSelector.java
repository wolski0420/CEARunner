package pl.edu.agh.cea.utils;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * This class is simple util to find best, worst and random solutions from the population and return them
 * @param <S>
 */
public class AwardedSolutionSelector<S> {
    private final Comparator<S> comparator;
    private final double bestPercent;
    private final double worstPercent;
    private final double randomPercent;

    /**
     * Setting some fixed parameters
     * @param comparator - compoarator by which we will be able to award solutions
     * @param bestPercent - a double from 0 to 1 that defines the percent of chosen best solutions
     * @param worstPercent - a double from 0 to 1 that defines the percent of chosen worst solutions
     * @param randomPercent - a double from 0 to 1 that defines the percent of chosen random solutions
     */
    public AwardedSolutionSelector(Comparator<S> comparator, double bestPercent, double worstPercent, double randomPercent) {
        this.comparator = comparator;
        this.bestPercent = bestPercent;
        this.worstPercent = worstPercent;
        this.randomPercent = randomPercent;
    }

    /**
     * Concatenating results of below methods
     * @param solutions - list of solutions, population
     * @return - set of all awarded solutions (Set is because of avoiding duplicates)
     */
    public Set<S> getAllAwarded(List<S> solutions) {
        return Stream.of(getBestSolutions(solutions), getWorstSolutions(solutions), getRandomSolutions(solutions))
                .flatMap(Collection::stream)
                .collect(Collectors.toSet());
    }

    private Set<S> getBestSolutions(List<S> solutions) {
        return solutions.stream()
                .sorted(comparator)
                .skip((int) (solutions.size() * (1 - bestPercent)))
                .collect(Collectors.toSet());
    }

    private Set<S> getWorstSolutions(List<S> solutions) {
        return solutions.stream()
                .sorted(comparator)
                .limit((int) (solutions.size() * worstPercent))
                .collect(Collectors.toSet());
    }

    private Set<S> getRandomSolutions(List<S> solutions) {
        return IntStream.range(0, (int) (solutions.size() * randomPercent))
                .map(number -> new Random().nextInt(solutions.size()))
                .mapToObj(solutions::get)
                .collect(Collectors.toSet());
    }
}
