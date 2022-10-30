package pl.edu.agh.cea.runner;

import java.io.FileNotFoundException;

/**
 * Modified default scenario - it takes ExtendedTwoDimensionalMesh as Neighbourhood
 */
public class ExtendedDefaultJMetalScenario {
    public static void main(String[] args) {
        try {
            ExtendedMOCellRunner.main(args);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
