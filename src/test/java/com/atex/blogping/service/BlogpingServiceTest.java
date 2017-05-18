package com.atex.blogping.service;

import com.atex.blogping.dao.BlogpingDAO;
import com.atex.blogping.dto.WeblogDTO;
import com.atex.blogping.jaxb.Response;
import com.atex.blogping.jaxb.Responses;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.BDDMockito.*;

@RunWith(MockitoJUnitRunner.class)
public class BlogpingServiceTest {

    @InjectMocks
    private BlogpingService service;

    @Mock
    private BlogpingDAO blogpingDAO;

    @Before
    public void init() {
        willDoNothing().given(blogpingDAO).saveWeblog(Matchers.<WeblogDTO>any());
    }

    @After
    public void noMoreInteractions() {
        verifyNoMoreInteractions(blogpingDAO);
    }

    @Test
    public void get_shouldAcceptRequestAndReturnOk() {
        Request valid = givenValidRequest();

        // when
        Response response = service.pingSiteFormGet(valid.name, valid.url, valid.changesURL);

        // then
        assertEquals(Responses.OK, response);
        verify(blogpingDAO).saveWeblog(Matchers.<WeblogDTO>any());
    }

    @Test
    public void post_shouldAcceptRequestAndReturnOk() {
        Request valid = givenValidRequest();

        // when
        Response response = service.pingSiteFormPost(valid.name, valid.url, valid.changesURL);

        // then
        assertEquals(Responses.OK, response);
        verify(blogpingDAO).saveWeblog(Matchers.<WeblogDTO>any());
    }

    @Test
    public void delete_should_return_ok() {
        //given
        Request valid = givenValidRequest();

        // when
        Response response = service.deleteSite(valid.name, valid.url);

        // then
        assertEquals(Responses.OK, response);
        verify(blogpingDAO).deleteWeblog(Matchers.<WeblogDTO>any());
    }

    @Test
    public void shouldAcceptRequestAndReturnOkForHttpsUrl() {
        Request valid = givenValidRequestWithHttpsUrl();

        // when
        Response response = service.pingSiteFormGet(valid.name, valid.url, valid.changesURL);

        // then
        assertEquals(Responses.OK, response);
        verify(blogpingDAO).saveWeblog(Matchers.<WeblogDTO>any());
    }

    @Test
    public void shouldReturnNameMustNotBeEmptyOnEmptyString() {
        Request request = givenRequestWithEmptyName();

        // when
        Response response = service.pingSiteFormGet(request.name, request.url, request.changesURL);

        // then
        assertEquals(Responses.NAME_MUST_NOT_BE_EMPTY, response);
        verify(blogpingDAO, never()).saveWeblog(Matchers.<WeblogDTO>any());
    }

    @Test
    public void shouldReturnNameMustNotBeEmptyOnNameNull() {
        Request request = givenRequestWithNameNull();

        // when
        Response response = service.pingSiteFormGet(request.name, request.url, request.changesURL);

        // then
        assertEquals(Responses.NAME_MUST_NOT_BE_EMPTY, response);
        verify(blogpingDAO, never()).saveWeblog(Matchers.<WeblogDTO>any());
    }

    @Test
    public void shouldReturnNameTooLong() {
        Request request = givenRequestWithNameTooLong();

        // when
        Response response = service.pingSiteFormGet(request.name, request.url, request.changesURL);

        // then
        assertEquals(Responses.NAME_TOO_LONG, response);
        verify(blogpingDAO, never()).saveWeblog(Matchers.<WeblogDTO>any());
    }

    @Test
    public void shouldReturnUrlMustNotBeEmptyOnEmptyString() {
        Request request = givenRequestWithEmptyUrl();

        // when
        Response response = service.pingSiteFormGet(request.name, request.url, request.changesURL);

        // then
        assertEquals(Responses.URL_MUST_NOT_BE_EMPTY, response);
        verify(blogpingDAO, never()).saveWeblog(Matchers.<WeblogDTO>any());
    }

    @Test
    public void shouldReturnUrlMustNotBeEmptyOnUrlNull() {
        Request request = givenRequestWithUrlNull();

        // when
        Response response = service.pingSiteFormGet(request.name, request.url, request.changesURL);

        // then
        assertEquals(Responses.URL_MUST_NOT_BE_EMPTY, response);
        verify(blogpingDAO, never()).saveWeblog(Matchers.<WeblogDTO>any());
    }

    @Test
    public void shouldReturnUrlTooLong() {
        Request request = givenRequestWithUrlTooLong();

        // when
        Response response = service.pingSiteFormGet(request.name, request.url, request.changesURL);

        // then
        assertEquals(Responses.URL_TOO_LONG, response);
        verify(blogpingDAO, never()).saveWeblog(Matchers.<WeblogDTO>any());
    }

    @Test
    public void shouldReturnUrlMustStartWithHttpS() {
        Request request = givenRequestWhereUrlDoesNotStartWithHttp();

        // when
        Response response = service.pingSiteFormGet(request.name, request.url, request.changesURL);

        // then
        assertEquals(Responses.URL_MUST_START_WITH_HTTP_S, response);
        verify(blogpingDAO, never()).saveWeblog(Matchers.<WeblogDTO>any());
    }

    @Test
    public void shouldReturnInternalServerError() {
        // given
        Request valid = givenValidRequest();
        willThrow(IllegalArgumentException.class).given(blogpingDAO).saveWeblog(Matchers.<WeblogDTO>any());

        // when
        Response response = service.pingSiteFormGet(valid.name, valid.url, valid.changesURL);

        // then
        assertEquals(Responses.INTERNAL_SERVER_ERROR, response);
        verify(blogpingDAO).saveWeblog(Matchers.<WeblogDTO>any());
    }


    private Request givenValidRequest() {
        return new Request("allotria", "http://www.allotria.ch/blog", "http://www.allotria.ch/blog/atom.xml");
    }

    private Request givenValidRequestWithHttpsUrl() {
        return new Request("allotria", "https://www.allotria.ch/blog", "https://www.allotria.ch/blog/atom.xml");
    }

    private Request givenRequestWithNameNull() {
        return givenValidRequest().setName(null);
    }

    private Request givenRequestWithEmptyName() {
        return givenValidRequest().setName("");
    }

    private Request givenRequestWithNameTooLong() {
        return givenValidRequest().setName(RandomStringUtils.randomAlphabetic(1025));
    }

    private Request givenRequestWithUrlNull() {
        return givenValidRequest().setUrl(null);
    }

    private Request givenRequestWithEmptyUrl() {
        return givenValidRequest().setUrl("");
    }

    private Request givenRequestWithUrlTooLong() {
        return givenValidRequest().setUrl("http://" + RandomStringUtils.randomAlphabetic(249));
    }

    private Request givenRequestWhereUrlDoesNotStartWithHttp() {
        return givenValidRequest().setUrl("www.example.com/blog");
    }

    private static class Request {
        private String name;
        private String url;
        private String changesURL;

        private Request(String name, String url, String changesURL) {
            this.name = name;
            this.url = url;
            this.changesURL = changesURL;
        }

        public String getName() {
            return name;
        }

        public Request setName(String name) {
            this.name = name;
            return this;
        }

        public String getUrl() {
            return url;
        }

        public Request setUrl(String url) {
            this.url = url;
            return this;
        }

        public String getChangesURL() {
            return changesURL;
        }

        public Request setChangesURL(String changesURL) {
            this.changesURL = changesURL;
            return this;
        }
    }
}