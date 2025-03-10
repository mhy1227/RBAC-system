package com.czj.rbac.mapper;

import com.czj.rbac.model.LoginInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface LoginInfoMapper {
    /**
     * 插入登录信息
     */
    int insert(LoginInfo loginInfo);
    
    /**
     * 更新登录信息
     */
    int update(LoginInfo loginInfo);
    
    /**
     * 根据ID查询
     */
    LoginInfo findById(@Param("id") Long id);
    
    /**
     * 查询用户最近的登录记录
     */
    LoginInfo findLatestByUserId(@Param("userId") Long userId);
    
    /**
     * 分页查询
     */
    List<LoginInfo> findPage(@Param("offset") Integer offset,
                            @Param("limit") Integer limit,
                            @Param("userId") Long userId,
                            @Param("username") String username,
                            @Param("loginIp") String loginIp,
                            @Param("startTime") LocalDateTime startTime,
                            @Param("endTime") LocalDateTime endTime,
                            @Param("loginStatus") Integer loginStatus);
    
    /**
     * 统计总数
     */
    int count(@Param("userId") Long userId,
              @Param("username") String username,
              @Param("loginIp") String loginIp,
              @Param("startTime") LocalDateTime startTime,
              @Param("endTime") LocalDateTime endTime,
              @Param("loginStatus") Integer loginStatus);
    
    /**
     * 更新登出时间
     */
    int updateLogoutTime(@Param("userId") Long userId,
                        @Param("loginId") String loginId,
                        @Param("logoutTime") LocalDateTime logoutTime);
    
    /**
     * 删除指定时间之前的数据
     */
    int deleteBeforeTime(@Param("time") LocalDateTime time);
    
    /**
     * 获取用户登录统计信息
     */
    int countLoginTimes(@Param("userId") Long userId,
                       @Param("startTime") LocalDateTime startTime,
                       @Param("endTime") LocalDateTime endTime,
                       @Param("loginStatus") Integer loginStatus);
} 