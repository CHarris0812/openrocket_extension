package SeniorResearchCode;

import net.sf.openrocket.material.Material;
import net.sf.openrocket.rocketcomponent.*;

import java.util.HashMap;

public class RocketModifier{
    private HashMap<Class, int[]> componentLocations;
    private Rocket rocket;
    private RocketParameter changeableParameters;
    private RocketSimulation rocketSimulation;
    private double minStability, maxStability, minMass, maxMass, minGoalHeight, maxGoalHeight, minTime, maxTime;
    private final int maxAmountHeightCanChangeBy = 5; //Centimeters
    private final int maxAmountWeightCanChangeBy = 10; //Grams


    public RocketModifier (Rocket r, RocketSimulation rs, RocketParameter rp, double minS, double maxS, double minM, double maxM,
                           double minH, double maxH, double minT, double maxT, HashMap cl) {
        rocket = r;
        rocketSimulation = rs;
        changeableParameters = rp;
        minStability = minS;
        maxStability = maxS;
        minMass = minM;
        maxMass = maxM;
        minGoalHeight = minH;
        maxGoalHeight = maxH;
        minTime = minT;
        maxTime = maxT;
        componentLocations = cl;
    }

    public void setSimulation(RocketSimulation rs) {
        rocketSimulation = rs;
    }

    /**
     * Method to modify a rocket
     *
     * @return modified rocket
     */
    //TODO: Medium: Make the amount that parameters change by dependent on how far the actual value is from the goal
    public Rocket modifyRocket(){
        boolean needToGoHigher = false;
        boolean needToGoLower = false;
        if(rocketSimulation.getHeight() < minGoalHeight) {
            needToGoHigher = true;
        } else if (rocketSimulation.getHeight() > maxGoalHeight) {
            needToGoLower = true;
        }

        boolean needToWeighMore = false;
        boolean needToWeighLess = false;
        if(rocketSimulation.getMass() < minMass) {
            needToWeighMore = true;
        } else if (rocketSimulation.getMass() > maxMass) {
            needToWeighLess = true;
        }

        boolean needToBeMoreStable = false;
        boolean needToBeLessStable = false;
        if (rocketSimulation.getStability() < minStability) {
            needToBeMoreStable = true;
        } else if (rocketSimulation.getStability() > maxStability) {
            needToBeLessStable = true;
        }

        boolean needToTakeMoreTime = false;
        boolean needToTakeLessTime = false;
        if (rocketSimulation.getTime() < minTime) {
            needToTakeMoreTime = true;
        } else if (rocketSimulation.getTime() > maxTime) {
            needToTakeLessTime = true;
        }

        if (needToGoHigher || needToGoLower) {
            rocket = modifyRocketHeight(rocket, needToGoHigher);
        } else if (needToWeighMore || needToWeighLess) {
            rocket = modifyRocketWeight(rocket, needToWeighMore);
        } else if (needToBeMoreStable || needToBeLessStable) {
            rocket = modifyRocketStability(rocket, needToBeMoreStable);
        } else if (needToTakeMoreTime || needToTakeLessTime) {
            rocket = modifyRocketTime(rocket, needToTakeMoreTime);
        }
        return rocket;
    }

    /**
     * Method to modify the height of a rocket. To modify the height, you can change either mass or length.
     *
     * @param rocket            Rocket to be modified
     * @param needToGoHigher    Boolean of whether the rocket needs to go higher or lower
     * @return                  The modified rocket
     */
    //TODO: MEDIUM: Make it possible to change components other than body tubes and nose cones
    //TODO: LOW: Make it possible to change things other than length (i.e. material, finish)
    private Rocket modifyRocketHeight(Rocket rocket, boolean needToGoHigher) {
        if (Math.random() > .75) { //Chance to make rocket change height by changing weight. 1/4 chance.
            rocket = modifyRocketWeight(rocket, !needToGoHigher);
        } else {
            int[] modifiableNosecones = componentLocations.get(NoseCone.class);
            int[] modifiableBodyTubes = componentLocations.get(BodyTube.class);
            int[] modifiableComponents = mergeArrays(modifiableNosecones, modifiableBodyTubes);
            int partToModifyIndex = modifiableComponents[(int)(Math.random() * modifiableComponents.length)];
            RocketComponent componentToModify;
            if (partToModifyIndex >= 10) { //If component is a child component
                throw new RuntimeException("Rocket structure is weird");
            } else { //Component is not a child component
                componentToModify = rocket.getChild(0).getChild(partToModifyIndex);
                if (changeableParameters.getChild(partToModifyIndex).getParameter().lengthChangeable()) {
                    if (needToGoHigher) {
                        double toChangeBy = (int)(Math.random() * -100) * maxAmountHeightCanChangeBy * 0.00001;
                        System.out.println(toChangeBy);
                        componentToModify = setComponentLength(componentToModify, componentToModify.getLength() + toChangeBy);
                    } else {
                        double toChangeBy = (int)(Math.random() * 100) * maxAmountHeightCanChangeBy * 0.00001;
                        componentToModify = setComponentLength(componentToModify, componentToModify.getLength() + toChangeBy);
                    }
                }
            }
        }
        return rocket;
    }

