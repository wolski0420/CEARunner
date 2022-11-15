package pl.edu.agh.cea.algorithms;

import org.uma.jmetal.algorithm.multiobjective.mocell.MOCell;
import org.uma.jmetal.operator.crossover.CrossoverOperator;
import org.uma.jmetal.operator.selection.SelectionOperator;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.util.archive.BoundedArchive;
import org.uma.jmetal.util.comparator.RankingAndCrowdingDistanceComparator;
import org.uma.jmetal.util.evaluator.SolutionListEvaluator;
import org.uma.jmetal.util.solutionattribute.Ranking;
import org.uma.jmetal.util.solutionattribute.impl.CrowdingDistance;
import org.uma.jmetal.util.solutionattribute.impl.DominanceRanking;
import pl.edu.agh.cea.model.neighbourhood.AdjacencyMaintainer;
import pl.edu.agh.cea.model.solution.AdjacencySolution;
import pl.edu.agh.cea.operator.AdjacencyMutationOperator;
import pl.edu.agh.cea.utils.AwardedSolutionSelector;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

/**
 * Here MOCell is extended - we are able to select some specially awarded solutions per iteration
 * It means that we are looking for the best and the worst solutions by fitness + some randoms
 * Then we are using them to check, if current individual has a neighbour from the calculates list
 * If so, mutation depends on the neighbour's genotype, else we do a typical mutation
 * This class is based on AdjacencySolution and uses AdjacencyMaintainer
 * These mentioned classes distinguishes ourself by Neighbourhood knowledge
 * Due to fact that AdjacentSolution knows his neighbours, replacement must be extended
 * @param <S> type of Solution
 */
public class AdjacencyMOCell<S extends AdjacencySolution<S, ?>> extends MOCell<S> {
    private final AwardedSolutionSelector<S> awardSelector;

    public AdjacencyMOCell(Problem<S> problem, int maxEvaluations, int populationSize, BoundedArchive<S> archive, AdjacencyMaintainer<S> neighborhood, CrossoverOperator<S> crossoverOperator, AdjacencyMutationOperator<S> mutationOperator, SelectionOperator<List<S>, S> selectionOperator, SolutionListEvaluator<S> evaluator, AwardedSolutionSelector<S> awardSelector) {
        super(problem, maxEvaluations, populationSize, archive, neighborhood, crossoverOperator, mutationOperator, selectionOperator, evaluator);
        this.awardSelector = awardSelector;
    }

    /**
     * This method is using an AdjacencyMaintainer which takes direct neighbours into consideration
     * There, replacement is extended - we not only replace offspring in population
     * We also set old solution's neighbours to him, and change neighbours' old solution to newborn
     * @param population - list of solutions
     * @param offspringPopulation - list of newborns
     * @return - updated (or no) list of solutions
     */
    @Override
    protected List<S> replacement(List<S> population, List<S> offspringPopulation) {
        int flag = this.dominanceComparator.compare(population.get(this.currentIndividual), offspringPopulation.get(0));

        if (flag >= 0) {
            AdjacencyMaintainer<S> adjacencyNeighbourhood = (AdjacencyMaintainer<S>) neighborhood;
            S offspring = offspringPopulation.get(0);

            if (flag == 0) {
                this.currentNeighbors.add(offspring);
                Ranking<S> rank = new DominanceRanking<>();
                rank.computeRanking(this.currentNeighbors);
                CrowdingDistance<S> crowdingDistance = new CrowdingDistance<>();

                IntStream.range(0, rank.getNumberOfSubFronts()).forEach(index ->
                        crowdingDistance.computeDensityEstimator(rank.getSubFront(index)));

                this.currentNeighbors.sort(new RankingAndCrowdingDistanceComparator<>());
                if (offspring.equals(this.currentNeighbors.get(this.currentNeighbors.size() - 1))) {
                    adjacencyNeighbourhood.replaceWithNew(population, this.currentIndividual, offspring);
                }
            } else {
                adjacencyNeighbourhood.replaceWithNew(population, this.currentIndividual, offspring);
            }

            this.archive.add(offspring);
        }

        return population;
    }

    /**
     * MOCell extension is based on extending reproduction - mutation can be done dependently to one of the neighbour
     * The condition must be met - current individual has to have a specially awarded neighbour
     * Awarded neighbour is a neighbour that belongs to the best or the worst by fitness individuals (or random)
     * In this method we just calculate a set of awarded solutions in current iteration
     * Rest of functionality is simply delegated to mutation operator by setting iterAwardedIndividuals
     * @param population - list of solutions
     * @return classic offspring
     */
    @Override
    protected List<S> reproduction(List<S> population) {
        List<S> result = new ArrayList<>(1);
        List<S> offspring = this.crossoverOperator.execute(population);

        AdjacencyMutationOperator<?> adjacencyMutationOperator = (AdjacencyMutationOperator<?>) this.mutationOperator;
        adjacencyMutationOperator.setIterAwardedSolutions(awardSelector.getAllAwarded(population));

        this.mutationOperator.execute(offspring.get(0));
        result.add(offspring.get(0));
        return result;
    }
}
