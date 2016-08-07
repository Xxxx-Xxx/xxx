package main.epamlab.tddtask.beans;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by al on 27/7/16.
 */
public class Floor {

    private Set<Passenger> dispatchStoryContainer;
    private Set<Passenger> arrivalStoryContainer;

    /**
     * Returns container that contains passengers in this floor, which are ready for transportation.
     * @return  container with passengers
     */
    public Set<Passenger> getDispatchStoryContainer() {
        return dispatchStoryContainer;
    }

    /**
     * Returns container from this floor that contains passengers which was arrived on this floor.
     * @return  container with arrived passengers.
     */
    public Set<Passenger> getArrivalStoryContainer() {
        return arrivalStoryContainer;
    }

    /**
     * Default constructor.
     * Initialize required containers.
     */
    public Floor() {
        dispatchStoryContainer = new HashSet<>();
        arrivalStoryContainer = new HashSet<>();
    }

    /**
     * Returns number of passengers located in this floor.
     * @return  passengers number.
     */
    public int countPassengers() {
        return dispatchStoryContainer.size();
    }

    /**
     * Places passenger as ready for transportation.
     * @param passenger passenger.
     */
    public void placePassenger(final Passenger passenger) {
        dispatchStoryContainer.add(passenger);
    }

    /**
     * Meet passenger in this floor, puts him into container for arrivals.
     * @param passenger passenger.
     */
    public void meetPassenger(final Passenger passenger) {
        arrivalStoryContainer.add(passenger);
    }

    /**
     * Mark passenger as send, so, passenger will be removed from dispatch container.
     * @param passenger passenger.
     */
    public void sendPassenger(final Passenger passenger) {
        dispatchStoryContainer.remove(passenger);
    }

}
