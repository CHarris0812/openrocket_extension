package SeniorResearchCode;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.Arrays;
import java.util.Scanner;

import javax.swing.*;

import net.sf.openrocket.gui.dialogs.preset.preferences.PreferencesPanel;
import net.sf.openrocket.gui.main.BasicFrame;
import net.sf.openrocket.gui.scalefigure.RocketPanel;

import net.sf.openrocket.document.OpenRocketDocument;
import net.sf.openrocket.l10n.Translator;
import net.sf.openrocket.rocketcomponent.Rocket;
import net.sf.openrocket.rocketcomponent.RocketComponent;
import net.sf.openrocket.startup.Application;
import net.sf.openrocket.unit.UnitGroup;


@SuppressWarnings("serial")
public class AIPanel extends JPanel implements ActionListener {
    private final BasicFrame basicFrame;
    private JButton importButton, runButton, saveParamsButton, updateComponentsButton;
    private JTextField minHeightField, maxHeightField, minMassField, maxMassField, minStabilityField,
            maxStabilityField, minTimeField, maxTimeField, iterationsField;
    private JCheckBox lengthChangeable, foreRadiusChangeable, aftRadiusChangeable, materialChangeable,
            finishChangeable, massChangeable;
    private JComboBox partSelector;
    private String[] componentList;
    private SpringLayout layout;
    private RocketParameter rocketParameter;

