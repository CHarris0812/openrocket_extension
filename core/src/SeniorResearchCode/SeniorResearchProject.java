package SeniorResearchCode;

import net.sf.openrocket.document.OpenRocketDocument;
import net.sf.openrocket.file.GeneralRocketLoader;
import net.sf.openrocket.file.RocketLoadException;
import net.sf.openrocket.file.openrocket.OpenRocketSaver;
import net.sf.openrocket.material.Material;
import net.sf.openrocket.optimization.general.OptimizationException;
import net.sf.openrocket.rocketcomponent.*;
import net.sf.openrocket.simulation.exception.SimulationException;
import net.sf.openrocket.unit.Unit;
import net.sf.openrocket.unit.UnitGroup;

import java.io.*;
import java.util.HashMap;
import java.util.Scanner;

public class SeniorResearchProject{
    private Rocket originalRocket;
    private File fileToSaveTo, fileToLoadFrom;
    private RocketParameter changeableParameters;
    private double minStability, maxStability, minMass, maxMass, minGoalHeight, maxGoalHeight, minTime, maxTime;
    private boolean useDefault;
    private HashMap<Class, int[]> componentLocations = new HashMap<Class, int[]>();
    private int maxIterations;


    /*
    Take input from the user on what the program should do
     */
    public SeniorResearchProject() throws RocketLoadException, OptimizationException {
        Setup setup = new Setup();
        defineValues(setup);

        doProject();
    }

    public SeniorResearchProject(OpenRocketDocument document) {
        originalRocket = document.getRocket();
    }

    /**
     * Main method. Modifies rocket repeatedly until it is good.
     * @throws OptimizationException
     */
    //TODO: Medium: Have the rocket reset after a certain number of changes if nothing works
    //TODO: HIGH: Make it so that instead of saving the rocket it displays the new rocket
    public Rocket doProject() throws OptimizationException {
        int count = 0;
        Rocket rocket = originalRocket;
        RocketSimulation rocketSimulation;

        indexLocationOfComponents();

        changeableParameters = new RocketParameter(originalRocket.getChild(0), true);

        try {
            rocketSimulation = new RocketSimulation(originalRocket);
        } catch (SimulationException s) {
            throw new RuntimeException("Failed to create the simulation");
        }

        System.out.println("Initial apogee: " + rocketSimulation.getHeight() * 3.281 + " feet");

        if(isComplete(rocketSimulation)){
            //Check if original rocket is correct. If so, end.
            System.out.println("The original rocket works just fine you dummy");
        }

        RocketModifier rocketModifier = new RocketModifier(rocket, rocketSimulation, changeableParameters,
                minStability, maxStability, minMass, maxMass, minGoalHeight, maxGoalHeight, minTime, maxTime, componentLocations);
        while(!isComplete(rocketSimulation) && count < maxIterations){
            //rocket = changeOneThingRandomly(rocket);
            rocket = rocketModifier.modifyRocket();
            try {
                rocketSimulation = new RocketSimulation(rocket);
            } catch (SimulationException e) {
                throw new RuntimeException("sim is fucked up");
            }

            System.out.println("New apogee: " + rocketSimulation.getHeight() * 3.281 + " feet");
            rocketModifier.setSimulation(rocketSimulation);
            count++;
        }

        if (count < maxIterations) {
            //TODO: MEDIUM: CHange this to a pop up
            System.out.println("Rocket Successfully Modified");
            /*try {
                saveRocket(rocket);
            } catch (IOException e) {
                throw new RuntimeException("Could not save rocket");
            }*/
        } else {
            //TODO: MEDIUM: Change this to a pop up
            System.out.println("The program couldn't finish in time.");
            System.out.println("The current progress of the rocket will still be saved.");
            System.out.println("Put in better parameters next time.");
        }

        return rocket;
    }

    /**
     * A method to save the completed rocket to a .ork file
     *
     * @param rocket            The rocket that should be saved
     * @throws IOException      Throws exception if the file cannot be accessed
     */
    private void saveRocket(Rocket rocket) throws IOException {
        OpenRocketDocument documentToSave = new OpenRocketDocument(rocket);
        OpenRocketSaver saver = new OpenRocketSaver();
        saver.save(new FileOutputStream(fileToSaveTo), documentToSave, documentToSave.getDefaultStorageOptions());
        System.out.println("Rocket is finished and saved");
    }

    private void defineValues(Setup setup) {
        originalRocket = setup.getOriginalRocket();
        fileToSaveTo = setup.getFileToSaveTo();
        changeableParameters = setup.getChangeableParameters();
        minStability = setup.getMinStability();
        maxStability = setup.getMaxStability();
        minMass = setup.getMinMass();
        maxMass = setup.getMaxMass();
        minGoalHeight = setup.getMinGoalHeight();
        maxGoalHeight = setup.getMaxGoalHeight();
        minTime = setup.getMinTime();
        maxTime = setup.getMaxTime();
        componentLocations = setup.getComponentLocations();
        maxIterations = setup.getMaxIterations();
    }

