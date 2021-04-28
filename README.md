# Abstract
This project is about an example of implementation of a Saga Pattern with a Microservices approach.

Technologies used:

* Apache Maven 3.6.0 
* Java EE 1.8
* Spring Boot 2.4.4
* Spring Web + Cloud
* Apache Kafka 2.12
* H2 database

# A brief introduction to Apache Kafka
Apache Kafka is an Event-Streaming Processing platform, designed to process large amounts of data in real time, enabling the creation of scalable systems with high throughput and low latency. 
By design, it also provides persistence of data on the cluster (main server and replicas) ensuring high reliability.
<br><br>
To better understand the solution given in the project, we will see how to create a Kafka cluster for the development environment, the creation of Topics, the logic of partitions and consumer groups, and obviously the publish-subscribe model.
<br><br>
The requirements are as follows:
- Java 8
- The latest version of Apache Kafka

**Note**: From now on, it is assumed that the environment variable KAFKA_HOME is set to the local installation of Kafka server. All commands examples are intended for a Windows machine (KAFKA_HOME/bin/windows/), if you are on Linux use the path (KAFKA_HOME/bin).

## Fundamental Concepts
Apache Kafka is a distributed system consisting of servers (clusters of one or more servers) and clients that communicate via the TCP protocol. 
It can be deployed on bare-metal hardware, virtual machines and containers (on-premise and cloud).
Kafka, among the various features, also provides APIs for the implementation of the Publish-Subscribe Messaging model:

