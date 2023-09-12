package com.jdoodle.docker.shell;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

@Component
public class ShellUtil {

    Logger logger = LoggerFactory.getLogger(ShellUtil.class);

    public String executeCommand(String command) {

        StringBuffer output = new StringBuffer();

        Process p;
        try {
            p = Runtime.getRuntime().exec(command);
            p.waitFor();
            logger.info("inside execute command: " + command);
            BufferedReader reader =
                    new BufferedReader(new InputStreamReader(p.getInputStream()));

            String line = "";
            while ((line = reader.readLine()) != null) {
                output.append(line + "\n");
            }

        } catch (Exception e) {
            logger.info("error: " + e);
            logger.info(e.getMessage());
            e.printStackTrace();
        }

        return output.toString();

    }


    public String processBuilder(String[] command) {

        ProcessBuilder builder = new ProcessBuilder(command);

        builder.redirectError(ProcessBuilder.Redirect.INHERIT);
        String outputString = "";
        Process process = null;
        try {
            process = builder.start();

            try (InputStream processOutput = process.getInputStream()) {
                outputString = new String(processOutput.readAllBytes());
            }

            int exitCode = process.waitFor();
            if (exitCode != 0) {
                outputString = "Exit code " + exitCode + " was returned by "
                        + builder.command();
                throw new IOException("Exit code " + exitCode + " was returned by "
                        + builder.command());
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return outputString;
    }

}



