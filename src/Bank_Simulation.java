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
    public static final AtomicInteger customersInBank = new AtomicInteger(0);

    private static int tellerIndex = 0;

    public static void main(String[] args) throws InterruptedException {
        List<Thread> customerThreads = new ArrayList<>();
        List<Thread> tellerThreads = new ArrayList<>();

        for(int i = 0; i < NUM_TELLERS; i++) {
            Teller teller = new Teller(i);
            tellers.add(teller);
            Thread t = new Thread(teller);
            t.start();
        }

        Thread.sleep(100);

        bankOpen.release(NUM_CUSTOMERS);

        for(int i = 0; i < NUM_CUSTOMERS; i++) {
           Customer customer = new Customer(i);
           Thread t = new Thread(customer);
           customerThreads.add(t);
           t.start();
        }

        for(Thread t : customerThreads) {
            t.join();
        }

        // Send one poison pill per teller
        for (Teller teller : tellers) {
            teller.myQueue.put(new PoisonPillCustomer());
        }

        Thread.sleep(100);

        System.out.println("The bank closes for the day");

    }

    static class PoisonPillCustomer extends Customer {
        public PoisonPillCustomer() {
            super(-1);
        }

    }

    static class Teller implements Runnable {
        int id;
        Semaphore customerReady = new Semaphore(0);
        Semaphore transactionDone = new Semaphore(0);
        Customer currentCustomer;
        BlockingQueue<Customer> myQueue = new LinkedBlockingQueue<>();
        //boolean open = true;

        public Teller(int id) {
            this.id = id;
        }

        @Override
        public void run() {
            System.out.printf("Teller %d []: Ready to serve\n",id);

            while(true) {
                try {
                    System.out.printf("Teller %d []: Waiting for a customer\n", id);
                    currentCustomer = myQueue.take();

                    if (currentCustomer.id == -1) {
                        System.out.printf("Teller %d []: No more customers, closing\n", id);
                        break;
                    }

                    System.out.printf("Teller %d [Customer %d]: Customer selected\n", id, currentCustomer.id);
                    customerReady.release();
                    currentCustomer.transactionTypeAvailable.acquire();

                    System.out.printf("Teller %d [Customer %d]: serving a customer\n", id, currentCustomer.id);
                    System.out.printf("Teller %d [Customer %d]: asks for transaction\n", id, currentCustomer.id);

                    if(currentCustomer.isWithdrawal) {
                        System.out.printf("Teller %d [Customer %d]: Handling withdrawal transaction\n", id, currentCustomer.id);
                        System.out.printf("Teller %d [Customer %d]: Going to manager\n", id, currentCustomer.id);
                        managerAccess.acquire();
                        System.out.printf("Teller %d [Customer %d]: Talking to manager\n", id, currentCustomer.id);
                        Thread.sleep(random(5,30));
                        managerAccess.release();
                    }
                    else {
                        System.out.printf("Teller %d [Customer %d]: Handling deposit transaction\n", id, currentCustomer.id);
                    }

                    System.out.printf("Teller %d [Customer %d]: Going to safe\n", id, currentCustomer.id);
                    safeAccess.acquire();
                    System.out.printf("Teller %d [Customer %d]: In safe\n", id, currentCustomer.id);
                    Thread.sleep(random(10,50));
                    System.out.printf("Teller %d [Customer %d]: Leaving safe\n", id, currentCustomer.id);
                    safeAccess.release();


                    System.out.printf("Teller %d [Customer %d]: Transaction complete\n", id, currentCustomer.id);
                    transactionDone.release();
                    currentCustomer.left.acquire();
                    System.out.printf("Teller %d [Customer %d]: Customer has left\n", id, currentCustomer.id);

                    customersServed.incrementAndGet();

                    if (customersServed.get() >= NUM_CUSTOMERS) {
                        System.out.printf("Teller %d []: No more customers, closing\n", id);
                        break;
                    }

                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }
    }

    static class Customer implements Runnable {
        int id;
        boolean isWithdrawal;
        Semaphore transactionTypeAvailable = new Semaphore(0);
        Semaphore left = new Semaphore(0);
        boolean transactionDone = false;

        Customer(int id) {
            this.id = id;
            this.isWithdrawal = new Random().nextBoolean();
        }

        @Override
        public void run() {
            try{
                Thread.sleep(random(0,100));
                System.out.printf("Customer %d []: Wants to perform %s transaction\n", id, isWithdrawal ? "withdrawal" : "deposit");

                bankOpen.acquire();
                customersInBank.incrementAndGet();

                System.out.printf("Customer %d []: Going to bank\n", id);
                doorAccess.acquire();
                System.out.printf("Customer %d []: Entering bank\n", id);
                System.out.printf("Customer %d []: Getting in line\n", id);
                System.out.printf("Customer %d []: Selecting a teller\n", id);

                Teller teller = getNextTeller();
                System.out.printf("Customer %d [Teller %d]: Selected teller\n", id, teller.id);
                System.out.printf("Customer %d [Teller %d]: Introduces self\n", id, teller.id);
                teller.myQueue.put(this);

                customerQueue.put(this);
                teller.customerReady.acquire();

                System.out.printf("Customer %d [Teller %d]: asks for %s transaction\n", id, teller.id, isWithdrawal ? "withdrawal" : "deposit");

                transactionTypeAvailable.release();
                teller.transactionDone.acquire();
                transactionDone = true;

                System.out.printf("Customer %d [Teller %d]: Leaving after transaction\n", id, teller.id);
                left.release();
                doorAccess.release();
                customersInBank.decrementAndGet();

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

        }
    }

    private static int random(int min, int max) {
        return ThreadLocalRandom.current().nextInt(min, max + 1);
    }

    private static synchronized Teller getNextTeller() {
        Teller teller = tellers.get(tellerIndex);
        tellerIndex = (tellerIndex + 1) % tellers.size();
        return teller;
    }
}