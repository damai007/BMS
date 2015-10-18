package com.lumachrome.bms.bms3.chat;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HttpContext;

import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ListView;

import com.lumachrome.bms.bms3.R;

public class ChatActivity extends ActionBarActivity {

	HttpPost httppost;
	StringBuffer buffer;
	HttpResponse response;
	HttpClient httpclient;
	List<NameValuePair> nameValuePairs;
	Utils utils;

	static String TAG = "GCM DEMO";
	String user_name;
	String regid;
	String chattingToName, chattingToDeviceID;

	String SENDER_ID = "360042659644";
	String API_KEY = "AIzaSyDursMCGtjgxCw9p2GJ9rCggT_O9MFOXTg";

	EditText edtMessage;
	ListView chatLV;
	DBOperation dbOperation;

	ChatListAdapter chatAdapater;
	ArrayList<ChatPeople> ChatPeoples;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		utils = new Utils(this);

		chatLV = (ListView) findViewById(R.id.listView1);
		edtMessage = (EditText) findViewById(R.id.editText_message);

		ChatPeoples = new ArrayList<ChatPeople>();

		regid = utils.getRegistrationId();

		Bundle b = getIntent().getExtras();

		if (b != null) {
			user_name = b.getString("chattingFrom");
			Log.i(TAG, "user_name : " + user_name);
			chattingToName = b.getString("chattingToName");
			chattingToDeviceID = b.getString("chattingToDeviceID");

			Log.i(TAG, "Chat From : " + user_name + " >> Chatting To : "
					+ chattingToName + ">> Reg Id : " + chattingToDeviceID);
		}

		registerReceiver(broadcastReceiver, new IntentFilter(
				"CHAT_MESSAGE_RECEIVED"));

		dbOperation = new DBOperation(this);
		dbOperation.createAndInitializeTables();

		populateChatMessages();

		chatLV.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
		chatLV.setStackFromBottom(true);

		getSupportActionBar().setTitle("Chatting to : " + chattingToName);
	}

	private void populateChatMessages() {

		getData();
		if (ChatPeoples.size() > 0) {
			chatAdapater = new ChatListAdapter(this, ChatPeoples);
			chatLV.setAdapter(chatAdapater);
		}

	}

	void clearMessageTextBox() {

		edtMessage.clearFocus();
		edtMessage.setText("");

		hideKeyBoard(edtMessage);

	}

	private void hideKeyBoard(EditText edt) {
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(edt.getWindowToken(), 0);
	}

	void addToDB(ChatPeople curChatObj) {

		ChatPeople people = new ChatPeople();
		ContentValues values = new ContentValues();
		values.put(people.getPERSON_NAME(), curChatObj.getPERSON_NAME());
		values.put(people.getPERSON_CHAT_MESSAGE(),
				curChatObj.getPERSON_CHAT_MESSAGE());
		values.put(people.getPERSON_DEVICE_ID(),
				curChatObj.getPERSON_DEVICE_ID());
		values.put(people.getPERSON_CHAT_TO_FROM(),
				curChatObj.getPERSON_CHAT_TO_FROM());
		values.put(people.getPERSON_EMAIL(), "demo_email@email.com");
		dbOperation.open();
		long id = dbOperation.insertTableData(people.getTableName(), values);
		dbOperation.close();
		if (id != -1) {
			Log.i(TAG, "Succesfully Inserted");
		}

		populateChatMessages();
	}

	void getData() {

		ChatPeoples.clear();

		Cursor cursor = dbOperation.getDataFromTable(chattingToDeviceID);
		if (cursor.getCount() > 0) {
			cursor.moveToFirst();
			do {
				// Log.i(TAG,
				// "Name = " + cursor.getString(0) + ", Message = "
				// + cursor.getString(1) + " Device ID = "
				// + cursor.getString(2));

				ChatPeople people = addToChat(cursor.getString(0),
						cursor.getString(1), cursor.getString(3));
				ChatPeoples.add(people);
			} while (cursor.moveToNext());
		}
		cursor.close();

	}

	BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {

			Bundle b = intent.getExtras();

			String message = b.getString("message");

			Log.i(TAG, " Received in Activity " + message + ", NAME = "
					+ chattingToName + ", dev ID = " + chattingToDeviceID);

			// this demo this is the same device
			ChatPeople curChatObj = addToChat(chattingToName, message,
					"Received");
			addToDB(curChatObj); // adding to db

			populateChatMessages();

		}
	};

	public void sendMessage() {

		final String messageToSend = edtMessage.getText().toString().trim();

		if (messageToSend.length() > 0) {

			Log.i(TAG, "sendMessage");

			Thread thread = new Thread() {
				@Override
				public void run() {
					try {
						httpclient = new DefaultHttpClient();
						httppost = new HttpPost(utils.getCurrentIPAddress()
								+ "GCM/gcm_engine.php");
						nameValuePairs = new ArrayList<NameValuePair>(1);
						nameValuePairs.add(new BasicNameValuePair("message",
								messageToSend));
						nameValuePairs.add(new BasicNameValuePair(
								"registrationIDs", chattingToDeviceID));
						nameValuePairs.add(new BasicNameValuePair("apiKey",
								API_KEY));
						nameValuePairs.add(new BasicNameValuePair("devId",
								utils.getRegistrationId()));
						nameValuePairs.add(new BasicNameValuePair("sender",
								utils.getFromPreferences(utils.UserName)));
						//Log.i(TAG, "Registration Id : " + utils.getRegistrationId());
						//Log.i(TAG, "Sender : " + utils.getFromPreferences(utils.UserName));

						httppost.setEntity(new UrlEncodedFormEntity(
								nameValuePairs));
						ResponseHandler<String> responseHandler = new BasicResponseHandler();
						final String response = httpclient.execute(httppost,
								responseHandler);
						Log.i(TAG, "Response : " + response);
						if (response.trim().isEmpty()) {
							Log.d(TAG, "Message Not Sent");
						}

					} catch (Exception e) {
						Log.d(TAG, "Exception : " + e.getMessage());
					}
				}
			};

			thread.start();

		}

	}

	public void onDestroy() {
		super.onDestroy();
		unregisterReceiver(broadcastReceiver);
	}

	ChatPeople addToChat(String personName, String chatMessage, String toOrFrom) {

		Log.i(TAG, "inserting : " + personName + ", " + chatMessage + ", "
				+ toOrFrom + " , " + chattingToDeviceID);
		ChatPeople curChatObj = new ChatPeople();
		curChatObj.setPERSON_NAME(personName);
		curChatObj.setPERSON_CHAT_MESSAGE(chatMessage);
		curChatObj.setPERSON_CHAT_TO_FROM(toOrFrom);
		curChatObj.setPERSON_DEVICE_ID(chattingToDeviceID);
		curChatObj.setPERSON_EMAIL("demo@gmail.com");

		return curChatObj;

	}

	public void onClick(final View view) {

		if (view == findViewById(R.id.send)) {

			ChatPeople curChatObj = addToChat(chattingToName, edtMessage
					.getText().toString().trim(), "Sent");
			addToDB(curChatObj); // adding to db

			sendMessage();

			clearMessageTextBox();

		}

	}
}
