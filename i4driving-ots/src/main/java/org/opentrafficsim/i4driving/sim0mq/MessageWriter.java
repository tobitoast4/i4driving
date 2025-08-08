package org.opentrafficsim.i4driving.sim0mq;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class MessageWriter {
    private String fileName;

    public MessageWriter(String fileName) {
        this.fileName = fileName;
    }

    public void writeMessage(String message) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(this.fileName, true))) {
            writer.write(message);
            writer.newLine();  // separate each message by a line
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
