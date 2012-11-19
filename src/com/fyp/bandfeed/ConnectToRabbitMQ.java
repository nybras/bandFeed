package com.fyp.bandfeed;

import java.io.IOException;
import java.util.ArrayList;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.ConsumerCancelledException;
import com.rabbitmq.client.QueueingConsumer;
import com.rabbitmq.client.ShutdownSignalException;

/**
 * Base class for objects that connect to a RabbitMQ Broker
 */
public class ConnectToRabbitMQ {
	private static final String server = "81.169.135.67";
	private static final String exchangeType = "direct";
	private String exchange;
	private String queue;


	private Channel channel = null;
	private Connection connection;

	/**
	 * 
	 * @param exchange
	 *            The exchange name (named after band name)
	 * @param queue
	 *            The queue name (named after username)
	 */
	public ConnectToRabbitMQ(String exchange, String queue) {
		this.exchange = exchange;
		this.queue = queue;

	}

	public void dispose() {

		try {
			if (connection != null)
				connection.close();
			if (channel != null)
				channel.abort();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public Channel getChannel() {
		return channel;
	}

	public boolean createExchange() {
		try {
			if (connectToRabbitMQ()) {
				channel.exchangeDeclare(exchange, exchangeType, true);
			}
			return true;
		} catch (IOException e) {
			return false;
		}
	}

	public boolean createQueue() {
		try {
			if (connectToRabbitMQ()) {
				channel.queueDeclare(queue, true, false, false, null);
			}
			return true;
		} catch (IOException e) {
			return false;
		}
	}

	public boolean createBind() {
		try {
			if (connectToRabbitMQ()) {
				channel.queueBind(queue, exchange, "");
			}
			return true;
		} catch (IOException e) {
			return false;
		}
	}

	public boolean sendMessage(byte[] message) {
		try {
			if (connectToRabbitMQ()) {
				channel.basicPublish(exchange, "", null, message);
			}
			return true;
		} catch (IOException e) {
			return false;
		}
	}


	public ArrayList<String> consumeMessages() {
		ArrayList<String> messages = new ArrayList<String>();
		try {
			if (connectToRabbitMQ()) {
				QueueingConsumer consumer = new QueueingConsumer(channel);
				
				int queueSize = channel.queueDeclarePassive(queue).getMessageCount();
				channel.basicConsume(queue, true, consumer);
				
				QueueingConsumer.Delivery delivery;
				//boolean noMessageYet = true;
				for (int i = 0; i < queueSize; i++) {
					delivery = consumer.nextDelivery();
					String message = new String(delivery.getBody());
					messages.add(message);
					
					//if (message != null) {
					//	noMessageYet = false;
					//}
				}
				
			}
		} catch (IOException e) {
			return null;
		} catch (ShutdownSignalException e) {
			return null;
		} catch (ConsumerCancelledException e) {
			return null;
		} catch (InterruptedException e) {
			return null;
		}
		
		
		return messages;
	}

	/**
	 * Connect to the broker and create the exchange
	 * 
	 * @return success
	 */
	public boolean connectToRabbitMQ() {
		try {
			ConnectionFactory connectionFactory = new ConnectionFactory();
			connectionFactory.setUsername("admin");
			connectionFactory.setPassword("prrpm5uBbf");
			connectionFactory.setHost(server);
			connectionFactory.setRequestedHeartbeat(10); // Might not need this but probably good for 3G
			connection = connectionFactory.newConnection();
			channel = connection.createChannel();

			return true;
		} catch (IOException e) {
			return false;
		} catch (ShutdownSignalException e) {
			return false;
		} catch (ConsumerCancelledException e) {
			return false;
		}
		
	}
}