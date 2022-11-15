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

import java.util.List;
import java.util.stream.IntStream;

/**
 * It has the same behaviour as the super class - ExtendedMOCell
 * But this class is bases on AdjacencySolution and uses AdjacencyMaintainer
 * These mentioned classes differ from typical ones by Neighbourhood knowledge
 * Due to fact that AdjacentSolution knows his neighbours, replacement must be extended
 * @param <S> - type of AdjacentSolution
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
}