    public AIPanel(BasicFrame parent) {
        basicFrame = parent;


        layout = new SpringLayout();
        setLayout(layout);

        importButton = new JButton("Import Parameters from file");
        importButton.addActionListener(this);
        add(importButton);

        layout.putConstraint(SpringLayout.WEST, importButton, 20, SpringLayout.WEST, this);

        runButton = new JButton("Run auto design");
        runButton.addActionListener(this);
        add(runButton);

        layout.putConstraint(SpringLayout.WEST, runButton, 20, SpringLayout.EAST, importButton);

        saveParamsButton = new JButton("Save Parameters to File");
        saveParamsButton.addActionListener(this);
        add(saveParamsButton);

        layout.putConstraint(SpringLayout.WEST, saveParamsButton, 20, SpringLayout.EAST, runButton);

        JLabel iterationsLabel = new JLabel("Maximum number of iterations");
        iterationsField = new JTextField(20);

        add(iterationsLabel);
        add(iterationsField);

        layout.putConstraint(SpringLayout.NORTH, iterationsLabel, 15, SpringLayout.SOUTH, importButton);
        layout.putConstraint(SpringLayout.EAST, iterationsLabel, 0, SpringLayout.EAST, importButton);

        layout.putConstraint(SpringLayout.NORTH, iterationsField, 0, SpringLayout.NORTH, iterationsLabel);
        layout.putConstraint(SpringLayout.WEST, iterationsField, 20, SpringLayout.EAST, iterationsLabel);


        JLabel minHeightLabel = new JLabel("Minimum required height");
        minHeightField = new JTextField(20);
        add(minHeightLabel);
        add(minHeightField);

        layout.putConstraint(SpringLayout.NORTH, minHeightLabel, 15, SpringLayout.SOUTH, iterationsLabel);
        layout.putConstraint(SpringLayout.EAST, minHeightLabel, 0, SpringLayout.EAST, iterationsLabel);

        layout.putConstraint(SpringLayout.NORTH, minHeightField, 0, SpringLayout.NORTH, minHeightLabel);
        layout.putConstraint(SpringLayout.WEST, minHeightField, 20, SpringLayout.EAST, minHeightLabel);


        JLabel maxHeightLabel = new JLabel("Maximum required height");
        maxHeightField = new JTextField(20);
        add(maxHeightLabel);
        add(maxHeightField);

        layout.putConstraint(SpringLayout.NORTH, maxHeightLabel, 15, SpringLayout.SOUTH, minHeightLabel);
        layout.putConstraint(SpringLayout.EAST, maxHeightLabel, 0, SpringLayout.EAST, minHeightLabel);

        layout.putConstraint(SpringLayout.NORTH, maxHeightField, 0, SpringLayout.NORTH, maxHeightLabel);
        layout.putConstraint(SpringLayout.WEST, maxHeightField, 20, SpringLayout.EAST, maxHeightLabel);



        JLabel minMassLabel = new JLabel("Minimum required mass");
        minMassField = new JTextField(20);
        add(minMassLabel);
        add(minMassField);

        layout.putConstraint(SpringLayout.NORTH, minMassLabel, 15, SpringLayout.SOUTH, maxHeightLabel);
        layout.putConstraint(SpringLayout.EAST, minMassLabel, 0, SpringLayout.EAST, maxHeightLabel);

        layout.putConstraint(SpringLayout.NORTH, minMassField, 0, SpringLayout.NORTH, minMassLabel);
        layout.putConstraint(SpringLayout.WEST, minMassField, 20, SpringLayout.EAST,    minMassLabel);


        JLabel maxMassLabel = new JLabel("Maximum required mass");
        maxMassField = new JTextField(20);
        add(maxMassLabel);
        add(maxMassField);

        layout.putConstraint(SpringLayout.NORTH, maxMassLabel, 15, SpringLayout.SOUTH, minMassLabel);
        layout.putConstraint(SpringLayout.EAST, maxMassLabel, 0, SpringLayout.EAST, minMassLabel);

        layout.putConstraint(SpringLayout.NORTH, maxMassField, 0, SpringLayout.NORTH, maxMassLabel);
        layout.putConstraint(SpringLayout.WEST, maxMassField, 20, SpringLayout.EAST, maxMassLabel);


        JLabel minStabilityLabel = new JLabel("Minimum required stability");
        minStabilityField = new JTextField(20);
        add(minStabilityLabel);
        add(minStabilityField);

        layout.putConstraint(SpringLayout.NORTH, minStabilityLabel, 15, SpringLayout.SOUTH, maxMassLabel);
        layout.putConstraint(SpringLayout.EAST, minStabilityLabel, 0, SpringLayout.EAST, maxMassLabel);

        layout.putConstraint(SpringLayout.NORTH, minStabilityField, 0, SpringLayout.NORTH, minStabilityLabel);
        layout.putConstraint(SpringLayout.WEST, minStabilityField, 20, SpringLayout.EAST,    minStabilityLabel);


        JLabel maxStabilityLabel = new JLabel("Maximum required stability");
        maxStabilityField = new JTextField(20);
        add(maxStabilityLabel);
        add(maxStabilityField);

        layout.putConstraint(SpringLayout.NORTH, maxStabilityLabel, 15, SpringLayout.SOUTH, minStabilityLabel);
        layout.putConstraint(SpringLayout.EAST, maxStabilityLabel, 0, SpringLayout.EAST, minStabilityLabel);

        layout.putConstraint(SpringLayout.NORTH, maxStabilityField, 0, SpringLayout.NORTH, maxStabilityLabel);
        layout.putConstraint(SpringLayout.WEST, maxStabilityField, 20, SpringLayout.EAST, maxStabilityLabel);


        JLabel minTimeLabel = new JLabel("Minimum required time");
        minTimeField = new JTextField(20);
        add(minTimeLabel);
        add(minTimeField);

        layout.putConstraint(SpringLayout.NORTH, minTimeLabel, 15, SpringLayout.SOUTH, maxStabilityLabel);
        layout.putConstraint(SpringLayout.EAST, minTimeLabel, 0, SpringLayout.EAST, maxStabilityLabel);

        layout.putConstraint(SpringLayout.NORTH, minTimeField, 0, SpringLayout.NORTH, minTimeLabel);
        layout.putConstraint(SpringLayout.WEST, minTimeField, 20, SpringLayout.EAST,    minTimeLabel);


        JLabel maxTimeLabel = new JLabel("Maximum required time");
        maxTimeField = new JTextField(20);
        add(maxTimeLabel);
        add(maxTimeField);

        layout.putConstraint(SpringLayout.NORTH, maxTimeLabel, 15, SpringLayout.SOUTH, minTimeLabel);
        layout.putConstraint(SpringLayout.EAST, maxTimeLabel, 0, SpringLayout.EAST, minTimeLabel);

        layout.putConstraint(SpringLayout.NORTH, maxTimeField, 0, SpringLayout.NORTH, maxTimeLabel);
        layout.putConstraint(SpringLayout.WEST, maxTimeField, 20, SpringLayout.EAST, maxTimeLabel);


        String[] options = {"Select part            "}; //TODO: URGENT: Make the box display the actual parts
        partSelector = new JComboBox<>(options);
        add(partSelector);
        partSelector.addActionListener(this);

        layout.putConstraint(SpringLayout.EAST, partSelector, -20, SpringLayout.EAST, this);


        updateComponentsButton = new JButton("Update rocket components");
        add(updateComponentsButton);
        updateComponentsButton.addActionListener(this);

        layout.putConstraint(SpringLayout.WEST, updateComponentsButton, 20, SpringLayout.EAST, saveParamsButton);


        JLabel lengthChangeableLabel = new JLabel("Length changeable");
        lengthChangeable = new JCheckBox();
        add(lengthChangeableLabel);
        add(lengthChangeable);

        layout.putConstraint(SpringLayout.EAST, lengthChangeable, 0, SpringLayout.EAST, partSelector);
        layout.putConstraint(SpringLayout.NORTH, lengthChangeable, 15, SpringLayout.SOUTH, partSelector);

        layout.putConstraint(SpringLayout.EAST, lengthChangeableLabel, -20, SpringLayout.WEST, lengthChangeable);
        layout.putConstraint(SpringLayout.NORTH, lengthChangeableLabel, 15, SpringLayout.SOUTH, partSelector);


        JLabel foreRadiusChangeableLabel = new JLabel("Fore radius changeable");
        foreRadiusChangeable = new JCheckBox();
        add(foreRadiusChangeableLabel);
        add(foreRadiusChangeable);

        layout.putConstraint(SpringLayout.EAST, foreRadiusChangeable, 0, SpringLayout.EAST, partSelector);
        layout.putConstraint(SpringLayout.NORTH, foreRadiusChangeable, 15, SpringLayout.SOUTH, lengthChangeable);

        layout.putConstraint(SpringLayout.EAST, foreRadiusChangeableLabel, -20, SpringLayout.WEST, foreRadiusChangeable);
        layout.putConstraint(SpringLayout.NORTH, foreRadiusChangeableLabel, 0, SpringLayout.NORTH, foreRadiusChangeable);


        JLabel aftRadiusChangeableLabel = new JLabel("Aft radius changeable");
        aftRadiusChangeable = new JCheckBox();
        add(aftRadiusChangeableLabel);
        add(aftRadiusChangeable);

        layout.putConstraint(SpringLayout.EAST, aftRadiusChangeable, 0, SpringLayout.EAST, partSelector);
        layout.putConstraint(SpringLayout.NORTH, aftRadiusChangeable, 15, SpringLayout.SOUTH, foreRadiusChangeable);

        layout.putConstraint(SpringLayout.EAST, aftRadiusChangeableLabel, -20, SpringLayout.WEST, aftRadiusChangeable);
        layout.putConstraint(SpringLayout.NORTH, aftRadiusChangeableLabel, 0, SpringLayout.NORTH, aftRadiusChangeable);


        JLabel materialChangeableLabel = new JLabel("Material changeable");
        materialChangeable = new JCheckBox();
        add(materialChangeableLabel);
        add(materialChangeable);

        layout.putConstraint(SpringLayout.EAST, materialChangeable, 0, SpringLayout.EAST, partSelector);
        layout.putConstraint(SpringLayout.NORTH, materialChangeable, 15, SpringLayout.SOUTH, aftRadiusChangeableLabel);

        layout.putConstraint(SpringLayout.EAST, materialChangeableLabel, -20, SpringLayout.WEST, materialChangeable);
        layout.putConstraint(SpringLayout.NORTH, materialChangeableLabel, 15, SpringLayout.SOUTH, materialChangeable);

        /**
        JLabel lengthChangeableLabel = new JLabel("Length changeable");
        lengthChangeable = new JCheckBox();
        add(lengthChangeableLabel);
        add(lengthChangeable);

        layout.putConstraint(SpringLayout.EAST, lengthChangeable, 0, SpringLayout.EAST, partSelector);
        layout.putConstraint(SpringLayout.NORTH, lengthChangeable, 15, SpringLayout.SOUTH, partSelector);

        layout.putConstraint(SpringLayout.EAST, lengthChangeableLabel, -20, SpringLayout.WEST, lengthChangeable);
        layout.putConstraint(SpringLayout.NORTH, lengthChangeableLabel, 15, SpringLayout.SOUTH, partSelector);


        JLabel lengthChangeableLabel = new JLabel("Length changeable");
        lengthChangeable = new JCheckBox();
        add(lengthChangeableLabel);
        add(lengthChangeable);

        layout.putConstraint(SpringLayout.EAST, lengthChangeable, 0, SpringLayout.EAST, partSelector);
        layout.putConstraint(SpringLayout.NORTH, lengthChangeable, 15, SpringLayout.SOUTH, partSelector);

        layout.putConstraint(SpringLayout.EAST, lengthChangeableLabel, -20, SpringLayout.WEST, lengthChangeable);
        layout.putConstraint(SpringLayout.NORTH, lengthChangeableLabel, 15, SpringLayout.SOUTH, partSelector);
         */
    }

