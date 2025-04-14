4-12-2025 @ 13:00
This project utilizes semaphore and synchronization logic to simulate a Customer-Bank Teller relation across multiple
threads. I anticipate that implementing the semaphores wouldn't be too hard, but getting them to properly sync up and
output might cause some issues.

4-12-2025 @ 23:20
Work on the customer and teller classes have been going relatively smooth, testing is still not possible in its current
state since the classes are unfinished. I still foresee that synchronization will be a hassle come testing phase, but
we'll see.

4-13-2025 @ 16:00
I had to reformat the project to be contained in a single file, as per the project specifications. No bottlenecks as of
yet while I implement the run functions for each thread, but I do anticipate them coming up as soon as the first run is
done.

4-13-2025 @ 21:00
The main backbone of the project is complete, now it is just testing and bugfixing. I had to reformat some of the code
since it didn't follow the expected output, but most inputs should be covered within the classes. 

4-13-2025 @ 21:24
I am now running into synchronization and deadlock issues, customers are waiting for a teller and the teller is being
hung up on mystery tasks. Needs further investigation.