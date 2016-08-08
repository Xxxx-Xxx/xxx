package main.epamlab.tddtask.beans;

import main.epamlab.tddtask.controller.ElevatorController;
import main.epamlab.tddtask.enums.Direction;
import main.epamlab.tddtask.enums.PassengerState;
import main.epamlab.tddtask.task.TransportationTask;


/**
 * Created by al on 7/8/16.
 */
public class Passenger {
    private final int id;
    private final int location;
    private final int destinationStory;
    private PassengerState state;
    private ElevatorController controller;
    private boolean notified;
    private TransportationTask task;

    /**
     * Constructor using fields
     *
     * @param id               passengers id
     * @param location         passengers initial location
     * @param destinationStory passengers destination floor.
     */
    public Passenger(final int id, final int location, final int destinationStory) {
        this.id = id;
        this.location = location;
        this.destinationStory = destinationStory;
  //      this.task = new TransportationTask(this);
        this.notified = false;
        state = PassengerState.NOT_STARTED;
    }

    /**
     * Returns passengers destination floor number
     *
     * @return floor number.
     */
    public int getDestinationStory() {
        return destinationStory;
    }

    /**
     * Returns number of floor, where passenger initially located
     *
     * @return floor number.
     */
    public int getLocation() {
        return location;
    }

    /**
     * Sets elevator controller object, which controlling transportation process
     *
     * @param controller elevator controller.
     */
    public void setElevatorController(final ElevatorController controller) {
        this.controller = controller;
    }

    /**
     * Return elevator controller object
     *
     * @return elevator controller object.
     */
    public ElevatorController getElevatorController() {
        return controller;
    }

    /**
     * Changed passwengers predefined state
     *
     * @param state new passengers state.
     */
    public void setState(final PassengerState state) {
        this.state = state;
    }

    /**
     * Returns passengers id
     *
     * @return id.
     */
    public int getId() {
        return id;
    }

    /**
     * Defines whether current floor is destination floor for passenger.
     *
     * @return true - if that floor is target, false - if not.
     */
    public boolean isTargetFloor() {
        int currentLocation = getCurrentLocation();
        if (currentLocation == destinationStory) {
            return true;
        }
        return false;
    }

    /**
     * Returns passengers current state
     *
     * @return passengers state
     */
    public PassengerState getState() {
        return state;
    }

    /**
     * Returns Transportation task, where this passenger is located
     *
     * @return Transportation task with this passenger.
     */
    public TransportationTask getTask() {
        return task;
    }

    /**
     * Sets task where passenger is incapsulated
     *
     * @param task Transportation task.
     */
    public void setTask(final TransportationTask task) {
        this.task = task;
    }

    /**
     * Checks whether current direction is suitable for passenger
     * Used in transportation task
     * @return true - if direction is proper for passenger, false - if not.
     */
    public boolean isTargetDirection() {
        boolean isSuitable = false;
        Direction direction = controller.getDirection();
        switch (direction) {
            case UP:
                isSuitable = (destinationStory > location);
                break;
            default:
                isSuitable = (destinationStory < location);
                break;
        }
        return isSuitable;
    }

    /**
     * Sets property "notified" for reference in future.
     *
     * @param notified true - if notified, false - if not.
     */
    public void setNotified(final boolean notified) {
        this.notified = notified;
    }

    /**
     * Checks whether passenger notified or not
     *
     * @return true - if notified, false - if not.
     */
    public boolean isNotified() {
        return notified;
    }

    /**
     * Returns current location of elevator, where passenger is.
     *
     * @return floor number.
     */
    public int getCurrentLocation() {
        return controller.getLocation();
    }

    @Override
    public String toString() {
        return "Passenger id: " + id + " with state " + state;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Passenger passenger = (Passenger) o;
        if (id != passenger.id) {
            return false;
        }
        if (location != passenger.location) {
            return false;
        }
        return destinationStory == passenger.destinationStory;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + location;
        result = 31 * result + destinationStory;
        return result;
    }
}

