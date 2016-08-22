package main.epamlab.tddtask.controller;

import main.epamlab.tddtask.beans.Elevator;
import main.epamlab.tddtask.beans.Floor;
import main.epamlab.tddtask.beans.House;
import main.epamlab.tddtask.beans.Passenger;
import main.epamlab.tddtask.enums.Direction;
import main.epamlab.tddtask.enums.ElevatorControllerAction;
import main.epamlab.tddtask.enums.PassengerState;
import main.epamlab.tddtask.utility.TransportationCompleteValidator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

/**
 * Created by Aliaksei Biazbubnau on 27.07.2016.
 */
public class ElevatorController implements Runnable {
    private static final int INDEX_1 = 1;
    private static final int INDEX_0 = 0;
    private House house;
    private Elevator elevator;
    private Direction direction;
    private ElevatorControllerAction action;
    private final Logger logger = LogManager.getLogger(ElevatorController.class);
    private TransportationCompleteValidator validator;
    private CountDownLatch doneSignal;


    /**
     * Constructor used for initialize fields and initialize fields by their current or default values
     *
     * @param house House object, where controller will be working
     */
    public ElevatorController(final House house) {
        this.house = house;
        this.elevator = house.getElevator();
        this.direction = Direction.UP;
        this.validator = TransportationCompleteValidator.getInstance(house);
        this.action = ElevatorControllerAction.STARTING_TRANSPORTATION;
        logger.info(action);
    }

    /**
     * Moves elevator on next floor
     */
    public void startElevator() {
        action = ElevatorControllerAction.MOVING_ELEVATOR;
        logger.info(action + " from story " + elevator.getCurrentLocation() + " to story " + nextFloor());
        elevator.setCurrentLocation(nextFloor());
    }

    /**
     * Define next floor
     *
     * @return int next floor number
     */
    private int nextFloor() {
        int next;
        if (isLastFloor()) {
            updateDirection();
        }
        if (direction == Direction.DOWN) {
            next = getLocation() - INDEX_1;
        } else {
            next = getLocation() + INDEX_1;
        }

        return next;
    }

    /**
     * Refresh direction in current moment
     */
    private void updateDirection() {
        Direction target = direction;
        if (isLastFloor()) {
            if (getLocation() == 1) {
                target = Direction.UP;
            } else {
                target = Direction.DOWN;
            }
        }
        direction = target;
    }

    /**
     * Checks current floor for conditions
     *
     * @return true    if floor number is 1 or last floor number in the building
     * false   otherwise
     */
    private boolean isLastFloor() {
        int upperLimit = house.getHeight();
        int location = getLocation();
        return (location == INDEX_1) || (location == upperLimit);
    }

    /**
     * Gets current location of elevator
     *
     * @return floor numbers where elevator located
     */
    public int getLocation() {
        return elevator.getCurrentLocation();
    }

    /**
     * Used of passenger to place itself in elevator by his demand.
     *
     * @param passenger passenger for operate
     */
    public synchronized void getInElevator(final Passenger passenger) {
        if (!isFull()) {
            action = ElevatorControllerAction.BOARDING_OF_PASSENGER;
            elevator.getIn(passenger);
            int location = elevator.getCurrentLocation();
            Floor floor = house.getFloor(location);
            floor.sendPassenger(passenger);
            elevator.getIn(passenger);
            passenger.setNotified(false);
            logger.info(action + " of passenger id:" + passenger.getId() + " on floor:" + location);
        }
    }

    /**
     * Checks elevator for its fullness
     *
     * @return true - if elevator is full, false - if not.
     */
    private boolean isFull() {
        return (elevator.getCapacity() == elevator.countPassengersInside());
    }

    /**
     * Used by passenger for get itself out from elevator
     *
     * @param passenger passenger for operate
     */
    public synchronized void getOutFromElevator(final Passenger passenger) {
        action = ElevatorControllerAction.DEBOARDING_OF_PASSENGER;
        elevator.getOut(passenger);
        int location = elevator.getCurrentLocation();
        Floor floor = house.getFloor(location);
        floor.meetPassenger(passenger);
        logger.info(action + " of passenger id:" + passenger.getId() + " on floor:" + location);
        passenger.setState(PassengerState.COMPLETED);
    }

    /**
     * Sequence of actions that will be run on the floor.
     */
    public void executeOnFloor() {
        action = ElevatorControllerAction.BOARDING_OF_PASSENGER;
        logger.info(action + " on " + elevator.getCurrentLocation() + " floor");
        Floor current = house.getFloor(getLocation());
        int candidates = current.getDispatchStoryContainer().size();
        if (candidates == INDEX_0) {
            return;
        }
        doneSignal = new CountDownLatch(candidates);
        Lock lock = current.getLock();
        Condition condition = current.getCondition();
        notifyPassengers(lock, condition);
        try {
            doneSignal.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Actions that will be executes for passengers located in elevator.
     */
    public void executeInElevator() {
        action = ElevatorControllerAction.DEBOARDING_OF_PASSENGER;
        logger.info(action + " on " + elevator.getCurrentLocation() + " floor");
        int passengers = elevator.countPassengersInside();
        if (passengers == 0) {
            return;
        }
        doneSignal = new CountDownLatch(passengers);
        Lock lock = elevator.getLock();
        Condition condition = elevator.getCondition();
        notifyPassengers(lock, condition);
        try {
            doneSignal.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            executeOnFloor();
            startElevator();
            executeInElevator();
            if (validator.isAllFinished()) {
                stop();
            }
        }
    }

    /**
     * Complete transportation as logging and finished thread.
     */
    private void stop() {
        action = ElevatorControllerAction.COMPLETION_TRANSPORTATION;
        logger.info(action);
        Thread.currentThread().interrupt();
    }

    /**
     * Returns direction that elevator should move
     * Direction is used by the passenger to determine whether to enter the elevator or not.
     * And also by controller, for define next floor to move.
     *
     * @return direction
     */
    public Direction getDirection() {
        return direction;
    }


    private void notifyPassengers(Lock lock, Condition condition){
        lock.lock();
        condition.signalAll();
        lock.unlock();

    }

    public House getHouse(){
        return house;
    }

    public CountDownLatch getDoneSignal() {
        return doneSignal;
    }

}
