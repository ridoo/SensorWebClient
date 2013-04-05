package org.n52.server.oxf.util.parser;


import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.io.InputStream;
import java.util.List;

import org.apache.xmlbeans.XmlObject;
import org.junit.Test;

public class DefaultMetadataHandlerTest {
    
    private static final String INVALID_FOI_RESPONSE = "/files/test-invalid-foi-response-hack.xml";

    @Test public void 
    shouldParseFeatureIdAlthoughFoiIsNotContainedInFoiMember()
    throws Exception {
        InputStream stream = getClass().getResourceAsStream(INVALID_FOI_RESPONSE);
        XmlObject invalidFoiResponse = XmlObject.Factory.parse(stream);
        DefaultMetadataHandler handler = new DefaultMetadataHandler();
        List<String> foiIdentifiers = handler.parseFoiIdentifiers(invalidFoiResponse);
        assertThat(foiIdentifiers.size(), is(1));
        assertThat(foiIdentifiers.get(0), is("http://www.portal-tideelbe.org/featureOfInterest/GWM_59_2"));
    }
    
}
