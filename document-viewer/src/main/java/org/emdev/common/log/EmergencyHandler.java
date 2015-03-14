package org.emdev.common.log;

import android.os.Build;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.emdev.BaseDroidApp;
import org.emdev.common.android.AndroidVersion;

class EmergencyHandler implements UncaughtExceptionHandler {

    private final SimpleDateFormat SDF = new SimpleDateFormat("yyyyMMdd.HHmmss");

    private final UncaughtExceptionHandler system;

    EmergencyHandler() {
        system = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    @Override
    public void uncaughtException(final Thread thread, final Throwable ex) {
        processException(ex);
        system.uncaughtException(thread, ex);
    }

    void processException(final Throwable th) {
        try {
            final String timestamp = SDF.format(new Date());

            final Writer result = new StringWriter();
            final PrintWriter printWriter = new PrintWriter(result);

            th.printStackTrace(printWriter);
            final String stacktrace = result.toString();
            printWriter.close();

            final String name = BaseDroidApp.APP_PACKAGE + "." + BaseDroidApp.APP_VERSION_NAME + "." + timestamp
                    + ".stacktrace";
            final File filename = new File(LogManager.LOG_STORAGE, name);

            writeToFile(stacktrace, filename);
        } catch (final Exception e) {
            e.printStackTrace();
        }
    }

    private void writeToFile(final String stacktrace, final File filename) {
        try {
            final BufferedWriter bos = new BufferedWriter(new FileWriter(filename));

            bos.write("Application information:\n\n");
            bos.write("This file was generated by the " + BaseDroidApp.APP_PACKAGE + "."
                    + BaseDroidApp.APP_VERSION_NAME + "(" + BaseDroidApp.APP_VERSION_CODE + ")\n");
            bos.write("\nDevice information:\n\n");
            bos.write("VERSION     : " + AndroidVersion.VERSION + "\n");
            bos.write("BOARD       : " + Build.BOARD + "\n");
            bos.write("BRAND       : " + Build.BRAND + "\n");
            bos.write("CPU_ABI     : " + BaseDroidApp.BUILD_PROPS.getProperty("ro.product.cpu.abi") + "\n");
            bos.write("CPU_ABI2    : " + BaseDroidApp.BUILD_PROPS.getProperty("ro.product.cpu.abi2") + "\n");
            bos.write("DEVICE      : " + Build.DEVICE + "\n");
            bos.write("DISPLAY     : " + Build.DISPLAY + "\n");
            bos.write("FINGERPRINT : " + Build.FINGERPRINT + "\n");
            bos.write("ID          : " + Build.ID + "\n");
            bos.write("MANUFACTURER: " + BaseDroidApp.BUILD_PROPS.getProperty("ro.product.manufacturer") + "\n");
            bos.write("MODEL       : " + Build.MODEL + "\n");
            bos.write("PRODUCT     : " + Build.PRODUCT + "\n");
            bos.write("\nError information:\n\n");
            bos.write(stacktrace);
            bos.flush();
            bos.close();
            System.out.println("Stacktrace is written: " + filename);
        } catch (final Exception e) {
            e.printStackTrace();
        }
    }
}