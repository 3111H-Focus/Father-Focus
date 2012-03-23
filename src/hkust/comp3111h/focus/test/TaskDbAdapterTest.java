package hkust.comp3111h.focus.test;

import hkust.comp3111h.focus.R;
import android.app.ListActivity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;


public class TaskDbAdapterTest extends ListActivity implements OnClickListener{
	
	EditText etStatus;
	Button bAddData;
	Button bTC1;
	Button bTC2;
	Button bTC3;
	Button bTC4;
	Button bTC5;
	Button bTC6;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.testalltables);
		etStatus = (EditText) findViewById(R.id.etStatus);
		bAddData = (Button) findViewById(R.id.bAddData);
		bTC1 = (Button) findViewById(R.id.bTestCase1);
		bTC2 = (Button) findViewById(R.id.bTestCase2);
		bTC3 = (Button) findViewById(R.id.bTestCase3);
		bTC4 = (Button) findViewById(R.id.bTestCase4);
		bTC5 = (Button) findViewById(R.id.bTestCase5);
		bTC6 = (Button) findViewById(R.id.bTestCase6);
		bTC1.setOnClickListener(this);
		bTC2.setOnClickListener(this);
		bTC3.setOnClickListener(this);
		bTC4.setOnClickListener(this);
		bTC5.setOnClickListener(this);
		bTC6.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){
		case R.id.bAddData:
			addData();
			break;
		case R.id.bTestCase1:
			TestCase1();
			break;
		case R.id.bTestCase2:
			TestCase2();
			break;
		case R.id.bTestCase3:
			TestCase3();
			break;
		case R.id.bTestCase4:
			TestCase4();
			break;
		case R.id.bTestCase5:
			TestCase5();
			break;
		case R.id.bTestCase6:
			TestCase6();
			break;
		}
	}
	
	public void addData(){
		etStatus.setText("Tisfdlaksfjkl;sadfjkl;sadjkfl;jsadl;fjkdlsajfkl;dsjf;lsdajfkldsajf;sdklajfls;dafsdkl;afl;sdjaf" +
				"dsaf;kjsda;fljdslafjsaldfs;adfjskld;ajflsaf" +
				"fas;dlfjas;ldfjklsdaf;sdaf" +
				"dsaflkjsad;lfk");
		
	}
	
	public void TestCase1(){
		
	}
	
	public void TestCase2(){
		
	}
	
	public void TestCase3(){
		
	}
	
	public void TestCase4(){
		
	}
	
	public void TestCase5(){
		
	}
	
	public void TestCase6(){
		
	}
}