    /**
     * Method to check if a given rocket meets all the parameters
     *
     * @param sim       A simulation of how the rocket performed
     * @return          A boolean saying whether or not the rocket meets all the parameters.
     */
    //TODO: HIGH: Actually consider things other than height
    private boolean isComplete(RocketSimulation sim){
        boolean withinHeightParameters = (sim.getHeight() >= minGoalHeight && sim.getHeight() <= maxGoalHeight);
        boolean withingStabilityParameters = (sim.getStability() >= minStability && sim.getStability() <= maxStability);
        boolean withinMassParameters = (sim.getMass() >= minMass && sim.getMass() <= maxMass);
        boolean withingTimeParameters = (sim.getTime() >= minTime && sim.getTime() <= maxTime);
        return withinHeightParameters && withinMassParameters;
    }

    public void setMinStability(String input) {
        if (!input.isEmpty()) {
            minStability = Double.parseDouble(input);
        } else {
            minStability = -10000;
        }
    }

    public void setMaxStability(String input) {
        if (!input.isEmpty()) {
            maxStability = Double.parseDouble(input);
        } else {
            maxStability = 10000;
        }
    }

    /**
     * Method that takes a string and converts it to SI units.
     *
     * @param input     The string that the user typed in the input box
     */
    public void setMinMass(String input) {
        if (!input.isEmpty()) {
            UnitGroup massUnits = UnitGroup.UNITS_MASS; //Get the list of all possible units
            Unit massUnit = massUnits.getDefaultUnit(); //Access the one which the user has assigned as default
            minMass = massUnit.fromUnit(Double.parseDouble(input)); //Convert the user's input from that unit to SI
        } else {
            minMass = -10000;
        }
    }

    public void setMaxMass(String input) {
        if (!input.isEmpty()) {
            UnitGroup massUnits = UnitGroup.UNITS_MASS; //Get the list of all possible units
            Unit massUnit = massUnits.getDefaultUnit(); //Access the one which the user has assigned as default
            maxMass = massUnit.fromUnit(Double.parseDouble(input)); //Convert the user's input from that unit to SI
        } else {
            maxMass = 10000;
        }
    }

    public void setMinGoalHeight(String input) {
        if (!input.isEmpty()) {
            UnitGroup distanceUnits = UnitGroup.UNITS_DISTANCE; //Get the list of all possible units
            Unit distanceUnit = distanceUnits.getDefaultUnit(); //Access the one which the user has assigned as default
            minGoalHeight = distanceUnit.fromUnit(Double.parseDouble(input)); //Convert the user's input from that unit to SI
        } else {
            minGoalHeight = -10000;
        }
    }

    public void setMaxGoalHeight(String input) {
        if (!input.isEmpty()) {
            UnitGroup distanceUnits = UnitGroup.UNITS_DISTANCE; //Get the list of all possible units
            Unit distanceUnit = distanceUnits.getDefaultUnit(); //Access the one which the user has assigned as default
            maxGoalHeight = distanceUnit.fromUnit(Double.parseDouble(input)); //Convert the user's input from that unit to SI
        } else {
            maxGoalHeight = 10000;
        }
    }

    public void setMinTime(String input) {
        if (!input.isEmpty()) {
            minTime = Double.parseDouble(input);
        } else {
            minTime = -10000;
        }
    }

    public void setMaxTime(String input) {
        if (!input.isEmpty()) {
            maxTime = Double.parseDouble(input);
        } else {
            maxTime = 10000;
        }
    }

    public void setMaxIterations(String input) {
        if (!input.isEmpty()) {
            maxIterations = Integer.parseInt(input);
        } else {
            maxIterations = 100;
        }
    }


    /**
     * Method to modify a rocket completely at random. Should not be used in final project.
     *
     * @param rocket    The rocket to be modified
     * @return          The modified rocket
     */
    /*
    private Rocket changeOneThingRandomly(Rocket rocket){
        RocketComponent sustainer = rocket.getChild(0);
        int partToModify = (int)(Math.random() * (sustainer.getChildCount() - 1));
        RocketParameter componentParameterToModify = changeableParameters.getChild(partToModify);
        RocketComponent componentToModify = sustainer.getChild(partToModify);
        int childPartToModify = (int)(Math.random() * (componentToModify.getChildCount() + 1));
        if(childPartToModify == componentToModify.getChildCount()){//Modify parent component
            RocketComponent modifiedComponent = modifyComponentRandomly(componentToModify, componentParameterToModify);

        } else {//Modify a subcomponent
            System.out.println("Modifying child");
            RocketComponent subcomponentToModify = componentToModify.getChild(childPartToModify);
            RocketParameter subcomponentParameterToModify = componentParameterToModify.getChild(childPartToModify);
            RocketComponent modifiedSubcomponent = modifyComponentRandomly(subcomponentToModify, subcomponentParameterToModify);
        }

        return rocket;
    }*/

