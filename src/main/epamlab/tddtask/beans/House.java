package main.epamlab.tddtask.beans;

import java.util.HashMap;
import java.util.Map;


/**
 * Created by al on 7/9/16.
 */
public class House {
    private final Map<Integer, Floor> floors;
    private final Elevator elevator;
    private final int height;
    private final int passengersCount;

    /**
     * Constructor for create House object
     * @param height    height
     * @param passengersNumber  passengers in house
     * @param elevator  elevator.
     */
    public House(final int height, final int passengersNumber, final Elevator elevator) {
        this.elevator = elevator;
        this.height = height;
        this.floors = initFloors();
        this.passengersCount = passengersNumber;
    }

    /**
     * Initialize floors
     * @return  floors in the house.
     */
    private  Map<Integer, Floor> initFloors() {
        Map<Integer, Floor> floors = new HashMap<>(height);
        for (int i = 1; i <= height; i++) {
            floors.put(i, new Floor());
        }
        return floors;
    }

    /**
     * Returns height of house
     * @return  height.
     */
    public int getHeight() {
        return height;
    }

    /**
     * Returns floor of the house
     * @return  floors.
     */
    public Map<Integer, Floor> getFloors() {
        return floors;
    }

    /**
     * Returns passengers count in the house
     * @return  number of passengers.
     */
    public int getPassengersCount() {
        return passengersCount;
    }

    /**
     * Returns elevator object
     * @return  elevator.
     */
    public Elevator getElevator() {
        return elevator;
    }

    /**
     * Returns concrete floor of house, defined by floor number
     * @param number    floor number
     * @return  floor.
     */
    public Floor getFloor(final int number) {
        return floors.get(number);
    }

    /**
     * Places passenger in concrete floor
     * @param passenger passenger
     * @param floorNumber   floor where passenger will be located.
     */
    public void placePassenger(final Passenger passenger, final int floorNumber) {
        Floor floor = floors.get(floorNumber);
        floor.placePassenger(passenger);
    }
}

