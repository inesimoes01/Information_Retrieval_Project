package unipi.it.mircv.common;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryPoolMXBean;
import java.lang.management.MemoryUsage;

public class Util{

private double threshold;


    public static boolean isMemoryFull(double threshold) {

        // Ottieni le informazioni sulla memoria
        for (MemoryPoolMXBean pool : ManagementFactory.getMemoryPoolMXBeans()) {
            MemoryUsage usage = pool.getUsage();
            long usedMemory = usage.getUsed();
            long maxMemory = usage.getMax();

            // Calcola la percentuale di memoria utilizzata
            double percentageUsed = (double) usedMemory / maxMemory * 100;

            // Controlla se la percentuale supera il 75%
            if (percentageUsed >= threshold) {
                return true;
            }

        }
        return false;
    }
    public void printUsage(){
                // Get the runtime object
                Runtime runtime = Runtime.getRuntime();

                // Calculate memory usage in bytes
                long totalMemory = runtime.totalMemory();
                long freeMemory = runtime.freeMemory();
                long usedMemory = totalMemory - freeMemory;

                // Calculate and print the percentage of used memory
                double percentageUsed = ((double) usedMemory / totalMemory) * 100;
                System.out.println("Total Memory: " + totalMemory + " bytes");
                System.out.println("Used Memory: " + usedMemory + " bytes");
                System.out.println("Free Memory: " + freeMemory + " bytes");
                System.out.println("Percentage Used: " + percentageUsed + "%");
    }

        public void setThreshold ( float threshold){
            this.threshold = threshold;
        }

    public double getThreshold() {
        return threshold;
    }
}