package com.imoxion.sensems.web.listener;

import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

import java.util.List;

public class SensEmsStartupListener implements ApplicationListener<ContextRefreshedEvent> {

    private List<StartupListener> listeners;

    public SensEmsStartupListener(List<StartupListener> contextListenerImpl){
        listeners = contextListenerImpl;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        for(StartupListener listener : listeners){
            listener.activate();
        }
    }
}
