RxWrapper4RequestIdLogging
===================

This repository contains a wrapper on top of Rx.Observable to perform request ID logging. RandomFetchV3 is the fine tuned version of the implementation.

----------


Running
-------------
 
**Run the main in RandomFetchV3.java**. This starts a vertx server that listens on 2020.
Whenever a request is made to localhost:2020, the server in turn makes three requests to www.random.org, results of which are used to demonstrate the behavior of  the wrapper implementation on top of the Rx Observable.

**Once the server is up, run the junit in RandomFetchV3Test**. The number of simultaneous requests made can be increased by increasing the value of count in the test.
