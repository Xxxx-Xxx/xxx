package test.epamlab.tddtask.task;

import main.epamlab.tddtask.beans.Elevator;
import main.epamlab.tddtask.beans.Floor;
import main.epamlab.tddtask.beans.House;
import main.epamlab.tddtask.beans.Passenger;
import main.epamlab.tddtask.builder.HouseBuilder;
import main.epamlab.tddtask.controller.ElevatorController;
import main.epamlab.tddtask.enums.PassengerState;
import main.epamlab.tddtask.task.TransportationTask;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;

import java.util.*;
import java.util.concurrent.*;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.inOrder;



/**
 * Created by Aliaksei Biazbubnau on 07.08.2016.
 */
public class TransportationTaskTest {
    private House house;
    private ElevatorController controller;
    private Elevator elevator;

    /**
     * Prepared object for tests
     */
    @Before
    public void init() {
        house = HouseBuilder.buildHouse();
        controller = new ElevatorController(house);
        elevator = house.getElevator();
    }

//    /**
//     * Passenger gets status NOT_STARTED before the create his transportation task . And COMPLETED after.
//     */
//    @Test
//    public void passengersHaveNOTSTARTEDstatusBeforeTransportationTaskRuns() {
//        List<Passenger> passengers = new ArrayList<>();
//        Map<Integer, Floor> floors = house.getFloors();
//        for (Map.Entry<Integer, Floor> entry : floors.entrySet()) {
//            passengers.addAll(entry.getValue().getDispatchStoryContainer());
//        }
//
//        for (Passenger passenger : passengers) {
//            assertThat("passenger status on floors should be NOT_STARTED",
//                    passenger.getState(), is(PassengerState.NOT_STARTED));
//        }
//    }
//
//    /**
//     * After creating transportation task, Passenger in it gets status IN_PROGRESS.
//     */
//    @Test
//    public void passengerStatusChangedToINPROGRESSinTask() {
//        //Need to remove one passenger for correct validation
//        removePassenger();
//        movePassengersToArrivalStoryContainer(house.getPassengersCount() - 1);
//        Passenger passenger = spy(new Passenger(100, 1, 5));
//        Floor floor = house.getFloor(1);
//        floor.placePassenger(passenger);
//
//        CountDownLatch startSignal = new CountDownLatch(1);
//        TransportationTask task = new TransportationTask(startSignal, passenger);
//        MatcherAssert.assertThat(passenger.getState(), CoreMatchers.is(PassengerState.NOT_STARTED));
//        passenger.setTask(task);
//        passenger.setElevatorController(controller);
//        ExecutorService service = Executors.newCachedThreadPool();
//        service.submit(task);
//        service.submit(controller);
//        try {
//            startSignal.await();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        service.shutdown();
//
//        PassengerState expected = PassengerState.IN_PROGRESS;
//        verify(passenger).setState(expected);
//    }
//
//    /**
//     * Checks for Passenger gets status NOT_STARTED before the create his transportation task.
//     * And whether is: when transportation task finished passengers state should be COMPLETED
//     */
//    @Test
//    public void passengerInTaskHasCOMPLETEDstatusAfterTaskFinished() {
//        removePassenger();
//        movePassengersToArrivalStoryContainer(house.getPassengersCount());
//        Passenger passenger = new Passenger(100, 1, 5);
//        Floor floor = house.getFloor(1);
//        floor.placePassenger(passenger);
//
//        CountDownLatch startSignal = new CountDownLatch(1);
//        TransportationTask task = new TransportationTask(startSignal, passenger);
//        MatcherAssert.assertThat(passenger.getState(), CoreMatchers.is(PassengerState.NOT_STARTED));
//        passenger.setTask(task);
//        passenger.setElevatorController(controller);
//        ExecutorService service = Executors.newCachedThreadPool();
//        service.submit(task);
//        service.submit(controller);
//        try {
//            startSignal.await();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        service.shutdown();
//        assertThat("passengers state should be completed", passenger.getState(), is(PassengerState.COMPLETED));
//    }

//    /**
//     * Passengers should get in elevator when it moves in suitable direction.
//     */
//    @Test(timeout = 5000)
//    public void whenElevatorDirectionIsNotSuitablePassengerShouldNotTryGetInElevator() {
//        List<Passenger> passengers = getAllPassengerFromFloors();
//        Passenger removed = passengers.get(0);
//        Floor floor = house.getFloor(removed.getLocation());
//        floor.sendPassenger(removed);
//        controller = spy(new ElevatorController(house));
//
//        Passenger passenger = spy(new Passenger(66, 8, 1));
//        passengers.add(passenger);
//        CountDownLatch startSignal = new CountDownLatch(passengers.size());
//        List<Callable<Passenger>> callableList = new ArrayList<>(passengers.size());
//        for (Passenger person: passengers) {
//            TransportationTask task = new TransportationTask(startSignal, person);
//            passenger.setElevatorController(controller);
//            callableList.add(task);
//        }
//
//        ExecutorService service = Executors.newCachedThreadPool();
//        for (Callable<Passenger> task: callableList) {
//            service.submit(task);
//        }
//        service.submit(controller);
//        try {
//            startSignal.await();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        service.shutdown();
//        InOrder inOrder = inOrder(controller, passenger);
//        inOrder.verify(passenger).isTargetDirection();
//        verify(controller).getInElevator(passenger);
//        verify(controller, times(1)).getOutFromElevator(passenger);
//        Floor targetFloor = house.getFloor(3);
//        assertFalse("Passenger should not be in elevator", elevator.getPassengersInside().contains(passenger));
//        assertTrue("Passenger have to arrived", targetFloor.getArrivalStoryContainer().contains(passenger));
//    }
//
//    /**
//     * Passengers should get in elevator when it moves in suitable direction.
//     */
//    @Test(timeout = 5000)
//    public void whenPassengerOnFloorAndDirectionIsSuitableHeShouldGetsInElevatorAfterInvokedControllerMethod() {
//        movePassengersToArrivalStoryContainer(house.getPassengersCount() - 1);
//        Passenger passenger = spy(new Passenger(100, 1, 5));
//        Floor floor = house.getFloor(1);
//        floor.placePassenger(passenger);
//        CountDownLatch startSignal = new CountDownLatch(1);
//        TransportationTask task = new TransportationTask(startSignal, passenger);
//        ElevatorController controller = spy(new ElevatorController(house));
//        passenger.setTask(task);
//        passenger.setElevatorController(controller);
//        ExecutorService service = Executors.newCachedThreadPool();
//        service.submit(task);
//        service.submit(controller);
//        try {
//            startSignal.await();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        service.shutdown();
//        InOrder inOrder = inOrder(passenger, controller);
//        inOrder.verify(passenger, times(1)).isTargetDirection();
//        inOrder.verify(controller, times(1)).getInElevator(passenger);
//    }

//    /**
//     * Passenger doing depends on either he notified or not
//     */
//    @Test(timeout = 5000)
//    public void passengerAskForGetInElevatorAfterSetNotifiedByController() {
//        removePassenger();
//        movePassengersToArrivalStoryContainer(house.getPassengersCount() - 1);
//        Passenger passenger = spy(new Passenger(88, 5, 4));
//        ElevatorController controller = spy(new ElevatorController(house));
//        CountDownLatch startSignal = new CountDownLatch(1);
//
//        house.getFloor(5).placePassenger(passenger);
//        passenger.setElevatorController(controller);
//
//        ExecutorService service = Executors.newCachedThreadPool();
//        service.submit(new TransportationTask(startSignal, passenger));
//        service.submit(controller);
//        try {
//            startSignal.await();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        service.shutdown();
//        InOrder inOrder = inOrder(passenger, controller);
//        inOrder.verify(passenger).setNotified(true);
//        inOrder.verify(controller).getInElevator(passenger);
//    }
//
//    /**
//     * Passenger doing depends on either he notified or not
//     */
//    @Test(timeout = 5000)
//    public void passengerAskForGetOutFromElevatorAfterSetNotifiedByController() {
//        removePassenger();
//        movePassengersToArrivalStoryContainer(house.getPassengersCount() - 1);
//        Passenger passenger = spy(new Passenger(99, 3, 4));
//        ElevatorController controller = spy(new ElevatorController(house));
//        CountDownLatch startSignal = new CountDownLatch(1);
//
//        elevator.getIn(passenger);
//        passenger.setState(PassengerState.IN_PROGRESS);
//        passenger.setElevatorController(controller);
//
//        ExecutorService service = Executors.newCachedThreadPool();
//        service.submit(new TransportationTask(startSignal, passenger));
//        service.submit(controller);
//        try {
//            startSignal.await();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        service.shutdown();
//
//        InOrder inOrder = inOrder(passenger, controller);
//        inOrder.verify(passenger).setNotified(true);
//        inOrder.verify(controller).getOutFromElevator(passenger);
//    }
//
//    /**
//     * Passengers should leave elevator on their destination floor.
//     */
//    @Test(timeout = 5000)
//    public void whenElevatorLocationNotMatchWithNeededFloorPassengerRemainsInElevator() {
//        removePassenger();
//        movePassengersToArrivalStoryContainer(house.getPassengersCount() - 1);
//        Passenger passenger = spy(new Passenger(100, 3, 5));
//        controller = spy(new ElevatorController(house));
//        CountDownLatch startSignal = new CountDownLatch(1);
//
//        elevator.getIn(passenger);
//        passenger.setState(PassengerState.IN_PROGRESS);
//        passenger.setElevatorController(controller);
//
//        ExecutorService service = Executors.newCachedThreadPool();
//        service.submit(new TransportationTask(startSignal, passenger));
//        service.submit(controller);
//        try {
//            startSignal.await();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        service.shutdown();
//
//        verify(controller, times(1)).getOutFromElevator(passenger);
//
//        Map<Integer, Floor> floors = house.getFloors();
//        Floor floor = floors.get(4);
//        assertTrue("passenger should be arrived", floor.getArrivalStoryContainer().contains(passenger));
//    }
//
//    /**
//     * Passengers should get in elevator when it moves in suitable direction.
//     * Passengers should leave elevator on their destination floor.
//     */
//    @Test(timeout = 5000)
//    public void whenElevatorComeToFloorPassengerGetsInElevatorAndWhenTargetFloorPassengerShouldLeaveElevator() {
//        removePassenger();
//        movePassengersToArrivalStoryContainer(house.getPassengersCount() - 1);
//        Passenger passenger = new Passenger(100, 1, 5);
//        Floor floor = house.getFloor(1);
//        floor.placePassenger(passenger);
//        CountDownLatch startSignal = new CountDownLatch(1);
//        TransportationTask task = new TransportationTask(startSignal, passenger);
//        ElevatorController controller = spy(new ElevatorController(house));
//        passenger.setTask(task);
//        passenger.setElevatorController(controller);
//        ExecutorService service = Executors.newCachedThreadPool();
//        service.submit(task);
//        service.submit(controller);
//        try {
//            startSignal.await();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        service.shutdown();
//        Floor targetFloor = house.getFloor(5);
//        verify(controller, times(1)).getInElevator(passenger);
//        verify(controller, times(1)).getOutFromElevator(passenger);
//        Assert.assertFalse("passenger can't be in elevator", elevator.getPassengersInside().contains(passenger));
//        Assert.assertTrue("passenger should be in target floor",
//                targetFloor.getArrivalStoryContainer().contains(passenger));
//    }



