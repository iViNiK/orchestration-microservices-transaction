# Abstract
This project is about an example of implementation of a Saga Pattern with a Microservices approach, based on Apache Kafka publish/subscription model.

Technologies used:

* Apache Maven 3.6.0 
* Java EE 1.8
* Spring Boot 2.4.4
* Spring Boot 2.1.10.RELEASE (including Web + Cloud Greenwich.SR3 + Contracts + JPA) 
* Junit 4/5
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

<img src="https://github.com/iViNiK/orchestration-microservices-transaction/blob/main/kafka.png" alt="Kafka Architecture" width="800"/>

Following are the main concepts of the _Consumer API_ and _Producer API_:
- **Event / Record**: It is the message that is written and read by the applications. It consists of a key, a value and a timestamp.
- **Topic**: It is the one who takes care of organizing, archiving and grouping a series of Events / Records.
- **Partition**: The subsections into which a topic is divided. Useful for scaling applications.
- **Offset**: Unique identifier of an Event / Record within a Partition - Topic.
- **Producer**: Whom sends the messages (Event / Record)
- **Consumer**: Whom receives the messages (Event / Record)

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

<img src="https://github.com/iViNiK/orchestration-microservices-transaction/blob/main/kafka1.png " alt="Kafka Producer and Consumer Groups" width="800"/>

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

<img src="https://github.com/iViNiK/orchestration-microservices-transaction/blob/main/saga.png " alt="Saga Workflow" width="800"/>

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
According to some test data provided in the project, it is possible to activate the saga in three scenarios. The post endpoint to call is always the same: 
```
http://localhost:8891/v1/orders/
```

The following paylaod generates an order of an item in stock:
```
{
	"itemId" : 1,
	"customerId" :1
}
```

The following paylaod generates an order of an item out of stock:
```
{
	"itemId" : 2,
	"customerId" :1
}
```

The following paylaod generates an order of a non existing item:
```
{
	"itemId" : -1,
	"customerId" :1
}
```

# Orchestration flow
Following is a graphic representation of how the saga is implemented on Apache Kafka basis.
As you can see, there are two **Consumer Groups**: the first for the Orchestrator Service and the latter for the Notification Service.
In a minimal configuration, in which we can have one single **Partition** for each **Topic**, a single instance of those services in the proper **Consumer Group** is enough.
Should we scale the system for better performance, we could - for example - split the **Topics** in two **Partitions** and run a new instance of the orchestrator service.

<img src="https://github.com/iViNiK/orchestration-microservices-transaction/blob/main/orchestratorpattern.png " alt="Orchestration Flow" width="1000"/>

