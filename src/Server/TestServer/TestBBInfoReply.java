package Server.TestServer;

import Server.Reply.BBInfoReply;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/* The following tests are for the data encapsulated in BBInfoReply object
 *
 * Here we have added comments to explain what each test
 * obliges you to do during Test-Driven Development
 */

public class TestBBInfoReply {
    /* Test 1: Construct a empty reply */
    @BeforeEach @Test
    public void TestEmptyBBInfoReply() {
        BBInfoReply bbInfoReply;
    }
    /* Test 2: Check if the input billboard info has been successfully encapsulated in the reply object  */
    @Test
    public void TestBBInfoReplyBBInfo() {
        BBInfoReply bbInfoReply = new BBInfoReply("Old town road");
        assertEquals("Old town road", bbInfoReply.getInformation());
    }
}
