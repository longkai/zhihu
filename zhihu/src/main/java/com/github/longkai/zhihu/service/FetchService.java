package com.github.longkai.zhihu.service;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.github.longkai.zhihu.R;
import com.github.longkai.zhihu.ZhihuApp;
import com.github.longkai.zhihu.ui.MainActivity;
import com.github.longkai.zhihu.util.Constants;
import com.github.longkai.zhihu.util.Utils;
import org.json.JSONArray;
import org.json.JSONException;

import java.util.Map;

/**
 * Created by longkai on 13-12-21.
 */
public class FetchService extends IntentService {

	public static final String TAG = "FetchService";

	public FetchService() {
		super(TAG);
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		ZhihuApp.getRequestQueue().add(new JsonArrayRequest(Utils.refreshUrl(), new Response.Listener<JSONArray>() {
			@Override
			public void onResponse(JSONArray response) {
				// 解析数据
				Map<String, ContentValues[]> map = Utils.process(response);
				ContentValues[] items = map.get(Constants.ITEMS);
				ContentValues[] topics = map.get(Constants.TOPICS);

				// 本地存储
				getContentResolver().bulkInsert(Utils.parseUri(Constants.ITEMS), items);
				getContentResolver().bulkInsert(Utils.parseUri(Constants.TOPICS), topics);

				// 通知栏提醒用户数据已更新
				notification(getString(R.string.done_fetch, response.length()));
			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				Log.wtf(TAG, "error " + error.toString());
				notification(getResources().getString(R.string.load_data_error, error.toString()));
			}
		}
		));
	}

	// 在通知栏弹出一个通知
	private void notification(String text) {
		NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
		builder.setSmallIcon(R.drawable.ic_launcher)
				.setContentTitle(getString(R.string.app_name))
				.setContentText(text)
				.setAutoCancel(true)
				.setTicker(text);

		Intent mainActivity = new Intent(this, MainActivity.class);
		TaskStackBuilder taskStackBuilder = TaskStackBuilder.create(this);
		taskStackBuilder.addParentStack(MainActivity.class);
		taskStackBuilder.addNextIntent(mainActivity);

		PendingIntent resultPendingIntent = taskStackBuilder
				.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
		builder.setContentIntent(resultPendingIntent);

		NotificationManager manager =
				(NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

		manager.notify(0, builder.build());
	}
}