# The _Transactional outbox_ pattern
All the _Producers_ in the mentioned Saga participant services are invoked applying the _Transactional outbox_ pattern (for further information, please refer to https://microservices.io/patterns/data/transactional-outbox.html).
<br><br>
The tipical action in a _Service_ class is composed of the following two operations:

1. Persist something on the DB
2. Publish the message on the specific topic

So, there is a concrete possibility that one of the two operations will fail, preventing the system from functioning correctly. This makes the system highly unreliable. One solution could be to use a distributed transaction, however this would have drawbacks and would introduce significant overhead. 
<br><br>
Adopting the _Transactional outbox_ pattern, in this project it was achieved the result of increasing the system reliability, while maintaining processing efficiency. The implementation is highly scalable, which allows its introduction into various microservices simply by the creation of a **Bean** of type **EventPublisher**, that will be built using the Microservice's local _DomainObjectRepository_ (to access the persistence layer) and a custom **EventSource** implementation (to access the message broker) instances. Besides, the **OutboxProxy** class provides the method to start the _Outbox Transaction_, by putting a record in the _Outbox_ table, in the current transaction (i.e. like in the previous list item 1). The following Class Diagram explains the design of this solution:

<img src="https://github.com/iViNiK/orchestration-microservices-transaction/blob/main/transactional-outbox.jpg" alt="Transaction Outbox Pattern" width="800"/>


# The _Consumer Driven Contracts_ pattern

As you can easily assume, an architectural solution such as _Saga Pattern_ largely relies on the availability of the services that interact, because the correct functionality of the system depends on them. 
<br><br>
A software solution based on microservices is, by definition, prone to rapid evolutions and for this reason its code can undergo several changes even in short periods of time. Furthermore, it is now very likely that the development of an application takes place in work groups often isolated from each other, which makes interpersonal communication critical regarding the implementation of software changes on certain services, which could impact other services, and compromise system functionality.
<br><br>
For example, **what about a developer changing the payload structure of a controller used in the _Saga Pattern_ to accomplish a step in the flow**? If such a change is not considered e.g. from the _orchestration_ point of view, it could make the system fail and generate unpredictable side effects. A lack of comunication, in this case, can lead to severe damage in a production environment. So, how we can protect the system from such eventualities? One interesting solution is provided from **Consumer Driven Contracts** pattern. 
<br><br>
Consumer-Driven Contracts is a pattern for evolving services. In Consumer-Driven Contracts, each consumer captures their expectations of the provider in a separate contract. All of these contracts are shared with the provider so they gain insight into the obligations they must fulfill for each individual client. For further information, please refer to the following article https://martinfowler.com/articles/consumerDrivenContracts.html.
<br><br>
In this project you can find a full implementation of this tecnique, using _Spring Cloud Contract_ (https://spring.io/projects/spring-cloud-contract) to develope integration tests that assure integrity of endpoints involved in the _Saga Pattern_ implementation.

<img src="https://github.com/iViNiK/orchestration-microservices-transaction/blob/main/consumerdrivencontractstests.png" alt="Consumer Driven Contracts Tests" width="800"/>

# Rest Clients and Resilience
The orchestrator service must interact with microservices involved in each step of the Saga.
This will be achieved by calling, with a Rest Client, specific endpoints exposed by the microservices.
To mantain consistency of the whole transaction even in case of failure of a Rest call in the middle of a step of the Saga,
we used **Feign Clients** with appropriate set of **Hystrix** properties. Furthermore, where necessary, **Fallback** methods are provided
to compensate actions and restore the original conditions.

To monitor the feign client operations, you can use the **Hystrix Dashboard**, that was configured within the **Orchestration Service**. To show the monitoring console, go to the URL: 
<br><br>
```http://<your_host>:8761/v1/orchestrator/hystrix``` 

and, in the "stream" inputbox, insert the following: 
<br><br>
```http://localhost:8761/v1/orchestrator/actuator/hystrix.stream```.

The following picture is an example:

<img src="https://github.com/iViNiK/orchestration-microservices-transaction/blob/main/hystrixdashboard.png" alt="Hystrix Dashboard" width="1000"/>

## Some points of attention ... 
The _Orchestrator Service_ is invoking the collaborating service's endpoints synchronously, even if it is "solicited" by asynchronous events. This is the reason why fallback strategy must be provided to manage any service providers failures. It is clear that a probable malfunction could be the excessive delay in the response of an API, which could trigger the fallback path while, in reality, the desired operation will be completed sooner or later. The results may be a big disaster!
<br><br>
So, the rapid response and the absence of potential delays must always be ensured by the service providers.

# Message Delivery Reliability
In reality, there may be many other moments in which a certain operation, exposed by an API involved in the _Saga Pattern_, could be called several times on the same object, due to message delivery behaviour. If there is no control of the potential side effects, delivering/receiving the same messages can bring to serious problems. 
<br><br>
When it comes to describing the semantics of a delivery mechanism, there are three basic categories:

1. <b>at-most-once</b> delivery means that for each message handed to the mechanism, that message is delivered once or not at all; in more casual terms it means that messages may be lost.
2. <b>at-least-once</b> delivery means that for each message handed to the mechanism potentially multiple attempts are made at delivering it, such that at least one succeeds; again, in more casual terms this means that messages may be duplicated but not lost.
3. <b>exactly-once</b> delivery means that for each message handed to the mechanism exactly one delivery is made to the recipient; the message can neither be lost nor duplicated.

The first one is the cheapest (highest performance, least implementation overhead) because it can be done in a "fire and forget" fashion without keeping state at the sending end or in the transport mechanism. The second one requires retries to counter transport losses, which means keeping state at the sending end and having an acknowledgement mechanism at the receiving end. The third is most expensive (and has consequently worst performance) because in addition to the second it requires state to be kept at the receiving end in order to filter out duplicate deliveries.

## Focus on Kafka perspective
For this project, it is very important to understand these concepts within Kafka context.

<b>At-most-once Configuration</b>

At-most-once message delivery means that the message will be delivered at most one time. Once delivered, there is no chance of delivering again. If the consumer is unable to handle the message due to some exception, the message is lost. This is because Kafka is automatically committing the last offset used. To achieve this behaviour:

- Set enable.auto.commit to true (so there is no need to call consumer.commitSync() from the consumer)
- Set auto.commit.interval.ms to low value

Note that it is also possible to have at-lest-once scenario with the same configuration. Let’s say consumer successfully processed the message into its store and in the meantime kafka was failing or restarted before it could commit the offset. In this scenario, consumer would again get the same message.
Hence, even if using at-most-once or at-least-once configuration, consumer should be always prepared to handle the duplicates.

<b>At-least-once Configuration</b>

At-least-once message delivery means that the message will be delivered at least one time. There is high chance that message will be delivered again as duplicate. To achieve this behaviour:

- Set enable.auto.commit to false 
- Consumer should take control of the message offset commits to Kafka by making the consumer.commitSync() call.

Let’s say consumer has processed the messages and committed the messages to its local store, but consumer crashes and did not get a chance to commit offset to Kafka before. When consumer restarts, Kafka would deliver messages from the last offset, resulting in duplicates.

<b>Exactly-once Configuration</b>

Exactly-once message delivery means that there will be only one and once message delivery. It difficult to achieve in practice.
In this case offset needs to be manually managed. To achieve this behaviour:

- Set enable.auto.commit to false
- Do not make call to consumer.commitSync()
- Implement a ConsumerRebalanceListener and within the listener perform consumer.seek(topicPartition,offset); to start reading from a specific offset of that topic/partition.
- While processing the messages, get hold of the offset of each message. Store the processed message’s offset in an atomic way along with the processed message using atomic-transaction. When data is stored in relational database atomicity is easier to implement. For non-relational data-store such as HDFS store or No-SQL store one way to achieve atomicity is as follows: Store the offset along with the message.

# A point of enhancement: the Idempotent Consumer Pattern
In an enterprise application, it’s usually a good practice to use a message broker that guarantees at-least-once delivery. As said, at-least-once delivery guarantees that a message broker will deliver a message to a consumer even if errors occur, but one side effect is that the consumer can be invoked repeatedly for the same message. Consequently, a consumer must be idempotent: the outcome of processing the same message repeatedly must be the same as processing the message once. If a consumer is not idempotent, multiple invocations can cause bugs (for example, a consumer of an AccountDebited message that subtracts the debit amount from the current balance would calculate the incorrect balance).

But, how does a message consumer should handle duplicate messages correctly?

The solution is to implement an idempotent consumer, which is a message consumer that can handle duplicate messages correctly. Some consumers are naturally idempotent, others must track the messages that they have processed in order to detect and discard duplicates. One way to make a consumer idempotent is recording in the database the IDs of the messages that it has processed successfully. When processing a message, a consumer can detect and discard duplicates by querying the database. There are a couple of different places to store the message IDs. One option is for the consumer to use a separate PROCESSED_MESSAGES table. The other option is for the consumer to store the IDs in the business entities that it creates or updates.

Anyway, in general, the best solution is always the one that best fits your needs. For example, if a mail send service receives more times the same message, the risk is that the recipients will receive the same mail again. If that is not a huge problem, the cost to write a logic that protect from this possibility may not be worth. 

# License
Copyright © 2021 by Vinicio Flamini <io@vinicioflamini.it>
