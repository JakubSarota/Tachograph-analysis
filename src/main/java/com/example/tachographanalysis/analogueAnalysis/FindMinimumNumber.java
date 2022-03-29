package com.example.tachographanalysis.analogueAnalysis;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;
import java.util.StringTokenizer;
import java.util.*;
import java.io.*;
import java.util.stream.Collectors;

public class FindMinimumNumber {
    public static Integer FindMinimum() throws IOException {
        double count = 0.0;
        double mag = 0.0;
        double total = 0.0;
        double min = Double.MAX_VALUE;

        Scanner inputFile = new Scanner(new FileReader("findMinimumWithoutDegree.txt"));

        while (inputFile.hasNextLine()) {
            String line = inputFile.nextLine();
            count++;
            StringTokenizer str = new StringTokenizer(line);
            if (str.hasMoreTokens()) {
                mag = Double.parseDouble(str.nextToken(","));
            }
            if (mag < min) {
                min = mag;
            }
            total = mag + total;
        }
        inputFile.close();

        BufferedReader br = new BufferedReader(new FileReader(new File("findMinimum.txt")));
        Double d = min;
        String minStr = Double.toString(d);
        String lineRead  = null;
        String problem = minStr;
        List<String> numberData = new ArrayList<String>();
        while((lineRead = br.readLine())!=null) {
            if(lineRead.contains(problem)) {
                StringTokenizer st = new StringTokenizer(lineRead," \n");
                String problemPart = st.nextToken();
                String numbersPart = st.nextToken();
                st = new StringTokenizer(lineRead,"|\n");
                while(st.hasMoreTokens()) {
                    String number = st.nextToken();
                    numberData.add(number);
                }
                break;
            }
        }

        String rs = numberData.stream()
                .map(n -> String.valueOf(n))
                .collect(Collectors.joining());
        String rsN = null;
        if(rs.length() > 3) {
            rsN = rs.substring(rs.length() - 3);
            rsN = rsN.strip();
        }

        String Result = rsN;
        int ResultNumber = Integer.parseInt(Result);
        return ResultNumber;
    }
}
