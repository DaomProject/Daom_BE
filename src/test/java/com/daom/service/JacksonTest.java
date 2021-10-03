package com.daom.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

public class JacksonTest {
    
    @Test
    public void jsonNodeTest() throws Exception {
        //given
        String str = "{\"status\":\"OK\",\"meta\":{\"totalCount\":1,\"page\":1,\"count\":1},\"addresses\":[{\"roadAddress\":\"전라남도 목포시 백년대로 147 용해동 광신프로그레스\",\"jibunAddress\":\"전라남도 목포시 용해동 1061 용해동 광신프로그레스\",\"englishAddress\":\"147, Baengnyeon-daero, Mokpo-si, Jeollanam-do, Republic of Korea\",\"addressElements\":[{\"types\":[\"SIDO\"],\"longName\":\"전라남도\",\"shortName\":\"전라남도\",\"code\":\"\"},{\"types\":[\"SIGUGUN\"],\"longName\":\"목포시\",\"shortName\":\"목포시\",\"code\":\"\"},{\"types\":[\"DONGMYUN\"],\"longName\":\"용해동\",\"shortName\":\"용해동\",\"code\":\"\"},{\"types\":[\"RI\"],\"longName\":\"\",\"shortName\":\"\",\"code\":\"\"},{\"types\":[\"ROAD_NAME\"],\"longName\":\"백년대로\",\"shortName\":\"백년대로\",\"code\":\"\"},{\"types\":[\"BUILDING_NUMBER\"],\"longName\":\"147\",\"shortName\":\"147\",\"code\":\"\"},{\"types\":[\"BUILDING_NAME\"],\"longName\":\"용해동 광신프로그레스\",\"shortName\":\"용해동 광신프로그레스\",\"code\":\"\"},{\"types\":[\"LAND_NUMBER\"],\"longName\":\"1061\",\"shortName\":\"1061\",\"code\":\"\"},{\"types\":[\"POSTAL_CODE\"],\"longName\":\"58703\",\"shortName\":\"58703\",\"code\":\"\"}],\"x\":\"126.4065875\",\"y\":\"34.7992078\",\"distance\":0.0}],\"errorMessage\":\"\"}";
        ObjectMapper mapper = new ObjectMapper();
        //when

        JsonNode node = mapper.readTree(str);

        int totalCount = node.get("meta").get("totalCount").asInt();
        System.out.println(totalCount);
        double x = 0;
        double y = 0;
        if( totalCount >= 1 ){
            x = node.get("addresses").get(0).get("x").asDouble();
            y = node.get("addresses").get(0).get("y").asDouble();
        }
        //then
        Assertions.assertThat(totalCount).isEqualTo(1);
        Assertions.assertThat(x).isEqualTo(126.4065875);
        Assertions.assertThat(y).isEqualTo(34.7992078);
    }
}
