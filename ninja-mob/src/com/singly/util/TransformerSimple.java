package com.singly.util;

import org.djd.fun.ninja.TransformerException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: acorn
 * Date: 9/28/12
 * Time: 5:24 PM
 * To change this template use File | Settings | File Templates.
 */
@Deprecated
public class TransformerSimple {

  private JSONObject json;
  private String resultId;
  private JSONObject resultJsonObject;

  public TransformerSimple(JSONObject json) {
    this.json = json;
  }

  /**
   * @return this
   * @throws org.djd.fun.ninja.TransformerException
   *
   */
  public TransformerSimple transform() throws TransformerException {
    Builder builder = new Builder();
    for (Iterator iter = json.keys(); iter.hasNext(); ) {
      String key = (String) iter.next();
      if (key.equals("id")) {
        try {
          resultId = json.getString(key);
        } catch (JSONException e) {
          throw new TransformerException("Error while getting id.", e);
        }
      } else {
        try {
          JSONArray jsonArray = json.getJSONArray(key);
          if (jsonArray.length() > 0) {
            // just deal with first element.
            JSONObject jsonObject = jsonArray.getJSONObject(0);
            builder.data(key, createServiceId(jsonObject));
          }
        } catch (JSONException e) {
          throw new TransformerException("serviceName: " + key, e);
        }
      }
    }
    resultJsonObject = builder.build();
    return this;
  }

  public String getResultId() {
    return resultId;
  }

  public JSONObject getResultJsonObject() {
    return resultJsonObject;
  }

  private Map<String, String> createServiceId(JSONObject jsonObject) throws JSONException {
    Map<String, String> data = new HashMap<String, String>();
    for (Iterator iter = jsonObject.keys(); iter.hasNext(); ) {
      String key = (String) iter.next();
      if ("id".equals(key)) {
        data.put(key, jsonObject.get(key).toString());
      }
    }
    return data;
  }

  private class Builder {

    // map(ServiceName,map(id,idValue))
    // i.e., "twitter":{"id":"456"}
    Map<String, Map<String, String>> services = new HashMap<String, Map<String, String>>();

    public Builder data(String serviceName, Map<String, String> data) {
      services.put(serviceName, data);
      return this;
    }

    public JSONObject build() throws TransformerException {
      JSONObject jsonObject = new JSONObject();
      try {
        for (Map.Entry<String, Map<String, String>> entry : services.entrySet()) {
          jsonObject.put(entry.getKey(), new JSONObject(entry.getValue()));
        }
      } catch (JSONException e) {
        throw new TransformerException("build Error", e);
      }
      return jsonObject;
    }
  }
}