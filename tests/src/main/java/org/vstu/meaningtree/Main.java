package org.vstu.meaningtree;

import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        try (FileReader testStream = new FileReader("sample.test")) {

            TestsRunner runner = new TestsRunner();
            runner.run(testStream);

        } catch (IOException exception) {
            System.out.print(exception.getMessage());
        }
    }
}
