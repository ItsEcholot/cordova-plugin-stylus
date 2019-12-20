package org.apache.cordova.plugin.stylus;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaWebView;

import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.view.MotionEvent;
import android.view.View;

public class Stylus extends CordovaPlugin implements View.OnTouchListener {
  private CallbackContext callbackContext;

  @Override
  public void initialize(CordovaInterface cordova, CordovaWebView webView) {
    // frame = (View) cordova.getActivity().findViewById(android.R.id.content);
    super.initialize(cordova, webView);
  }

  @Override
  public boolean execute(String action, JSONArray args, CallbackContext callbackContext) {
    this.callbackContext = callbackContext;
    if(action.equals("registerListeners")) {
        webView.getView().setOnTouchListener(this);
        return true;
    }
    return false;
  }

  @Override
  public boolean onTouch(View v, MotionEvent event) {
    int pointerId = event.getPointerId(0);
    if (event.getToolType(pointerId) != MotionEvent.TOOL_TYPE_STYLUS) {
      return false;
    }
    int historySize = event.getHistorySize();

    float prevX = 0, prevY = 0;
    float pressure;
    for (int i = 0; i < historySize; i++) {
      if (Math.abs(event.getHistoricalX(pointerId, i) - prevX) < 1 &&
              Math.abs(event.getHistoricalY(pointerId, i) - prevY) < 1) {
        continue;
      }
      prevX = event.getHistoricalX(pointerId, i);
      prevY = event.getHistoricalY(pointerId, i);
      pressure = event.getHistoricalPressure(pointerId, i);

      generateJSON(event, (int) prevX, (int) prevY, pressure);
    }
    generateJSON(event, (int) event.getX(), (int) event.getY(), event.getPressure());

    v.performClick();
    return true;
  }

  private void generateJSON(MotionEvent event, int x, int y, float pressure) {
    JSONObject json = new JSONObject();
    String type;
    switch (event.getAction()) {
      case MotionEvent.ACTION_DOWN:
        type = "down";
        break;
      case MotionEvent.ACTION_UP:
        type = "up";
        break;
      case MotionEvent.ACTION_MOVE:
        type = "move";
        break;
      default:
        type = "unknown";
    }
    try {
      json.put("type", type);
      json.put("x", x);
      json.put("y", y);
      json.put("pressure", pressure);
    } catch (JSONException ex) {
      throw new RuntimeException("Failed to create onTouch JSON obj " + ex.getMessage());
    }

    PluginResult pluginResult = new PluginResult(PluginResult.Status.OK, json);
    pluginResult.setKeepCallback(true);
    callbackContext.sendPluginResult(pluginResult);
  }
}