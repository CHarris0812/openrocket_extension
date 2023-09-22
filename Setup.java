package SeniorResearchCode;

import net.sf.openrocket.document.OpenRocketDocument;
import net.sf.openrocket.file.GeneralRocketLoader;
import net.sf.openrocket.file.RocketLoadException;
import net.sf.openrocket.rocketcomponent.Rocket;
import net.sf.openrocket.rocketcomponent.RocketComponent;


import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Scanner;


/**
 * This whole class is completely useless. Left in because I don't feel like dealing with all the
 * errors removing it would cause. Surely this won't cause any unforseen problems later.
 */
public class Setup {
    private Rocket originalRocket;
    private File fileToSaveTo, fileToLoadFrom;
    private RocketParameter changeableParameters;
    private double minStability, maxStability, minMass, maxMass, minGoalHeight, maxGoalHeight, minTime, maxTime;
    private boolean useDefault;
    private HashMap<Class, int[]> componentLocations = new HashMap<Class, int[]>();
    private int maxIterations;

    public Setup() {

        Scanner input = new Scanner(System.in);
        System.out.println("Do you want to load parameters from a file? (y/n)");
        String answer = input.nextLine();
        if(answer.compareToIgnoreCase("y") == 0) {
            try {
                setUpProjectFromFile();
            } catch (Exception e) {
                System.out.println(e);
                System.exit(0);
            }
        }
        else {
            setUpProject();
        }
    }

    /**
     * Method to do all the setup tasks
     */
    private void setUpProject() {
        Scanner input = new Scanner(System.in);
        System.out.println("Do you want to manually set the files to load from and save to? (y/n)");
        String answer = input.nextLine();
        if(answer.compareToIgnoreCase("y") == 0){
            fileToLoadFrom = setFileToLoadFrom();
            fileToSaveTo = setFileToSaveTo();
        }
        else{
            fileToSaveTo = new File("C:\\Users\\CWHar\\Downloads\\Coding\\Senior-Research-Project\\SavedRockets\\FinishedRocket.ork");
            fileToLoadFrom = new File("C:\\Users\\CWHar\\Downloads\\Coding\\Senior-Research-Project\\SavedRockets\\SampleRocket.ork");
        }

        System.out.println("What is the maximum number of iterations to do?");
        answer = input.nextLine();
        try{
            maxIterations = Integer.parseInt(answer);
        } catch (NumberFormatException e) {
            System.out.println("TYPE IN AN INT YOU DUMB FUCK");
            System.exit(0);
        }

        setGoalHeight();
        setGoalWeight();

        try {
            originalRocket = loadRocket();
        } catch (RocketLoadException e) {
            throw new RuntimeException("Couldn't load rocket. This is your fault not mine.");
        }
        changeableParameters = setChangeableParameters();
        indexLocationOfComponents();
    }

    private void setGoalHeight () {
        Scanner input = new Scanner(System.in);
        String answer = "";
        System.out.println("Do you want to use feet or meters? (f/m)");
        answer = input.nextLine();
        boolean useFeet = false;
        if(answer.compareToIgnoreCase("f") == 0) {
            useFeet = true;
        }
        System.out.println("What is the maximum goal height? (feet/meters)");
        maxGoalHeight = Double.parseDouble(input.nextLine());
        System.out.println("What is the minimum goal height? (feet/meters)");
        minGoalHeight = Double.parseDouble(input.nextLine());

        if(useFeet) { //Openrocket only uses metric so I have to change good units into metric
            maxGoalHeight = maxGoalHeight / 3.281; //Feet to meters
            minGoalHeight = minGoalHeight / 3.281; //Feet to meters
        }
    }

    private void setGoalWeight () {
        Scanner input = new Scanner(System.in);
        String answer = "";
        System.out.println("Do you want to use ounces or grams? (o/g)");
        answer = input.nextLine();
        boolean useOunces = false;
        if(answer.compareToIgnoreCase("o") == 0) {
            useOunces = true;
        }
        System.out.println("What is the maximum weight? (ounces/grams)");
        maxMass = Double.parseDouble(input.nextLine());
        System.out.println("What is the minimum weight? (ounces/grams)");
        minMass = Double.parseDouble(input.nextLine());

        if(useOunces) { //Openrocket only uses metric so I have to change good units into metric
            maxMass = maxMass * 28.3495; //Ounces to grams
            minMass = minMass * 28.3495; //Ounces to grams
        }
    }

