package main.epamlab.tddtask.builder;

import main.epamlab.tddtask.beans.Elevator;
import main.epamlab.tddtask.beans.Floor;
import main.epamlab.tddtask.beans.House;
import main.epamlab.tddtask.beans.Passenger;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

/**
 * Created by al on 7/9/16.
 */
public class HouseBuilder {
    private static int storiesNumber;
    private static int elevatorCapacity;
    private static int passengersNumber;
    private final static String PROPERTIES_FILE_PATH = "src/resources/config.property";

    public HouseBuilder() {
    }

    /**
     * close inStrieam in finally!
     */
    private static void readProperty() {
        Properties properties = new Properties();
        try {
            FileInputStream inStream = new FileInputStream(PROPERTIES_FILE_PATH);
            properties.load(inStream);
            inStream.close();
            storiesNumber = Integer.valueOf(properties.getProperty("storiesNumber"));
            elevatorCapacity = Integer.valueOf(properties.getProperty("elevatorCapacity"));
            passengersNumber = Integer.valueOf(properties.getProperty("passengersNumber"));

        } catch (FileNotFoundException | NumberFormatException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static House build() {
        readProperty();
        Elevator elevator = new Elevator(elevatorCapacity);
        House house = new House(storiesNumber, passengersNumber, elevator);

        for (int i = 1; i <= passengersNumber; i++) {
            int location = 0;
            int destination = 0;
            int id = i;
            while (location == destination) {
                location = generateFloor();
                destination = generateFloor();
            }
            Passenger passenger = new Passenger(id, location, destination);
            house.placePassenger(passenger, location);
        }
        return house;
    }

    private static int generateFloor() {
        Random generator = new Random();
        int floorNumber = generator.nextInt(storiesNumber - 1) + 1;
        return floorNumber;
    }
}