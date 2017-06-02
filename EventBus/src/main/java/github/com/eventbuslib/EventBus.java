package github.com.eventbuslib;

import android.os.Handler;
import android.os.Looper;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Email  1562363326@qq.com
 * Github https://github.com/skcodestack
 * Created by sk on 2017/6/2
 * Version  1.0
 * Description:
 */

public class EventBus {

    //单例
    private static volatile EventBus defaultInstance;
    //保存带有Subscriber注解的方法
    private Map<Object,CopyOnWriteArrayList<SubscriberMethod>> map = new HashMap<>();

    //主线程
    private Handler mHadnler=null;
    //线程池
    private ExecutorService executorService=null;


    private EventBus(){
        mHadnler=new Handler(Looper.getMainLooper());
        executorService = Executors.newCachedThreadPool();
    }

    /**
     * 单例
     * @return
     */
    public static EventBus getDefault(){
        if(defaultInstance == null){
            synchronized (EventBus.class){
                if(defaultInstance == null){
                    defaultInstance = new EventBus();
                }
            }
        }
        return defaultInstance;
    }


    /**
     * 注册
     * @param activity
     */
    public void register(Object activity){
        CopyOnWriteArrayList<SubscriberMethod> list = map.get(activity);
        if(list == null){
            list=obtainSubscriberMethods(activity);
            map.put(activity,list);
        }

    }

    /**
     * 注销
     * @param activity
     */
    public void  unregister(Object activity){
        map.remove(activity);
    }

    /**
     * 发布
     * @param event
     */
    public void post(final Object event){
        Set<Map.Entry<Object, CopyOnWriteArrayList<SubscriberMethod>>> entries = map.entrySet();

        for (Map.Entry<Object, CopyOnWriteArrayList<SubscriberMethod>> entry : entries) {
            final Object object = entry.getKey();
            CopyOnWriteArrayList<SubscriberMethod> list = entry.getValue();
            for (SubscriberMethod subscriberMethod : list) {
                if(subscriberMethod.getEventType().isAssignableFrom(event.getClass())){
                    final Method method = subscriberMethod.getMethod();

                    switch (subscriberMethod.getThreadMode()){
                        case PostThread:
                            invoke(object,method,event);

                            break;
                        case MainThread:
                            if(Looper.myLooper() == Looper.getMainLooper()){
                                invoke(object,method,event);
                            }else {
                                mHadnler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        invoke(object,method,event);
                                    }
                                });
                            }
                            break;
                        case BackgroundThread:
                        case Async:
                            if(Looper.myLooper() == Looper.getMainLooper()){
                                executorService.execute(new Runnable() {
                                    @Override
                                    public void run() {
                                        invoke(object,method,event);
                                    }
                                });
                            }else {
                                invoke(object,method,event);
                            }
                        default:
                            break;
                    }
                }

            }

        }
    }

    /**
     * 调用方法
     * @param object
     * @param method
     * @param event
     */
    private void invoke(Object object,Method method,Object event){
        try {
            if(!method.isAccessible()){
                method.setAccessible(true);
            }
            method.invoke(object,event);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取注册类中的方法
     * @param activity
     * @return
     */
    private CopyOnWriteArrayList<SubscriberMethod> obtainSubscriberMethods(Object activity) {

        CopyOnWriteArrayList<SubscriberMethod> list=new CopyOnWriteArrayList<>();
        Class<?> clazz = activity.getClass();
        while(clazz != null){

            String clazzName = clazz.getName();
            if(clazzName.startsWith("android.") || clazzName.startsWith("java.")
                    || clazzName.startsWith("javax.") || clazzName.startsWith("org.")){
                break;
            }

            Method[] methods =
                    clazz.getDeclaredMethods();
            for (int i = 0; i < methods.length; i++) {
                Method method = methods[i];
                Subscriber annotation = method.getAnnotation(Subscriber.class);
                if(annotation == null){
                    continue;
                }
                Class<?>[] parameterTypes = method.getParameterTypes();
                if(parameterTypes == null || parameterTypes.length!=1){
                    throw new RuntimeException("eventbus method params size must be one!");
                }

                ThreadMode threadMode = annotation.value();
                Class<?> parameterType = parameterTypes[0];

                SubscriberMethod subscriberMethod = new SubscriberMethod(method,threadMode,parameterType);

                list.add(subscriberMethod);
            }

            clazz = clazz.getSuperclass();
        }




        return list;
    }
}
