package org;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;


public class Timer {

    private static Map<String, Long> startTimes = new HashMap<String, Long>();
    private static Map<String, Long> runningTimes = new HashMap<String, Long>();
    private static DecimalFormat formatter = new DecimalFormat("#.00");
    
    public static void start(String label) {
        Timer.startTimes.put(label, System.nanoTime());
    }
      
    public static void stop(String label) {
        Timer.runningTimes.put(label, System.nanoTime() - Timer.startTimes.get(label));
    }
    
    public static long get(String label) {
        return Timer.runningTimes.get(label);
    }
    
    public static String getFormatted(String label) {
        return Timer.formatter.format((double)Timer.runningTimes.get(label) / 1000 / 1000 / 1000);
    }
    
    public static void print(String label) {
        System.out.println("Running time of '" + label + "' is: " + Timer.getFormatted(label) + " seconds.");
    }
}
