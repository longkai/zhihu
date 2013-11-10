package com.github.longkai.zhihu.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import com.github.longkai.zhihu.R;

public class MainActivity extends ActionBarActivity implements ActionBar.TabListener {

	public static final String TAG = "MainActivity";

    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.github.longkai.android.R.layout.pager);

        final ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        mViewPager = (ViewPager) findViewById(R.id.pager);
	    String[] titles = getResources().getStringArray(R.array.pager_titles);
	    mViewPager.setAdapter(new ZhihuPagerAdapter(getSupportFragmentManager(), titles));


        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                actionBar.setSelectedNavigationItem(position);
            }
        });

	    for (int i = 0; i < titles.length; i++) {
            actionBar.addTab(
                    actionBar.newTab()
                            .setText(titles[i])
                            .setTabListener(this));
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
	    getMenuInflater().inflate(R.menu.main, menu);
	    return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
		    case R.id.action_quit:
			    // fake, go back to home = =
			    Intent i = new Intent(Intent.ACTION_MAIN);
			    i.addCategory(Intent.CATEGORY_HOME);
			    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			    startActivity(i);
			    break;
		    default:
			    Log.e(TAG, "no this option!");
			    break;
	    }
	    return super.onOptionsItemSelected(item);
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

	/**
	 * 主界面pager适配器
	 */
	private static class ZhihuPagerAdapter extends FragmentPagerAdapter {

		private Fragment[] fragments;
		private String[] titles;

		public ZhihuPagerAdapter(FragmentManager fm, String[] titles) {
			super(fm);
			this.titles = titles;
			fragments = new Fragment[titles.length];
			for (int i = 0; i < fragments.length; i++) {
				fragments[i] = new HotAnswerFragment();
			}
		}

		@Override
		public Fragment getItem(int position) {
			return fragments[position];
		}

		@Override
		public int getCount() {
			return titles.length;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			return titles[position];
		}
	}

}