    private void updatePartSelector() {
        RocketPanel rocketPanel = basicFrame.getRocketPanel();
        OpenRocketDocument rocketDocument = rocketPanel.getDocument();
        if (rocketDocument != null) {
            Rocket rocket = rocketDocument.getRocket();
            componentList = getRocketComponents(rocket);
            String[] options = new String[componentList.length + 1];
            options[0] = "Select part            ";
            for (int i = 1; i < componentList.length + 1; i++) {
                options[i] = componentList[i - 1].split("\n")[0];
            }

            remove(partSelector);
            partSelector = new JComboBox(options);
            partSelector.addActionListener(this);

            add(partSelector);
            layout.putConstraint(SpringLayout.EAST, partSelector, -20, SpringLayout.EAST, this);
        }
    }

    private String[] getRocketComponents (Rocket rocket) {
        String[] components = {};
        for (int i = 0; i < rocket.getChild(0).getChildCount(); i++) {
            RocketComponent component = rocket.getChild(0).getChild(i);
            if (component.getChildCount() == 0) {
                String[] tempArray = Arrays.copyOf(components, components.length + 1);
                tempArray[tempArray.length - 1] = component.toString() + "\n" + String.valueOf(i);
                components = Arrays.copyOf(tempArray, tempArray.length);
            } else {
                String[] childComponents = getRocketComponentsHelper(component, String.valueOf(i));
                String[] tempArray = Arrays.copyOf(components, components.length + childComponents.length + 1);
                tempArray[components.length] = component.toString() + "\n" + String.valueOf(i);
                for (int j = 0; j < childComponents.length; j++) {
                    tempArray[components.length + 1 + j] = childComponents[j];
                }
                components = Arrays.copyOf(tempArray, tempArray.length);
            }
        }
        return components;
    }

