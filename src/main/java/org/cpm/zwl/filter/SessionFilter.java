package org.cpm.zwl.filter;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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

  @Override
  public void destroy() {
    // TODO Auto-generated method stub

  }

  @Override
  public void doFilter(ServletRequest req, ServletResponse res, FilterChain filterChain)
      throws IOException, ServletException {

    HttpServletRequest request = (HttpServletRequest) req;
    HttpServletResponse response = (HttpServletResponse) res;

    String path = request.getRequestURI().substring(request.getContextPath().length())
        .replaceAll("[/]+$", "");

    System.out.println("path: " + path);

    // 不須驗證白名單
    boolean pathWithoutCheck = this.checkPath(path);
    if (pathWithoutCheck) {
      filterChain.doFilter(req, res);
      return;
    }

    System.out.println("doFilter");
    // 從header中取得token
    String token = request.getHeader("X-Auth-Token");
    System.out.println("token: " + token);

    // 取得session中的資訊(同到redis查詢該redis), 若無則表示攜帶的token錯誤
    String attribute = (String) request.getSession().getAttribute("userDetail");
    System.out.println("userDetail: " + attribute);

    // token和attribute均不為空則登入成功
    if (token != null && attribute != null) {
      System.out.println("pass");
      filterChain.doFilter(req, res);
      System.out.println("pass");
      return;
    }

    if (token == null) {
      System.out.println("token is null");
    } else {
      System.out.println("attribute is null");
    }
    System.out.println("cannot pass");
    this.returnFail(response);
    return;

  }

  private boolean checkPath(String path) {
    List<String> list = Arrays.asList("/tokens/login", "/swagger-ui.html", "/v2/api-docs");
    Set<String> whiteList = new HashSet<>(list);
    return whiteList.contains(path) || path.contains("/webjars/springfox-swagger-ui/")
        || path.contains("/swagger-resources");
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
