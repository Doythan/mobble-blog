package org.example.mobble.board;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.web.servlet.MvcResult;

import java.util.*;

public class TestUtils {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static void printRequestAttributesAsJson(MvcResult result) {
        MockHttpServletRequest req = result.getRequest();

        Map<String, Object> attributes = new LinkedHashMap<>();
        Collections.list(req.getAttributeNames()).forEach(name -> {
            // 스프링 내부 attribute는 제외
            if (!name.startsWith("org.springframework")) {
                attributes.put(name, req.getAttribute(name));
            }
        });

        try {
            String json = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(attributes);
            System.out.println("👉 Request Attributes (filtered):\n" + json);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}