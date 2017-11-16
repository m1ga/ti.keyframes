/**
 * This file was auto-generated by the Titanium Module SDK helper for Android
 * Appcelerator Titanium Mobile
 * Copyright (c) 2009-2010 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the Apache Public License
 * Please see the LICENSE included with this distribution for details.
 *
 */
package ti.animation;

import org.appcelerator.kroll.KrollDict;
import org.appcelerator.kroll.KrollProxy;
import org.appcelerator.kroll.common.Log;
import org.appcelerator.kroll.common.TiConfig;
import org.appcelerator.titanium.util.TiConvert;
import org.appcelerator.titanium.proxy.TiViewProxy;
import org.appcelerator.titanium.view.TiUIView;
import android.widget.ImageView.ScaleType;
import android.view.LayoutInflater;
import android.view.View;
import android.content.res.Resources;
import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.TextDelegate;
import com.airbnb.lottie.*;
import android.animation.Animator;
import android.animation.ValueAnimator;
import org.appcelerator.titanium.TiApplication;
import org.appcelerator.titanium.io.TiBaseFile;
import org.appcelerator.titanium.io.TiFileFactory;
import java.io.InputStream;
import java.lang.Exception;
import org.json.JSONObject;
import org.appcelerator.kroll.common.TiMessenger;
import java.util.HashMap;
import org.appcelerator.kroll.KrollFunction;
import java.lang.Float;

public class LottieView extends TiUIView implements OnCompositionLoadedListener {
	
	private static final String LCAT = "LottieViewProxy";
	private static final boolean DBG = TiConfig.LOGD;
	
	private Resources resources;
	private LottieAnimationView lottieView;
	private TiViewProxy proxy;
	private KrollFunction callbackUpdate = null;
	private KrollFunction callbackComplete = null;
	private KrollFunction callbackReady = null;
	private float initialDuration = 0;
	private ValueAnimator va = null;
	private TextDelegate delegate = new TextDelegate(lottieView);

	public LottieView(TiViewProxy proxy) {
		super(proxy);
		
		this.proxy = proxy;
		String packageName = proxy.getActivity().getPackageName();
		resources = proxy.getActivity().getResources();
		View viewWrapper;

		int resId_viewHolder = -1;
		int resId_lotti = -1;

		resId_viewHolder = resources.getIdentifier("layout_lottie", "layout", packageName);
		resId_lotti = resources.getIdentifier("animation_view", "id", packageName);

		LayoutInflater inflater = LayoutInflater.from(proxy.getActivity());
		viewWrapper = inflater.inflate(resId_viewHolder, null);

		lottieView = (LottieAnimationView)viewWrapper.findViewById(resId_lotti);
		setNativeView(viewWrapper);

		setScaleMode(TiConvert.toString(proxy.getProperty("scaleMode")));
		lottieView.addAnimatorUpdateListener(new AnimatorUpdateListener());
		lottieView.addAnimatorListener(new AnimatorListener());
		if (TiConvert.toBoolean(proxy.getProperty("disableHardwareAcceleration"))) {
			lottieView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
		} else {
			lottieView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
		}
		lottieView.enableMergePathsForKitKatAndAbove(TiConvert.toBoolean(proxy.getProperty("mergePath")));
	}

	@Override
	public void processProperties(KrollDict d) {
		super.processProperties(d);

		if (d.containsKey("scaleMode")) {
			setScaleMode(d.getString("scaleMode"));
		}
		if (d.containsKey("disableHardwareAcceleration")) {
			if (d.getBoolean("disableHardwareAcceleration")) {
				lottieView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
			} else {
				lottieView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
			}
		}
		if (d.containsKey("loop")) {
			lottieView.loop(d.getBoolean("loop"));
		}
		if (d.containsKey("update")) {
			callbackUpdate =(KrollFunction) d.get("update");
		}
		if (d.containsKey("ready")) {
			callbackReady =(KrollFunction) d.get("ready");
		}
		if (d.containsKey("complete")) {
			callbackComplete =(KrollFunction) d.get("complete");
		}
		if (d.containsKey("mergePath")) {
			lottieView.enableMergePathsForKitKatAndAbove(d.getBoolean("mergePath"));
		}
		if (d.containsKey("progress")) {
			setProgress(Float.parseFloat(d.getString("progress")));
		}
		if (d.containsKey("speed")) {
			proxy.setProperty("duration", (initialDuration / TiConvert.toFloat(d.get("speed"))));
		}

		if (d.containsKey("file") && d.getString("file") != "") {
			if (TiApplication.isUIThread()) {
				loadFile(d.getString("file"));
			} else {
				TiMessenger.sendBlockingMainMessage(proxy.getMainHandler().obtainMessage(LottieViewProxy.MSG_LOADFILE, d.getString("file")));
			}
		} else if (d.containsKey("json")) {
			loadJson(d.getString("json"));
		}
	}


