package ControlPanel.billboard.BillboardTests;

import Server.Request.XmlRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertEquals;

/** The following tests are for the data encapsulated in XmlRequest object
 *
 * Here we have added comments to explain what each test
 * obliges you to do during Test-Driven Development
 */

public class TestXmlRequest {
    /* Test 1: Construct a empty reply */
    @BeforeEach @Test
    public void TestEmptyXmlRequest() {
        XmlRequest xmlRequest;
    }
    /* Test 2: Check if billboard xml file has been successfully encapsulated in the Request object for importing */
    @Test
    public void TestImportBillboardXml() {
        File testFile = new File("src/xmlBillboards/starWars.xml");
        XmlRequest xmlRequest = new XmlRequest(testFile, "chad");
        assertEquals(testFile, xmlRequest.getXmlFile());
    }
    /* Test 3: Check if username has been successfully encapsulated in the Request object for importing */
    @Test
    public void TestImportBillboardUsername() {
        File testFile = new File("src/xmlBillboards/starWars.xml");
        XmlRequest xmlRequest = new XmlRequest(testFile, "chad");
        assertEquals("chad", xmlRequest.getUserName());
    }
    /* Test 4: Check if billboard name has been successfully encapsulated in the Request object for exporting */
    @Test
    public void TestExportBillboard() {
        XmlRequest xmlRequest = new XmlRequest("test1.xml");
        assertEquals("test1.xml", xmlRequest.getXmlName());
    }
}