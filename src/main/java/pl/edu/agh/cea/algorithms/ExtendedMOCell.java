package pl.edu.agh.cea.algorithms;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.uma.jmetal.algorithm.multiobjective.mocell.MOCell;
import org.uma.jmetal.operator.crossover.CrossoverOperator;
import org.uma.jmetal.operator.mutation.MutationOperator;
import org.uma.jmetal.operator.selection.SelectionOperator;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.archive.BoundedArchive;
import org.uma.jmetal.util.evaluator.SolutionListEvaluator;
import org.uma.jmetal.util.neighborhood.Neighborhood;
import pl.edu.agh.cea.utils.AwardedSolutionSelector;

/**
 * Here MOCell is extended - we are able to select some specially awarded solutions per iteration
 * It means that we are looking for the best and the worst solutions by fitness + some randoms
 * Then we are using them to check, if current individual has a neighbour from the calculates list
 * If so, mutation depends on the neighbour's genotype, else we do a typical mutation
 * @param <S> type of Solution
 */
public class ExtendedMOCell<S extends Solution<?>> extends MOCell<S> {
    protected final AwardedSolutionSelector<S> awardSelector;

    public ExtendedMOCell(Problem<S> problem, int maxEvaluations, int populationSize, BoundedArchive<S> archive, Neighborhood<S> neighborhood, CrossoverOperator<S> crossoverOperator, MutationOperator<S> mutationOperator, SelectionOperator<List<S>, S> selectionOperator, SolutionListEvaluator<S> evaluator, AwardedSolutionSelector<S> awardSelector) {
        super(problem, maxEvaluations, populationSize, archive, neighborhood, crossoverOperator, mutationOperator, selectionOperator, evaluator);
        this.awardSelector = awardSelector;
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

        Set<S> iterAwardedIndividuals = awardSelector.getAllAwarded(population);

        // @TODO here must be put a logic that give iterAwardedIndividuals to mutationOperator (it can be a simply setter)
        // @TODO of course, special extended mutationOperator is also required (typical one don't allow us to depend on neighbours)
        // @TODO I recommend creating an interface which extends MutationOperator interface and has a field with setter
        // @TODO for holding iterAwardedIndividuals, then dependently of our client decision, we will be extending some of
        // @TODO already existing mutation operators and also implementing our interface what obligates us to implement setter

        this.mutationOperator.execute(offspring.get(0));
        result.add(offspring.get(0));
        return result;
    }
}
