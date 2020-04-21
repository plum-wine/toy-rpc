package com.github.event;

import java.util.Observable;


public class InvokeEventWatcher extends Observable {
    public void watch(AbstractInvokeEventBus.ModuleEvent event) {
        setChanged();
        notifyObservers(event);
    }
}
