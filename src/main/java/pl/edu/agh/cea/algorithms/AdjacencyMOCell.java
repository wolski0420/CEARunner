package pl.edu.agh.cea.algorithms;

import org.uma.jmetal.algorithm.multiobjective.mocell.MOCell;
import org.uma.jmetal.operator.crossover.CrossoverOperator;
import org.uma.jmetal.operator.selection.SelectionOperator;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.util.archive.BoundedArchive;
import org.uma.jmetal.util.comparator.RankingAndCrowdingDistanceComparator;
import org.uma.jmetal.util.densityestimator.DensityEstimator;
import org.uma.jmetal.util.densityestimator.impl.CrowdingDistanceDensityEstimator;
import org.uma.jmetal.util.evaluator.SolutionListEvaluator;
import org.uma.jmetal.util.ranking.Ranking;
import org.uma.jmetal.util.ranking.impl.FastNonDominatedSortRanking;
import pl.edu.agh.cea.fitness.AdjacencyFitnessCalculator;
import pl.edu.agh.cea.model.neighbourhood.AdjacencyMaintainer;
import pl.edu.agh.cea.model.solution.AdjacencySolution;
import pl.edu.agh.cea.observation.Observable;
import pl.edu.agh.cea.observation.Subscriber;
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
public class AdjacencyMOCell<S extends AdjacencySolution<S, ?>> extends MOCell<S> implements Observable {
    private final AwardedSolutionSelector<S> awardSelector;
    private final AdjacencyFitnessCalculator<S> fitnessCalculator;
    private final List<Subscriber> subscribers;

    public AdjacencyMOCell(Problem<S> problem, int maxEvaluations, int populationSize, BoundedArchive<S> archive, AdjacencyMaintainer<S> neighborhood, CrossoverOperator<S> crossoverOperator, AdjacencyMutationOperator<S> mutationOperator, SelectionOperator<List<S>, S> selectionOperator, SolutionListEvaluator<S> evaluator, AwardedSolutionSelector<S> awardSelector, AdjacencyFitnessCalculator<S> fitnessCalculator) {
        super(problem, maxEvaluations, populationSize, archive, neighborhood, crossoverOperator, mutationOperator, selectionOperator, evaluator);
        this.awardSelector = awardSelector;
        this.fitnessCalculator = fitnessCalculator;
        this.subscribers = new ArrayList<>();
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
                Ranking<S> rank = new FastNonDominatedSortRanking<>();
                rank.compute(this.currentNeighbors);
                DensityEstimator<S> crowdingDistance = new CrowdingDistanceDensityEstimator<>();

                IntStream.range(0, rank.getNumberOfSubFronts()).forEach(index ->
                        crowdingDistance.compute(rank.getSubFront(index))
                );

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

    /**
     * This MOCell extension needs to be aware of fitness in contrast to the base class
     * Calculating fitness must be done at the beginning of the execution and in all iterations
     * To avoid extending run method, there is first method from execution taken into consideration
     * Before initializing progress, fitness is being calculated and set to all the solutions
     */
    @Override
    protected void initProgress() {
        fitnessCalculator.calculate(this.population, this.problem);
        super.initProgress();
        updateAll();
    }

    /**
     * This MOCell extension needs to be aware of fitness in contrast to the base class
     * Calculating fitness must be done at the beginning of the execution and in all iterations
     * To avoid extending run method, there is one method inside iteration loop which update progress
     * Before update, fitness is being calculated again and set to all the solutions
     * After update, subscribers are
     */
    @Override
    protected void updateProgress() {
        fitnessCalculator.calculate(this.population, this.problem);
        super.updateProgress();
        updateAll();
    }

    @Override
    public void addSubscriber(Subscriber subscriber) {
        subscribers.add(subscriber);
    }

    @Override
    public void updateAll() {
        subscribers.forEach(subscriber -> subscriber.update(population));
    }
}
