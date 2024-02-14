package io.luxus.sample.include;

import io.luxus.sample.exclude.MyInvisibleRepository;

public class MyServiceImpl implements MyService {

    private final MyVisibleRepository myVisibleRepository = new MyVisibleRepository();
    private final MyInvisibleRepository myInvisibleRepository = new MyInvisibleRepository();

    @Override
    public void doSomething(Instance instance, int i, String s) {
        myVisibleRepository.doSomething();
        myInvisibleRepository.doSomething();
    }

    public void foo() {
        myVisibleRepository.doSomething();
    }
}
