package com.gzu.queswer.util;

import com.alibaba.fastjson.JSONObject;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

public class DecodeWrapper extends HttpServletRequestWrapper {
    private Map map;

    public DecodeWrapper(HttpServletRequest request) {
        super(request);
        Map requestParameterMap = request.getParameterMap();
        String params = null ;
        try {
            params=SecurityUtil.rsaDecode(((String[])requestParameterMap.get("params"))[0]);
            map = JSONObject.parseObject(params);
        } catch (Exception e) {
            e.printStackTrace();
            map=new HashMap();
        }
    }

    @Override
    public Enumeration<String> getParameterNames() {
        return Collections.enumeration(map.keySet());
    }

    @Override
    public String[] getParameterValues(String name) {
        return new String[]{map.get(name).toString()};
    }

}
