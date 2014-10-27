package org.ziaagikian.demo.linq_android;

import org.linq4android.collections.LinqQuery;
import org.linq4android.collections.Predicate;
import org.linq4android.collections.Queries;
import org.linq4android.collections.Selector;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends ActionBarActivity {

	protected final String TAG = getClass().getSimpleName();
	private int[] mTestArray;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		int size = 100;
		mTestArray = new int[size];

		for (int i = 0; i < mTestArray.length; i++) {
			mTestArray[i] = i;
		}

		try {
			testSelectQuery();
			testWhereQuery();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void testWhereQuery() throws Exception {

		LinqQuery<Integer> primeArray = Queries.query(mTestArray).where(
				new Predicate<Integer>() {

					@Override
					public boolean evaluate(Integer val) {
						return val % 2 != 0;
					}
				});
		for (Integer val : primeArray) {
			lo(val);
		}
	}

	private void lo(Integer val) {
		Log.d(TAG, val + "");
	}

	private void testSelectQuery() throws Exception {

		LinqQuery<Integer> selectArray = Queries.query(mTestArray).select(
				new Selector<Integer, Integer>() {

					@Override
					public Integer select(Integer val) {
						return val % 2;
					}

				});
		for (Integer val : selectArray) {
			lo(val);
		}
	}

	private void testCountQuery() {
		int count = Queries.query(mTestArray).count();
		lo(count);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
