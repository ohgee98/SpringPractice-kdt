package org.prgms.kdt.servlet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.WebApplicationInitializer;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;

// WebApplicationInitializer 상속받아 구현해 servlet 등록
public class KdtWebApplicationInitializer implements WebApplicationInitializer {
    private static final Logger logger = LoggerFactory.getLogger(KdtWebApplicationInitializer.class);
    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {
        logger.info("Starting Server...");
        var servletRegistration = servletContext.addServlet("test", new TestServlet());
        servletRegistration.addMapping("/*");
        servletRegistration.setLoadOnStartup(1);
    }
}