	@Override
	public void propertyChanged(String key, Object oldValue, Object newValue, KrollProxy proxy) {
		KrollDict d = new KrollDict();
		d.put(key, newValue);
		processProperties(d);
	}


	private void setScaleMode(String smode) {
		// Set scale mode on view
		//
		if (smode.equals("center")) {
			lottieView.setScaleType( ScaleType.CENTER);
		} else if (smode.equals("centerCrop")) {
			lottieView.setScaleType( ScaleType.CENTER_CROP);
		} else if (smode.equals("centerInside")) {
			lottieView.setScaleType( ScaleType.CENTER_INSIDE);
		} else {
			lottieView.setScaleType( ScaleType.CENTER_INSIDE);
		}
	}

	private void parseJson(String json) {
		try {
			JSONObject jsonObject = new JSONObject(json);
			proxy.setProperty("width", jsonObject.optInt("w", 0));
			proxy.setProperty("height", jsonObject.optInt("h", 0));
		} catch (Exception e) {
			Log.e(LCAT, "Couldn't read width/height");
		}
	}

	private void loadJson(String jsonString) {
		try {
			parseJson(jsonString);
			JSONObject jsonObject = new JSONObject(jsonString);
			LottieComposition.Factory.fromJson(resources, jsonObject, this);
		} catch (Exception e) {
			Log.e(LCAT, "Could not parse JSON string");
		}
	}


	@Override
	public void onCompositionLoaded(LottieComposition composition) {
		lottieView.setComposition(composition);
		lottieView.setImageAssetsFolder("Resources/" + TiConvert.toString(proxy.getProperty("assetFolder")));
		lottieView.setTextDelegate(delegate);
		lottieView.addAnimatorUpdateListener(new AnimatorUpdateListener());
		lottieView.addAnimatorListener(new AnimatorListener());

		initialDuration = lottieView.getDuration();
		if (TiConvert.toFloat(proxy.getProperty("speed")) == 1.0f) {
			proxy.setProperty("duration", initialDuration);
		} else {
			proxy.setProperty("duration", (initialDuration / TiConvert.toFloat(proxy.getProperty("speed"))));
		}
		if (TiConvert.toBoolean(proxy.getProperty("loop"))) {
			lottieView.loop(true);
		}
		if (TiConvert.toBoolean(proxy.getProperty("autoStart"))) {
			startAnimation(TiConvert.toInt(proxy.getProperty("startFrame")), TiConvert.toInt(proxy.getProperty("endFrame")));
		}
		if (callbackReady != null) {
			HashMap<String,Object> event = new HashMap<String, Object>();
			callbackReady.call(proxy.getKrollObject(), event);
		}
	}

	public void loadFile(String f) {
		String url = proxy.resolveUrl(null, f);
		TiBaseFile file = TiFileFactory.createTitaniumFile(new String[] { url }, false);

		if (file.exists()) {
			try {
				InputStream stream = file.getInputStream();
				int size = stream.available();
				byte[] buffer = new byte[size];
				stream.read(buffer);
				String json = new String(buffer, "UTF-8");
				parseJson(json);
				LottieComposition.Factory.fromAssetFileName(TiApplication.getInstance(), url.replaceAll("file:///android_asset/", ""), this);
			} catch (Exception e){
				Log.e(LCAT, "Error opening file " + file.name());
			}
		} else {
			Log.e(LCAT, "File " + file.name() + " not found!");
		}
	}

	protected class AnimatorUpdateListener implements ValueAnimator.AnimatorUpdateListener
	{
		public void onAnimationUpdate(ValueAnimator animation)
		{
			animationEvent(animation.getAnimatedFraction(), LottieViewProxy.ANIMATION_RUNNING);
		}
	}

