package main.epamlab.tddtask.utility;

import main.epamlab.tddtask.beans.Elevator;
import main.epamlab.tddtask.beans.Floor;
import main.epamlab.tddtask.beans.House;
import main.epamlab.tddtask.beans.Passenger;
import main.epamlab.tddtask.controller.ElevatorController;
import main.epamlab.tddtask.enums.PassengerState;

import java.util.Map;
import java.util.Set;

/**
 * Created by Aliaksei Biazbubnau on 27.07.2016.
 */
public final class TransportationCompleteValidator {

    private static final int INDEX_0 = 0;
    private static ElevatorController controller;
    private House house;
    private Elevator elevator;
    private static TransportationCompleteValidator instance;

    private TransportationCompleteValidator() {
    }

    private TransportationCompleteValidator(final House house) {
        this.house = house;
        this.elevator = house.getElevator();
    }

    /**
     * Returns instance of this class
     *
     * @param house House object
     * @return instance
     */
    public static TransportationCompleteValidator getInstance(final House house) {
        if (instance == null) {
            return new TransportationCompleteValidator(house);
        }
        return instance;
    }

    /**
     * Method for complex validation  of all transportation processes by requirement
     * Gather all checks from current class private methods
     * @return true - when all completes in right conditions, falst - otherwise
     */
    public boolean isAllFinished() {
        boolean result;
        result = isAllDispatchStoryContainersEmpty()
                && isElevatorContainerEmpty() && isAllPassengersArrived()
                && isAllPassengersArrivedCorrectly();
        return result;
    }

    /**
     * Checks dispatch containers for passengers.
     * @return true - when there are not passengers in dispatch containers, false - otherwise.
     */
    public boolean isAllDispatchStoryContainersEmpty() {
        boolean result = false;
        Map<Integer, Floor> floors = house.getFloors();
        for (Map.Entry<Integer, Floor> entry : floors.entrySet()) {
            if (!(entry.getValue().getDispatchStoryContainer().isEmpty())) {
                return false;
            } else {
                result = true;
            }
        }
        return result;
    }

    /**
     * Elevator container should be empty for successful validation
     * @return  true - elevator empty;  false - elevator container is not empty
     */
    public boolean isElevatorContainerEmpty() {
        return elevator.countPassengersInside() == INDEX_0;
    }

    /**
     *Checks arrival containers for amount of passengers as total in house.
     * @return  true - if match with total, false - otherwise.
     */
    public boolean isAllPassengersArrived() {
        Map<Integer, Floor> floors = house.getFloors();
        int target = 0;
        for (Map.Entry<Integer, Floor> entry : floors.entrySet()) {
            target += entry.getValue().getArrivalStoryContainer().size();
        }

        if (target == house.getPassengersCount()) {
            return true;
        }
        return false;
    }

    /**
     * Checks all arrived passengers for status COMPLETED and for location in right arrival container, as their destinationStory.
     * @return  true - if all completed and destination story match with current floor. false - otherwise.
     */
    public boolean isAllPassengersArrivedCorrectly() {
        boolean result = false;
        Map<Integer, Floor> floors = house.getFloors();
        for (Map.Entry<Integer, Floor> entry : floors.entrySet()) {
            int floorNumber = entry.getKey();
            Floor floor = entry.getValue();
            Set<Passenger> passengers = floor.getArrivalStoryContainer();
            for (Passenger passenger : passengers) {
                result = ((passenger.getState() == PassengerState.COMPLETED)
                        && (passenger.getDestinationStory() == floorNumber));
                if (!result) {
                    return result;
                }
            }
        }
        return result;
    }

}
