package SeniorResearchCode;

import net.sf.openrocket.database.Databases;
import net.sf.openrocket.material.Material;
import net.sf.openrocket.rocketcomponent.*;

import java.util.Scanner;

public class Parameter{
    private boolean lengthChangeable, foreRadiusChangeable, aftRadiusChangeable, materialChangeable,
            finishChangeable, massChangeable = false;

    /*
    Class used to keep track of what parameters of a component are changeable
     */
    public Parameter(RocketComponent c, boolean d){
        if(d){
            setParamsDefault(c);
        } else{
            setParams();
        }
    }

    public Parameter(boolean l, boolean fr, boolean ar, boolean m, boolean f, boolean ma){
        lengthChangeable = l;
        foreRadiusChangeable = fr;
        aftRadiusChangeable = ar;
        materialChangeable = m;
        finishChangeable = f;
        massChangeable = ma;
    }

    /*
    All things are set to not changeable by default.
    In this method, something is only done if a parameter is changeable.
     */
    //TODO: MEDIUM: Set defualt parameters for all objects
    public void setParamsDefault(RocketComponent component){


        if(component instanceof AxialStage){
        } else if (component instanceof NoseCone) {
            lengthChangeable = true;
        } else if (component instanceof BodyTube) {
            lengthChangeable = true;
        } else if (component instanceof Transition) {
        } else if (component instanceof TrapezoidFinSet) {
        } else if (component instanceof Parachute) {
        } else if (component instanceof EllipticalFinSet) {
        } else if (component instanceof FreeformFinSet) {
        } else if (component instanceof TubeFinSet) {
        } else if (component instanceof LaunchLug) {
        } else if (component instanceof InnerTube) {
        } else if (component instanceof TubeCoupler) {
        } else if (component instanceof CenteringRing) {
        } else if (component instanceof Bulkhead) {
        } else if (component instanceof EngineBlock) {
        } else if (component instanceof Streamer) {
        } else if (component instanceof ShockCord) {
        } else if (component instanceof MassComponent) {
            massChangeable = true;
        } else {
            throw new RuntimeException("You are trying to set parameters for a class you didnt bother to implement dummy " + component.toString());
        }
    }

    /*
    Used to allow user to set what can be changed
    //TODO: LOW: Implement a way for the user to set changeable parameters
     */
    public void setParams(){
        Scanner input = new Scanner(System.in);
        String val;
    }

    public boolean[] getChangeableComponents(){
        boolean[] toReturnArray = {lengthChangeable, foreRadiusChangeable, aftRadiusChangeable,
                materialChangeable, finishChangeable, massChangeable};
        return toReturnArray;
    }

    public boolean lengthChangeable() {
        return lengthChangeable;
    }

    public boolean foreRadiusChangeable() {
        return foreRadiusChangeable;
    }

    public boolean aftRadiusChangeable() {
        return aftRadiusChangeable;
    }

    public boolean finishChangeable() {
        return finishChangeable;
    }

    public boolean massChangeable() {
        return massChangeable;
    }

    public boolean materialChangeable() {
        return materialChangeable;
    }
}