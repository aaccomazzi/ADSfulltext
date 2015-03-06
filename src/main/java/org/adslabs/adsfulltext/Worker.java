package org.adslabs.adsfulltext;

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.QueueingConsumer;

import org.adslabs.adsfulltext.ConfigLoader;

public class Worker {

    public Connection connection;
    public Channel channel;
    int prefetchCount;
    ConfigLoader config;

    // Class constructor
    public Worker() {
        prefetchCount = 1;
        config = new ConfigLoader();
        config.loadConfig();
    }

    public boolean disconnect() {
        try {

            this.channel.close();
            this.connection.close();
            return true;

        } catch (java.io.IOException error) {

            System.out.println("There is probably no connection with RabbitMQ currently made: " + error.getMessage());
            return false;

        }
    }

    public boolean connect () {

        ConnectionFactory rabbitMQInstance;

        // This creates a connection object, that allows connections to be open to the same
        // RabbitMQ instance running
        //
        // There seems to be a lot of catches, but each one should list the reason for it's raise
        //
        // It is not necessary to disconnect the connection, it should be dropped when the program exits
        // See the API guide for more info: https://www.rabbitmq.com/api-guide.html

        try {

            rabbitMQInstance = new ConnectionFactory();
            System.out.println();
            rabbitMQInstance.setUri(this.config.data.RABBITMQ_URI);
            this.connection = rabbitMQInstance.newConnection();
            this.channel = this.connection.createChannel();

            // This tells RabbitMQ not to give more than one message to a worker at a time.
            // Or, in other words, don't dispatch a new message to a worker until it has processed
            // and acknowledged the previous one.
            this.channel.basicQos(this.prefetchCount);

            return true;

        } catch (java.net.URISyntaxException error) {

            System.out.println("URI error: " + error.getMessage());
            return false;

        } catch (java.io.IOException error) {

            System.out.println("IO Error, is RabbitMQ running???: " + error.getMessage());
            return false;

        } catch (java.security.NoSuchAlgorithmException error) {

            System.out.println("Most likely an SSL related error: " + error.getMessage());
            return false;

        } catch (java.security.KeyManagementException error) {

            System.out.println("Most likely an SSL related error: " + error.getMessage());
            return false;

        }
    }

//    public QueuingConsumer callBack () {
//
//        return new QueuingConsumer();
//    }

    public String subscribe() {

        // for array in subscribe array:
        //   start basic_consume
        //   if this is not a testing phase, then stay consuming

        String message = "Test";

        String QueueName = "PDFFileExtractorQueue";
        boolean autoAck = false; // This means it WILL acknowledge

        QueueingConsumer consumer = new QueueingConsumer(channel);

        try {
            this.channel.basicConsume(QueueName, autoAck, consumer);

//            QueueingConsumer.Delivery delivery = consumer.nextDelivery();
//            message = new String(delivery.getBody());

            return message;

        } catch (java.io.IOException error) {
            System.out.println("IO Error, does the queue exist and is RabbitMQ running???: " + error.getMessage());
            return error.getMessage();

        } //catch (java.lang.InterruptedException error) {
//            System.out.println("Interruption I guess there is no .next delivery");
//            return error.getMessage();
//        }

    }

    public boolean declare_all() {

//        try {
//            this.channel.exchangeDeclare(this.config.data.exchange.exchange, this.config.data.exchange.exchange_type, this.config.data.exchange.passive, this.config.data.exchange.durable);
//            return false;
//        } catch (java.io.IOException error) {
//            System.out.println("IO Error, is RabbitMQ running, check the passive/active settings!: " + error.getMessage());
//            return false;
//        }
        return true;

    }
}