    /**
     * Method to go through all of the components in the rocket and record their locations.
     */
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
    public void indexLocationOfChildComponents(int parentLocation, RocketComponent parentComponent) {
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

    /**
     * Method to ask user where the base rocket that they want to modify is stored
     *
     * @return      A file where the original rocket is
     */
    private File setFileToLoadFrom(){
        Scanner input = new Scanner(System.in);
        System.out.println("What file do you want to load from?");
        return new File(input.nextLine());
    }

    /**
     * Method to ask user where the completed rocket should be saved
     *
     * @return      A file that the rocket should be saved to
     */
    private File setFileToSaveTo(){
        Scanner input = new Scanner(System.in);
        System.out.println("What file do you want to save to?");
        return new File(input.nextLine());
    }

    /**
     * Method to define what parts of the rocket the program is allowed to change.
     * Asks user whether to use default. If user chooses no, they are prompted to input
     * their chosen parameters.
     *
     * @return      A RocketParameter that describes what parts of the rocket can be changed
     */
    //TODO: MEDIUM: Allow user to input own parameters
    private RocketParameter setChangeableParameters(){
        Scanner input = new Scanner(System.in);
        RocketParameter p;
        System.out.println("Do you want to use default changeable parameters? (y/n)");
        if(input.nextLine().compareToIgnoreCase("y") == 0){
            useDefault = true;
            p = new RocketParameter(originalRocket.getChild(0), useDefault);
        } else {
            throw new RuntimeException("Too bad, i didnt implement that");
        }
        return p;
    }

    /**
     * Method to load the rocket that should be modified
     *
     * @return                          Returns the rocket that was loaded
     * @throws RocketLoadException      Throws exception if rocket cannot be loaded
     */
    //TODO: LOW: Make sure the rocket is valid and makes sense
    private Rocket loadRocket() throws RocketLoadException {
        GeneralRocketLoader rocketLoader = new GeneralRocketLoader(fileToLoadFrom);
        OpenRocketDocument loadedRocket = rocketLoader.load();
        return loadedRocket.getRocket();
    }

    public Rocket getOriginalRocket() {
        return originalRocket;
    }

    public File getFileToSaveTo() {
        return fileToSaveTo;
    }

    public RocketParameter getChangeableParameters() {
        return changeableParameters;
    }

    public double getMinStability() {
        return minStability;
    }

    public double getMaxStability() {
        return maxStability;
    }

    public double getMinMass() {
        return minMass;
    }

    public double getMaxMass() {
        return maxMass;
    }

    public double getMinGoalHeight() {
        return minGoalHeight;
    }

    public double getMaxGoalHeight() {
        return maxGoalHeight;
    }

    public double getMinTime() {
        return minTime;
    }

    public double getMaxTime() {
        return maxTime;
    }

    public HashMap<Class, int[]> getComponentLocations() {
        return componentLocations;
    }

    public int getMaxIterations() {
        return maxIterations;
    }

    //TODO: LOW: Allow the use of non-default parameters
    private void setUpProjectFromFile() throws RocketLoadException {
        String fileLocation = "C:\\Users\\CWHar\\Downloads\\Coding\\Senior-Research-Project\\Parameters.txt";
        Scanner reader = null;
        try {
            File f = new File(fileLocation);
            reader = new Scanner(f);
        } catch (Exception e) {
            System.out.println("Can't find file");
            throw new RuntimeException();
        }

        Parser parser = new Parser(reader);



        boolean useDefaultParams = parser.getDefaultParameters();
        fileToSaveTo = parser.getFileToSaveTo();
        fileToLoadFrom = parser.getFileToLoadFrom();
        minStability = parser.getMinStability();
        maxStability = parser.getMaxStability();
        minMass = parser.getMinMass();
        maxMass = parser.getMaxMass();
        minGoalHeight = parser.getMinGoalHeight();
        maxGoalHeight = parser.getMaxGoalHeight();
        minTime = parser.getMinTime();
        maxTime = parser.getMaxTime();
        maxIterations = parser.getMaxIterations();

        originalRocket = loadRocket();
        if (useDefaultParams) {
            changeableParameters = new RocketParameter(originalRocket.getChild(0), useDefaultParams);
        } else {
            System.out.println("USE THE DEFAULT PARAMETERS YOU MOTHER FUCKER");
            System.exit(0);
        }

        indexLocationOfComponents();
    }
}