    private String[] getRocketComponentsHelper (RocketComponent rocketComponent, String parents) {
        String[] components = {};
        for (int i = 0; i < rocketComponent.getChildCount(); i++) {
            RocketComponent component = rocketComponent.getChild(i);
            if (component.getChildCount() == 0) {
                String[] tempArray = Arrays.copyOf(components, components.length + 1);
                tempArray[tempArray.length - 1] = component.toString() + "\n" + parents + String.valueOf(i);
                components = Arrays.copyOf(tempArray, tempArray.length);
            } else {
                String[] childComponents = getRocketComponentsHelper(component, parents + String.valueOf(i));
                String[] tempArray = Arrays.copyOf(components, components.length + childComponents.length + 1);
                tempArray[components.length] = component.toString() + "\n" + parents + String.valueOf(i);
                for (int j = 0; j < childComponents.length; j++) {
                    tempArray[components.length + 1 + j] = childComponents[j];
                }
                components = Arrays.copyOf(tempArray, tempArray.length);
            }
        }
        return components;
    }

    private int totalChildrenCount (RocketComponent rocketComponent) {
        int children = 0;
        if (rocketComponent.getChildCount() != 0) {
            for (int i = 0; i < rocketComponent.getChildCount(); i++) {
                children += totalChildrenCount(rocketComponent.getChild(i));
            }
        } else {
            return 1;
        }
        return 1 + children;
    }

    private void run() {
        RocketPanel rocketPanel = basicFrame.getRocketPanel();
        OpenRocketDocument rocketDocument = rocketPanel.getDocument();
        SeniorResearchProject seniorProject = new SeniorResearchProject(rocketDocument);

        String[] vars = {minHeightField.getText(), maxHeightField.getText(), minMassField.getText(),
                maxMassField.getText(), minStabilityField.getText(), maxStabilityField.getText(),
                minTimeField.getText(), maxTimeField.getText(), iterationsField.getText()};
        boolean allFitPattern = true;
        for (int i = 0; i < 9; i++) {
            if (!fitsPattern(vars[i])) {
                allFitPattern = false;
            }
        }

        if(iterationsField.getText().contains(".")) {
            allFitPattern = false;
        }

        if (allFitPattern) {
            seniorProject.setMinGoalHeight(minHeightField.getText());
            seniorProject.setMaxGoalHeight(maxHeightField.getText());
            seniorProject.setMinMass(minMassField.getText());
            seniorProject.setMaxMass(maxMassField.getText());
            seniorProject.setMinStability(minStabilityField.getText());
            seniorProject.setMaxStability(maxStabilityField.getText());
            seniorProject.setMinTime(minTimeField.getText());
            seniorProject.setMaxTime(maxTimeField.getText());
            seniorProject.setMaxIterations(iterationsField.getText());
        } else {
            System.out.println("One of the fields is bad");
        }

        Rocket rocket = null;
        try {
            rocket = seniorProject.doProject();
        } catch (Exception e) {
            System.out.println("error lol");
        }

        OpenRocketDocument openRocketDocument = new OpenRocketDocument(rocket);
        basicFrame.updateRocketPanel(openRocketDocument);
    }

