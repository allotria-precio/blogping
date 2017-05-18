package com.atex.blogping;


import com.atex.blogping.jaxb.Changes;
import com.atex.blogping.jaxb.Responses;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.URIException;
import org.apache.commons.httpclient.methods.DeleteMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.util.URIUtil;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.io.StringReader;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.lessThan;

public class BlogpingIT {

    private HttpClient client = new HttpClient();

    @Test
    public void send_update_and_verify_response() throws IOException {
        // given
        String name = "AtexBlog";
        String url = "http://www.atex.com";
        GetMethod ping = givenGetMethod(name, url);

        // when
        String response = whenExecuteGet(ping);

        // then
        assertTrue(response.contains(Responses.OK.getMessage()));
        String changesResponse = getChanges();
        assertTrue(changesResponse.contains(name));
        assertTrue(changesResponse.contains(url));
    }

    @Test
    public void delete_and_verify_response() throws IOException {
        // given
        String name = "AtexBlog";
        String url = "http://www.atex.com";
        GetMethod ping = givenGetMethod(name, url);
        String getResponse = whenExecuteGet(ping);
        assertTrue(getResponse.contains(Responses.OK.getMessage()));

        DeleteMethod deleteMethod = givenDeleteMethod(name, url);

        // when
        String deleteResponse = whenExecuteDelete(deleteMethod);

        // then
        assertTrue(deleteResponse.contains(Responses.OK.getMessage()));
        String changesResponse = getChanges();
        assertFalse(changesResponse.contains(name));
        assertFalse(changesResponse.contains(url));


    }


    @Test
    public void send_update_using_post_and_verify_response() throws IOException {
        // given
        String name = "AtexBlog";
        String url = "http://www.atex.com";
        PostMethod ping = givenPostMethod(name, url);

        // when
        String postResponse = whenExecutePost(ping);

        // then
        String changesResponse = getChanges();
        assertTrue(postResponse.contains(Responses.OK.getMessage()));
        assertTrue(changesResponse.contains(name));
        assertTrue(changesResponse.contains(url));
    }


    @Test
    public void send_missing_name_parameter() throws IOException {
        // given
        String url = "http://" + RandomStringUtils.randomAlphabetic(30);
        GetMethod ping = givenGetMethod(null, url);

        // when
        String getResponse = whenExecuteGet(ping);

        // then
        String changesResponse = getChanges();
        assertTrue(getResponse.contains(Responses.NAME_MUST_NOT_BE_EMPTY.getMessage()));
        assertFalse(changesResponse.contains(url));
    }

    @Test
    public void send_empty_name_parameter() throws IOException {
        // given
        String url = "http://" + RandomStringUtils.randomAlphabetic(30);
        GetMethod ping = givenGetMethod("", url);

        // when
        String getResponse = whenExecuteGet(ping);

        // then
        String changesResponse = getChanges();
        assertTrue(getResponse.contains(Responses.NAME_MUST_NOT_BE_EMPTY.getMessage()));
        assertFalse(changesResponse.contains(url));
    }

    @Test
    public void send_missing_url_parameter() throws IOException {
        // given
        String name = RandomStringUtils.randomAlphabetic(30);
        GetMethod ping = givenGetMethod(name, null);

        // when
        String getResponse = whenExecuteGet(ping);

        // then
        String changesResponse = getChanges();
        assertTrue(getResponse.contains(Responses.URL_MUST_NOT_BE_EMPTY.getMessage()));
        assertFalse(changesResponse.contains(name));
    }

    @Test
    public void send_empty_url_parameter() throws IOException {
        // given
        String name = RandomStringUtils.randomAlphabetic(30);
        GetMethod ping = givenGetMethod(name, "");

        // when
        String getResponse = whenExecuteGet(ping);

        // then
        String changesResponse = getChanges();
        assertTrue(getResponse.contains(Responses.URL_MUST_NOT_BE_EMPTY.getMessage()));
        assertFalse(changesResponse.contains(name));
    }

    @Test
    public void subsequent_calls_to_get_changes_increment_count() throws IOException, JAXBException {
        // given
        Changes changes1 = getChangesAsJAXBObject();

        // when
        Changes changes2 = getChangesAsJAXBObject();

        // then
        assertThat(changes1.getCount(), is(lessThan(changes2.getCount())));
    }

    private Changes getChangesAsJAXBObject() throws JAXBException, IOException {
        String changesResponse = getChanges();
        JAXBContext jaxbContext = JAXBContext.newInstance(Changes.class);
        Changes changes = (Changes) jaxbContext.createUnmarshaller().unmarshal(new StringReader(changesResponse));

        return changes;
    }

    private GetMethod givenGetMethod(String name, String url) throws URIException {
        final StringBuilder sb = new StringBuilder("http://localhost:8080/pingSiteForm");

        addParam(sb, "name", name);
        addParam(sb, "url", url);

        return new GetMethod(sb.toString());
    }

    private DeleteMethod givenDeleteMethod(String name, String url) throws URIException {
        final StringBuilder sb = new StringBuilder("http://localhost:8080/pingSiteForm");

        addParam(sb, "name", name);
        addParam(sb, "url", url);

        return new DeleteMethod(sb.toString());
    }

    private void addParam(final StringBuilder sb, String parameterName, String parameterValue) throws URIException {
        if (parameterValue != null) {
            addParameterDelimiter(sb);
            sb.append(parameterName);
            sb.append("=");
            sb.append(URIUtil.encodeWithinQuery(parameterValue));
        }
    }

    private void addParameterDelimiter(final StringBuilder sb) {
        if (!StringUtils.contains(sb, "?")) {
            sb.append("?");
        } else {
            sb.append("&");
        }
    }

    private PostMethod givenPostMethod(String name, String url) {
        PostMethod ping = new PostMethod("http://localhost:8080/pingSiteForm");
        if (name != null) {
            ping.setParameter("name", name);
        }

        if (url != null) {
            ping.setParameter("url", url);
        }

        return ping;
    }

    private String whenExecuteGet(GetMethod ping) throws IOException {
        try {
            client.executeMethod(ping);
            return ping.getResponseBodyAsString();
        } finally {
            ping.releaseConnection();
        }
    }

    private String whenExecutePost(PostMethod ping) throws IOException {
        try {
            client.executeMethod(ping);
            return ping.getResponseBodyAsString();
        } finally {
            ping.releaseConnection();
        }
    }

    private String whenExecuteDelete(DeleteMethod deleteMethod) throws IOException {
        try {
            client.executeMethod(deleteMethod);
            return deleteMethod.getResponseBodyAsString();
        } finally {
            deleteMethod.releaseConnection();
        }
    }


    private String getChanges() throws IOException {
        GetMethod changes = new GetMethod("http://localhost:8080/changes.xml");

        try {
            client.executeMethod(changes);
            return changes.getResponseBodyAsString();
        } finally {
            changes.releaseConnection();
        }
    }
}
