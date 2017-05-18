package com.atex.blogping.service;

import com.atex.blogping.dao.BlogpingDAO;
import com.atex.blogping.dto.WeblogDTO;
import com.atex.blogping.jaxb.Response;
import com.atex.blogping.jaxb.Responses;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.sun.org.apache.regexp.internal.RE;
import org.apache.commons.lang3.StringUtils;

import javax.ws.rs.*;

import static org.apache.commons.lang3.StringUtils.*;

@Singleton
@Path("pingSiteForm")
public class BlogpingService {

    public static final String HTTP = "http://";
    public static final String HTTPS = "https://";
    public static final int URL_MAX_LENGTH = 255;
    public static final int NAME_MAX_LENGTH = 1024;
    private final BlogpingDAO blogpingDAO;


    @Inject
    public BlogpingService(BlogpingDAO blogpingDAO) {
        this.blogpingDAO = blogpingDAO;
    }


    @GET
    @Produces("application/xml")
    public Response pingSiteFormGet(@QueryParam("name") String name,
                                    @QueryParam("url") String url,
                                    @QueryParam("changesURL") String changesURL) {

        // For this implementation we ignore the 'changesURL' parameter

        return validateAndSaveWeblog(name, url);
    }


    @POST
    @Produces("application/xml")
    public Response pingSiteFormPost(@FormParam("name") String name,
                                     @FormParam("url") String url,
                                     @FormParam("changesURL") String changesURL) {

        // changesURL is ignored here as well

        return validateAndSaveWeblog(name, url);
    }

    @DELETE
    public Response deleteSite(@QueryParam("name") String name, @QueryParam("url") String url) {

        validateParameters(name, url);
        return deleteWeblog(name, url);
    }

    private Response validateAndSaveWeblog(String name, String url) {

        Response error = validateParameters(name, url);

        return error != null ? error : saveWeblog(name, url);
    }

    private Response saveWeblog(String name, String url) {
        try {

            blogpingDAO.saveWeblog(new WeblogDTO(name, url));
            return Responses.OK;

        } catch (Exception e) {
            return Responses.INTERNAL_SERVER_ERROR;
        }
    }

    private Response deleteWeblog(String name, String url) {
        try {

            blogpingDAO.deleteWeblog(new WeblogDTO(name, url));
            return Responses.OK;

        } catch (Exception e) {
            return Responses.INTERNAL_SERVER_ERROR;
        }
    }

    private Response validateParameters(String name, String url) {

        if (isEmpty(url)) {
            return Responses.URL_MUST_NOT_BE_EMPTY; // URL_MUST_NOT_BE_EMPTY
        }

        if (!startsWithAny(url, HTTP, HTTPS)) {
            return Responses.URL_MUST_START_WITH_HTTP_S;
        }

        if (length(url) > URL_MAX_LENGTH) {
            return Responses.URL_TOO_LONG;
        }

        if (isEmpty(name)) {
            return Responses.NAME_MUST_NOT_BE_EMPTY;
        }

        if (length(name) > NAME_MAX_LENGTH) {
            return Responses.NAME_TOO_LONG;
        }
        return null;
    }


}
