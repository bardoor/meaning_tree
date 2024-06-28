package org.vstu.meaningtree;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class TestsRunner {
    public void run(FileReader testStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(testStream);

        StringBuilder tests = new StringBuilder();
        String line;

        while ((line = bufferedReader.readLine()) != null) {
            tests.append(line).append("\n");
        }

        TestGroup[] testGroups = TestsParser.parse(tests.toString());
    }
}