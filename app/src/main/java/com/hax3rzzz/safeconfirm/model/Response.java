package com.hax3rzzz.safeconfirm.model;

/**
 * Created by MILESWT on 7/22/2017.
 */

import org.json.JSONObject;

public class Response {

    private JSONObject weather = new JSONObject();

    public JSONObject getAndroid() {
        return weather;
    }
}