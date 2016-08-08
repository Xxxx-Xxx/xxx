package main.epamlab.tddtask.task;

import main.epamlab.tddtask.beans.Passenger;
import main.epamlab.tddtask.enums.PassengerState;

import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;

/**
 * Created by Aliaksei Biazbubnau on 27.07.2016.
 */
public class TransportationTask implements Callable<Passenger> {
    private Passenger passenger;
    private CountDownLatch startSignal;
    private CountDownLatch doneSignal;

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
     * Define signal that mean the task finished.
     *
     * @param latch object.
     */
    public void setDoneSignal(final CountDownLatch latch) {
        this.doneSignal = latch;
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
            if (passenger.isNotified()) {
                execute();
            } else {
                synchronized (passenger) {
                    try {
                        passenger.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return passenger;
    }

    /**
     * Performed operations, suitable for current passengers state.
     */
    private void execute() {
        PassengerState state = passenger.getState();
        switch (state) {
            case NOT_STARTED:
                if (passenger.isNotified()) {
                    passenger.setState(PassengerState.IN_PROGRESS);
                    if (passenger.isTargetDirection()) {
                        passenger.getElevatorController().getInElevator(passenger);
                    }
                    passenger.setNotified(false);
                }
                break;

            case IN_PROGRESS:
                if (passenger.isNotified()) {
                    if (passenger.isTargetFloor()) {
                        passenger.getElevatorController().getOutFromElevator(passenger);
                        passenger.setState(PassengerState.COMPLETED);
                        doneSignal.countDown();
                        return;
                    }
                } else {
                    synchronized (passenger) {
                        try {
                            passenger.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
                break;

            default:
                startSignal.countDown();
                Thread.currentThread().interrupt();
                return;
        }
        doneSignal.countDown();
    }
}
