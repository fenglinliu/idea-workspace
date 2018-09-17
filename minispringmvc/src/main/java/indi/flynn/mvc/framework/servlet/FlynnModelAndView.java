package indi.flynn.mvc.framework.servlet;

import java.util.Map;

/**
 * FlynnModelAndView
 *
 * @author Flynn
 * @version 1.0
 * @description TODO
 * @email liufenglin@163.com
 * @date 2018/9/17
 */
public class FlynnModelAndView {
    //页面模板
    private String view;

    /** Model Map */
    //要往页面上带过去的值
    private Map<String,Object> model;

    public FlynnModelAndView(String view){
        this.view = view;
    }

    public FlynnModelAndView(String view,Map<String,Object> model){
        this.view = view;
        this.model = model;
    }

    public String getView() {
        return view;
    }

    public void setView(String view) {
        this.view = view;
    }

    public Map<String, Object> getModel() {
        return model;
    }

    public void setModel(Map<String, Object> model) {
        this.model = model;
    }
}
