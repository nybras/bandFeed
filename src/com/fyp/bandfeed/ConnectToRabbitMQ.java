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
	private AppendToLog logIt;


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
		logIt = new AppendToLog();

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
				logIt.append(exchange + " EXCHANGE CREATED");
			}
			return true;
		} catch (IOException e) {
			logIt.append(exchange + " EXCHANGE FAILED");
			return false;
		}
	}

	public boolean createQueue() {
		try {
			if (connectToRabbitMQ()) {
				channel.queueDeclare(queue, true, false, false, null);
				logIt.append(queue + " QUEUE CREATED");
			}
			return true;
		} catch (IOException e) {
			logIt.append(queue + " QUEUE FAILED");
			return false;
		}
	}

	public boolean createBind(String rKey) {
		try {
			if (connectToRabbitMQ()) {
				channel.queueBind(queue, exchange, rKey);
				logIt.append("BIND " + rKey + " CREATED BETWEEN " + queue + " AND " + exchange);
			}
			return true;
		} catch (IOException e) {
			logIt.append("FAILED TO CREATE BIND " + rKey + " FOR " + queue + " AND " + exchange);
			return false;
		}
	}
	
	public boolean deleteBind(String rKey) {
		try {
			if (connectToRabbitMQ()) {
				channel.queueUnbind(queue, exchange, rKey);
				logIt.append("BIND " + rKey + " DELETED BETWEEN " + queue + " AND " + exchange);
			}
			return true;
		} catch (IOException e) {
			logIt.append("FAILED TO DELETE BIND " + rKey + " FOR " + queue + " AND " + exchange);
			return false;
		}
	}

	public boolean sendMessage(byte[] message, String strMes) {
		
		try {
			if (connectToRabbitMQ()) {
				channel.basicPublish(exchange, "", null, message);
				logIt.append(exchange + " PUBLISHED MESSAGE: " + strMes);
			}
			return true;
		} catch (IOException e) {
			logIt.append(exchange + " FAILED TO PUBLISH MESSAGE: " + strMes);
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
					
				}
				logIt.append(queue + " RETRIEVED MESSAGES");
				
			}
		} catch (IOException e) {
			logIt.append(queue + " FAILED TO RETREIVED MESSAGES");
			return null;
		} catch (ShutdownSignalException e) {
			logIt.append(queue + " FAILED TO RETRIEVE MESSAGES");
			return null;
		} catch (ConsumerCancelledException e) {
			logIt.append(queue + " FAILED TO RETRIEVE MESSAGES");
			return null;
		} catch (InterruptedException e) {
			logIt.append(queue + " FAILED TO RETRIEVE MESSAGES");
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
		
		//TODO user account with restricted permissions needs making
		try {
			ConnectionFactory connectionFactory = new ConnectionFactory();
			connectionFactory.setUsername("admin");
			connectionFactory.setPassword("prrpm5uBbf");
			connectionFactory.setHost(server);
			
			connection = connectionFactory.newConnection();
			channel = connection.createChannel();

			
			
		} catch (IOException e) {
			return false;
		} catch (ShutdownSignalException e) {
			return false;
		} catch (ConsumerCancelledException e) {
			return false;
		}
		
		return true;
		
	}
}