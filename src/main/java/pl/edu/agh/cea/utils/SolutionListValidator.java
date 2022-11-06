package pl.edu.agh.cea.utils;

import org.uma.jmetal.util.JMetalException;

import java.util.List;
import java.util.Objects;

/**
 * Class for validating solution list
 */
public class SolutionListValidator {
    /**
     * Method checks List of Solution violations
     * @param solutionList - list of solutions
     * @param solutionPosition - index of solution in given list
     * @param sizeGiven - given size of the list to check
     */
    public static void checkSolutionListViolations(List<?> solutionList, int solutionPosition, int sizeGiven) {
        if (Objects.isNull(solutionList)) {
            throw new JMetalException("The solution list is null") ;
        } else if (solutionList.isEmpty()) {
            throw new JMetalException("The solution list is empty") ;
        } else if (solutionPosition < 0) {
            throw new JMetalException("The solution position value is negative: " + solutionPosition) ;
        } else if (solutionList.size() != sizeGiven) {
            throw new JMetalException("The solution list size " + solutionList.size() + " is not"
                    + "equal to the grid size: " + sizeGiven) ;
        }
        else if (solutionPosition >= solutionList.size()) {
            throw new JMetalException("The solution position value " + solutionPosition +
                    " is equal or greater than the solution list size "
                    + solutionList.size()) ;
        }
    }
}
