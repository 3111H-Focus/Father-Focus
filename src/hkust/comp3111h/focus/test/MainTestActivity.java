package hkust.comp3111h.focus.test;


import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class MainTestActivity extends ListActivity {

	String classes[] = { "Activity.MainActivity", "test.TestUserTable", "test.TaskDbAdapterTest",
			"TBA", "TBA", "TBA", "TBA" };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setListAdapter(new ArrayAdapter<String>(MainTestActivity.this,
				android.R.layout.simple_list_item_1, classes) {
		});
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		// TODO Auto-generated method stub
		String choice = classes[position];
		super.onListItemClick(l, v, position, id);

		try {
			Class cl = Class.forName("hkust.comp3111h.focus." + choice);
			Intent i = new Intent(MainTestActivity.this, cl);
			startActivity(i);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} finally {

		}
	}
}
