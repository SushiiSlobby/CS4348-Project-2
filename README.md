# CS4348-Project-2
A bank simulation demonstrating the relationship between a customer and a teller, simulated through multiple threads
across a set-size consumer base.

## How to run
To run the program, you need to utilize the `javac` command with `Bank_Simulation` set as
your argument. 
```shell
javac Bank_Simulation
```
You will then have to run the compiled .class file with the `java` command.
```shell
java Bank_Simulation
```

The simulator will then run in the terminal and self-terminate after all customers are served.

## How it works
The bank simulation uses java threads to simulate individual entities, such as individual tellers and customers. Their
actions are then synced with each other using java semaphores, tying tellers to customers and syncing their actions with
each other. This simulates an end-to-end transaction on an individual scale, which is then expanded out across 3 tellers
and 50 customers. After each customer is served, a stop code is sent to the tellers and the tellers close their lanes,
which in turn closes the bank.




