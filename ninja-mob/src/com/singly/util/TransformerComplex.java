package com.singly.util;

import org.djd.fun.ninja.TransformerException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: acorn
 * Date: 9/28/12
 * Time: 5:24 PM
 * To change this template use File | Settings | File Templates.
 */
@Deprecated
public class TransformerComplex {

  private JSONObject json;

  public TransformerComplex(JSONObject json) {
    this.json = json;
  }

  public JSONObject transform() throws TransformerException {
    Builder builder = new Builder();
    for (Iterator iter = json.keys(); iter.hasNext(); ) {
      String key = (String) iter.next();
      if (key.equals("id")) {
        builder.id(key);
      } else {
        builder.serviceName(key);
      }
    }
    for (String serviceName : builder.serviceNames()) {
      try {
        JSONArray jsonArray = json.getJSONArray(serviceName);
        if (jsonArray.length() > 0) {
          // just deal with first element.
          JSONObject jsonObject = jsonArray.getJSONObject(0);
          builder.data(serviceName, toMap(jsonObject));
        }
      } catch (JSONException e) {
        throw new TransformerException("serviceName: " + serviceName, e);
      }
    }
    return builder.build();
  }

  private Map<String, String> toMap(JSONObject jsonObject) throws JSONException {
    Map<String, String> data = new HashMap<String, String>();
    for (Iterator iter = json.keys(); iter.hasNext(); ) {
      String key = (String) iter.next();
      String value = json.get(key).toString();
      data.put(key, value);
    }
    return data;
  }

  private class Builder {
    String id;
    Map<String, Map<String, String>> services = new HashMap<String, Map<String, String>>();

    public Builder serviceName(String serviceName) {
      services.put(serviceName, new HashMap<String, String>());
      return this;
    }

    public Builder id(String id) {
      this.id = id;
      return this;
    }

    public Builder data(String serviceName, Map<String, String> data) {
      services.put(serviceName, data);
      return this;
    }

    public Set<String> serviceNames() {
      return services.keySet();
    }

    public JSONObject build() throws TransformerException {
      JSONObject jsonObject = new JSONObject();
      try {
        jsonObject.put("id", id);
        for (Map.Entry<String, Map<String, String>> entry : services.entrySet()) {

        }
      } catch (JSONException e) {
        throw new TransformerException("build Error", e);
      }
      return jsonObject;
    }
  }
}