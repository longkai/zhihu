package com.github.longkai.zhihu.ui;

import android.app.AlertDialog;
import android.content.AsyncQueryHandler;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.*;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.Html;
import android.util.Log;
import android.view.*;
import android.widget.*;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.StringRequest;
import com.github.longkai.android.app.ActionBarDrawerHelper;
import com.github.longkai.zhihu.R;
import com.github.longkai.zhihu.ZhihuApp;
import com.github.longkai.zhihu.service.FetchService;
import com.github.longkai.zhihu.util.Utils;

import static com.github.longkai.zhihu.util.Constants.*;

public class MainActivity extends ActionBarActivity implements
		ActionBar.TabListener, DrawerLayout.DrawerListener,
		AdapterView.OnItemClickListener, LoaderManager.LoaderCallbacks<Cursor> {

	public static final String TAG = "MainActivity";

    private ViewPager mViewPager;

	// 抽屉相关
	private CursorAdapter mAdapter;
	private DrawerLayout mDrawerLayout;
	private ListView mDrawer;
	private ActionBarDrawerToggle mDrawerToggle;
	private ActionBarDrawerHelper mActionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

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

	    // tab titles
	    for (int i = 0; i < titles.length; i++) {
            actionBar.addTab(
                    actionBar.newTab()
                            .setText(titles[i])
                            .setTabListener(this));
        }

	    // drawer
	    mDrawer = (ListView) findViewById(R.id.left_drawer);
	    mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
	    mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
			    R.drawable.ic_drawer, R.string.open_drawer, R.string.close_drawer);
	    mDrawerLayout.setDrawerListener(this);

	    mActionBar = new ActionBarCompatDrwaerHelper(getSupportActionBar());
	    mActionBar.init();


	    mAdapter = new TopicsAdapter(this);
	    mDrawer.setAdapter(mAdapter);
	   	mDrawer.setOnItemClickListener(this);
	    getSupportLoaderManager().initLoader(0, null, this);
    }

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		mDrawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		mDrawerToggle.onConfigurationChanged(newConfig);
	}

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
	    getMenuInflater().inflate(R.menu.main, menu);
	    return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
	    if (item != null && item.getItemId() == android.R.id.home && mDrawerToggle.isDrawerIndicatorEnabled()) {
		    if (mDrawerLayout.isDrawerVisible(GravityCompat.START)) {
			    mDrawerLayout.closeDrawer(GravityCompat.START);
		    } else {
			    mDrawerLayout.openDrawer(GravityCompat.START);
		    }
		    return true;
	    }
	    switch (item.getItemId()) {
		    case R.id.action_quit: // 退出，实际上是返回的桌面
			    // fake, go back to home = =
			    Intent i = new Intent(Intent.ACTION_MAIN);
			    i.addCategory(Intent.CATEGORY_HOME);
			    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			    startActivity(i);
			    break;
		    case R.id.refresh: // 抓取新的数据并缓存到本地
				Toast.makeText(this, R.string.loading_data, Toast.LENGTH_LONG).show();
				Intent intent = new Intent(this, FetchService.class);
				startService(intent);
				break;
		    case R.id.delete:
//			    new AsyncQueryHandler(getContentResolver()) {
//				    @Override
//				    protected void onDeleteComplete(int token, Object cookie, int result) {
//					    Toast.makeText(MainActivity.this, getString(R.string.delte_cache_success), Toast.LENGTH_SHORT).show();
//				    }
//			    }.startDelete(0, null, Constants.parseUri(Constants.DELETE), null, null);
//              todo 直接删除再重建居然有问题。。。
			    Toast.makeText(this, getString(R.string.not_yet_impl), Toast.LENGTH_SHORT).show();
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

	@Override
	public void onDrawerSlide(View drawerView, float slideOffset) {
		mDrawerToggle.onDrawerSlide(drawerView, slideOffset);
	}

	@Override
	public void onDrawerOpened(View drawerView) {
		mDrawerToggle.onDrawerOpened(drawerView);
		mActionBar.onDrawerOpened();
	}

	@Override
	public void onDrawerClosed(View drawerView) {
		mDrawerToggle.onDrawerClosed(drawerView);
		mActionBar.onDrawerClosed();
	}

	@Override
	public void onDrawerStateChanged(int newState) {
		mDrawerToggle.onDrawerStateChanged(newState);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String selection = "topics like " + Utils.like(id + "");

        // 这里写的复杂了= =
        new AsyncQueryHandler(getContentResolver()) {
            @Override
            protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
                // 标题和相关的id对应上
                String[] titles = new String[cursor.getCount()];
                final long[] ids = new long[titles.length];
                int i = 0;
                while (cursor.moveToNext()) {
                    titles[i] = cursor.getString(cursor.getColumnIndex(TITLE));
                    ids[i] = cursor.getLong(cursor.getColumnIndex(ANSWER_ID));
                    i++;
                }
                // 弹出的对话框中包含我们查询到的结果
                new AlertDialog.Builder(MainActivity.this)
                        .setItems(titles, new DialogInterface.OnClickListener() {
                            // 处理在对话框的列表项目的点击事件
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(MainActivity.this, AnswerActivity.class);
                                intent.putExtra(ANSWER_ID, ids[which]);
                                startActivity(intent);
                            }
                        })
                        .setIcon(R.drawable.ic_launcher)
                        .setTitle(R.string.search_found)
                        .setNeutralButton(android.R.string.ok, null)
                        .show();
            }
        }.startQuery(0, null, Utils.parseUri(ITEMS),
                ITEMS_PROJECTION, selection, null, DESC_ORDER);

