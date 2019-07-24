package com.treefintech.b2b.oncecenter.common.designpattern;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * 代理模式 --- 结构型模式
 * 功能：在调用方和被调用方之间起到中介的作用。
 *       可以通过代理类给被调用方增加功能。
 *
 * 方式一：静态代理
 *        不建议使用，需要对每一个需要代理的被调用方，创建一个代理类。
 *
 * 方式二：动态代理，反射
 *        动态代理需要被调用方必须实现接口
 *
 * 方式三：CGLIB代理，
 *        被调用方不需要必须实现接口
 *
 */
public class ProxyPatternTest {

    public static void main(String[] args) {
        //方式一, 静态代理
//        BuyHouse buyHouse = new BuyHouseImpl();
//        buyHouse.buyHouse();
//        System.out.println();
//        BuyHouseProxy buyHouseProxy = new BuyHouseProxy(new BuyHouseImpl());
//        buyHouseProxy.buyHouse();


        //方式二，动态代理
        BuyHouse buyHouse = new BuyHouseImpl();
        BuyHouse proxyBuyHouse = (BuyHouse) Proxy.newProxyInstance(BuyHouseImpl.class.getClassLoader(), new
                Class[]{BuyHouse.class}, new DynamicProxyHandler(buyHouse));
        proxyBuyHouse.buyHouse();


        //方式三，CGLIB代理
//        BuyHouse buyHouse = new BuyHouseImpl();
//        CglibProxy cglibProxy = new CglibProxy();
//        BuyHouseImpl buyHouseCglibProxy = (BuyHouseImpl) cglibProxy.getInstance(buyHouse);
//        buyHouseCglibProxy.buyHouse();

    }
}


interface BuyHouse {
    void buyHouse();
}


class BuyHouseImpl implements BuyHouse {

    @Override
    public void buyHouse() {
        System.out.println("BuyHouse");
    }
}


class BuyHouseProxy implements BuyHouse {

    private BuyHouse buyHouse;

    public BuyHouseProxy(BuyHouse buyHouse) {
        this.buyHouse = buyHouse;
    }

    @Override
    public void buyHouse() {
        System.out.println("before BuyHouse");
        buyHouse.buyHouse();
        System.out.println("after BuyHouse");
    }
}


/**
 * 动态代理，利用反射
 */
class DynamicProxyHandler implements InvocationHandler {

    private Object object;

    public DynamicProxyHandler(Object object) {
        this.object = object;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        System.out.println("before invoke");
        Object result = method.invoke(object, args);
        System.out.println("after invoke");
        return result;
    }
}


/**
 * CGLIB代理
 */
class CglibProxy implements MethodInterceptor {

    private Object target;

    public Object getInstance(Object target) {
        this.target = target;
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(this.target.getClass());
        enhancer.setCallback(this);
        return enhancer.create();
    }

    @Override
    public Object intercept(Object object, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
        System.out.println("before intercept");
        Object result = methodProxy.invoke(target, args);
        System.out.println("after intercept");
        return result;
    }
}



























