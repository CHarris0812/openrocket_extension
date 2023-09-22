package SeniorResearchCode;

import SeniorResearchCode.*;
import net.sf.openrocket.rocketcomponent.*;

import java.util.ArrayList;

/*
Class used to define what can and cannot be changed. Currently pretty good actually.
 */
public class RocketParameter {
    private ArrayList<RocketParameter> children;
    private Parameter parameterOfCurrentPiece;
    private RocketComponent component;
    private final boolean useDefault;

    public RocketParameter(RocketComponent c, boolean d){
        component = c;
        useDefault = d;
        parameterOfCurrentPiece = createParameter(useDefault, component);
        children = new ArrayList<>();
        for(RocketComponent child : component.getChildren()){
            children.add(new RocketParameter(child, useDefault));
        }
    }

    private Parameter createParameter(boolean useDefault, RocketComponent component){
        return new Parameter(component, useDefault);
    }

    public ArrayList<RocketParameter> getChildren() {
        return children;
    }

    public Parameter getParameter() {
        return parameterOfCurrentPiece;
    }

    public RocketComponent getComponent() {
        return component;
    }

    public void setParameter(Parameter p) {
        parameterOfCurrentPiece = p;
    }

    public RocketParameter getChild(int child) { return children.get(child); }

    public void setChild(RocketParameter p, int child) {
        children.set(child, p);
    }

    public int getChildCount() { return children.size(); }

    public void setComponent(RocketComponent rc) { component = rc; }


}
