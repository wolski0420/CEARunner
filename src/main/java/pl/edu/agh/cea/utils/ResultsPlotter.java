package pl.edu.agh.cea.utils;

import com.github.sh0nk.matplotlib4j.NumpyUtils;
import com.github.sh0nk.matplotlib4j.Plot;
import com.github.sh0nk.matplotlib4j.PythonExecutionException;

import java.io.IOException;
import java.util.List;

public class ResultsPlotter {
    public void plotFitnessAvgPerEpoch(List<Double> averages, String additionalTitle) {
        List<Double> x = NumpyUtils.linspace(0, averages.size(), averages.size());

        try {
            Plot plt = Plot.create();
            plt.plot().add(x, averages).label("Fitness");
            plt.xlabel("Average fitness");
            plt.ylabel("Iteration");
            plt.legend().loc("upper right");
            plt.title("Avg fitness - " + additionalTitle);
            plt.show();
        } catch(PythonExecutionException | IOException e) {
            e.printStackTrace();
        }
    }

    public void plotHyperVolumeAvgPerEpoch(List<Double> averages, String additionalTitle) {
        List<Double> x = NumpyUtils.linspace(0, averages.size(), averages.size());

        try {
            Plot plt = Plot.create();
            plt.plot().add(x, averages).label("HyperVolume");
            plt.xlabel("Average HyperVolume");
            plt.ylabel("Iteration");
            plt.legend().loc("upper right");
            plt.title("Avg HyperVolume - " + additionalTitle);
            plt.show();
        } catch(PythonExecutionException | IOException e) {
            e.printStackTrace();
        }
    }
}
