package com.treefintech.b2b.oncecenter.common.designpattern;

/**
 * 适配器模式 --- 结构型模式
 * 场景：1、方式一，方式二：类似转换器，已经实现了A接口，却发现还需要一个方法在B接口存在。
 *      2、方式三：实现C接口，但是只需要部分的方法。
 *
 * （建议，尽量少用这种模式）
 *  原因：若用的太多，会导致以为是调A接口，结果A接口内部适配到了B接口，增加代码可读的难度。
 */
public class AdapterPatternTest {

    public static void main(String[] args) {
        //方式一
//        Ps2 ps2 = new Adapter();
//        ps2.isPs2();
        //方式二
//        Ps2 ps2 = new AdapterOne(new Usber());
//        ps2.isPs2();

        //方式三
        TestClass testClass = new TestClass();
        testClass.d();
        testClass.e();
    }
}


interface Ps2 {
    void isPs2();
}

interface Usb {
    void isUsb();
}

class Usber implements Usb {

    @Override
    public void isUsb() {
        System.out.println("isUsb");
    }
}

class Adapter extends Usber implements Ps2 {

    @Override
    public void isPs2() {
        isUsb();
    }
}


class AdapterOne implements Ps2 {

    private Usb usb;

    public AdapterOne(Usb usb) {
        this.usb = usb;
    }

    @Override
    public void isPs2() {
        usb.isUsb();
    }
}


interface A {
    void a();
    void b();
    void c();
    void d();
    void e();
}


abstract class AbstractAdapterTwo implements A {
    @Override
    public void a() {
    }

    @Override
    public void b() {
    }

    @Override
    public void c() {

    }
}


class TestClass extends AbstractAdapterTwo {
    @Override
    public void d() {
        System.out.println("test for d");
    }

    @Override
    public void e() {
        System.out.println("test for e");
    }
}