    private List<Passenger> getAllPassengerFromFloors() {
        List<Passenger> candidates = new ArrayList<>();
        Map<Integer, Floor> floors = house.getFloors();
        for (Map.Entry<Integer, Floor> entry : floors.entrySet()) {
            candidates.addAll(entry.getValue().getDispatchStoryContainer());
        }
        return candidates;
    }

    /**
     * Removes passenger from its dispatchStoryContainer.
     * Method used in tests for save correct validation.
     */
    private void removePassenger() {
        List<Passenger> passengers = getAllPassengerFromFloors();
        Passenger passenger = passengers.get(0);
        int location = passenger.getLocation();
        Floor target = house.getFloor(location);
        target.sendPassenger(passenger);
    }

    /**
     * Utility method for some test providing.
     *
     * @param count number of passengers needed
     */
    private void movePassengersToArrivalStoryContainer(final int count) {
        List<Passenger> passengers = getAllPassengerFromFloors();
        int number = count;
        for (Passenger passenger : passengers) {
            if (number == 0) {
                return;
            }
            Floor destinationFloor = house.getFloor(passenger.getDestinationStory());
            Floor locationFloor = house.getFloor(passenger.getLocation());
            locationFloor.sendPassenger(passenger);
            destinationFloor.meetPassenger(passenger);
            passenger.setState(PassengerState.COMPLETED);
            --number;
        }
    }

    class FakeController extends ElevatorController{

        /**
         * Constructor used for initialize fields and initialize fields by their current or default values
         *
         * @param house House object, where controller will be working
         */
        public FakeController(House house) {
            super(house);
        }
    }
}