    /**
     * Method to modify the weight of a rocket. While it is technically possible to change the mass of any
     * component, this method will only modify mass objects. Changing other components has a high chance
     * of fucking other stuff up for very little benefit.
     *
     * @param rocket            Rocket to be modified
     * @param needToWeighMore   Whether the rocket needs to weigh more or less
     * @return                  The modified rocket
     */
    //TODO: LOW: Make change vary based on how far from goal it is
    //TODO: LOW: Make it so that if the initial component that is picked is not allowed to change mass it repicks.
    //TODO: LOW: Make sure that after implementing the previous TODO it stops if no component can be changed
    private Rocket modifyRocketWeight(Rocket rocket, boolean needToWeighMore) {
        int[] possiblePartsToModify = componentLocations.get(MassComponent.class);
        int partToModifyIndex = possiblePartsToModify[(int)(Math.random() * possiblePartsToModify.length)];
        RocketComponent componentToModify;
        if (partToModifyIndex >= 10) { //If component is a child component
            componentToModify = rocket.getChild(0).getChild(partToModifyIndex / 10)
                    .getChild(partToModifyIndex % 10);
            System.out.println("current mass" + componentToModify.getComponentMass());
            if (changeableParameters.getChild(partToModifyIndex / 10).getChild(partToModifyIndex % 10)
                    .getParameter().massChangeable()) {
                if (needToWeighMore) {
                    double toChangeBy = (int)(Math.random() * 10) * maxAmountWeightCanChangeBy * 0.0001;
                    componentToModify = setComponentMass(componentToModify, componentToModify.getComponentMass() + toChangeBy);
                } else {
                    double toChangeBy = (int)(Math.random() * -10) * maxAmountWeightCanChangeBy * 0.0001;
                    System.out.println("Decreasing weight by " + toChangeBy);
                    componentToModify = setComponentMass(componentToModify, componentToModify.getComponentMass() + toChangeBy);
                }
            }

        } else { //Component is not a child component
            throw new RuntimeException("Mass component not a child component");
        }
        return rocket;
    }

    /**
     * Method to modify the stability of a rocket
     *
     * @param rocket                Rocket to be modified
     * @param needToBeMoreStable    Whether the rocket needs to be more or less stable
     * @return                      The modified rocket
     */
    //TODO: MEDIUM: Implement method
    private Rocket modifyRocketStability(Rocket rocket, boolean needToBeMoreStable) {
        return null;
    }

    /**
     * Method to change the duration of the rocket's flight
     *
     * @param rocket                Rocket to be modified
     * @param needToTakeMoreTime    Whether the flight needs to be longer or shorter
     * @return                      The modified rocket
     */
    //TODO: LOW: Implement method
    private Rocket modifyRocketTime (Rocket rocket, boolean needToTakeMoreTime) {
        return null;
    }

    /**
     * Helper method to merge two int[]
     *
     * @param a     The first int[]
     * @param b     The second int[]
     * @return      The merged int[]
     */
    private int[] mergeArrays (int[] a, int[] b) {
        int[] toReturn = new int[a.length + b.length];
        System.arraycopy(a, 0, toReturn, 0, a.length);
        System.arraycopy(b, 0, toReturn, a.length, b.length);
        return toReturn;
    }

    /**
     * A method to change the length of a RocketComponent
     *
     * @param component     The original component to be modified
     * @param len           The new length
     * @return              A modified component with the new length
     */
    private RocketComponent setComponentLength(RocketComponent component, double len) {
        if(component instanceof NoseCone){
            ((NoseCone) component).setLength(len);
        } else if (component instanceof BodyTube) {
            ((BodyTube) component).setLength(len);
        } else if (component instanceof Transition) {
            ((Transition) component).setLength(len);
        } else if (component instanceof TubeCoupler) {
            ((TubeCoupler) component).setLength(len);
        } else {
            throw new RuntimeException("You are trying to set length of something that you can't. Maybe your fault, " +
                    "maybe mine." + component.getClass().toString());
        }
        return component;
    }

    /**
     * A method to change the mass of a RocketComponent
     *
     * @param component     The original component to be modified
     * @param m             The new mass
     * @return              A modified component with the new mass
     */
    private RocketComponent setComponentMass(RocketComponent component, double m) {
        if(component instanceof MassComponent) {
            ((MassComponent) component).setComponentMass(m);
        } else {
            throw new RuntimeException("You are trying to set mass of something other than a mass component. Don't.");
        }
        return component;
    }

    /**
     * A method to change the fore radius of a RocketComponent
     *
     * @param component     The original component to be modified
     * @param r             The new fore radius
     * @return              A modified component with the new fore radius
     */
    //TODO: MEDIUM: Implement method
    private RocketComponent setForeRadius(RocketComponent component, double r) {
        if (component instanceof Transition) {
            ((Transition) component).setForeRadius(r);
        } else {
            throw new RuntimeException("Trying to set radius of something that isnt a transition. git gud");
        }
        return component;
    }

    /**
     * A method to change the aft radius of a RocketComponent
     *
     * @param component     The original component to be modified
     * @param r             The new aft radius
     * @return              A modified component with the new aft radius
     */
    //TODO: MEDIUM: Implement method
    private RocketComponent setAftRadius(RocketComponent component, double r) {
        if (component instanceof Transition) {
            ((Transition) component).setAftRadius(r);
        } else {
            throw new RuntimeException("Trying to set radius of something that isnt a transition. git gud");
        }
        return component;
    }

    /**
     * A method to change the finish of a RocketComponent
     *
     * @param component     The original component to be modified
     * @param f             The finish that should be set
     * @return              A modified component with the new finish
     */
    //TODO: LOW: Implement method
    private RocketComponent setFinish(RocketComponent component, ExternalComponent.Finish f) {
        return null;
    }

    /**
     * A method to change the material of a RocketComponent
     *
     * @param component     The original component to be modified
     * @param m             The material that should be set
     * @return              A modified component with the new material
     */
    //TODO: LOW: Implement method
    private RocketComponent setMaterial(RocketComponent component, Material m) {
        return null;
    }
}