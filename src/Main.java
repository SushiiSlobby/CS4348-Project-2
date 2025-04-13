import java.util.concurrent.atomic.*;
import java.util.concurrent.*;
import java.util.*;

public class Main {
    public static final int NUM_TELLERS = 3;
    public static final int NUM_CUSTOMERS = 50;

    // Shared semaphores and state
    public static final Semaphore managerAccess = new Semaphore(1);
    public static final Semaphore safeAccess = new Semaphore(2);
    public static final Semaphore doorAccess = new Semaphore(2);
    public static final Semaphore bankOpen = new Semaphore(0);

    public static final BlockingQueue<Customer> customerQueue = new LinkedBlockingQueue<>();
    public static final List<Teller> tellers = new ArrayList<>();

    public static final AtomicInteger customersServed = new AtomicInteger(0);

    private static int tellerIndex = 0;

    public static void main(String[] args) {
        System.out.println("Hello, World!");
    }
}