package com.atex.blogping.http;


import com.atex.blogping.dao.BlogpingDAO;
import com.atex.blogping.dao.impl.QueueBackedBlogpingDAO;
import com.atex.blogping.service.BlogpingService;
import com.atex.blogping.service.ChangesService;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import com.google.inject.servlet.GuiceServletContextListener;
import com.sun.jersey.guice.JerseyServletModule;
import com.sun.jersey.guice.spi.container.servlet.GuiceContainer;
import com.sun.jersey.spi.container.servlet.ServletContainer;

import java.util.HashMap;
import java.util.Map;

public class ServletContextListener extends GuiceServletContextListener {

    @Override
    protected Injector getInjector() {

        return Guice.createInjector(new JerseyServletModule() {
            @Override
            protected void configureServlets() {

                // binding the servlet container
                bind(ServletContainer.class).in(Singleton.class);

                // binding our own services
                bind(BlogpingService.class);
                bind(ChangesService.class);

                // binding our resource interfaces to an actual implementation
                bind(BlogpingDAO.class).to(QueueBackedBlogpingDAO.class);

                // serving all paths through Jerseys Guice integration
                filter("/*").through(GuiceContainer.class, staticContentFilterParams());
            }
        });
    }

    private Map<String, String> staticContentFilterParams() {
        Map<String, String> params = new HashMap<>();
        params.put("com.sun.jersey.config.property.WebPageContentRegex",
                "/client/.*");
        return params;
    }
}