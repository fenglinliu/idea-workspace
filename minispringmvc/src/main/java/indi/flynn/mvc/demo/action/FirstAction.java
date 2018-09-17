package indi.flynn.mvc.demo.action;

import indi.flynn.mvc.demo.service.INamedService;
import indi.flynn.mvc.demo.service.IService;
import indi.flynn.mvc.framework.annotation.*;
import indi.flynn.mvc.framework.servlet.FlynnModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * FirstAction
 *
 * @author Flynn
 * @version 1.0
 * @description TODO
 * @email liufenglin@163.com
 * @date 2018/9/12
 */
@FlynnController
@FlynnRequestMapping("/web")
public class FirstAction {

    @FlynnAutowired private IService service;

    @FlynnAutowired("myName") private INamedService namedService;

    @FlynnRequestMapping("/query/.*.json")
//    @FlynnResponseBody
//    public void query(HttpServletRequest req, HttpServletResponse resp, @FlynnRequestParameter("name") String name) {
    public FlynnModelAndView query(HttpServletRequest req, HttpServletResponse resp, @FlynnRequestParameter("name") String name) {
//        try {
//            resp.getWriter().write("get parameters name = " + name);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return;
        Map<String, Object> model = new HashMap<String, Object>();
        model.put("name", name);
        return new FlynnModelAndView("first.gpml", model);
    }

}
