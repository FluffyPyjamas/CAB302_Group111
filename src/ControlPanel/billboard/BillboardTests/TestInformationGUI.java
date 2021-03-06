package ControlPanel.billboard.BillboardTests;

import Server.Request.BBInfoRequest;
import Server.SessionToken;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * The following tests are for the data encapsulated in BBInfoRequest object
 * <p>
 * Here we have added comments to explain what each test
 * obliges you to do during Test-Driven Development
 */

public class TestInformationGUI {
    // Session token for testing
    SessionToken token = new SessionToken("token", LocalDateTime.now());

    /* Test 1: Construct a empty request */
    @BeforeEach
    @Test
    public void TestEmptyBBInfoRequest() {
        BBInfoRequest bbInfoRequest;
    }

    /* Test 2: Check if the input billboard name has been successfully encapsulated in the request object  */
    @Test
    public void TestBBInfoRequestBBName() {
        BBInfoRequest bbInfoRequest = new BBInfoRequest(token, "chad");
        assertEquals("chad", bbInfoRequest.getBillboardName());
    }

    /* Test 3: Check if the input session token has been successfully encapsulated in the request object  */
    @Test
    public void TestBBInfoRequestToken() {
        BBInfoRequest bbInfoRequest = new BBInfoRequest(token, "chad");
        assertEquals(token, bbInfoRequest.getSessionToken());
    }
}
