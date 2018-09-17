package indi.flynn.mvc.framework.servlet;

import indi.flynn.mvc.framework.annotation.FlynnController;
import indi.flynn.mvc.framework.annotation.FlynnRequestMapping;
import indi.flynn.mvc.framework.annotation.FlynnRequestParameter;
import indi.flynn.mvc.framework.context.FlynnApplicationContext;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * FlynnDispatcherServlet
 *
 * @author Flynn
 * @version 1.0
 * @description TODO
 * @email liufenglin@163.com
 * @date 2018/9/4
 */
public class FlynnDispatcherServlet extends HttpServlet {

    private static final String LOCATION = "contextConfigLocation";

//    private Map<String, Handler> handlerMapping = new HashMap<String, Handler>();
//    private Map<Pattern, Handler> handlerMapping = new HashMap<Pattern, Handler>();
    private List<Handler> handlerMapping = new ArrayList<Handler>();

    private Map<Handler,HandlerAdapter> adapterMapping = new HashMap<Handler, HandlerAdapter>();

    private List<ViewResolver> viewResolvers = new ArrayList<ViewResolver>();
//    private List<Handler> handlerMapping = new ArrayList<Handler>();

    @Override
    public void init(ServletConfig config) throws ServletException {
        // IOC容器初始化
        // 用new模拟容器已启动
        FlynnApplicationContext context = new FlynnApplicationContext(config.getInitParameter(LOCATION));

        Map<String, Object> ioc = context.getAll();
        System.out.println(ioc);
        System.out.println(ioc.get("firstAction"));

        // SpringMVC九大组件初始化，本次手动实现三大组件
        //请求解析
        initMultipartResolver(context); // context就是我们的IoC容器
        //多语言、国际化
        initLocaleResolver(context);
        //主题View层的
        initThemeResolver(context);
        //============== 重要 ================
        //解析url和Method的关联关系
        initHandlerMappings(context);
        //适配器（匹配的过程）：具体调用哪个方法，以及参数填充
        initHandlerAdapters(context);
        //============== 重要 ================
        //异常解析
        initHandlerExceptionResolvers(context);
        //视图转发（根据视图名字匹配到一个具体模板）
        initRequestToViewNameTranslator(context);
        //解析模板中的内容（拿到服务器传过来的数据，生成HTML代码）
        initViewResolvers(context);
        initFlashMapManager(context);
        System.out.println("GPSpring MVC is init.");
    }

