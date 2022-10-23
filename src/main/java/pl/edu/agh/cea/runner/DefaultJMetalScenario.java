package pl.edu.agh.cea.runner;

import org.uma.jmetal.example.multiobjective.mocell.MOCellRunner;

import java.io.FileNotFoundException;

/**
 *  Default scenario borrowed from jMetal examples for Cellular Evolutionary Algorithm
 */
public class DefaultJMetalScenario {
    public static void main(String[] args) {
        try {
            MOCellRunner.main(args);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
