package io.luxus.sample.include;

public class MyController {

    private final MyService myService = new MyServiceImpl();

    public void doSomething() {
        myService.doSomething(null, 0, "");
    }

    public void foo() {
        ((MyServiceImpl) myService).foo();
    }

}
