package org.cpm.zwl.presentation;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.cpm.zwl.dao.entity.User;
import org.cpm.zwl.dao.entity.UserDetail;
import org.cpm.zwl.dao.persistence.UserDetailRepository;
import org.cpm.zwl.dao.persistence.UserRepository;
import org.cpm.zwl.presentation.vos.ResponseModel;
import org.cpm.zwl.util.ResultStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

/**
 * token操作範例
 * 
 * @author CPM
 *
 */
@RestController
@RequestMapping("/tokens")
public class TokenController {

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private UserDetailRepository userDetailRepository;

  /**
   * 登入
   * 
   * @param userId
   * @param password
   * @return
   * @throws JsonProcessingException
   */
  @ApiOperation(value = "登入")
  @ApiImplicitParams({
      @ApiImplicitParam(name = "userId", value = "帳號", required = true, dataType = "string",
          paramType = "query"),
      @ApiImplicitParam(name = "password", value = "密碼", required = true, dataType = "string",
          paramType = "query")})
  @RequestMapping(value = "/login", method = RequestMethod.POST)
  public ResponseEntity<ResponseModel> login(@RequestParam String userId,
      @RequestParam String password, HttpServletRequest request, HttpServletResponse response)
      throws JsonProcessingException {

    System.out.println("userId: " + userId);
    System.out.println("password: " + password);

    User user = userRepository.findByUserId(userId);

    // 未註冊或密碼錯誤則登入失敗
    if (user == null || !user.getPassword().equals(password)) {
      return new ResponseEntity<>(ResponseModel.error(ResultStatus.USERNAME_OR_PASSWORD_ERROR),
          HttpStatus.NOT_FOUND);
    }

    // 找出使用者資料後存入session(同步到redis)
    UserDetail userDetail = userDetailRepository.findByUserId(userId);
    ObjectMapper mapper = new ObjectMapper();
    String data = mapper.writeValueAsString(userDetail);
    System.out.println("data save in redis: " + data);
    request.getSession().setAttribute("userDetail", data);

    // token資訊
    System.out.println("token: " + request.getSession().getId());
    System.out.println("token in header: " + request.getHeader("X-Auth-Token"));

    System.out.println("login");
    return new ResponseEntity<>(ResponseModel.ok(), HttpStatus.OK);
  }

  /**
   * 登入後印出hello
   * 
   * @param name
   * @return
   */
  @ApiOperation(value = "登入後印出hello")
  @ApiImplicitParam(name = "authorization", value = "authorization", required = true,
      dataType = "string", paramType = "header")
  @RequestMapping(value = "/hello", method = RequestMethod.GET)
  public ResponseEntity<ResponseModel> hello(String name) {
    String hello = "hello " + name;
    return new ResponseEntity<>(ResponseModel.ok(hello), HttpStatus.OK);
  }

  /**
   * 登出
   * 
   * @param request
   * @return
   * @throws ServletException
   */
  @ApiOperation(value = "登出")
  @ApiImplicitParam(name = "authorization", value = "authorization", required = true,
      dataType = "string", paramType = "header")
  @RequestMapping(value = "/logout", method = RequestMethod.DELETE)
  public ResponseEntity<ResponseModel> logout(HttpServletRequest request) throws ServletException {
    
    //TODO
    request.logout();
    System.out.println("logout");
    return new ResponseEntity<>(ResponseModel.ok(), HttpStatus.OK);
  }



}
