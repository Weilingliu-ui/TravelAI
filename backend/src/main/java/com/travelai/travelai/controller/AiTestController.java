package com.travelai.travelai.controller;

import com.travelai.travelai.common.response.Result;
import com.travelai.travelai.dto.AiTestRequest;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * AI 测试 Controller
 * <p>
 * 验证 Spring AI + DeepSeek 调用链路是否正常
 *
 * @author TravelAI Team
 */
@RestController
@RequestMapping("/api/ai")
public class AiTestController {

    @Resource
    private ChatClient chatClient;

    @PostMapping("/test")
    public Result<String> test(@Valid @RequestBody AiTestRequest request) {
        String reply = chatClient.prompt()
                .user(request.getMessage())
                .call()
                .content();
        return Result.success(reply);
    }
}
