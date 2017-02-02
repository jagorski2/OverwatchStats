/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package overwatchstats;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

/**
 *
 * @author gorsk
 */
public class OverwatchStats extends Application {

    @Override
    public void start(Stage primaryStage) throws IOException {

        //defining the axes
        final NumberAxis xAxis = new NumberAxis();
        final NumberAxis yAxis = new NumberAxis();
        xAxis.setMinorTickVisible(false);
        xAxis.setTickLabelsVisible(false);
        //creating the chart
        yAxis.setAutoRanging(true);

        yAxis.setTickUnit(100);
        final LineChart<Number, Number> lineChart
                = new LineChart<Number, Number>(xAxis, yAxis);

        lineChart.setTitle("SR");
        //defining a series
        XYChart.Series series = new XYChart.Series();
        series.setName("My SR");
        //populating the series with data
        String file = "sr.txt";
        int index = 1;
        int min = Integer.MAX_VALUE;
        int max = Integer.MIN_VALUE;

        File myfile;
        myfile = new File("sr.txt");

        if (!myfile.exists()) {
            myfile.createNewFile();
        }

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (!"".equals(line)) {
                    try {
                        int tempint = Integer.parseInt(line);
                        series.getData().add(new XYChart.Data(index++, Integer.parseInt(line)));
                        if (tempint > max) {
                            max = tempint;
                        }
                        if (tempint < min) {
                            min = tempint;
                        }
                    } catch (NumberFormatException e) {
                        //
                    }
                }
            }
        }

        lineChart.getData().add(series);

        GridPane root = new GridPane();
        root.add(addGridPane(lineChart, series, min, max), 1, 1);
        root.add(lineChart, 2, 1);

        Scene scene = new Scene(root, 800, 480);

        primaryStage.setFullScreen(false);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

    public GridPane addGridPane(LineChart<Number, Number> lineChart, XYChart.Series myser, int min, int max) {
        int col = 1;
        int row = 2;
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        Label label = new Label("");
        label.setPrefHeight(50);
        label.setTextAlignment(TextAlignment.CENTER);
        label.setFont(new Font("Arial", 30));

        Button buttons[] = new Button[10];

        for (int ii = 0; ii < 10; ++ii) {
            buttons[ii] = new Button();
            buttons[ii].setPrefWidth(50);
            buttons[ii].setPrefHeight(50);
            buttons[ii].setOnAction(new EventHandler<ActionEvent>() {

                @Override
                public void handle(ActionEvent event) {
                    Button temp = (Button) event.getSource();
                    String temps = temp.getText();
                    temps = label.getText() + temps;
                    label.setText(temps);
                }
            });
            if (ii != 9) {
                buttons[ii].setText(String.valueOf(ii + 1));
                grid.add(buttons[ii], col++, row);

                if (col > 3) {
                    col = 1;
                    row++;
                }
            } else {
                buttons[ii].setText("0");
                grid.add(buttons[ii], 2, 5);
            }
        }

        grid.add(label, 1, 1, 3, 1);

        Button clear = new Button();
        clear.setText("C");
        clear.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                label.setText("");
            }
        });
        grid.add(clear, 1, 5);

        Button back = new Button();
        back.setText("B");
        back.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                label.setText(label.getText().substring(0, label.getText().length() - 1));
            }
        });
        grid.add(back, 3, 5);

        Button enter = new Button();
        enter.setPrefWidth(90);
        enter.setText("Enter");
        enter.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                int sr = Integer.parseInt(label.getText());
                if (sr > 5000) {
                    label.setText("");
                    return;
                }

                try {
                    File file = new File("sr.txt");
                    FileWriter fw = null;
                    fw = new FileWriter(file.getAbsoluteFile(), true);
                    BufferedWriter bw = new BufferedWriter(fw);

                    bw.write(label.getText());
                    bw.newLine();

                    bw.close();
                    fw.close();

                } catch (IOException ex) {
                    Logger.getLogger(OverwatchStats.class.getName()).log(Level.SEVERE, null, ex);
                }
                myser.getData().add(new XYChart.Data(myser.getData().size(), Integer.parseInt(label.getText())));
                label.setText("");

            }

        });
        grid.add(enter, 1, 6, 3, 1);
        return grid;
    }

}
