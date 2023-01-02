package org.prgms.kdt.servlet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

// 초기에는 web.xml로 servlet을 알렸지만 현재는 애노테이션으로 servlet 등록 가능
// loadOnStartup은 미리 로드를 하겠다 (요청 받기 전) (default는 -1 : 요청 받아서 하겠다)
//@WebServlet(value = "/*", loadOnStartup = 1)

// 인터페이스 상속 받아 구현하면서 servlet 등록도 가능함
public class TestServlet extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(TestServlet.class);

    @Override
    public void init() throws ServletException {
        super.init();
        logger.info("Init Servlet");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        var requestURI = req.getRequestURI();
        logger.info("Got Request from {}", requestURI);

        var writer = resp.getWriter();
        writer.println("Hello Servlet!");
    }

}