    //请求解析
    private void initMultipartResolver(FlynnApplicationContext context){}
    //多语言、国际化
    private void initLocaleResolver(FlynnApplicationContext context){}
    //主题View层的
    private void initThemeResolver(FlynnApplicationContext context){}
    //解析url和Method的关联关系
    private void initHandlerMappings(FlynnApplicationContext context){
        Map<String,Object> ioc = context.getAll();
        if(ioc.isEmpty()){ return;}
        //只要是由Cotroller修饰类，里面方法全部找出来
        //而且这个方法上应该要加了RequestMaping注解，如果没加这个注解，这个方法是不能被外界来访问的
        for (Map.Entry<String, Object> entry : ioc.entrySet()) {
            Class<?> clazz = entry.getValue().getClass();
            // 如果没有加Controller注解
            if(!clazz.isAnnotationPresent(FlynnController.class)){ continue;}
            String url = "";
            // 如果加了Controller注解
            if(clazz.isAnnotationPresent(FlynnRequestMapping.class)){
                FlynnRequestMapping requestMapping = clazz.getAnnotation(FlynnRequestMapping.class);
                url = requestMapping.value();
            }

            //扫描Controller下面的所有的方法
            Method[] methods = clazz.getMethods();
            for (Method method : methods) {
                // 如果请求没加FlynnRequestMapping注解，就表示这个方法不对外开放
                if(!method.isAnnotationPresent(FlynnRequestMapping.class)){ continue;}

                FlynnRequestMapping requestMapping = method.getAnnotation(FlynnRequestMapping.class);
//                String mapingUrl = (url + requestMapping.value());
//                handlerMapping.put(mapingUrl, new Handler(entry.getValue(), method));
                /////
                String regex = (url + requestMapping.value()).replaceAll("/+", "/");
                Pattern pattern = Pattern.compile(regex);
                handlerMapping.add(new Handler(pattern,entry.getValue(), method));
//                handlerMapping.put(pattern, new Handler(entry.getValue(), method));
                System.out.println("Mapping: " + regex + " " +  method.toString());
//                String regex = (url + requestMapping.value()).replaceAll("/+", "/");
//                Pattern pattern = Pattern.compile(regex);
//                handlerMapping.put(url, new Handler(pattern,entry.getValue(),method));
//                System.out.println("Mapping: " + mapingUrl + " " +  method.toString());
            }

        }

    }
    //适配器（匹配的过程）
    //主要是用来动态匹配我们参数的
    private void initHandlerAdapters(FlynnApplicationContext context){
        if(handlerMapping.isEmpty()){ return;}

        //参数类型作为key，参数的索引号作为值，paramMapping是参数列表
        Map<String,Integer> paramMapping = new HashMap<String,Integer>();

        //只需要取出来具体的某个方法
//        for (Map.Entry<String, Handler> entry: handlerMapping.entrySet()) {
//        for (Map.Entry<Pattern, Handler> entry: handlerMapping.entrySet()) {
            for (Handler handler : handlerMapping) {
            //把这个方法上面所有的参数全部获取到
//            Class<?> [] paramsTypes = entry.getValue().method.getParameterTypes();
              Class<?> [] paramsTypes = handler.method.getParameterTypes();

            //参数有顺序，但是通过反射，没法拿到形参名字，所以通过顺序来区分
            //匹配自定参数列表
            for (int i = 0;i < paramsTypes.length ; i++) {
                Class<?> type = paramsTypes[i];
                // HttpServletRequest、HttpServletResponse可以在不要注解的情况下赋值到形参
                if(type == HttpServletRequest.class ||
                        type == HttpServletResponse.class){
                    paramMapping.put(type.getName(), i);
                }
            }

            //这里是匹配Request和Response，因为每个参数上是可以加多个注解的，所以这里是一个二维数组
//            Annotation[][] pa = entry.getValue().method.getParameterAnnotations();
            Annotation[][] pa = handler.method.getParameterAnnotations();
            for (int i = 0; i < pa.length; i ++) { // 通过索引号来区分参数的顺序
                for(Annotation a : pa[i]){
                    if(a instanceof FlynnRequestParameter){
                        String paramName = ((FlynnRequestParameter) a).value();
                        if(!"".equals(paramName.trim())){
                            paramMapping.put(paramName, i);
                        }
                    }
                }
            }
//            adapterMapping.put(entry.getValue(), new HandlerAdapter(paramMapping));
             adapterMapping.put(handler, new HandlerAdapter(paramMapping));
        }
    }
    //异常解析
    private void initHandlerExceptionResolvers(FlynnApplicationContext context){}
    //视图转发（根据视图名字匹配到一个具体模板）
    private void initRequestToViewNameTranslator(FlynnApplicationContext context){}
    //解析模板中的内容（拿到服务器传过来的数据，生成HTML代码）
    private void initViewResolvers(FlynnApplicationContext context){
        //模板一般是不会放到WebRoot下的，而是放在WEB-INF下，或者classes下
        //这样就避免了用户直接请求到模板
        //加载模板的个数，存储到缓存中
        //检查模板中的语法错误

        String tempateRoot = context.getConfig().getProperty("templateRoot");

        //归根到底就是一个文件，普通文件
        String rootPath = this.getClass().getClassLoader().getResource(tempateRoot).getFile();

        File rootDir = new File(rootPath);
        for (File template : rootDir.listFiles()) {
            viewResolvers.add(new ViewResolver(template.getName(),template));
        }
    }
    //
    private void initFlashMapManager(FlynnApplicationContext context){}

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("调用");
        try {
            doDispatch(req, resp);
        } catch (Exception e) {
            resp.getWriter().write("500 Exception, Msg:" + e.getStackTrace().toString());
        }
    }

    private Handler getHandler(HttpServletRequest req){
        // 循环handlerMapping
        if(handlerMapping.isEmpty()){ return null; }

        String url = req.getRequestURI();
        String contextPath = req.getContextPath();
        url = url.replace(contextPath, "").replaceAll("/+", "/");

//        for (Map.Entry<Pattern, Handler> entry : handlerMapping.entrySet()) {
        for (Handler handler : handlerMapping) {
//            Matcher matcher = entry.getKey().matcher(url);
            Matcher matcher = handler.pattern.matcher(url);
            // 匹配不上就继续
            if (!matcher.matches()) {continue;}
//            return entry.getValue();
            return handler;

//            if (url.equals(entry.getKey())) {
//                return entry.getValue();
//            }
//            return handler;
        }

//        return handlerMapping.get(url);
        return null;
    }

    private HandlerAdapter getHandlerAdapter(Handler handler) {
        if(adapterMapping.isEmpty()){return null;}
        return adapterMapping.get(handler);
    }

    // doGet和doPost都要调用doDispatch方法
    private void doDispatch(HttpServletRequest req, HttpServletResponse resp) {

        try {
            //先取出一个handler，由getHandler方法从handlerMapping中取出来
            Handler handler = getHandler(req);
            if (handler == null) {
                resp.getWriter().write("404 Not Found");
                return;
            }
            // 如果存在handler，则得到一个适配器
            HandlerAdapter adapter = getHandlerAdapter(handler);

            // adapter.handle(req, resp, handler);
            FlynnModelAndView modelAndView = adapter.handle(req, resp, handler);
            // 真正的调用过程，由我们的适配器去调用具体的方法


            //写一个咕泡模板框架
            //Veloctiy #
            //Freemark  #
            //JSP   ${name}

            //咕泡模板   @{name}
            applyDefaultViewName(resp, modelAndView); // 根据用户设置得值去找到对应的模板

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * HandlerMapping 定义
     */
    private class Handler{
        protected Object controller;
        protected Method method;
        protected Pattern pattern;

//        protected Handler(Object controller,Method method){
//            this.controller = controller;
//            this.method = method;
//        }
        protected Handler(Pattern pattern,Object controller,Method method){
            this.pattern = pattern;
            this.controller = controller;
            this.method = method;
        }
    }

    public void applyDefaultViewName(HttpServletResponse resp,FlynnModelAndView mv) throws Exception{
        if(null == mv){ return;}
        if(viewResolvers.isEmpty()){ return;}

        for (ViewResolver resolver : viewResolvers) {
            if(!mv.getView().equals(resolver.getViewName())){ continue; }

            String r = resolver.parse(mv);

            if(r != null){
                resp.getWriter().write(r);
                break;
            }
        }
    }

    /**
     *  方法适配器
     */
    private class HandlerAdapter{
        private Map<String,Integer> paramMapping;

        //主要目的是用反射调用url对应的method
//        public void handle(HttpServletRequest req, HttpServletResponse resp,Handler handler) throws Exception{
        public FlynnModelAndView handle(HttpServletRequest req, HttpServletResponse resp,Handler handler) throws Exception{
            //为什么要传req（因为action，有这个参数的话，是必须要赋值的）、为什么要传resp（因为action，有这个参数的话，是必须要赋值的）、为什么传handler（为了拿到method）
            Class<?> [] paramTypes = handler.method.getParameterTypes();

            //要想给参数赋值，只能通过索引号来找到具体的某个参数
            Object [] paramValues = new Object[paramTypes.length];

            Map<String,String[]> params = req.getParameterMap();
            for (Map.Entry<String, String[]> param : params.entrySet()) {
                String value = Arrays.toString(param.getValue()).replaceAll("\\[|\\]", "").replaceAll(",\\s", ",");
                if(!this.paramMapping.containsKey(param.getKey())){continue;}

                int index = this.paramMapping.get(param.getKey());

                //单个赋值是不行的，要一起赋值
                paramValues[index] = castStringValue(value,paramTypes[index]);
            }

            //request 和 response 要赋值
            String reqName = HttpServletRequest.class.getName();
            if(this.paramMapping.containsKey(reqName)){
                int reqIndex = this.paramMapping.get(reqName);
                paramValues[reqIndex] = req;
            }

            String resqName = HttpServletResponse.class.getName();
            if(this.paramMapping.containsKey(resqName)){
                int respIndex = this.paramMapping.get(resqName);
                paramValues[respIndex] = resp;
            }

            boolean isModelAndView = handler.method.getReturnType() == FlynnModelAndView.class;
            Object r =  handler.method.invoke(handler.controller, paramValues);
            if (isModelAndView) {
                return (FlynnModelAndView) r;
            } else {
                return null;
            }
//            boolean isModelAndView = handler.method.getReturnType() == GPModelAndView.class;
//            Object r = handler.method.invoke(handler.controller, paramValues);
//            if(isModelAndView){
//                return (GPModelAndView)r;
//            }else{
//                return null;
//            }

        }

        public HandlerAdapter(Map<String,Integer> paramMapping){
            this.paramMapping = paramMapping;
        }

        private Object castStringValue(String value,Class<?> clazz){
            if(clazz == String.class){
                return value;
            }else if(clazz == Integer.class){
                return Integer.valueOf(value);
            }else if(clazz == int.class){
                return Integer.valueOf(value).intValue();
            }else{
                return null;
            }
        }
    }

    private class ViewResolver{
        private String viewName;
        private File file;

        protected ViewResolver(String viewName,File file){
            this.viewName = viewName;
            this.file = file;
        }
//
        protected String parse(FlynnModelAndView mv) throws Exception{

            StringBuffer sb = new StringBuffer();

            RandomAccessFile ra = new RandomAccessFile(this.file, "r");

            try{
                //模板框架的语法是非常复杂，但是，原理是一样的
                //无非都是用正则表达式来处理字符串而已
                //就这么简单，不要认为这个模板框架的语法是有多么的高大上
                //来我现在来做一个最接地气的模板，也就是咕泡学院独创的模板语法
                String line = null;
                while(null != (line = ra.readLine())){
                    Matcher m = matcher(line);
                    while (m.find()) {
                        for (int i = 1; i <= m.groupCount(); i ++) {
                            String paramName = m.group(i);
                            Object paramValue = mv.getModel().get(paramName);
                            if(null == paramValue){ continue; }
                            line = line.replaceAll("@\\{" + paramName + "\\}", paramValue.toString());
                        }
                    }

                    sb.append(line);
                }
            }finally{
                ra.close();
            }
            return sb.toString();
        }

        private Matcher matcher(String str){
            Pattern pattern = Pattern.compile("@\\{(.+?)\\}",Pattern.CASE_INSENSITIVE);
            Matcher m = pattern.matcher(str);
            return m;
        }
//
//
        public String getViewName() {
            return viewName;
        }

    }

}
