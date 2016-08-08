package test.epamlab.tddtask.utility;

import main.epamlab.tddtask.beans.Elevator;
import main.epamlab.tddtask.beans.Floor;
import main.epamlab.tddtask.beans.House;
import main.epamlab.tddtask.beans.Passenger;
import main.epamlab.tddtask.builder.HouseBuilder;
import main.epamlab.tddtask.enums.PassengerState;
import main.epamlab.tddtask.utility.TransportationCompleteValidator;
import org.junit.Before;
import org.junit.Test;

import java.util.*;
import static junit.framework.Assert.assertTrue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;


/**
 * Created by Aliaksei Biazbubnau on 08.08.2016.
 */
public class TransportationCompleteValidatorTest {
    private House house;
    private Elevator elevator;
    private TransportationCompleteValidator validator;

    @Before
    public void init(){
        house = HouseBuilder.buildHouse();
        elevator = house.getElevator();
        validator = TransportationCompleteValidator.getInstance(house);
    }

    /**
     *Elevator controller should be empty  as requirement of validation.
     */
    @Test
    public void whenElevatorContainerIsEmptyValidationPass(){
        assertTrue(elevator.countPassengersInside() == 0);
        assertThat("Should return true, because elevator is empty", validator.isElevatorContainerEmpty(), is(true));
    }

    /**
     * Elevator controller should be empty  as requirement of validation.
     */
    @Test
    public void whenElevatorContainerIsNotEmptyValidationFail(){
        Passenger passenger = new Passenger(2,4,5);
        elevator.getIn(passenger);
        assertThat("Should return false, elevator not empty", validator.isElevatorContainerEmpty(), is(false));
    }

    /**
     * All dispatch containers should be empty
     */
    @Test
    public void whenAllDispatchStoryContainersEmptyValidationPass(){
        moveAllPassengersToArrivalStoryContainer();
        List<Passenger> passengers = getAllPassengerFromFloors();
        assertThat("If not null that not all dispatch containers empty", passengers.size(), is(0));
        assertThat("All dispatch story containers should be empty", validator.isAllDispatchStoryContainersEmpty(), is(true));
    }

    /**
     * All dispatch containers should be empty
     */
    @Test
    public void whenOneDispatchStoryNotEmptyValidationFail(){
        List<Passenger> passengers = getAllPassengerFromFloors();
        Passenger passenger = passengers.get(0);
        moveAllPassengersToArrivalStoryContainer();
        house.getFloor(passenger.getLocation()).sendPassenger(passenger);
        passengers.remove(passenger);
        Passenger person = new Passenger(100,2,3);
        house.getFloor(1).placePassenger(person);
        List<Passenger> newPassengers = getAllPassengerFromFloors();
        assertThat("Should be one passenger on floors", newPassengers.size(), is(1));
        assertThat("Validation should be failed", validator.isAllDispatchStoryContainersEmpty(), is(false));
    }

    /**
     * Amount of people in arrivalStoryContainers should be equals with passengersNumber
     */
    @Test
    public void whenAllPassengersArrivedValidationPass(){
        moveAllPassengersToArrivalStoryContainer();
        assertThat("All passengers in their arrival containers and have status COMPLETE. Validation pass.", validator.isAllPassengersArrived(), is(true));
    }

    /**
     * All passengers in arrivalStoryContainers should have COMPLETE transportationState,
     * and their destinations stories should be match with current location of their arrivalStory containers.
     */
    @Test
    public void whenAllPassengersArrivedAndArrivedCorrectlyValidationPass(){
        moveAllPassengersToArrivalStoryContainer();
        assertThat("All passengers in their arrival containers and have status COMPLETE. Validation pass.", validator.isAllPassengersArrived(), is(true));
        assertThat("All passengers located on their destination floors and COMPLETED, validation is pass",
                validator.isAllPassengersArrivedCorrectly(), is(true));
    }

    /**
     * All passengers in arrivalStoryContainers should have COMPLETE transportationState,
     * and their destinations stories should be match with current location of their arrivalStory containers.
     */
    @Test
    public void whenNotAllPassengersArrivedAndNotCorrectlyArrivedValidationFail(){
        List<Passenger> passengers = getAllPassengerFromFloors();
        Passenger first = passengers.get(0);
        Passenger second = passengers.get(1);
        moveAllPassengersToArrivalStoryContainer();

        first.setState(PassengerState.IN_PROGRESS);
        assertThat("Not all passengers have status COMPLETED, validation fail", validator.isAllPassengersArrivedCorrectly(), is(false));

        house.getFloor(second.getDestinationStory()).getArrivalStoryContainer().remove(second);
        int destination = second.getDestinationStory();
        int wrongLocation = (destination == house.getHeight() ? house.getHeight()-1: destination++);
        house.getFloor(wrongLocation).meetPassenger(second);

        assertThat("Not all passengers arrived correctly, validation fail", validator.isAllPassengersArrivedCorrectly(), is(false));

        Floor floor = house.getFloor(first.getDestinationStory());
        floor.getArrivalStoryContainer().remove(first);
        assertThat("Amount of passengers not match with default passengers number in house. Validation fail",
                validator.isAllPassengersArrived(), is(false));
    }

    /**
     * Validation is passed when all states matches with requirements
     */
    @Test
    public void whenAllInAccordingRequirementFinalValidationPass(){
        moveAllPassengersToArrivalStoryContainer();
        assertThat("All states by requirement. Validation should be passed", validator.isAllFinished(), is(true));
    }

    private List<Passenger> getAllPassengerFromFloors(){
        List<Passenger> candidates = new ArrayList<>();
        Map<Integer, Floor> floors = house.getFloors();
        for (Map.Entry<Integer, Floor> entry : floors.entrySet()){
            candidates.addAll(entry.getValue().getDispatchStoryContainer());
        } return candidates;
    }

    private void moveAllPassengersToArrivalStoryContainer() {
        List<Passenger> passengers = getAllPassengerFromFloors();
        for (Passenger passenger: passengers){
            house.getFloor(passenger.getLocation()).sendPassenger(passenger);
            house.getFloor(passenger.getDestinationStory()).meetPassenger(passenger);
            passenger.setState(PassengerState.COMPLETED);
        }
    }
}