![Kafka Architecture](file:///kafka.png "Kafka Architecture")

Following are the main concepts of the _Consumer API_ and _Producer API_:
- **Event / Record**: It is the message that is written and read by the applications. It consists of a key, a value and a timestamp.
- **Topic**: He is the one who takes care of organizing, archiving and grouping a series of Events / Records.
- **Partition**: The subsections into which a topic is divided. Useful for scaling applications.
- **Offset**: Unique identifier of an Event / Record within a Partition - Topic.
- **Producer**: Who sends the messages (Event / Record)
- **Consumer**: Who receives the messages (Event / Record)

## Configuring a Kafka cluster and basic commands
Once the Kafka release has been downloaded, simply unpack the archive and run the following commands:
```
cd %KAFKA_HOME%

START /B .\bin\windows\zookeeper-server-start.bat config\zookeeper.properties > zookeeper.log

.\bin\windows\kafka-server-start.bat config\server.properties
```
Once started our (single-server) Kafka cluster, we can create our first topic. Open a new terminal and type:
```
%KAFKA_HOME%\bin\windows\kafka-topics.bat --zookeeper localhost --create --topic MY-TOPIC-ONE --partitions 2 --replication-factor 1
```
In the command shown we specified:
- MY-TOPIC-ONE as the name of the **Topic**;
- 2 **Partitions**;
- 1 **Replication Factor** (must be less than or equal to the number of nodes in the Kafka cluster; since we are running a single-server Cluster it is necessary to indicate as a value 1 otherwise we will receive an error).

Now if all went well, with the following command we should see the list of **Topics** present on the Cluster:
```
%KAFKA_HOME%\bin\windows\kafka-topics.bat --zookeeper localhost --list
```
To get more information on the topic, for example see the number of partitions, we can run:
```
%KAFKA_HOME%\bin\windows\kafka-topics.bat --zookeeper localhost --topic MY-TOPIC-ONE --describe
```

## Publish-Subscribe
Let's explore various configurations and see the behaviour.

### Basic configuration

First, let's create two **Consumers** listening to our **Topic**. We will have to launch the command on two different terminals:
```
%KAFKA_HOME%\bin\windows\kafka-console-consumer.bat --bootstrap-server localhost:9092 --topic MY-TOPIC-ONE
```
To post a **Message** on the **Topic**, we need to create our **Producer**:
```
%KAFKA_HOME%\bin\windows\kafka-console-producer.bat --broker-list localhost:9092 --topic MY-TOPIC-ONE
```
It will show you a prompt where you can enter a text and, by pressing the Enter key, send the message.
<br>
As you can easily see in the **Consumer** consoles, both **Messages** were read by both **Consumers**.

### Consumer Groups vs Partitions
In a scenario where you are asked to scale the application that queues the messages, we could replicate the **Consumers**, but in the above configuration the latter would also receive all the messages. 
Depending on the application, this could be an information/data duplication problem.
Since we want to create a scenario in which a set of **Consumers** only receive a certain **Message** only once, we need to use the **Consumer Groups**.
If the **Consumers** are part of a **Consumer Group**, the partitions of the **Topic** (and therefore also the **Messages (Event / Record)**) will be distributed among the members of the group.

![Kafka Producer and Consumer Groups](file:///kafka1.png "Kafka Producer and Consumer Groups")

In particular, given:
- C = number of **Consumers** of the same **Consumer Group**
- P = number of **Partitions**

We will have the following possible scenarios:
- if C > P, each **Consumer** will have assigned its own partition, once the partitions are finished the remaining **Consumers** will remain without assigned partitions.
- if C = P, each **Consumer** will have one and only one partition;
- if C < P, the **Partitions** will be distributed evenly among the members of the Group.

On Apache Kafka it is also possible to work on the partitioning logics of the Events / Records, writing a record on a specific partition; in case this is not specified Apache Kafka proceed with a round-robin mechanism.

#### Publish-Subscribe with Consumer Groups and Partitions (Case 1)
We proceed to create two **Consumers** listening on our **Topic**, having the same **Consumer Group**. 
We will have to launch the following command on two different terminals:
```
%KAFKA_HOME%\bin\windows\kafka-console-consumer.bat --bootstrap-server localhost:9092 --topic MY-TOPIC-ONE --group CONSUMER-GROUP-ONE
```
As already seen, to publish a **Message** on the **Topic** we must create our **Producer**:
```
%KAFKA_HOME%\bin\windows\kafka-console-producer.bat --broker-list localhost:9092 --topic MY-TOPIC-ONE
```
and type some text (followed by Enter key).
<br><br>
As you can easily see in the **Consumer** consoles, now by creating a **Consumer Group** each **Consumer** only receives the message sent by the **Producer** once.

#### Publish-Subscribe with Consumer Groups and Partitions (Case 2)
In this case, we will to create three **Consumers** listening on our **Topic**, having the same **Consumer Group**. 
We will have to launch the following command on three different terminals:
```
%KAFKA_HOME%\bin\windows\kafka-console-consumer.bat --bootstrap-server localhost:9092 --topic MY-TOPIC-ONE --group CONSUMER-GROUP-ONE
```
Again, to publish a **Message** on the **Topic** we must create our **Producer**:
```
%KAFKA_HOME%\bin\windows\kafka-console-producer.bat --broker-list localhost:9092 --topic MY-TOPIC-ONE
```
and type some text (followed by Enter key).
<br><br>
As now you can see in the **Consumer** consoles, only two **Consumers** are de-queuing the messages, as the first two **Consumers** have taken the two available **Partitions** and the third **Consumer**, being not linked to any **Partition**, does not receive messages.

# The Saga use case
In this project the scenario assumed is the following:
1. A customer created an order to buy an item.
2. The system notifies the customer of order received.
3. The system checks the item availability, and in case updates the inventory. If the item is out of stock: 
	- the order must be cancelled;
	- the customer must be notified.
4. The customer pays for the item. If the payment is rejected:
	- the item must be re-added to the inventory;
	- the order must be canceled;
	- the customer must be notified.
5. The system issues the shipment of the item to the customer. Also, notifies the customer that order was processed.

![Saga Workflow](file:///saga.png "Saga Workflow")

## Services participating in Saga
Therefore, in a microservices architecture, the participants in the Saga are:
- Order service
- Inventory service
- Payment service
- Shipping service

## Customer notification
Apart of the Saga-managed transaction, we will provide asyncronous customer notification with the:
- Notification service

## Orchestrator 
The implemented Saga Pattern is based on _Orchestration_ concept. So we will have a specific microservice that will do that role:

- Orchestration service

# Build and Run the project
Since it is a Maven project, to build it enter the following command:
```
mvn clean install
```
To run the entire solution, you will have to launch each service separately.

## Start transaction in saga by order post endpoint
POST Endpoint: 
```
http://localhost:8891/v1/orders/
```
Paylaod:
```
{
	"itemId" : 2,
	"customerId" :1
}
```

# Orchestration flow
Following is a graphic representation of how the saga is implemented on Apache Kafka basis.
As you can see, there are two **Consumer Groups**: the first for the Orchestrator Service and the latter for the Notification Service.
In a minimal configuration, in which we can have one single **Partition** for each **Topic**, a single instance of those services in the proper **Consumer Group** is enough.
Should we scale the system for better performance, we could - for example - split the **Topics** in two **Partitions** and run a new instance of the orchestrator service.

![Flow](file:///orchestratorpattern.png "Flow")

# The _Transactional outbox_ pattern
All the _Producers_ in the mentioned Saga participant services are invoked applying the _Transactional outbox_ pattern (for further information, please refer to https://microservices.io/patterns/data/transactional-outbox.html).
<br><br>
The tipical action in a _Service_ class is composed of the following two operations:

1. Persist something on the DB
2. Publish the message on the specific topic

So, there is a concrete possibility that one of the two operations will fail, preventing the system from functioning correctly. This makes the system highly unreliable. One solution could be to use a distributed transaction, however this would have drawbacks and would introduce significant overhead. 
<br><br>
Adopting the _Transactional outbox_ pattern, in this project it was achieved the result of increasing the system reliability, while maintaining processing efficiency. The implementation is highly scalable, which allows its introduction into various microservices simply by the creation of a **Bean** of type **EventPublisher**, that will be built using the Microservice's local _DomainObjectRepository_ (to access the persistence layer) and a custom **EventSource** implementation (to access the message broker) instances. Besides, the **OutboxProxy** class provides the method to start the _Outbox Transaction_, by putting a record in the _Outbox_ table, in the current transaction (i.e. like in the previous list item 1). The following Class Diagram explains the design of this solution:

![Flow](file:///transactional-outbox.jpg "Flow")

# Rest Clients and Resilience
The orchestrator service must interact with microservices involved in each step of the Saga.
This will be achieved by calling, with a Rest Client, specific endpoints exposed by the microservices.
To mantain consistency of the whole transaction even in case of failure of a Rest call in the middle of a step of the Saga,
we used **Feign Clients** with appropriate set of **Hystrix** properties. Furthermore, where necessary, **Fallback** methods are provided
to compensate actions and restore the original conditions.

## Extendes Scenarios
As you maybe have noticed, all the steps of the example Saga are "compensable". Nevertheless, with such a solution, it is easy to manage also 
a scenario with "retryable" steps.

## License
Copyright © 2021 by Vinicio Flamini <io@vinicioflamini.it>
