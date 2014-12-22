package com.fmakdemir.insight.webservice.request;

import com.fmakdemir.insight.utils.DataHolder;
import com.fmakdemir.insight.utils.Helper;
import com.fmakdemir.insight.webservice.model.BaseResponse;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author fma
 * @date 19.12.2014.
 */
public class MediaWebApiHandler extends BaseWebApiHandler {
	private static final String URL_BASE = DataHolder.getServerUrl();
	private static final String URL_REGISTER_INSIGHT = URL_BASE+"/insight/sound";


	public static void downloadAudio(String insightId,
									   WebApiCallback<BaseResponse> callback) {
		HttpPost req = new HttpPost(URL_REGISTER_INSIGHT);
		String username = Helper.getUsername();

		List<NameValuePair> nameValuePairs = new ArrayList<>();
		nameValuePairs.add(new BasicNameValuePair("insight_id", insightId));
		nameValuePairs.add(new BasicNameValuePair("username", username));

		try {
			req.setEntity(new UrlEncodedFormEntity(nameValuePairs, "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		connect(req, callback, BaseResponse.class);
	}

}
