/**
 * @author Brett Flitter
 * @version Prototype1 - 20/02/2013
 * @title Project bandFeed
 */

package com.fyp.bandfeed;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.SAXException;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConsumerCancelledException;
import com.rabbitmq.client.QueueingConsumer;
import com.rabbitmq.client.ShutdownSignalException;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.app.ActionBar;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

public abstract class Feeds extends SwipeActivity {

	private Document doc, newFeeds;
	private AppendToLog logIt;
	private ListView list;
	protected SharedPreferences prefs;
	private LazyAdapter adapter;
	private ArrayList<Feed> listOfFeeds;
	ArrayList<String> messages;
	// Need handler for callbacks to the UI thread
	final Handler mHandler = new Handler();
	private String username;
	protected String attType;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_all_feed);

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			// gets the activity's default ActionBar
			ActionBar actionBar = getActionBar();
			actionBar.show();
		}

		listOfFeeds = new ArrayList<Feed>();
		messages = new ArrayList<String>();
		logIt = new AppendToLog();
		list = (ListView) findViewById(R.id.listView1);
		adapter = new LazyAdapter(this, listOfFeeds, null);
		list.setAdapter(adapter);
		prefs = getSharedPreferences("userPrefs", 0);
		username = prefs.getString("userName", null);

		// Set listener so we can deal with link request
		list.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				if (listOfFeeds.get(position).getType().equals("news")
						|| listOfFeeds.get(position).getType()
								.equals("releases")
						|| listOfFeeds.get(position).getType()
								.equals("updates")
						|| listOfFeeds.get(position).getType().equals("gigs")) {
					// Do nothing! Only interest if its a link req (anything
					// other than above).
				} else {
					//Deal with link req
					Intent i = new Intent(getApplicationContext(),
							DealWithLinkRequest.class);
					i.putExtra("user", listOfFeeds.get(position).getType());
					i.putExtra("member", listOfFeeds.get(position).getName());
					i.putExtra("band", listOfFeeds.get(position).getMessage());
					startActivity(i);
				}

			}
		});

		parseDoc();
		readXMLFeeds(attType);
	}

	@Override
	public void onResume() {
		super.onResume();
		//Refresh feed to get updated feeds each time activity is resumed
		refresh();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu2, menu);
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		// respond to menu item selection
		switch (item.getItemId()) {
		case R.id.about:
			startActivity(new Intent(this, About.class));
			return true;
		case R.id.send_feedback:
			Intent i = new Intent(this, SendFeedback.class);
			i.putExtra("page", "AllFeed");
			startActivity(i);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	public void parseDoc() {
		// modified code from
		// http://www.cafeconleche.org/books/xmljava/chapters/ch09s09.html
		// http://www.sussex.ac.uk/Users/im74/G5074/examples/SerializeHack.java
		try {

			FileInputStream fIn = openFileInput(username + ".xml");

			DocumentBuilderFactory factory = DocumentBuilderFactory
					.newInstance();
			factory.setIgnoringElementContentWhitespace(true);
			DocumentBuilder builder = factory.newDocumentBuilder();
			doc = builder.parse(fIn);
		} catch (FactoryConfigurationError fce) {
			informUser("Could not create DocumentBuilderFactory. Try re-installing bandFeed!");
		} catch (ParserConfigurationException pce) {
			informUser("Could not locate a JAXP parser. Try re-installing bandFeed!");
		} catch (SAXException se) {
			//informUser("XML file is not well-formed. Try re-installing bandFeed!");
		} catch (IOException ioe) {
			informUser("IOException. Try re-installing bandFeed!");
		}
	}

	private void initialiseDoc() {
		// If the xml file is blank (no messages have received yet)
		// then there is no root yet
		// create the root element and add it to the document
		try {
			DocumentBuilderFactory dbfac = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = dbfac.newDocumentBuilder();
			doc = docBuilder.newDocument();
			Element root = doc.createElement("feeds");
			doc.appendChild(root);
			writeDoc();

		} catch (ParserConfigurationException e1) {
			informUser("ParserConfigException. Try re-installing bandFeed!");
		}

	}

	public void readXMLFeeds(String att) {

		NodeList entries = null;
		try {
			entries = doc.getElementsByTagName("feed");
		} catch (NullPointerException e) {
			initialiseDoc();
			entries = doc.getElementsByTagName("feed");
		}

		listOfFeeds.clear();

		for (int i = 0; i < entries.getLength(); i++) {

			Node feed = entries.item(i);
			Element e = (Element) feed;
			String attribute = e.getAttribute("type");
			if (attribute.equals(att) || att == null) {

				Feed f = new Feed();
				f.setType(attribute);
				NodeList feedContents = feed.getChildNodes();

				// Name
				Node name = feedContents.item(0);
				Text nameText = (Text) name.getChildNodes().item(0);
				f.setName(nameText.getNodeValue());

				// Date
				Node date = feedContents.item(1);
				Text dateText = (Text) date.getChildNodes().item(0);
				f.setDate(dateText.getNodeValue());

				// Message
				Node message = feedContents.item(2);
				Text messageText = (Text) message.getChildNodes().item(0);
				f.setMessage(messageText.getNodeValue());

				listOfFeeds.add(f);
			}

		}

	}

	private void addNewFeeds() {

		StringBuilder sb = new StringBuilder();
		sb.append("<feeds>");

		for (int i = messages.size() - 1; i > -1; i--) {
			sb.append(messages.get(i));
		}
		sb.append("</feeds>");

		try {
			InputStream is = new ByteArrayInputStream(sb.toString().getBytes());
			DocumentBuilderFactory factory = DocumentBuilderFactory
					.newInstance();
			factory.setIgnoringElementContentWhitespace(true);
			DocumentBuilder builder = factory.newDocumentBuilder();
			newFeeds = builder.parse(is);

		} catch (SAXException e) {
			informUser("SAXException. Try re-installing bandFeed!");
		} catch (IOException e) {
			informUser("IOException. Try re-installing bandFeed!");
		} catch (ParserConfigurationException e) {
			informUser("ParserConfigException. Try re-installing bandFeed!");
		}

		NodeList newEntries = newFeeds.getElementsByTagName("feed");

		@SuppressWarnings("unused")
		NodeList entries = null;
		Element firstRoot = null;
		try {
			entries = doc.getElementsByTagName("feed");
		} catch (NullPointerException e) {
			initialiseDoc();
			entries = doc.getElementsByTagName("feed");
		}

		for (int i = 0; i < newEntries.getLength(); i++) {

			Node first = doc.getElementsByTagName("feed").item(i);

			firstRoot = doc.getDocumentElement();
			Node node = doc.importNode(newEntries.item(i), true);

			if (first == null) {
				firstRoot.appendChild(node);

			} else {
				firstRoot.insertBefore(node, first);
			}
		}

		int num = firstRoot.getChildNodes().getLength();
		while (num > 200) {
			firstRoot.removeChild(firstRoot.getLastChild());
			num--;
		}
		// Write new feeds to XML file

		writeDoc();
		parseDoc();
		messages.clear();
		readXMLFeeds(attType);

	}

	// Writes the new feeds to the existing XML file
	public void writeDoc() {

		// modified code from
		// http://www.cafeconleche.org/books/xmljava/chapters/ch09s09.html
		// http://www.sussex.ac.uk/Users/im74/G5074/examples/SerializeHack.java
		try {
			FileOutputStream fOut = openFileOutput(username + ".xml", 0);

			OutputStreamWriter osw = new OutputStreamWriter(fOut);

			// Write the document using an identity transform
			TransformerFactory xformFactory = TransformerFactory.newInstance();
			Transformer idTransform = xformFactory.newTransformer();
			Source input = new DOMSource(doc);
			StreamResult result = new StreamResult(new File(username + ".xml"));
			result.setWriter(osw);
			// Result output = new StreamResult(out);
			idTransform.transform(input, result);
		} catch (TransformerFactoryConfigurationError e) {
			informUser("Could not locate a factory class. Try re-installing bandFeed!");
		} catch (TransformerConfigurationException e) {
			informUser("This DOM does not support transforms. Try re-installing bandFeed!");
		} catch (TransformerException e) {
			informUser("Transform failed. Try re-installing bandFeed!");
		} catch (FileNotFoundException e) {
			informUser("XML file not found. Try re-installing bandFeed!");
		}

	}

	private void refresh() {
		new Thread(new Runnable() {
			public void run() {

				ConnectToRabbitMQ connection = new ConnectToRabbitMQ(null,
						username);

				try {
					if (connection.connectToRabbitMQ()) {
						Channel channel = connection.getChannel();
						QueueingConsumer consumer = new QueueingConsumer(
								channel);

						int queueSize = channel.queueDeclarePassive(username)
								.getMessageCount();
						channel.basicConsume(username, true, consumer);

						QueueingConsumer.Delivery delivery;
						// boolean noMessageYet = true;
						for (int i = 0; i < queueSize; i++) {
							delivery = consumer.nextDelivery();
							String message = new String(delivery.getBody());
							messages.add(message);
						}
						connection.dispose();
					}
				} catch (IOException e) {
					logIt.append(username + " FAILED TO RETRIEVE MESSAGES - IOException");
				} catch (ShutdownSignalException e) {
					logIt.append(username + " FAILED TO RETRIEVE MESSAGES - ShutdownSignalException");
				} catch (ConsumerCancelledException e) {
					logIt.append(username + " FAILED TO RETRIEVE MESSAGES - ConsumerCancelledException");
				} catch (InterruptedException e) {
					logIt.append(username + " FAILED TO RETRIEVE MESSAGES - InterruptedException");
				}

				if (messages.size() > 0) {
					addNewFeeds();
					mHandler.post(new Runnable() {
						public void run() {

							adapter.notifyDataSetChanged();
						}
					});
				}
			}
		}).start();
	}
	
	private void informUser(String msg) {
		Toast toast = Toast.makeText(this, msg,
				Toast.LENGTH_LONG);
		toast.show();
	}

}
