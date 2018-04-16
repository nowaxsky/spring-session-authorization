package org.cpm.zwl.filter;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.cpm.zwl.presentation.vos.ResponseModel;
import org.cpm.zwl.util.ResultStatus;
import org.springframework.core.annotation.Order;
import com.fasterxml.jackson.databind.ObjectMapper;

@Order(1)
@WebFilter(filterName = "sessionFilter", urlPatterns = "/*")
public class SessionFilter implements Filter {

  private final String savePath = "/tokens/login";

  @Override
  public void destroy() {
    // TODO Auto-generated method stub

  }

  @Override
  public void doFilter(ServletRequest req, ServletResponse res, FilterChain filterChain)
      throws IOException, ServletException {

    HttpServletRequest request = (HttpServletRequest) req;
    HttpServletResponse response = (HttpServletResponse) res;
    System.out.println("doFilter");

    String path = request.getRequestURI().substring(request.getContextPath().length())
        .replaceAll("[/]+$", "");
    System.out.println("path: " + path);
    boolean allowedPath = savePath.contains(path);
    if (allowedPath) {
      filterChain.doFilter(req, res);
      return;
    }

    // 從header中取得token
    String token = request.getHeader("X-Auth-Token");
    System.out.println("token: " + token);

    // 若token為空則登入失敗
    if (token == null) {
      System.out.println("token is null");
      this.returnFail(response);
      return;
    }

    // 取得session中的資訊(同到redis查詢該redis), 若無則表示攜帶的token錯誤
    Object attribute = request.getSession().getAttribute("userDetail");
    if (attribute == null) {
      System.out.println("attribute is null");
      this.returnFail(response);
      return;
    }

    System.out.println("pass");
    filterChain.doFilter(req, res);

  }

  @Override
  public void init(FilterConfig arg0) throws ServletException {
    // TODO Auto-generated method stub

  }

  private void returnFail(HttpServletResponse response) throws IOException {
    response.setContentType("application/json");
    response.setStatus(HttpServletResponse.SC_OK);
    ObjectMapper mapper = new ObjectMapper();
    String errorMessage =
        mapper.writeValueAsString(ResponseModel.error(ResultStatus.USER_NOT_LOGIN));
    response.getOutputStream().println(errorMessage);
  }

}
