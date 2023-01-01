package pl.edu.agh.cea.utils;

import com.github.sh0nk.matplotlib4j.NumpyUtils;
import com.github.sh0nk.matplotlib4j.Plot;
import com.github.sh0nk.matplotlib4j.PythonExecutionException;

import java.io.IOException;
import java.util.List;

public class ResultsPlotter {
    public void plotFitnessAvgPerEpoch(List<Double> averages) {
        List<Double> x = NumpyUtils.linspace(0, averages.size(), averages.size());

        try {
            Plot plt = Plot.create();
            plt.plot().add(x, averages, "o").label("Fitness");
            plt.legend().loc("upper right");
            plt.title("Avg. fitness per iterations");
            plt.show();
        } catch(PythonExecutionException | IOException e) {
            e.printStackTrace();
        }
    }
}
