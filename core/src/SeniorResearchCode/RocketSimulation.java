package SeniorResearchCode;

import net.sf.openrocket.document.Simulation;
import net.sf.openrocket.motor.Motor;
import net.sf.openrocket.motor.MotorConfiguration;
import net.sf.openrocket.optimization.general.OptimizationException;
import net.sf.openrocket.optimization.rocketoptimization.parameters.StabilityParameter;
import net.sf.openrocket.rocketcomponent.FlightConfiguration;
import net.sf.openrocket.rocketcomponent.FlightConfigurationId;
import net.sf.openrocket.rocketcomponent.MotorMount;
import net.sf.openrocket.rocketcomponent.Rocket;
import net.sf.openrocket.simulation.FlightData;
import net.sf.openrocket.simulation.exception.SimulationException;

/*
Class designed to contain all the data about a specific rocket and simulation.
 */
public class RocketSimulation{
    private double height;
    private double time;
    private double stability;
    private double mass;
    private Simulation simulation;
    private Rocket rocket;

    public RocketSimulation(Rocket r) throws OptimizationException, SimulationException {
        rocket = r;
        simulation = new Simulation(rocket);
        simulation.simulate();
        height = getExpectedHeight();
        time = getExpectedTime();
        stability = findRocketStability();
        mass = getRocketMass();
    }

    /*
    Method to determine the time a rocket is in the air
     */
    private double getExpectedTime() {
        FlightData flightData = simulation.getSimulatedData();
        return flightData.getFlightTime();
    }

    /*
    Method to simulate the rocket and determine how high up it went.
    Necessary to determine whether the program has met its goal.
     */
    private double getExpectedHeight(){
        FlightData flightData = simulation.getSimulatedData();
        return flightData.getMaxAltitude();
    }

    /*
    Method to determine the stability of a given rocket.
     */
    private double findRocketStability() throws OptimizationException {
        StabilityParameter stabilityParameter = new StabilityParameter(true);
        return stabilityParameter.computeValue(simulation);
    }

    /**
     * Method to determine the mass of a rocket including motors.
     *
     * @return The mass of the rocket in kilograms
     */
    private double getRocketMass() {
        double rocketMass = 0;
        for (int i = 0; i < rocket.getChild(0).getChildCount(); i++) {
            rocketMass += rocket.getChild(0).getChild(i).getMass();

            if (rocket.getChild(0).getChild(i).getChildCount() > 0) {
                for (int j = 0; j < rocket.getChild(0).getChild(i).getChildCount(); j++) {
                    rocketMass += rocket.getChild(0).getChild(i).getChild(j).getMass();
                }
            }

            //Get mass of motors
            if (rocket.getChild(0).getChild(i).isMotorMount()) {
                MotorMount mount = (MotorMount) rocket.getChild(0).getChild(i);
                FlightConfigurationId fcid = rocket.getSelectedConfiguration().getFlightConfigurationID();
                MotorConfiguration currentConfig = mount.getMotorConfig(fcid);
                Motor motor = currentConfig.getMotor();
                rocketMass += motor.getLaunchMass();
            }
        }
        return rocketMass;
    }

    /*
    The next couple methods are simple accessor methods
     */
    public double getHeight() {
        return height;
    }

    public double getMass() {
        return mass;
    }

    public double getTime() {
        return time;
    }

    public double getStability() {
        return stability;
    }
}