	protected class AnimatorListener implements Animator.AnimatorListener {
		public void onAnimationStart(Animator animation) {
			 animationEvent(getProgress(), LottieViewProxy.ANIMATION_START);
		}

		public void onAnimationEnd(Animator animation) {
			if (getProgress()>=1) {
				animationEvent(getProgress(), LottieViewProxy.ANIMATION_END);
				if (callbackComplete != null) {
					HashMap<String,Object> event = new HashMap<String, Object>();
					event.put("status", LottieViewProxy.ANIMATION_END);
					event.put("loop", TiConvert.toBoolean(proxy.getProperty("loop")));
					callbackComplete.call(proxy.getKrollObject(), event);
				}
			}
		}

		public void onAnimationCancel(Animator animation) {
			 animationEvent(getProgress(), LottieViewProxy.ANIMATION_CANCEL);
		}

		public void onAnimationRepeat(Animator animation) {
			 animationEvent(getProgress(), LottieViewProxy.ANIMATION_REPEAT);
		}
	}

	private void animationEvent(float percentage, int status) {
		if (callbackUpdate != null && !TiConvert.toBoolean(proxy.getProperty("paused"))) {
			HashMap<String,Object> event = new HashMap<String, Object>();
			event.put("time", TiConvert.toFloat(proxy.getProperty("duration"))*percentage);
			event.put("percentage", percentage);
			event.put("status", status);
			callbackUpdate.call(proxy.getKrollObject(), event);
		}
	}

	public void startAnimation(int startFrame, int endFrame) {
		boolean restart = lottieView.isAnimating();
		lottieView.cancelAnimation();
		lottieView.setProgress(0f);
		proxy.setProperty("paused", false);

		if (TiConvert.toFloat(proxy.getProperty("speed")) == 1.0f) {
			if (startFrame != -1 && endFrame != 1) {
				lottieView.setMinFrame(startFrame);
				lottieView.setMaxFrame(endFrame);
			}
			lottieView.playAnimation();
			va = null;
		} else {
			va = ValueAnimator.ofFloat(0f, 1f);
			va.setDuration((long) TiConvert.toFloat(proxy.getProperty("duration")));

			if (TiConvert.toBoolean(proxy.getProperty("loop"))) {
				va.setRepeatCount(-1);
			}
			va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
				public void onAnimationUpdate(ValueAnimator animation) {
					lottieView.setProgress( (Float)animation.getAnimatedValue() );
					animationEvent(animation.getAnimatedFraction(), LottieViewProxy.ANIMATION_RUNNING);
				}
			});

			va.addListener(new Animator.AnimatorListener() {
				@Override
				public void onAnimationStart(Animator animation) {

				}
				@Override
				public void onAnimationCancel(Animator animation) {

				}
				@Override
				public void onAnimationRepeat(Animator animation) {

				}
				@Override
				public void onAnimationEnd(Animator animation) {
					animationEvent(getProgress(), LottieViewProxy.ANIMATION_END);
					if (callbackComplete != null) {
						HashMap<String,Object> event = new HashMap<String, Object>();
						event.put("status", LottieViewProxy.ANIMATION_END);
						event.put("loop", TiConvert.toBoolean(proxy.getProperty("loop")));
						callbackComplete.call(proxy.getKrollObject(), event);
					}
				}
			});
			va.start();
		}
	}

	public void pauseAnimation() {
		proxy.setProperty("paused", true);
		
		if (va != null) {
			va.pause();
		} else {
			lottieView.pauseAnimation();
		}
	}

	public void resumeAnimation() {
		proxy.setProperty("paused", false);
		if (va != null) {
			va.resume();
		} else {
			lottieView.resumeAnimation();
		}
	}

	public void stopAnimation() {
		proxy.setProperty("paused", false);
		if (va != null) {
			va.cancel();
		} else {
			lottieView.cancelAnimation();
		}
	}

	public void setProgress(float val) {
		lottieView.setProgress(val);
	}

	public void setText(String layer, String text) {
		delegate.setText(layer, text);
	}

	public float getProgress() {
		return lottieView.getProgress();
	}

}
