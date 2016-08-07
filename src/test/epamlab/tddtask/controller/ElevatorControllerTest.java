package test.epamlab.tddtask.controller;

import main.epamlab.tddtask.beans.Elevator;
import main.epamlab.tddtask.beans.Floor;
import main.epamlab.tddtask.beans.House;
import main.epamlab.tddtask.beans.Passenger;
import main.epamlab.tddtask.builder.HouseBuilder;
import main.epamlab.tddtask.controller.ElevatorController;
import main.epamlab.tddtask.task.TransportationTask;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;

import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.inOrder;

/**
 * Created by Aliaksei Biazbubnau on 27.07.2016.
 */
public class ElevatorControllerTest {
    private Elevator elevator;
    private House house;
    private ElevatorController controller;

    @Before
    public void init() {
        house = HouseBuilder.buildHouse();
        elevator = house.getElevator();
        controller = new ElevatorController(house);
    }

    /**
     * ElevatorController moves elevator sequentially by floors, in up/down cycle.
     */
    @Test
    public void controllerMovesElevatorByOrderFloorByFloor() {
        int height = house.getHeight();
        for (int i = 1; i <= height; i++) {
            elevator.setCurrentLocation(i);
            assertEquals("Location of elevator should be on next floor by order", elevator.getCurrentLocation(), i);
            controller.startElevator();
        }
        for (int i = height; i >= 1; i--) {
            elevator.setCurrentLocation(i);
            assertEquals("Location could not be more then highest floor and less then first floor",
                    elevator.getCurrentLocation(), i);
            controller.startElevator();
        }
    }

    /**
     * ElevatorController moves elevator sequentially by floors, in up/down cycle.
     */
    @Test
    public void controllerMovesElevatorUpFromFirstFloor() {
        int currentFloorNumb = elevator.getCurrentLocation();
        assertThat("Default elevator location is 1", currentFloorNumb, is(1));
        controller = new ElevatorController(house);
        controller.startElevator();
        int newFloorNumb = elevator.getCurrentLocation();
        assertTrue("Location of elevator should be increased", currentFloorNumb < newFloorNumb);
    }

    /**
     * ElevatorController moves elevator sequentially by floors, in up/down cycle.
     */
    @Test
    public void controllerMovesElevatorDownFromLastFloor() {
        elevator.setCurrentLocation(house.getHeight());
        int currentFloorNumb = elevator.getCurrentLocation();
        assertThat("Elevator on last floor", currentFloorNumb, is(house.getHeight()));
        controller = new ElevatorController(house);
        controller.startElevator();
        int newFloorNumb = elevator.getCurrentLocation();
        assertTrue("Location of elevator should be decreased", currentFloorNumb > newFloorNumb);
    }

    /**
     * Controller puts passengers in elevator while elevator container isn’t full.
     */
    @Test
    public void controllerPutsPassengerInElevatorContainer() {
        Passenger passenger = new Passenger(44, 1, 5);
        controller.getInElevator(passenger);
        Floor floor = house.getFloor(1);
        Set<Passenger> passengersIn = elevator.getPassengersInside();
        assertTrue("passenger should be in elevator", passengersIn.contains(passenger));
    }

    /**
     * Controller puts passengers in elevator while elevator container isn’t full.
     */
    @Test
    public void controllerPutsPassengersInElevatorContainerNoMoreAsCapacity() {
        List<Passenger>passengers = createPassengers(elevator.getCapacity()+2);
        for(Passenger passenger:passengers){
            elevator.getIn(passenger);
        }
        assertThat("controller puts in elevator no more passengers as capacity", elevator.getCapacity(), is(elevator.countPassengersInside()));
    }

    /**
     * Controller puts passengers in elevator while elevator container isn’t full.
     */
    @Test
    public void controllerPutsPassengersInElevatorWhileThereArePassengersOnFloor() {
        int initial = 1;
        Floor floor = house.getFloor(initial);
        Set<Passenger> container = floor.getDispatchStoryContainer();
        List<Passenger> passengers = createPassengers(3);
        ElevatorController spyController = spy(new ElevatorController(house));
        if (container.size() == 0) {
            spyController.startElevator();
        } else {
            int countInside = elevator.countPassengersInside();
            verify(spyController, times(countInside)).getInElevator(any());
        }
    }

    /**
     * Controller puts passenger in elevator container and removes from dispatchContainer
     */
    @Test
    public void controllerPutsPassengerInElevatorContainerAndRemovesFromDispatchContainer() {
        Passenger passenger = new Passenger(44, 1, 5);
        controller.getInElevator(passenger);
        Floor floor = house.getFloor(1);
        Set<Passenger> passengersIn = elevator.getPassengersInside();
        assertTrue("passenger should be in elevator", passengersIn.contains(passenger));
        Set<Passenger> passengerOn = floor.getDispatchStoryContainer();
        assertFalse("Elevator container must contains passenger, story container - not",
                passengerOn.contains(passenger));
    }

