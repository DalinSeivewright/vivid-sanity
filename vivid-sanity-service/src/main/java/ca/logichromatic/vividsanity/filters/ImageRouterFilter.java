package ca.logichromatic.vividsanity.filters;

import ca.logichromatic.vividsanity.configuration.ApplicationProperties;
import ca.logichromatic.vividsanity.service.ImageService;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.netflix.zuul.filters.ZuulProperties;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;
import software.amazon.awssdk.core.ResponseBytes;

import javax.servlet.http.HttpServletRequest;

import java.net.URI;

import static org.springframework.cloud.netflix.zuul.filters.support.FilterConstants.PRE_TYPE;


@Slf4j
public class ImageRouterFilter extends ZuulFilter {

    private PathMatcher pathMatcher = new AntPathMatcher();

    @Autowired
    private ImageService imageService;

    @Autowired
    private ZuulProperties zuulProperties;

    @Autowired
    private ApplicationProperties applicationProperties;

    @Override
    public String filterType() {
        return PRE_TYPE;
    }

    @Override
    public int filterOrder() {
        return 0;
    }

    @Override
    public boolean shouldFilter() {
        RequestContext requestContext = RequestContext.getCurrentContext();

        HttpServletRequest request = requestContext.getRequest();

        String imageRoutingId = applicationProperties.getImageProxy().getZuulRouteId();
        if (imageRoutingId == null) {
            notFound(requestContext);
            return false;
        }

        if (!zuulProperties.getRoutes().containsKey(imageRoutingId)) {
            notFound(requestContext);
            return false;
        }

        String imageProxyPath = zuulProperties.getRoutes().get(imageRoutingId).getPath();
        if (!checkMatchingPath(imageProxyPath, request.getRequestURI())) {
            notFound(requestContext);
            return false;
        }
        return true;
    }

    @Override
    public Object run() throws ZuulException {
        RequestContext requestContext = RequestContext.getCurrentContext();

        HttpServletRequest request = requestContext.getRequest();

        String requestUrl = URI.create(request.getRequestURI()).getPath();
        log.error(requestUrl);
        try {
            ResponseBytes test = imageService.getImageAsStream("ar326121.jpg");
            requestContext.getResponse().addHeader("Content-Length", String.valueOf(test.asByteArray().length));
            requestContext.getResponse().addHeader("Content-Type", "image/jpeg");
            requestContext.setResponseDataStream(test.asInputStream());
        } catch (Exception e) {

        }
        //requestContext.setResponseDataStream(imageService.getImageAsStream("ar326121.jpg"));

        return null;
    }

    private boolean checkMatchingPath(String imageProxyPath, String requestURI) {
        int imageProxyPathLength = imageProxyPath.length();
        if (requestURI == null || requestURI.length() < imageProxyPathLength) {
            return false;
        }
        return pathMatcher.match(imageProxyPath, requestURI);
    }

    private Object notFound(RequestContext ctx) {
        ctx.setResponseStatusCode(404);
        ctx.setSendZuulResponse(false);
        return null;
    }
}

