package org.bigbear.impressweibo.ui.browser;

import org.bigbear.impressweibo.bean.GeoBean;
import org.bigbear.impressweibo.dao.location.BaiduGeoCoderDao;
import org.bigbear.impressweibo.dao.location.GoogleGeoCoderDao;
import org.bigbear.impressweibo.dao.map.MapDao;
import org.bigbear.impressweibo.support.error.WeiboException;
import org.bigbear.impressweibo.support.lib.MyAsyncTask;
import org.bigbear.impressweibo.support.utils.GlobalContext;
import org.bigbear.impressweibo.support.utils.Utility;

import android.app.Activity;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * User: qii
 * Date: 13-1-25
 */
public class GetWeiboLocationInfoTask extends MyAsyncTask<Void, String, Bitmap> {

    private Activity activity;
    private TextView location;
    private ImageView mapView;

    private GeoBean geoBean;

    public GetWeiboLocationInfoTask(Activity activity, GeoBean geoBean, ImageView mapView,
            TextView location) {
        this.geoBean = geoBean;
        this.activity = activity;
        this.mapView = mapView;
        this.location = location;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        location.setVisibility(View.VISIBLE);
        location.setText(String.valueOf(geoBean.getLat() + "," + geoBean.getLon()));
    }

    @Override
    protected Bitmap doInBackground(Void... params) {
        if (Utility.isGPSLocationCorrect(geoBean)) {
            String gpsLocationString = new GoogleGeoCoderDao(activity, geoBean).get();

            try {
                if (TextUtils.isEmpty(gpsLocationString)) {
                    publishProgress(new BaiduGeoCoderDao(geoBean.getLat(), geoBean.getLon()).get());
                }
            } catch (WeiboException e) {
                e.printStackTrace();
            }
        }

        MapDao dao = new MapDao(GlobalContext.getInstance().getSpecialToken(), geoBean.getLat(),
                geoBean.getLon());
        try {
            return dao.getMap();
        } catch (WeiboException e) {
            return null;
        }
    }

    @Override
    protected void onProgressUpdate(String... values) {
        super.onProgressUpdate(values);
        if (!TextUtils.isEmpty(values[0])) {
            location.setVisibility(View.VISIBLE);
            location.setText(values[0]);
        }
    }

    @Override
    protected void onPostExecute(Bitmap s) {
        mapView.setImageBitmap(s);
        super.onPostExecute(s);
    }
}
