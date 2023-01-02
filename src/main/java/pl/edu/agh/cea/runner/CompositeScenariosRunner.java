package pl.edu.agh.cea.runner;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CompositeScenariosRunner {
    public static void main(String[] args) {
        ExecutorService executor = Executors.newFixedThreadPool(2);

        executor.execute(() -> DefaultRewrittenJMetalScenario.main(new String[]{
                "org.uma.jmetal.problem.multiobjective.Schaffer"
        }));
        executor.execute(() -> AdjacencyDoubleMOCellRunner.main(new String[]{
                "pl.edu.agh.cea.problems.AdjacencyDoubleSchaffer"
        }));

        executor.shutdown();
    }
}
