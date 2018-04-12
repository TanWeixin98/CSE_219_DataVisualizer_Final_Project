package ui;

import Algorithm.AlgorithmType;
import Algorithm.ClassificationAlgorithm;
import Algorithm.ClusteringAlgorithm;
import Algorithm.Configuration;
import actions.AppActions;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import settings.AppPropertyTypes;
import vilij.propertymanager.PropertyManager;
import vilij.settings.PropertyTypes;
import vilij.templates.ApplicationTemplate;
import vilij.templates.UITemplate;

import javax.xml.soap.Text;
import java.util.HashSet;
import java.util.Set;

public class AppUI extends UITemplate {
    ApplicationTemplate applicationTemplate;

    private Button                              scrnShootButton;
    private Button                              display;
    private LineChart<Number,Number>            chart;
    private TextArea                            textArea;
    private Label                               InfoText;
    private AlgorithmType                       selectedAlgorithm;
    private Configuration                       configuration;
    private Pane                                selectionPane;
    private Set<AlgorithmType>                  algorithmTypeSet;
    private ToggleButton                        isComplete;



    public AppUI(Stage primaryStage, ApplicationTemplate applicationTemplate) {
        super(primaryStage, applicationTemplate);
        this.applicationTemplate = applicationTemplate;
        algorithmTypeSet= new HashSet<>();
        algorithmTypeSet.add(new ClassificationAlgorithm());
        algorithmTypeSet.add(new ClusteringAlgorithm());
    }
    @Override
    protected void setResourcePaths(ApplicationTemplate applicationTemplate) {
        super.setResourcePaths(applicationTemplate);
    }
    @Override
    protected void setToolBar(ApplicationTemplate applicationTemplate) {
        PropertyManager manager = applicationTemplate.manager;
        super.setToolBar(applicationTemplate);
        String scrnShotPath =manager.getPropertyValue(AppPropertyTypes.Separator.name())+String.join(manager.getPropertyValue(AppPropertyTypes.Separator.name()),
                manager.getPropertyValue(PropertyTypes.GUI_RESOURCE_PATH.name()),
                manager.getPropertyValue(PropertyTypes.ICONS_RESOURCE_PATH.name()),
                manager.getPropertyValue(AppPropertyTypes.SCREENSHOT_ICON.name()));
        scrnShootButton = setToolbarButton(scrnShotPath,manager.getPropertyValue(AppPropertyTypes.SCREENSHOT_TOOLTIP.name()),true);
        toolBar = new ToolBar(newButton,saveButton,loadButton,exitButton,scrnShootButton);
    }
    @Override
    protected void setToolbarHandlers(ApplicationTemplate applicationTemplate) {
        applicationTemplate.setActionComponent(new AppActions(applicationTemplate));
        newButton.setOnAction(e -> applicationTemplate.getActionComponent().handleNewRequest());
        saveButton.setOnAction(e -> applicationTemplate.getActionComponent().handleSaveRequest());
        loadButton.setOnAction(e -> applicationTemplate.getActionComponent().handleLoadRequest());
        exitButton.setOnAction(e -> applicationTemplate.getActionComponent().handleExitRequest());
    }
    @Override
    public void initialize(){
        layout();
        setWorkSpaceActions();
    }