//		todo bug here fc =.=
//		mDrawerLayout.closeDrawer(mDrawer);
    }

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		return new CursorLoader(this, Utils.parseUri(TOPICS), null, null, null, DESC_ORDER);
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
		mAdapter.changeCursor(data);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		mAdapter.changeCursor(null);
	}

	/**
	 * 左边抽屉action bar工具类
	 */
	private static class ActionBarCompatDrwaerHelper implements ActionBarDrawerHelper {

		private ActionBar mActionBar;

		private ActionBarCompatDrwaerHelper(ActionBar mActionBar) {
			this.mActionBar = mActionBar;
		}

		@Override
		public void init() {
			mActionBar.setDisplayHomeAsUpEnabled(true);
			mActionBar.setHomeButtonEnabled(true);
		}

		@Override
		public void onDrawerClosed() {
		}

		@Override
		public void onDrawerOpened() {
		}

		@Override
		public void setTitle(CharSequence title) {
		}
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
			fragments[0] = new HotItemsFragment();
			fragments[1] = new UsersFragment();
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

	/**
	 * 抽屉界面items适配器
	 */
	private static class TopicsAdapter extends CursorAdapter {

		public TopicsAdapter(Context context) {
			super(context, null, 0);
		}

		@Override
		public View newView(Context context, Cursor cursor, ViewGroup parent) {
			// use the same layout with user info...
			View view = LayoutInflater.from(context).inflate(R.layout.user_row, null);
			ViewHolder holder = new ViewHolder();
			holder.avatar = (ImageView) view.findViewById(R.id.avatar);
			holder.avatarIndex = cursor.getColumnIndex(TOPIC_AVATAR);
			holder.name = (TextView) view.findViewById(R.id.nick);
			holder.nameIndex = cursor.getColumnIndex(TOPIC_NAME);
			holder.desc = (TextView) view.findViewById(R.id.status);
			holder.descIndex = cursor.getColumnIndex(TOPIC_DESCRIPTION);

			view.setTag(holder);
			return view;
		}

		@Override
		public void bindView(View view, Context context, Cursor cursor) {
			ViewHolder holder = (ViewHolder) view.getTag();
			ZhihuApp.getImageLoader().get(cursor.getString(holder.avatarIndex),
					ImageLoader.getImageListener(holder.avatar,
							R.drawable.ic_launcher, R.drawable.ic_launcher));
			holder.name.setText(cursor.getString(holder.nameIndex));
			String str = cursor.getString(holder.descIndex);
			str = str.length() > 50 ? str.substring(0, 50) + "..." : str;
			holder.desc.setText(Html.fromHtml(str.equals("null") ? "" : str));
		}

		private static class ViewHolder {

			ImageView avatar;
			int avatarIndex;
			TextView name;
			int nameIndex;
			TextView desc;
			int descIndex;
		}
	}

}
