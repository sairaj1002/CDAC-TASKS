package in.gov.wamis.integration.gepnic.response;

import com.fasterxml.jackson.databind.ObjectMapper;
import in.gov.wamis.common.exception.IntegrationException;
import in.gov.wamis.integration.gepnic.response.GePNICResponseParser.ParsedResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GePNICResponseParserTest {

    private GePNICResponseParser parser;

    @BeforeEach
    void setUp() {
        parser = new GePNICResponseParser(new ObjectMapper());
    }

    @Test
    @DisplayName("Parses tender push nested JSON successfully")
    void testParseTenderPushNestedSuccess() {
        String json = "{\"responseCode\":1,\"responseMessage\":\"{\\\"responseCode\\\":101,\\\"responseMessage\\\":\\\"Inserted Successfully\\\"}\"}";
        ParsedResponse parsed = parser.parse(json);

        assertNotNull(parsed);
        assertEquals(1, parsed.getOuterCode());
        assertEquals(101, parsed.getInnerCode());
        assertEquals("Inserted Successfully", parsed.getInnerMessage());
        assertTrue(parsed.isSuccess());
    }

    @Test
    @DisplayName("Parses document upload nested JSON successfully")
    void testParseDocumentUploadSuccess() {
        String json = "{\"responseCode\":1,\"responseMessage\":\"{\\\"responseCode\\\":105,\\\"responseMessage\\\":\\\"Document Uploaded Successfully\\\"}\"}";
        ParsedResponse parsed = parser.parse(json);

        assertNotNull(parsed);
        assertEquals(1, parsed.getOuterCode());
        assertEquals(105, parsed.getInnerCode());
        assertEquals("Document Uploaded Successfully", parsed.getInnerMessage());
        assertTrue(parsed.isSuccess());
    }

    @Test
    @DisplayName("Parses non-nested plaintext error response safely")
    void testParsePlaintextError() {
        String json = "{\"responseCode\":0,\"responseMessage\":\"Invalid credentials or unauthorized\"}";
        ParsedResponse parsed = parser.parse(json);

        assertNotNull(parsed);
        assertEquals(0, parsed.getOuterCode());
        assertNull(parsed.getInnerCode());
        assertEquals("Invalid credentials or unauthorized", parsed.getInnerMessage());
        assertFalse(parsed.isSuccess());
    }

    @Test
    @DisplayName("Throws IntegrationException on empty or null response")
    void testParseEmpty() {
        assertThrows(IntegrationException.class, () -> parser.parse(null));
        assertThrows(IntegrationException.class, () -> parser.parse("   "));
    }

    @Test
    @DisplayName("Throws IntegrationException on completely malformed JSON")
    void testParseMalformedJson() {
        assertThrows(IntegrationException.class, () -> parser.parse("<HTML>502 Bad Gateway</HTML>"));
    }
}

