package ru.netology;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.List;
import java.util.stream.Collectors;

public class Request {
    private final String method;
    private final String path;
    private final List<NameValuePair> queryParams;
    private List<NameValuePair> postParams;

    public Request(String method, String path) throws URISyntaxException {
        this.method = method;
        URI uri = new URI(path);
        this.path = uri.getPath();
        this.queryParams = URLEncodedUtils.parse(uri, Charset.defaultCharset());
    }

    public String getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public List<String> getQueryParams(String name) {
        return getParam(queryParams, name);
    }

    public List<NameValuePair> getQueryParams() {
        return queryParams;
    }

    public List<NameValuePair> getPostParams() {
        return postParams;
    }

    public List<String> getPostParams(String name) {
        return getParam(postParams, name);
    }

    private List<String> getParam(List<NameValuePair> params, String name) {
        return params.stream()
                .filter(o -> o.getName().startsWith(name))
                .map(NameValuePair::getValue)
                .collect(Collectors.toList());
    }

    public void setPostParams(List<NameValuePair> postParams) {
        this.postParams = postParams;
    }
}