    public void layout(){
        PropertyManager manager = applicationTemplate.manager;

        chart= new LineChart<>(new NumberAxis(),new NumberAxis());
        InfoText = new Label();
        textArea = new TextArea();
        display = new Button(manager.getPropertyValue(AppPropertyTypes.DISPLAY_BUTTON_TEXT.name()));
        isComplete = new ToggleButton("hello");

        VBox leftPanel= new VBox(10);
        leftPanel.setAlignment(Pos.TOP_CENTER);
        leftPanel.setPadding(new Insets(10));
        leftPanel.setMaxSize(windowWidth * 0.29, windowHeight * 0.69);
        leftPanel.setMinSize(windowWidth * 0.29, windowHeight * 0.69);
        VBox.setVgrow(leftPanel,Priority.ALWAYS);

        selectionPane=new VBox(10);
        Label selectionLabel = new Label("Algorithms");
        selectionPane.getChildren().add(selectionLabel);
        selectionPane.setPadding(new Insets(10,10,10,10));
        for (AlgorithmType algorithmType : algorithmTypeSet) {
            RadioButton algorithm = new RadioButton(algorithmType.toString());
            algorithm.setWrapText(true);
            algorithm.setMinWidth(leftPanel.getMaxWidth()-30);
            selectionPane.getChildren().add(algorithm);
            algorithm.setOnAction((ActionEvent e) -> {
                selectedAlgorithm = algorithmType;
                showAlgorithmSelection(algorithmType);
            });
        }

        VBox.setVgrow(selectionPane,Priority.ALWAYS);

        //selectionPane.setVisible(false);

        leftPanel.getChildren().addAll( textArea,InfoText,isComplete,selectionPane);

        VBox rightPanel = new VBox(chart);
        rightPanel.setMaxWidth(windowWidth*.69);
        rightPanel.setMinWidth(windowWidth*.69);
        VBox.setVgrow(chart,Priority.ALWAYS);

        workspace= new HBox(leftPanel,rightPanel);
        HBox.setHgrow(workspace, Priority.ALWAYS);
        appPane.getChildren().add(workspace);
        VBox.setVgrow(appPane,Priority.ALWAYS);
        primaryScene.getStylesheets().add(getClass().getResource(manager.getPropertyValue(AppPropertyTypes.CSS_Path.name())).toExternalForm());
    }
    public void setWorkSpaceActions(){
        textArea.textProperty().addListener((observable, oldValue, newValue)->{
            if(textArea.getText().isEmpty()) {
                saveButton.setDisable(true);
                newButton.setDisable(true);
            }
            else {
                saveButton.setDisable(false);
                newButton.setDisable(false);
            }
        });
        display.setOnAction(e->{
        });
    }

    public void showDataInformation(String dataInfo){InfoText.setText(dataInfo);}

    public void disableSaveButton(Boolean disable){
        saveButton.setDisable(disable);
    }
    public void disableScrnShotButton(Boolean disable){
        scrnShootButton.setDisable(disable);
    }
    public void disableRunButton(Boolean disable){
        display.setDisable(disable);
    }

    @Override
    public void clear(){
        clearChart();
        clearTextArea();
    }

    private void clearTextArea(){textArea.clear();}
    public TextArea getTextArea(){
        return textArea;
    }
    private void clearChart(){
        while(!chart.getData().isEmpty())
            chart.getData().remove((int)(Math.random()*(chart.getData().size()-1)));
    }

    private void showAlgorithmTypeSelection(){
        selectionPane.setVisible(true);
    }
    private void showAlgorithmSelection(AlgorithmType algorithmType){
        for(int i=0;i<=algorithmTypeSet.size();i++){
            selectionPane.getChildren().remove(0);
        }
        selectionPane.getChildren().add(display);
    }
    public void initConfiguration(Stage owner){
        Stage configurationStage = new Stage();

        configurationStage.initModality(Modality.WINDOW_MODAL);
        configurationStage.initOwner(owner);

        CheckBox continous = new CheckBox("Continous Run");
        VBox configurationPanel= new VBox();
        TextField maxIntervalInput = new TextField();
        TextField iterationInterval = new TextField();
        Button setButton = new Button("Confirm");

        configurationPanel.getChildren().addAll(new Label("Max Interval"),
                maxIntervalInput,new Label("Iteration Interval"),
                iterationInterval,continous,setButton);
        configurationPanel.setAlignment(Pos.TOP_LEFT);
        configurationPanel.setSpacing(20);
        configurationPanel.setPadding(new Insets(10,10,10,10));
        Scene configurationScene = new Scene(configurationPanel);
        configurationStage.setScene(configurationScene);
        configurationStage.show();
        setButton.setOnAction(e->ConfigurationAction(maxIntervalInput.getText(), iterationInterval.getText(),continous.isSelected(),configurationStage));
    }

    private void ConfigurationAction(String maxInterval,String IterationInterval,Boolean iscontinousRun, Stage configurationStage){
        try{
            Integer maxInt = new Integer(maxInterval);
            Integer iterationInt = new Integer(IterationInterval);
            configuration = new Configuration(maxInt,iterationInt,iscontinousRun,selectedAlgorithm);
            configurationStage.close();
        }catch (NumberFormatException error){

        }
    }



}
