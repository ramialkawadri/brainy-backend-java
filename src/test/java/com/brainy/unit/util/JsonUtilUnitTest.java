package com.brainy.unit.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.brainy.util.JsonUtil;

public class JsonUtilUnitTest {
    
    @Test
    public void shouldCompressJson() {
        String uncompressedJson = "   {\"isJson\"   :  true}";
        String compressedJson = uncompressedJson.replace(" ", "");
        String returnValue = JsonUtil.compressJson(uncompressedJson);
        Assertions.assertEquals(compressedJson, returnValue);
    }

    @Test
    public void shouldNotAcceptInvalidJson() {
        String json = "   aa{ \"isJson\": false}";
        String returnValue = JsonUtil.compressJson(json);
        Assertions.assertNull(returnValue);
    }
}
