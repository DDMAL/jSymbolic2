package jsymbolic2;

import java.io.IOException;

/**
 * Run jSymbolic as a separate process if the heap size is not large enough.
 * This allows the programmers to control the size of the heap at runtime
 * as opposed to making the user manually perform this action.
 *
 * @author Tristano Tenaglia
 */
public class jSymbolicRunner {
    private final static int MIN_HEAP_SIZE = 511;
    private final static int RECOMMENDED_HEAP_SIZE = 2047;

    public static void main(String[] args) throws IOException {
        double heapSizeMegs = (Runtime.getRuntime().maxMemory()/1024)/1024;
        if (heapSizeMegs > MIN_HEAP_SIZE) {
            try {
                Main.main(args);
            } catch (OutOfMemoryError e) {
                newProcess();
            }
        } else {
                newProcess();
        }
    }

    /**
     * Start jSymbolic as a new process in case Java heap space is not enough.
     * @throws IOException Thrown here in case appropriate OS system files are not found
    *                       or if working directory does not exist or is not accessible
     */
    private static void newProcess() throws IOException {
        String classPath = System.getProperty("java.class.path");
        ProcessBuilder pb = new ProcessBuilder("java","-Xmx" + RECOMMENDED_HEAP_SIZE + "m", "-classpath", classPath, "jsymbolic2.Main");
        //Exception thrown here in case appropriate OS system files are not found
        //or if working directory does not exist or is not accessible
        pb.start();
    }
}
