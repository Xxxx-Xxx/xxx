package main.epamlab.tddtask.beans;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by al on 7/7/16.
 */
public class Elevator {
    private int currentLocation;
    private Set<Passenger> elevatorContainer;
    private final int capacity;
    private static final int DEFAULT_LOCATION = 1;

    /**
     * Constructor for getting Elevator object
     *
     * @param capacity elevator capacity
     */
    public Elevator(final int capacity) {
        this.capacity = capacity;
        this.elevatorContainer = new HashSet<>(capacity);
        currentLocation = DEFAULT_LOCATION;
    }

    /**
     * Accessor to get elevator capacity
     *
     * @return Elevator capacity
     */
    public int getCapacity() {
        return capacity;
    }

    /**
     * Returns current location of elevator
     *
     * @return location. Floor number.
     */
    public int getCurrentLocation() {
        return currentLocation;
    }

    /**
     * Mutator for change elevator location
     *
     * @param currentLocation new location. Floor number.
     */
    public void setCurrentLocation(final int currentLocation) {
        this.currentLocation = currentLocation;
    }

    /**
     * Puts passenger in elevator
     *
     * @param passenger object for action.
     */
    public void getIn(final Passenger passenger) {
        if (elevatorContainer.size() < capacity) {
            elevatorContainer.add(passenger);
        }
    }

    /**
     * Getting passenger out of elevator
     *
     * @param passenger object for action.
     */
    public void getOut(final Passenger passenger) {
        elevatorContainer.remove(passenger);
    }

    /**
     * Return all passengers who are inside elevator
     *
     * @return Set of passengers inside.
     */
    public Set<Passenger> getPassengersInside() {
        return elevatorContainer;
    }

    /**
     * Returns number of passengers inside elevator
     *
     * @return number of passengers.
     */
    public int countPassengersInside() {
        return elevatorContainer.size();
    }

    @Override
    public String toString() {
        return "Elevator now on " + currentLocation + "floor.";
    }
}

