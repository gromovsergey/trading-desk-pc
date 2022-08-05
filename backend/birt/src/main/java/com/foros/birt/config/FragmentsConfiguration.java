package com.foros.birt.config;

import org.eclipse.birt.report.presentation.aggregation.IFragment;
import org.eclipse.birt.report.presentation.aggregation.layout.EngineFragment;
import org.eclipse.birt.report.presentation.aggregation.layout.FramesetFragment;
import org.eclipse.birt.report.presentation.aggregation.layout.RequesterFragment;
import org.eclipse.birt.report.presentation.aggregation.layout.RunFragment;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FragmentsConfiguration {

    @Bean
    public IFragment runFragment() {
        return configureFragment(new RunFragment());
    }

    @Bean
    public IFragment framesetFragment() {
        return configureFragment(new FramesetFragment());
    }

    @Bean
    public IFragment requesterFragment() {
        return configureFragment(new RequesterFragment());
    }

    private IFragment configureFragment(IFragment fragment) {
        fragment.buildComposite( );
        fragment.setJSPRootPath( "/webcontent/birt" );
        return fragment;
    }

    @Bean
    public IFragment engineFragment() {
        return new EngineFragment();
    }
}
