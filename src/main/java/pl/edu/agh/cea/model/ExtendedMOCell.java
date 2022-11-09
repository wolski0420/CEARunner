package pl.edu.agh.cea.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import org.uma.jmetal.algorithm.impl.AbstractGeneticAlgorithm;
import org.uma.jmetal.operator.crossover.CrossoverOperator;
import org.uma.jmetal.operator.mutation.MutationOperator;
import org.uma.jmetal.operator.selection.SelectionOperator;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.archive.BoundedArchive;
import org.uma.jmetal.util.comparator.DominanceComparator;
import org.uma.jmetal.util.comparator.RankingAndCrowdingDistanceComparator;
import org.uma.jmetal.util.evaluator.SolutionListEvaluator;
import org.uma.jmetal.util.neighborhood.Neighborhood;
import org.uma.jmetal.util.solutionattribute.Ranking;
import org.uma.jmetal.util.solutionattribute.impl.CrowdingDistance;
import org.uma.jmetal.util.solutionattribute.impl.DominanceRanking;

public class ExtendedMOCell<S extends Solution<?>> extends AbstractGeneticAlgorithm<S, List<S>> {
    protected int evaluations;
    protected int maxEvaluations;
    protected final SolutionListEvaluator<S> evaluator;
    protected Neighborhood<S> neighborhood;
    protected int currentIndividual;
    protected List<S> currentNeighbors;
    protected BoundedArchive<S> archive;
    protected Comparator<S> dominanceComparator;

    public ExtendedMOCell(Problem<S> problem, int maxEvaluations, int populationSize, BoundedArchive<S> archive, Neighborhood<S> neighborhood, CrossoverOperator<S> crossoverOperator, MutationOperator<S> mutationOperator, SelectionOperator<List<S>, S> selectionOperator, SolutionListEvaluator<S> evaluator) {
        super(problem);
        this.maxEvaluations = maxEvaluations;
        this.setMaxPopulationSize(populationSize);
        this.archive = archive;
        this.neighborhood = neighborhood;
        this.crossoverOperator = crossoverOperator;
        this.mutationOperator = mutationOperator;
        this.selectionOperator = selectionOperator;
        this.dominanceComparator = new DominanceComparator();
        this.evaluator = evaluator;
    }

    protected void initProgress() {
        this.evaluations = 0;
        this.currentIndividual = 0;
        Iterator var1 = this.population.iterator();

        while(var1.hasNext()) {
            S solution = (S) var1.next();
            this.archive.add((S) solution.copy());
        }

    }

    protected void updateProgress() {
        ++this.evaluations;
        this.currentIndividual = (this.currentIndividual + 1) % this.getMaxPopulationSize();
    }

    protected boolean isStoppingConditionReached() {
        return this.evaluations == this.maxEvaluations;
    }

    protected List<S> evaluatePopulation(List<S> population) {
        population = this.evaluator.evaluate(population, this.getProblem());
        return population;
    }

    protected List<S> selection(List<S> population) {
        List<S> parents = new ArrayList(2);
        this.currentNeighbors = this.neighborhood.getNeighbors(population, this.currentIndividual);
        this.currentNeighbors.add(population.get(this.currentIndividual));
        parents.add(this.selectionOperator.execute(this.currentNeighbors));
        if (this.archive.size() > 0) {
            parents.add(this.selectionOperator.execute(this.archive.getSolutionList()));
        } else {
            parents.add(this.selectionOperator.execute(this.currentNeighbors));
        }

        return parents;
    }

    protected List<S> reproduction(List<S> population) {
        List<S> result = new ArrayList<>(1);
        List<S> offspring = this.crossoverOperator.execute(population);
        this.mutationOperator.execute((S) offspring.get(0));
        // I add only execution on second selected solution. Mutation shoudn't depend on another solution
        this.mutationOperator.execute((S) offspring.get(1));
        result.add((S) offspring.get(0));
        result.add((S) offspring.get(1));
        return result;
    }

    protected List<S> replacement(List<S> population, List<S> offspringPopulation) {
        int flag = this.dominanceComparator.compare((S) population.get(this.currentIndividual), (S) offspringPopulation.get(0));
        if (flag == 1) {
            population = this.insertNewIndividualWhenItDominatesTheCurrentOne(population, offspringPopulation);
        } else if (flag == 0) {
            population = this.insertNewIndividualWhenItAndTheCurrentOneAreNonDominated(population, offspringPopulation);
        }

        return population;
    }

    public List<S> getResult() {
        return this.archive.getSolutionList();
    }

    private List<S> insertNewIndividualWhenItDominatesTheCurrentOne(List<S> population, List<S> offspringPopulation) {
        population.set(this.currentIndividual, offspringPopulation.get(0));
        this.archive.add(offspringPopulation.get(0));
        return population;
    }

    private List<S> insertNewIndividualWhenItAndTheCurrentOneAreNonDominated(List<S> population, List<S> offspringPopulation) {
        this.currentNeighbors.add((S) offspringPopulation.get(0));
        Ranking<S> rank = new DominanceRanking();
        rank.computeRanking(this.currentNeighbors);
        CrowdingDistance<S> crowdingDistance = new CrowdingDistance();

        for(int j = 0; j < rank.getNumberOfSubFronts(); ++j) {
            crowdingDistance.computeDensityEstimator(rank.getSubFront(j));
        }

        Collections.sort(this.currentNeighbors, new RankingAndCrowdingDistanceComparator());
        S worst = this.currentNeighbors.get(this.currentNeighbors.size() - 1);
        this.archive.add(offspringPopulation.get(0));
        if (worst != offspringPopulation.get(0)) {
            population.set(this.currentIndividual, offspringPopulation.get(0));
        }

        return population;
    }

    public String getName() {
        return "ExtendedMOCell";
    }

    public String getDescription() {
        return "Extended Multi-Objective Cellular evolutionary algorithm";
    }
}
