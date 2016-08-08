import main.epamlab.tddtask.beans.Floor;
import main.epamlab.tddtask.beans.House;
import main.epamlab.tddtask.beans.Passenger;
import main.epamlab.tddtask.builder.HouseBuilder;
import main.epamlab.tddtask.controller.ElevatorController;
import main.epamlab.tddtask.task.TransportationTask;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Aliaksei Biazbubnau on 30.07.2016.
 */
final class Runner {
    private static House house;
    private static CountDownLatch startSignal;
    private Runner() { }
    public static void main(final String[] args) {
        house = HouseBuilder.buildHouse();
        ElevatorController controller = new ElevatorController(house);
        ExecutorService service = Executors.newCachedThreadPool();
        service.submit(controller);
        startAllTransportationTasks();
        try {
            startSignal.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        service.shutdown();
    }

    private static void startAllTransportationTasks() {
        ExecutorService service = Executors.newCachedThreadPool();
        Set<Passenger> passengers = new HashSet<>();
        Map<Integer, Floor> floors = house.getFloors();
        for (Map.Entry<Integer, Floor> entry : floors.entrySet()) {
            Set<Passenger> passenger = entry.getValue().getDispatchStoryContainer();
            passengers.addAll(passenger);
        }
        int number = passengers.size();
        startSignal = new CountDownLatch(number);
        for (Passenger passenger:passengers) {
            TransportationTask task = new TransportationTask(startSignal, passenger);
            passenger.setTask(task);
            service.submit(task);
        }
    }
}
