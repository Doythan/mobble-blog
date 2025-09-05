package org.example.mobble._util.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.example.mobble._util.error.ErrorEnum;
import org.example.mobble._util.error.ex.Exception401;
import org.example.mobble._util.error.ex.ExceptionApi401;
import org.example.mobble.user.domain.User;
import org.springframework.web.servlet.HandlerInterceptor;

public class LoginInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String uri = request.getRequestURI();
        System.out.println("uri: " + uri);

        HttpSession session = request.getSession(false);
        User sessionUser = session == null ? null : (User) session.getAttribute("user");

        if (sessionUser == null) {
            if (uri.contains("/api")) {
                throw new ExceptionApi401("인증이 필요합니다.");
//                response.setStatus(401);
//                response.setHeader("Content-Type", "application/json");
//                PrintWriter out = response.getWriter();
//                Resp<?> resp = Resp.fail(401, "인증이 필요합니다");
//                ObjectMapper mapper = new ObjectMapper();
//                String responseBody = mapper.writeValueAsString(resp);
//                out.println(responseBody);
//                return false;
            } else {
                throw new Exception401(ErrorEnum.UNAUTHORIZED_NO_EXISTS_USER_INFO);
//                response.setStatus(401);
//                response.setHeader("Content-Type", "text/html");
//                PrintWriter out = response.getWriter();
//                out.println(Script.href("인증이 필요합니다", "/login-form"));
//                return false;
            }
        }

        return true;
    }
}
