import java.util.Random;
import java.util.concurrent.Semaphore;

public class Customer implements Runnable {
    int id;
    boolean isWithdrawal;
    Semaphore transactionTypeAvailable = new Semaphore(0);
    Semaphore Left = new Semaphore(0);

    public Customer(int id) {
        this.id = id;
        this.isWithdrawal = new Random().nextBoolean();
    }

    @Override
    public void run() {

    }
}
