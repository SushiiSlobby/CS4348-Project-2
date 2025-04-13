import java.util.concurrent.Semaphore;

public class Teller implements Runnable {
    private final int id;
    Semaphore customerReady = new Semaphore(0);
    Semaphore transactionDone = new Semaphore(0);
    Customer currentCustomer;

    public Teller(int id) {
        this.id = id;
    }


    @Override
    public void run() {
        System.out.printf("Teller #%d [Teller %d]: Ready to serve\n", id, id);
        while(Main.customersServed.get() < Main.NUM_CUSTOMERS) {
            try {

            } catch(InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