    /**
     * A method to randomly change a given RocketComponent. Should not be used in final project.
     *
     * @param component         The original component to be modified
     * @param rocketParameter   The parameter showing what can be changed about the component
     * @return                  A modified component
     */
    /*
    private RocketComponent modifyComponentRandomly(RocketComponent component, RocketParameter rocketParameter) {
        boolean[] changeablePartsOfCurrentComponent = rocketParameter.getParameter().getChangeableComponents();
        boolean somethingIsChangeable = false;
        for(boolean b: changeablePartsOfCurrentComponent){
            if(b){
                somethingIsChangeable = true;
            }
        }
        if(!somethingIsChangeable) {
            System.out.println("Nothing can be changed");
            return component;
        }

        int valToModify = (int)(Math.random() * changeablePartsOfCurrentComponent.length); //Pick which value should
        while(!changeablePartsOfCurrentComponent[valToModify]) {                           //be modified. Redo until
            valToModify = (int)(Math.random() * changeablePartsOfCurrentComponent.length); //you get something valid.
        }

        switch (valToModify){
            case 0: double valToChangeLengthBy = (Math.random() * 0.1 - 0.05); //Length (meters)
                    component = setComponentLength(component, component.getLength() + valToChangeLengthBy);
                    break;
            case 1: System.out.println("Changing fore radius not implemented"); //Fore radius
                    break;
            case 2: System.out.println("Changing aft radius not implemented"); //Aft radius
                    break;
            case 3: System.out.println("Changing material not implemented"); //Material
                    break;
            case 4: System.out.println("Changing finish not implemented"); //Finish
                    break;
            case 5: double valToChangeMassBy = (int)(Math.random() * 100 - 50); //Mass
                component = setComponentMass(component, component.getMass() + valToChangeMassBy);
                break;
        }
        System.out.println("Finished modifying " + component.toString());
        return component;
    }*/

    /**
     * Sets up project in default configuration. I did this because i am sick of typing things in everyttime.
     * Should not be in final;
     */
    /*private void setUpProjectDefault() {
        fileToSaveTo = new File("C:\\Users\\CWHar\\Downloads\\Coding\\Senior-Research-Project\\SavedRockets\\FinishedRocket.ork");
        fileToLoadFrom = new File("C:\\Users\\CWHar\\Downloads\\Coding\\Senior-Research-Project\\SavedRockets\\SampleRocket.ork");

        useImperial = true;
        maxGoalHeight = 900.0;
        minGoalHeight = 850.0;

        if(useImperial) { //Openrocket only uses metric so I have to change good units into metric
            maxGoalHeight = maxGoalHeight / 3.281; //Feet to meters
            minGoalHeight = minGoalHeight / 3.281; //Feet to meters
        }

        try {
            originalRocket = loadRocket();
        } catch (RocketLoadException e) {
            throw new RuntimeException("Couldn't load rocket. This is your fault not mine.");
        }
        changeableParameters = setChangeableParametersDefault();
    }*/

    /**
     * Default setChangeableParameters method. Doesn;t ask for user input. Should not be in final;
     * @return
     */
    /*private RocketParameter setChangeableParametersDefault(){
        RocketParameter p;
        useDefault = true;
        p = new RocketParameter(originalRocket.getChild(0), useDefault);
        return p;
    }*/

    /**
     * Method to go through all of the components in the rocket and record their locations.
     */
    //TODO: MEDIUM: Right now, component locations are just saved as a string. If the rocket has 10+ components, everything will break. Fix it. or not.
    private void indexLocationOfComponents() {
        RocketComponent sustainer = originalRocket.getChild(0);
        for (int i = 0; i < sustainer.getChildCount(); i++) {
            RocketComponent component = sustainer.getChild(i);
            Class key = component.getClass();
            if (componentLocations.containsKey(key)) {
                int[] prevArray = componentLocations.get(key);
                int[] intArray = new int[prevArray.length + 1];
                for (int j = 0; j < prevArray.length; j++) {
                    intArray[j] = prevArray[j];
                }
                intArray[prevArray.length] = i;
                componentLocations.put(key, intArray);
            } else {
                int[] intArray = new int[1];
                intArray[0] = i;
                componentLocations.put(key, intArray);
            }

            if (component.getChildCount() != 0) {
                indexLocationOfChildComponents(i, component);
            }
        }
    }

    /**
     * Helper method for indexLocationOfComponents(). Used to index the locations of children of a parent component.
     *
     * @param parentLocation
     * @param parentComponent
     */
    private void indexLocationOfChildComponents(int parentLocation, RocketComponent parentComponent) {
        for (int i = 0; i < parentComponent.getChildCount(); i++) {
            RocketComponent component = parentComponent.getChild(i);
            Class key = component.getClass();
            if (componentLocations.containsKey(key)) {
                int[] prevArray = componentLocations.get(key);
                int[] intArray = new int[prevArray.length + 1];
                for (int j = 0; j < prevArray.length; j++) {
                    intArray[j] = prevArray[j];
                }
                intArray[prevArray.length] = i + (10 * parentLocation);
                componentLocations.put(key, intArray);
            } else {
                int[] intArray = new int[1];
                intArray[0] = i + (10 * parentLocation);
                componentLocations.put(key, intArray);
            }
        }
    }
}