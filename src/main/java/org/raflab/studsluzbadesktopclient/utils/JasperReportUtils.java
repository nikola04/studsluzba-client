package org.raflab.studsluzbadesktopclient.utils;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.xml.JRXmlLoader;

import java.io.File;
import java.io.InputStream;
import java.util.function.Consumer;

public class JasperReportUtils {
    public static JasperReport getReport(String reportName) throws JRException {
        InputStream stream = JasperReportUtils.class.getResourceAsStream("/reports/" + reportName + ".jrxml");

        if (stream == null)
            throw new JRException("JRXML not found: " + reportName);

        JasperDesign design = JRXmlLoader.load(stream);
        return JasperCompileManager.compileReport(design);
    }

    public static void exportPdf(Window window, JasperPrint jasperPrint, String fileName) {
        Platform.runLater(() -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Sačuvaj " + fileName + "?");
            fileChooser.setInitialFileName(fileName + ".pdf");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF Files", "*.pdf"));

            File file = fileChooser.showSaveDialog(window);
            if (file == null)
                return;
            try {
                JasperExportManager.exportReportToPdfFile(jasperPrint, file.getAbsolutePath());
            } catch (JRException e) {
                ErrorHandler.displayError(e);
            }
        });
    }

    public static void runTask(ThrowingRunnable reportLogic, Consumer<Void> onSuccess, Runnable onFinish) {
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                reportLogic.run();
                return null;
            }
        };

        task.setOnSucceeded(e -> {
            if (onSuccess != null) onSuccess.accept(null);
            if (onFinish != null) onFinish.run();
        });

        task.setOnFailed(e -> {
            ErrorHandler.displayError(task.getException());
            if (onFinish != null) onFinish.run();
        });
        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }

    @FunctionalInterface
    public interface ThrowingRunnable {
        void run() throws Exception;
    }
}