    /**
     * Controller locates passenger from elevatorContainer to arrivalStoryContainer.
     */
    @Test
    public void controllerGetsPassengersOutFromElevatorAndPutsOnFloorInArrivalContainer() {
        Passenger passenger = new Passenger(100, 4, 5);
        controller.getInElevator(passenger);
        controller.getOutFromElevator(passenger);
        assertFalse("Elevator should not contains passenger inside now",
                elevator.getPassengersInside().contains(passenger));
        int location = elevator.getCurrentLocation();
        Floor floor = house.getFloor(location);
        assertTrue("Arrival container on target floor must contains passenger",
                floor.getArrivalStoryContainer().contains(passenger));
    }

    /**
     *Controller notifies passengers in elevator and passengers on floor.
     */
    @Test
    public void whenControllerExecutesNotifyPassengersOnFloorMethodPassengersOnFloorNotified() {
        int amount = house.getPassengersCount();
        locatePassengers(amount);

        for(int i = 1; i<= house.getHeight(); i++){
            Floor floor = house.getFloor(i);
            int number = floor.countPassengers();
            CountDownLatch latch = new CountDownLatch(number);
            controller.notifyPassengersOnFloor(latch);
            Set<Passenger> passengers = floor.getDispatchStoryContainer();
            for(Passenger passenger: passengers) {
                assertTrue("passenger should be notified", passenger.isNotified());
            }
            controller.startElevator();
        }
    }

    /**
     * Controller notifies passengers in elevator and passengers on floor.
     */
    @Test
    public void whenControllerExecutesNotifyInElevatorMethodPassengersInElevatorNotified() {
       int amount = house.getPassengersCount();
        List<Passenger>passengers = createPassengers(amount);
        locatePassengers(amount);

        for(int i = 0; i < elevator.getCapacity(); i++) {
            Passenger passenger = passengers.get(i);
            elevator.getIn(passenger);
            Floor floor = house.getFloor(passenger.getLocation());
            floor.sendPassenger(passenger);
        }

        CountDownLatch latch = new CountDownLatch(elevator.countPassengersInside());
        controller.notifyPassengersInElevator(latch);
        Set<Passenger> candidates = elevator.getPassengersInside();

        for(Passenger passenger: candidates){
            assertTrue("passengers should be notified", passenger.isNotified());
        }
    }

    /**
     *Passengers in elevator asked controller for getting out from elevator to floor.
     */
    @Test
    public void controllerPutsPassengerInElevatorAfterInvokeGetInElevatorMethod() {
        Passenger passenger = new Passenger(88, 3, 5);
        passenger.setElevatorController(controller);
        elevator.setCurrentLocation(3);
        controller.getInElevator(passenger);
        Set<Passenger> passengers = elevator.getPassengersInside();
        assertTrue("passenger should be located in elevator", passengers.contains(passenger));
    }

    /**
     * Passengers in elevator asked controller for getting out from elevator to floor.
     */
    @Test
    public void controllerGetOutPassengerFromElevatorAfterInvokeGetOutFromElevatorMethod() {
        Passenger passenger = new Passenger(55, 2, 5);
        passenger.setElevatorController(controller);
        elevator.setCurrentLocation(2);
        controller.getInElevator(passenger);
        controller.startElevator();
        controller.getOutFromElevator(passenger);
        Set<Passenger> passengersOnFloor = house.getFloor(elevator.getCurrentLocation()).getArrivalStoryContainer();
        Set<Passenger> elevatorContainer = elevator.getPassengersInside();
        assertFalse("elevator should be empty", elevatorContainer.contains(passenger));
        assertTrue("arrivalcontainer on floor should contains passenger", passengersOnFloor.contains(passenger));
    }

    private List<Passenger> createPassengers(int number) {
        List<Passenger> passengers = new ArrayList<>();
        for (int i = 0; i <= number; i++) {
            int location = 0;
            int destination = 0;
            int id = i;
            while (location == destination) {
                location = randomAttribute(number);
                destination = randomAttribute(number);
            }
            Passenger passenger = new Passenger(id, location, destination);
            passengers.add(passenger);
        } return passengers;
    }

    private void locatePassengers(int number){
        List<Passenger>passengers = createPassengers(number);
        for(Passenger passenger: passengers){
            Floor location = house.getFloor(passenger.getLocation());
            location.placePassenger(passenger);
        }
    }

    private int randomAttribute(int number){
        Random generator = new Random();
        int attribute = generator.nextInt(number - 1) + 1;
        return attribute;
    }

}





