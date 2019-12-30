package org.apache.cordova.plugin.stylus;

import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.PluginResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;

public class Stylus extends CordovaPlugin implements View.OnTouchListener {
  private CallbackContext callbackContext;
  private DisplayMetrics displayMetrics;

  public void initialize(CordovaInterface cordovaInterface, CordovaWebView webView) {
    displayMetrics = cordovaInterface.getContext().getResources().getDisplayMetrics();
  }

  @Override
  public boolean execute(String action, JSONArray args, CallbackContext callbackContext) {
    this.callbackContext = callbackContext;
    if(action.equals("registerListeners")) {
        webView.getView().setOnTouchListener(this);
        return true;
    }
    callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.ERROR, "Invalid action"));
    return false;
  }

  @Override
  public boolean onTouch(View v, MotionEvent event) {
    int pointerId = event.getPointerId(0);
    try {
      if (event.getToolType(pointerId) != MotionEvent.TOOL_TYPE_STYLUS) {
        return false;
      }
    } catch (IllegalArgumentException e) {
      return false;
    }
    /*int historySize = event.getHistorySize();

    float prevX = 0, prevY = 0;
    float pressure;
    for (int i = 0; i < historySize - 6; i += 6) { // TODO: Support changeable step size
      if (Math.abs(event.getHistoricalX(pointerId, i) - prevX) < 1 &&
              Math.abs(event.getHistoricalY(pointerId, i) - prevY) < 1) {
        i -= 5;
        continue;
      }
      prevX = event.getHistoricalX(pointerId, i) / displayMetrics.density;
      prevY = event.getHistoricalY(pointerId, i) / displayMetrics.density;
      pressure = event.getHistoricalPressure(pointerId, i);

      generateJSON(event, pointerId, prevX, prevY, pressure);
    }*/
    generateJSON(event, pointerId, event.getX(pointerId) / displayMetrics.density, event.getY(pointerId) / displayMetrics.density, event.getPressure(pointerId));
    return true;
  }

  private void generateJSON(MotionEvent event, int pointerId, float x, float y, float pressure) {
    JSONObject json = new JSONObject();
    String type;
    switch (event.getAction()) {
      case MotionEvent.ACTION_DOWN:
        type = "stylusplugin-down";
        break;
      case MotionEvent.ACTION_UP:
        type = "stylusplugin-up";
        break;
      case MotionEvent.ACTION_MOVE:
        type = "stylusplugin-move";
        break;
      default:
        type = "stylusplugin-unknown";
    }
    try {
      json.put("altKey", event.getMetaState() == KeyEvent.META_ALT_ON);
      json.put("ctrlKey", event.getMetaState() == KeyEvent.META_CTRL_ON);
      json.put("metaKey", event.getMetaState() == KeyEvent.META_META_ON);
      json.put("shiftKey", event.getMetaState() == KeyEvent.META_SHIFT_ON);
      JSONObject touch = new JSONObject();
      touch.put("identifier", pointerId);
      touch.put("screenX", null);
      touch.put("screenY", null);
      touch.put("clientX", x);
      touch.put("clientY", y);
      touch.put("pageX", null);
      touch.put("pageY", null);
      touch.put("target", null);
      touch.put("radiusX", event.getAxisValue(MotionEvent.AXIS_X, pointerId));
      touch.put("radiusY", event.getAxisValue(MotionEvent.AXIS_Y, pointerId));
      touch.put("rotationAngle", event.getOrientation(pointerId));
      touch.put("force", pressure);
      touch.put("stylusButton", event.getButtonState() == MotionEvent.BUTTON_STYLUS_PRIMARY ? "primary" :
              ( event.getButtonState() == MotionEvent.BUTTON_STYLUS_SECONDARY ? "secondary" : null));
      JSONArray touches = new JSONArray();
      touches.put(touch);
      json.put("targetTouches", touches);
      json.put("touches", touches);
      json.put("changedTouches", touches);
      json.put("type", type);
    } catch (JSONException ex) {
      throw new RuntimeException("Failed to create onTouch JSON obj " + ex.getMessage());
    }

    PluginResult pluginResult = new PluginResult(PluginResult.Status.OK, json);
    pluginResult.setKeepCallback(true);
    callbackContext.sendPluginResult(pluginResult);
  }
}