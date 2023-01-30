package pl.edu.agh.cea.algorithms;

import org.uma.jmetal.algorithm.multiobjective.mocell.MOCell;
import org.uma.jmetal.operator.crossover.CrossoverOperator;
import org.uma.jmetal.operator.mutation.MutationOperator;
import org.uma.jmetal.operator.selection.SelectionOperator;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.archive.BoundedArchive;
import org.uma.jmetal.util.evaluator.SolutionListEvaluator;
import org.uma.jmetal.util.neighborhood.Neighborhood;
import pl.edu.agh.cea.fitness.FitnessCalculator;
import pl.edu.agh.cea.observation.Observable;
import pl.edu.agh.cea.observation.Subscriber;

import java.util.ArrayList;
import java.util.List;

public class RewrittenMOCell<S extends Solution<?>> extends MOCell<S> implements Observable {
    private final FitnessCalculator<S> fitnessCalculator;
    private final List<Subscriber> fitnessSubscribers;
    private final List<Subscriber> hyperVolumeSubscribers;

    public RewrittenMOCell(Problem<S> problem, int maxEvaluations, int populationSize, BoundedArchive<S> archive, Neighborhood<S> neighborhood, CrossoverOperator<S> crossoverOperator, MutationOperator<S> mutationOperator, SelectionOperator<List<S>, S> selectionOperator, SolutionListEvaluator<S> evaluator, FitnessCalculator<S> fitnessCalculator) {
        super(problem, maxEvaluations, populationSize, archive, neighborhood, crossoverOperator, mutationOperator, selectionOperator, evaluator);
        this.fitnessCalculator = fitnessCalculator;
        this.fitnessSubscribers = new ArrayList<>();
        this.hyperVolumeSubscribers = new ArrayList<>();
    }

    /**
     * This rewritten MOCell needs to be aware of fitness in contrast to the base class
     * because here we want to observe the difference between original and modified version of MOCell
     * Calculating fitness must be done at the beginning of the execution and in all iterations
     * To avoid extending run method, there is first method from execution taken into consideration
     * Before initializing progress, fitness is being calculated and set to all the solutions
     * After update, subscribers are notified about change
     */
    @Override
    protected void initProgress() {
        fitnessCalculator.calculate(this.population, this.problem);
        super.initProgress();
        updateAll();
    }

    /**
     * This MOCell extension needs to be aware of fitness in contrast to the base class
     * because here we want to observe the difference between original and modified version of MOCell
     * Calculating fitness must be done at the beginning of the execution and in all iterations
     * To avoid extending run method, there is one method inside iteration loop which update progress
     * Before update, fitness is being calculated again and set to all the solutions
     * After update, subscribers are notified about change
     */
    @Override
    protected void updateProgress() {
        fitnessCalculator.calculate(this.population, this.problem);
        super.updateProgress();
        updateAll();
    }

    @Override
    public void addFitnessSubscriber(Subscriber subscriber) {
        fitnessSubscribers.add(subscriber);
    }

    @Override
    public void addHyperVolumeSubscriber(Subscriber subscriber) {hyperVolumeSubscribers.add(subscriber);}

    @Override
    public void updateAll() {
        fitnessSubscribers.forEach(subscriber -> subscriber.update(population));
        hyperVolumeSubscribers.forEach(subscriber -> subscriber.update(population));
    }
}
