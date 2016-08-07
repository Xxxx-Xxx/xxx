package test.epamlab.tddtask.builder;

import main.epamlab.tddtask.beans.Elevator;
import main.epamlab.tddtask.beans.Floor;
import main.epamlab.tddtask.beans.House;
import main.epamlab.tddtask.beans.Passenger;
import main.epamlab.tddtask.builder.HouseBuilder;
import org.junit.Before;
import org.junit.Test;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.*;

/**
 * Created by Aliaksei Biazbubnau on 07.08.2016.
 */
public class HouseBuilderTest {
    private int storiesNumber;
    private int elevatorCapacity;
    private int passengersNumber;
    private static final String PROPERTIES_FILE_PATH = "src/resources/config.property";
    private Properties properties = new Properties();

    private House house;

    @Before
    public void init(){
        readProperty();
        storiesNumber = Integer.valueOf(properties.getProperty("storiesNumber"));
        elevatorCapacity = Integer.valueOf(properties.getProperty("elevatorCapacity"));
        passengersNumber = Integer.valueOf(properties.getProperty("passengersNumber"));
    }

    @Test
    public void houseHasSuchFloorsAsInConfigFile(){
        house = new House (storiesNumber);
        int floorsNumber = house.getHeight();
        assertThat(storiesNumber, is(floorsNumber));
    }

    @Test
    public void everyFloorHasDispatchAndArrivalStoryContainer(){
        house = new House(storiesNumber);
        Map<Integer, Floor>floors = house.getFloors();
        for(Map.Entry<Integer,Floor> entry: floors.entrySet()){
            Set<Passenger>dispatchContainer = entry.getValue().getDispatchStoryContainer();
            Set<Passenger>arrivalContainer = entry.getValue().getArrivalStoryContainer();
            assertFalse(dispatchContainer == null);
            assertFalse(arrivalContainer == null);
        }
    }


    @Test
    public void houseHasElevatorCertainCapacity(){
        Elevator elevator = new Elevator(elevatorCapacity);
        house = new House (storiesNumber, passengersNumber, elevator);
        assertThat(house.getElevator(), instanceOf(Elevator.class));
        int elevatorCapacity = Integer.valueOf(properties.getProperty("elevatorCapacity"));
        assertThat("Elevator capacity in house should match with elevatorCapacity property",
                elevator.getCapacity(), is(elevatorCapacity));
    }

    @Test
    public void thereArePassengersOnFloorsSuchAsInConfigProperty(){
        Elevator elevator = new Elevator(elevatorCapacity);
        house = new House(storiesNumber, passengersNumber, elevator);
        addPassengers(house);
        Map<Integer, Floor> floors = house.getFloors();
        int count = 0;
        for(Map.Entry<Integer, Floor> entry: floors.entrySet()){
            Set<Passenger> passengerSet = entry.getValue().getDispatchStoryContainer();
            count += passengerSet.size();
        }
        assertThat(count, is(passengersNumber));
    }

    @Test
    public void passengersOnFloorsLocatesRandomly(){
        Elevator elevator = new Elevator(elevatorCapacity);
        House houseOne = new House(storiesNumber, passengersNumber, elevator);
        House houseTwo = new House(storiesNumber, passengersNumber, elevator);

        Set<Passenger> passengers = getAllPassengers(houseOne);
        Set<Passenger> otherPassengers = getAllPassengers(houseTwo);
        int matchCount = 0;
        for (Passenger passenger : passengers) {
            for (Passenger secondPassenger : otherPassengers) {
                if (passenger.equals(secondPassenger)) {
                    matchCount++;
                }
            }
        }
        assertTrue("Let assume that equal passenger by next house creation won't be more as some count",
                matchCount < 8);
    }

    private Set<Passenger> getAllPassengers(final House house) {
        Set<Passenger> result = new HashSet<>();
        Map<Integer, Floor> floors = house.getFloors();
        for (Map.Entry<Integer, Floor> entry : floors.entrySet()) {
            Set<Passenger> target = entry.getValue().getDispatchStoryContainer();
            result.addAll(target);
        }
        return result;
    }


    @Test
    public void everyPassengerHasUniqueId(){
        Elevator elevator = new Elevator(elevatorCapacity);
        House house = new House(storiesNumber, passengersNumber, elevator);

        List<Integer> ids = new ArrayList<>();
        Set<Passenger> passengers = getAllPassengers(house);
        for (Passenger passenger : passengers) {
            int id = passenger.getId();
            ids.add(id);
        }
        for (int id : ids) {
            int position = ids.indexOf(id);
            int secondPosition = ids.lastIndexOf(id);
            assertThat("There must be unique id for every passenger", position, is(secondPosition));
        }

    }

    @Test
    public void everyPassengerHasNotMatchingLocationAndDestinationStories(){
        Elevator elevator = new Elevator(elevatorCapacity);
        House house = new House(storiesNumber, passengersNumber, elevator);
        Set<Passenger> passengers = getAllPassengers(house);
        for (Passenger passenger : passengers) {
            int destination = passenger.getDestinationStory();
            int location = passenger.getLocation();
            assertThat("Location and destination shuldn't match", location, not(is(destination)));
        }
    }

    private Properties readProperty() {
        FileInputStream inStream = null;
        try {
            inStream = new FileInputStream(PROPERTIES_FILE_PATH);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
            properties.load(inStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            inStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return properties;

    }

    private int generateFloor() {
        Random generator = new Random();
        int floorNumber = generator.nextInt(storiesNumber - 1) + 1;
        return floorNumber;
    }

    private void addPassengers(House house) {
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
    }

}
