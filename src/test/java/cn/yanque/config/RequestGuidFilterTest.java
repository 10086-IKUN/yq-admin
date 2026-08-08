package cn.yanque.config;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.assertj.core.api.Assertions.assertThat;

class RequestGuidFilterTest {

    private final RequestGuidFilter filter = new RequestGuidFilter();

    @Test
    void shouldPropagateValidRequestGuid() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(RequestGuidFilter.REQUEST_GUID_HEADER, "trace_123-abc");
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilter(request, response, new MockFilterChain());

        assertThat(response.getHeader(RequestGuidFilter.REQUEST_GUID_HEADER)).isEqualTo("trace_123-abc");
        assertThat(request.getAttribute(RequestGuidFilter.REQUEST_GUID_ATTR)).isEqualTo("trace_123-abc");
    }

    @Test
    void shouldReplaceInvalidRequestGuid() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(RequestGuidFilter.REQUEST_GUID_HEADER, "invalid guid");
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilter(request, response, new MockFilterChain());

        String generated = response.getHeader(RequestGuidFilter.REQUEST_GUID_HEADER);
        assertThat(generated).matches("[a-f0-9]{32}");
        assertThat(generated).isNotEqualTo("invalid guid");
    }
}
