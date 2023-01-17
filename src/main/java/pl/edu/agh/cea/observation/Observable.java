package pl.edu.agh.cea.observation;

/**
 * Interface for algorithms to be observed by some subscribers, mainly for gaining population data
 */
public interface Observable {
    /**
     * Subscribing object which wants to receive some data and manipulate it
     * @param subscriber - object receiving and manipulating data
     */
    void addFitnessSubscriber(Subscriber subscriber);

    void addHyperVolumeSubscriber(Subscriber subscriber);

    /**
     * Informs all subscribers about changes
     */
    void updateAll();
}
