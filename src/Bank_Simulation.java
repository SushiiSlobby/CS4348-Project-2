import java.util.concurrent.atomic.*;
import java.util.concurrent.*;
import java.util.*;

public class Bank_Simulation {
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

    public static void main(String[] args) throws InterruptedException {
        for(int i = 0; i < NUM_TELLERS; i++) {
            Teller teller = new Teller(i);
            tellers.add(teller);
            Thread t = new Thread(teller);
            t.start();
        }

        Thread.sleep(100);

        bankOpen.release(NUM_CUSTOMERS);

        for(int i = 0; i < NUM_TELLERS; i++) {
           Customer customer = new Customer(i);
           Thread t = new Thread(customer);
           t.start();
        }

    }

    static class Teller implements Runnable {
        int id;
        Semaphore customerReady = new Semaphore(0);
        Semaphore transactionDone = new Semaphore(0);
        Customer currentCustomer;
        boolean open = true;

        public Teller(int id) {
            this.id = id;
        }

        @Override
        public void run() {
            System.out.printf("Teller %d [");
        }
    }

    static class Customer implements Runnable {
        int id;
        boolean isWithdrawal;
        Semaphore transactionTypeAvailable = new Semaphore(0);
        Semaphore left = new Semaphore(0);

        Customer(int id) {
            this.id = id;
            this.isWithdrawal = new Random().nextBoolean();
        }

        @Override
        public void run() {

        }
    }

    private static int random(int min, int max) {
        return ThreadLocalRandom.current().nextInt(min, max + 1);
    }

    private static synchronized Teller findAvailableTeller() {
        Teller teller = tellers.get(tellerIndex);
        tellerIndex = (tellerIndex + 1) % tellers.size();
        return teller;
    }
}