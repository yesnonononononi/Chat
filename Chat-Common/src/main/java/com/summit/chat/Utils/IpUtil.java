package com.summit.chat.Utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.summit.chat.Exception.BusinessException;
import com.summit.chat.Result.Result;
import com.summit.chat.model.dto.IpAddressDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

@Slf4j
public class IpUtil {
    private static final RestTemplate restTemplate = new RestTemplate();
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final String url = "https://v2.xxapi.cn/api/ip";

    /**
     * 根据 IP 地址获取地理位置信息
     * @param ip IP 地址
     * @return IpAddressDTO IP 地址信息
     */
    public static IpAddressDTO getIpAddressInfo(String ip){
        if(ip == null || ip.trim().isEmpty()){
            throw new BusinessException("IP 不能为空");
        }
        try {
            // 直接使用 ParameterizedTypeReference 来指定返回类型
            ResponseEntity<Result> response = restTemplate.exchange(
                String.format("%s?ip=%s", url, ip),
                HttpMethod.GET,
                null,
                Result.class
            );
            Result body = response.getBody();
            if(body == null){
                log.warn("【IP 获取】获取失败，返回结果为空");
                return null;
            }
            Object data = body.getData();
            IpAddressDTO result = objectMapper.convertValue(data, IpAddressDTO.class);

            log.debug("【IP 查询】IP:{},地址:{}", ip, result != null ? result.getAddress() : "null");
            
            return result;
        } catch (Exception e) {
            log.error("【IP 查询】查询失败，IP:{}", ip, e);
            return null; // 查询失败时返回 null，不影响主流程
        }
    }
    
    /**
     * 构建完整的地址字符串
     * @param dto IP 地址 DTO
     * @return 完整地址
     */
    public static String buildAddress(IpAddressDTO dto) {
        if (dto == null) {
            return "未知";
        }
        StringBuilder address = new StringBuilder();
        address.append(dto.getAddress());
        return !address.isEmpty() ? address.toString() : "未知";
    }
}
