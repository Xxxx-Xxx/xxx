package main.epamlab.tddtask.utility;

import main.epamlab.tddtask.beans.Elevator;
import main.epamlab.tddtask.beans.House;
import main.epamlab.tddtask.controller.ElevatorController;

/**
 * Created by Aliaksei Biazbubnau on 27.07.2016.
 */
public class TransportationCompleteValidator {

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



}
