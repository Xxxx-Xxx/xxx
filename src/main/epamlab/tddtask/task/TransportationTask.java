package main.epamlab.tddtask.task;

import main.epamlab.tddtask.beans.Elevator;
import main.epamlab.tddtask.beans.Floor;
import main.epamlab.tddtask.beans.House;
import main.epamlab.tddtask.beans.Passenger;
import main.epamlab.tddtask.controller.ElevatorController;
import main.epamlab.tddtask.enums.PassengerState;

import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

/**
 * Created by Aliaksei Biazbubnau on 27.07.2016.
 */
public class TransportationTask implements Callable<Passenger> {
    private Passenger passenger;
    private CountDownLatch startSignal;

    /**
     * Constructor using fields
     *
     * @param latch     start signal, signaling that current thread finished its work.
     * @param passenger passenger that was puts in current task.
     */
    public TransportationTask(final CountDownLatch latch, final Passenger passenger) {
        this.startSignal = latch;
        this.passenger = passenger;
        passenger.setTask(this);
    }

    /**
     * Returns passenger who's in TransportationTask
     *
     * @return passenger.
     */
    public Passenger getPassenger() {
        return passenger;
    }

    /**
     * Computes a result, or throws an exception if unable to do so.
     *
     * @return computed result
     * @throws Exception if unable to compute a result
     */
    @Override
    public Passenger call() throws Exception {
        while (!Thread.currentThread().interrupted()) {
                doWait();
                execute();
            }
        return passenger;
    }

    /**
     * Performed operations, suitable for current passengers state.
     */
    private void execute() {
        ElevatorController controller = passenger.getElevatorController();
        CountDownLatch doneSignal = controller.getDoneSignal();
        PassengerState state = passenger.getState();
        switch (state) {
            case NOT_STARTED:
                    if (passenger.isTargetDirection()) {
                        controller.getInElevator(passenger);
                        passenger.setState(PassengerState.IN_PROGRESS);
                    }
                passenger.setNotified(false);
                break;

            case IN_PROGRESS:
                if (passenger.isTargetFloor()) {
                        controller.getOutFromElevator(passenger);
                        passenger.setState(PassengerState.COMPLETED);
                        doneSignal.countDown();
                        return;
                    }
                passenger.setNotified(false);
                break;

            default:
                doneSignal.countDown();
                startSignal.countDown();
                Thread.currentThread().interrupt();
                return;
        }
        doneSignal.countDown();
    }

    public void doWait(){
        ElevatorController controller = passenger.getElevatorController();
        PassengerState state = passenger.getState();
        House house = controller.getHouse();
        Lock lock;
        Condition condition;
        if(state == PassengerState.NOT_STARTED){
            Floor floor = house.getFloor(passenger.getLocation());
            lock = floor.getLock();
            condition = floor.getCondition();
        } else {
            Elevator elevator = house.getElevator();
            lock = elevator.getLock();
            condition = elevator.getCondition();
        }
        lock.lock();
        try {
            condition.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        lock.unlock();
    }
}
