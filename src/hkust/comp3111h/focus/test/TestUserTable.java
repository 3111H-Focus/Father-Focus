package hkust.comp3111h.focus.test;

import hkust.comp3111h.focus.R;
import hkust.comp3111h.focus.R.id;
import hkust.comp3111h.focus.R.layout;
import hkust.comp3111h.focus.database.TaskDbAdapter;
import android.app.Activity;
import android.database.Cursor;
import android.database.SQLException;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

/*
 * **********************************************************************************
 * READ ME:
 * This is just a test activity for the table Account.
 * Also demos how to use cursor to access the record. 
 * 
 * input userid, username and password and press "add".
 * click "clear" to clear all 3 textedit info.
 * input the userid and retrieve all the info.
 * 
 * If you find any bugs, please inform me!!! Thanks.
 * **********************************************************************************
 */

public class TestUserTable extends Activity implements View.OnClickListener{

	//Declaration of UIs.
	private Button bAdd;
	private Button bRetrieve;
	private Button bClear;
	private EditText etUserId;
	private EditText etUserName;
	private EditText etPassword;
	
	//The taskDbAdapter we'll use. 
	private TaskDbAdapter mTaskDbAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.testuser);
		bAdd = (Button)findViewById(R.id.bAdd);
		bRetrieve = (Button)findViewById(R.id.bRetrieve);
		bClear = (Button)findViewById(R.id.bClear);
		etUserId = (EditText)findViewById(R.id.etUID);
		etUserName = (EditText)findViewById(R.id.etUsername);
		etPassword = (EditText)findViewById(R.id.etPassword);
		
		bAdd.setOnClickListener(this);
		bRetrieve.setOnClickListener(this);
		bClear.setOnClickListener(this);
		
		//Create my taskDbAdapter and open it.
		//You can only write after you open the database. 
		mTaskDbAdapter = new TaskDbAdapter(this);
		mTaskDbAdapter.open();
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()){
		case R.id.bAdd:
			addToDb();
			break;
		case R.id.bRetrieve:
			retrieveFromDb();
			break;
		case R.id.bClear:
			clearInfo();
			break;
		}
	}
	
	//add an account to database
	public void addToDb(){
		String userName = etUserName.getText().toString();
		String password = etPassword.getText().toString();
		
		//create user will return true or false. 
		//you can use this info to pop up windows to warn the user. 
		//maybe need more other information from database
		//tell me your needs and i'll try to implement it. 
		mTaskDbAdapter.createUser(userName, password);
	}
	
	//retrieve info from database.
	public void retrieveFromDb(){
		String userId = etUserId.getText().toString();
		Cursor mCursor;
		try{
			/* ***********************************************
			 * My comprehension on Cursor. 
			 * If you already know, ignore me. If not, go on reading and search a bit. 
			 * You will use a lot of cursors in the future coz i'll return lots of it. :)
			 * 
			 * Cursor is to Database as Pointer is to Object in C++ or as iterator is to Vector.
			 * Say, you query the database and I return several records.
			 * You can use mCursor.moveToFirst() to go to first record.
			 * Also moveToLast, moveTo, moveToPosition, moveToNext, etc.
			 * 
			 * Trick here is, the default value of mCursor is at -1 ("before the first item"). so something like:
			 * 		do{ mCursor.blabhlbalbha()
			 * 			}while(mCursor.moveToNext());
			 * will generate outOfIndexException. LOOK OUT!!!!
			 * 
			 * Iterate all the records can be done by:
			 * for(c.moveToFirst(); !c.isAfterLast(); c.moveToNext()){
			 * 	//do whatever you want. 
			 * }
			 * 
			 * In no circumstances will mCursor itself be null. 
			 * so no need to check if(mCursor != null) //I don't know why google tutor have this but.. anyway.
			 * 
			 * However mCursor may be empty (no valid records return)
			 * Two ways to check whether it's empty.
			 *	 1. if(mCursor.moveToFirst())
			 *	 2. if(mCursor.getCount() == 0)
			 * I personally prefer the second one as it's easier to understand. 
			 * 
			 * What startManagingCursor(mCursor) do?
			 * 	cursor, imo, is something that you use a few times and then you throw away.
			 * 	after the query function, you probably don't need it any more.
			 *  under normal circumstance, you gotta delete it yourself. 
			 *  but if you call this function, the current activity will take charge
			 *  and help you handle the life cycle of the cursor.
			 *  basically, call it every time you get a new cursor. 
			 * 
			 * Thank you for tolerating my long long talk. 
			 * 
			 */
			long userIdInLong = Integer.parseInt(userId);
			mCursor = mTaskDbAdapter.fetchUser(userIdInLong);
			startManagingCursor(mCursor);
			
			if(mCursor.getCount() == 0){
				// this means the cursor is empty, what we want to retrieve does not exist
				clearInfo();
				etUserId.setText("UserID not exist!");
				etPassword.setText("mCursor.getCount() = " + mCursor.getCount());
			}
			else{
				etUserId.setText(mCursor.getString(mCursor.getColumnIndexOrThrow(TaskDbAdapter.KEY_USER_USERID)));
				etUserName.setText(mCursor.getString(mCursor.getColumnIndexOrThrow(TaskDbAdapter.KEY_USER_USERNAME)));
				etPassword.setText(mCursor.getString(mCursor.getColumnIndexOrThrow(TaskDbAdapter.KEY_USER_PASSWORD)));
			}
		}
		catch(SQLException e){
			e.printStackTrace();
		}
		catch(NumberFormatException e){
			etUserId.setText("UserId not valid (only digit)");
		}
	}
	
	public void clearInfo(){
		etUserId.setText("");
		etUserName.setText("");
		etPassword.setText("");
	}

}
