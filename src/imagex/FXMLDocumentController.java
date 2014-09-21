/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package imagex;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javax.imageio.ImageIO;

/**
 *
 * @author toshiba
 */
public class FXMLDocumentController implements Initializable {

    private final static double XXHDPI = 100;
    private final static double XHDPI = 66.66;
    private final static double HDPI = 50;
    private final static double MDPI = 33.33;
    private final static double LDPI = 22.22;

    private final static double NON_RETINA = 50;
    private final static double RETINA = 100;

    private Stage primaryStage;

    @FXML
    private Label label, interimMessage;

    @FXML
    private TextField inFilePath, outFilePath;

    @FXML
    private ProgressBar progress;

    @FXML
    private Button btnStartConv;

    @FXML
    private CheckBox chkLdpi, chkMdpi, chkHdpi, chkXhdpi, chkXXhdpi, chkNonRetina, chkRetina, chkRetina4Inch;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        inFilePath.setText("F:\\ImageX\\Test");
        outFilePath.setText("F:\\ImageX\\Test");

        EventHandler eh = new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (event.getSource() instanceof CheckBox) {
                    CheckBox chk = (CheckBox) event.getSource();
                    String viewId = chk.getId();
                    if (viewId.equals(chkLdpi.getId()) || viewId.equals(chkMdpi.getId()) || viewId.equals(chkHdpi.getId()) || viewId.equals(chkXhdpi.getId()) || viewId.equals(chkXXhdpi.getId())) {
                        chkNonRetina.setSelected(false);
                        chkRetina.setSelected(false);
                        chkRetina4Inch.setSelected(false);
                    }
                    if (viewId.equals(chkNonRetina.getId()) || viewId.equals(chkRetina.getId()) || viewId.equals(chkRetina4Inch.getId())) {
                        chkLdpi.setSelected(false);
                        chkMdpi.setSelected(false);
                        chkHdpi.setSelected(false);
                        chkXhdpi.setSelected(false);
                        chkXXhdpi.setSelected(false);
                    }
                }
            }
        };
        chkLdpi.setOnAction(eh);
        chkMdpi.setOnAction(eh);
        chkHdpi.setOnAction(eh);
        chkXhdpi.setOnAction(eh);
        chkXXhdpi.setOnAction(eh);
        chkNonRetina.setOnAction(eh);
        chkRetina.setOnAction(eh);
        chkRetina4Inch.setOnAction(eh);
        chkNonRetina.setOnAction(eh);
        chkRetina.setOnAction(eh);
        chkRetina4Inch.setOnAction(eh);
    }

    public void setStage(Stage temp) {
        primaryStage = temp;
    }

    @FXML
    private void handleButtonAction(ActionEvent event) {
        System.out.println("You clicked me!");
        label.setText("Hello World!");
    }

    @FXML
    private void openInDirecotryChooser(ActionEvent event) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        File selectedDirectory
                = directoryChooser.showDialog(primaryStage);

        if (selectedDirectory == null) {
            System.out.println("No Directory selected");
        } else {
            inFilePath.setText(selectedDirectory.getAbsolutePath());
            outFilePath.setText(selectedDirectory.getAbsolutePath());
            System.out.println(selectedDirectory.getAbsolutePath());
        }
    }

    @FXML
    private void openOutDirecotryChooser(ActionEvent event) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        File selectedDirectory
                = directoryChooser.showDialog(primaryStage);

        if (selectedDirectory == null) {
            System.out.println("No Directory selected");
        } else {
            outFilePath.setText(selectedDirectory.getAbsolutePath());
            System.out.println(selectedDirectory.getAbsolutePath());
        }
    }

    @FXML
    private void startConversion(ActionEvent event) {
        ResizeTask task = new ResizeTask(interimMessage, progress);
        new Thread(task).start();
    }

    private float getProgressCount(int currentCount, int totalCount) {
        return ((float) (currentCount + 1)) / (totalCount + 1);
    }

    private void scaleImage(double dpi, File image) throws Exception {
        BufferedImage inputImage = ImageIO.read(image);

        int height = getScaledPixel(inputImage.getHeight(), dpi);
        int width = getScaledPixel(inputImage.getWidth(), dpi);
        BufferedImage outputImage = new BufferedImage(width, height,
                inputImage.getType());
        Graphics2D g2d = outputImage.createGraphics();
        g2d.drawImage(inputImage, 0, 0, width, height, null);
        g2d.dispose();

        writeScaledImage(outputImage, image.getName(), dpi);

    }

    private int getScaledPixel(int baseValue, double percent) {
        return (int) ((percent * baseValue) / 100);
    }

    private void writeScaledImage(BufferedImage outputImage,
            String formatName, double dpi) throws Exception {
        String outputPath = null;
        File outputDirectry = null;
        if (dpi == XXHDPI) {
            outputPath = outFilePath.getText() + File.separator + "drawable-xxhdpi";
        } else if (dpi == XHDPI) {
            outputPath = outFilePath.getText() + File.separator + "drawable-xhdpi";
        } else if (dpi == HDPI) {
            outputPath = outFilePath.getText() + File.separator + "drawable-hdpi";
        } else if (dpi == MDPI) {
            outputPath = outFilePath.getText() + File.separator + "drawable-mdpi";
        } else {
            outputPath = outFilePath.getText() + File.separator + "drawable-ldpi";
        }

        outputDirectry = new File(outputPath);
        if (!outputDirectry.exists()) {
            outputDirectry.mkdir();
        }
        File outputFile = new File(outputDirectry, formatName);
        outputFile.createNewFile();
        formatName = formatName.substring(formatName.lastIndexOf(".") + 1);
        ImageIO.write(outputImage, formatName, outputFile);
    }

    class ResizeTask extends Task<Void> {

        private Label interimMessage;
        private ProgressBar progressBar;

        ResizeTask(Label interimMessage, ProgressBar progressBar) {
            this.interimMessage = interimMessage;
            this.progressBar = progressBar;
        }

        @Override
        protected Void call() throws Exception {
            File directory = new File(inFilePath.getText());
            File[] files = directory.listFiles();
            boolean isLdpi = chkLdpi.isSelected();
            boolean isMdpi = chkMdpi.isSelected();
            boolean isHdpi = chkHdpi.isSelected();
            boolean isXhdpi = chkXhdpi.isSelected();
            boolean isXXhdpi = chkXXhdpi.isSelected();

            boolean isNonRetina = chkNonRetina.isSelected();
            boolean isRetina = chkRetina.isSelected();
            boolean isRetina4Inch = chkRetina4Inch.isSelected();

            if (files != null && files.length > 0) {
                int fileCount = files.length;
                publishProgress(0);
                publishMessage("Sit back and relax while we resize image for you...");
                for (int count = 0; count < fileCount; count++) {
                    try {
                        if (files[count].isFile()) {
                            if (isLdpi) {
                                scaleImage(LDPI, files[count]);
                            }
                            if (isMdpi) {
                                scaleImage(MDPI, files[count]);
                            }
                            if (isHdpi) {
                                scaleImage(HDPI, files[count]);
                            }
                            if (isXhdpi) {
                                scaleImage(XHDPI, files[count]);
                            }
                            if (isXXhdpi) {
                                scaleImage(XXHDPI, files[count]);
                            }
                            if (isNonRetina) {
                                scaleImage(NON_RETINA, files[count]);
                            }
                            if (isRetina) {
                                scaleImage(RETINA, files[count]);
                            }
                            if (isRetina4Inch) {
                                scaleImage(RETINA, files[count]);
                            }
                            publishProgress(getProgressCount(count, fileCount));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        continue;
                    }
                }
                publishProgress(1);
                publishMessage("Resizing done, check the respected folders");
            } else {
                publishMessage("We don't find any images");
            }
            return null;
        }

        void publishProgress(final float progress) {
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    progressBar.setProgress(progress);
                }
            });
        }

        void publishMessage(final String message) {
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    interimMessage.setText(message);
                }
            });
        }
    }
}
