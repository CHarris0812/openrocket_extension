package SeniorResearchCode;

import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Scanner;

public class Parser {
    Scanner scanner;
    private File fileToSaveTo, fileToLoadFrom;
    private RocketParameter changeableParameters;
    private double minStability, maxStability, minMass, maxMass, minHeight, maxHeight, minTime, maxTime;
    private boolean useDefaultChangeableParameters;
    private int maxIterations;
    private String massUnits, heightUnits;
    private double feetToMeters = 1 / 3.281;
    private double ouncesToGrams = 28.3495;
    public Parser (Scanner s){
        scanner = s;
        read();
    }

    //TODO: MEDIUM: Allow user to implement non-default parameters
    private void read(){
        while (scanner.hasNextLine()){
            String line = scanner.nextLine();
            String[] parts = line.split("=");

            switch (parts[0]) {
                case "fileToSaveTo":
                    fileToSaveTo = new File(parts[1]);
                    break;
                case "fileToLoadFrom":
                    fileToLoadFrom = new File(parts[1]);
                    break;
                case "maxIterations":
                    maxIterations = Integer.parseInt(parts[1]);
                    break;
                case "heightUnits":
                    heightUnits = parts[1];
                    break;
                case "minHeight":
                    minHeight = Double.parseDouble(parts[1]);
                    break;
                case "maxHeight":
                    maxHeight = Double.parseDouble(parts[1]);
                    break;
                case "massUnits":
                    massUnits = parts[1];
                    break;
                case "minMass":
                    minMass = Double.parseDouble(parts[1]);
                    break;
                case "maxMass":
                    maxMass = Double.parseDouble(parts[1]);
                    break;
                case "minStability":
                    minStability = Double.parseDouble(parts[1]);
                    break;
                case "maxStability":
                    maxStability = Double.parseDouble(parts[1]);
                    break;
                case "minTime":
                    minTime = Double.parseDouble(parts[1]);
                    break;
                case "maxTime":
                    maxTime = Double.parseDouble(parts[1]);
                    break;
                case "useDefaultChangeableParameters":
                    useDefaultChangeableParameters = Boolean.parseBoolean(parts[1]);
                    break;
                default:
                    System.out.println("Invalid option: " + parts[0]);
            }
        }
        System.out.println("test");
    }

    public boolean getDefaultParameters() {
        return useDefaultChangeableParameters;
    }

    public File getFileToSaveTo() {
        return fileToSaveTo;
    }

    public File getFileToLoadFrom() {
        return fileToLoadFrom;
    }

    public double getMinStability() {
        return minStability;
    }

    public double getMaxStability() {
        return maxStability;
    }

    //TODO: URGENT: Figure out whether openRocket uses grams or kilograms
    public double getMinMass() {
        if(massUnits.compareToIgnoreCase("ounces") == 0) {
            return minMass * ouncesToGrams / 1000;
        }
        return minMass / 1000;
    }

    //TODO: URGENT: Figure out whether openRocket uses grams or kilograms
    public double getMaxMass() {
        if(massUnits.compareToIgnoreCase("ounces") == 0) {
            return maxMass * ouncesToGrams / 1000;
        }
        return maxMass / 1000;
    }

    public double getMinGoalHeight() {
        if(heightUnits.compareToIgnoreCase("feet") == 0) {
            return minHeight * feetToMeters;
        }
        return minHeight;
    }

    public double getMaxGoalHeight() {
        if(heightUnits.compareToIgnoreCase("feet") == 0) {
            return maxHeight * feetToMeters;
        }
        return maxHeight;
    }

    public double getMinTime() {
        return minTime;
    }

    public double getMaxTime() {
        return maxTime;
    }

    public int getMaxIterations() {
        return maxIterations;
    }

}