    //TODO: LOW: Make it so that there is a popup when this returns false.
    public boolean fitsPattern(String input) {
        int dotCount = 0;
        for (char c : input.toCharArray()) {
            if (!Character.isDigit(c) && c != '.') {
                return false;
            }
            if (c == '.') {
                dotCount++;
            }
        }
        return dotCount <= 1;
    }

    //TODO: LOW: Check that the file is the correct type
    //TODO: MeDIUM: load units
    private void loadParams() {
        JFileChooser fileChooser = new JFileChooser();
        int result = fileChooser.showOpenDialog(importButton);
        if (result == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            Scanner scanner = null;
            try {
                scanner = new Scanner(file);
            } catch (Exception e) {
                System.out.println("File not found");
            }

            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] parts = line.split("=");

                if (parts.length == 1) {
                    String temp = parts[0];
                    parts = new String[]{parts[0], ""};
                }

                switch (parts[0]) {
                    case "maxIterations":
                        iterationsField.setText(parts[1]);
                        break;
                    case "minHeight":
                        minHeightField.setText(parts[1]);
                        break;
                    case "maxHeight":
                        maxHeightField.setText(parts[1]);
                        break;
                    case "minMass":
                        minMassField.setText(parts[1]);
                        break;
                    case "maxMass":
                        maxMassField.setText(parts[1]);
                        break;
                    case "minStability":
                        minStabilityField.setText(parts[1]);
                        break;
                    case "maxStability":
                        maxStabilityField.setText(parts[1]);
                        break;
                    case "minTime":
                        minTimeField.setText(parts[1]);
                        break;
                    case "maxTime":
                        maxTimeField.setText(parts[1]);
                        break;
                    default:
                        break;
                }
            }


        }
    }


    //TODO: MEDIUM: Save units in file
    private void saveParams() {
        String[] vars = {minHeightField.getText(), maxHeightField.getText(), minMassField.getText(),
                maxMassField.getText(), minStabilityField.getText(), maxStabilityField.getText(),
                minTimeField.getText(), maxTimeField.getText(), iterationsField.getText()};
        boolean allFitPattern = true;
        for (int i = 0; i < 9; i++) {
            if (!fitsPattern(vars[i])) {
                allFitPattern = false;
            }
        }

        if(iterationsField.getText().contains(".")) {
            allFitPattern = false;
        }


        if (allFitPattern) {
            JFileChooser fileChooser = new JFileChooser();
            int result = fileChooser.showSaveDialog(saveParamsButton);
            if (result == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
                try {
                    FileOutputStream fileOutputStream = new FileOutputStream(file);

                    String toWrite = "maxIterations=" + iterationsField.getText() + "\nminHeight=" + minHeightField.getText() +
                            "\nmaxHeight=" + maxHeightField.getText() + "\nminMass=" + minMassField.getText() + "\nmaxMass=" +
                            maxMassField.getText() + "\nminStability=" + minStabilityField.getText() + "\nmaxStability" +
                            maxStabilityField.getText() + "\nminTime=" + minTimeField.getText() + "\nmaxTime=" + maxTimeField.getText();

                    char[] toWriteAsCharArray = toWrite.toCharArray();
                    for (int i = 0; i < toWriteAsCharArray.length; i++) {
                        fileOutputStream.write(toWriteAsCharArray[i]);
                    }
                    fileOutputStream.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    private void updateParameterSelection() {

    }

    public void actionPerformed(ActionEvent e) {
        if (rocketParameter == null) {
            RocketPanel rocketPanel = basicFrame.getRocketPanel();
            OpenRocketDocument rocketDocument = rocketPanel.getDocument();
            if (rocketDocument != null) {
                Rocket rocket = rocketDocument.getRocket();
                rocketParameter = new RocketParameter(rocket.getChild(0), true);
            }

        }

        if (rocketParameter != null) {
            if (e.getSource() == runButton) {
                run();
            } else if (e.getSource() == importButton) {
                loadParams();
            } else if (e.getSource() == saveParamsButton) {
                saveParams();
            } else if (e.getSource() == updateComponentsButton) {
                updatePartSelector();
            } else if (e.getSource() == partSelector) {
                updateParameterSelection();
            }
        }
    }
}