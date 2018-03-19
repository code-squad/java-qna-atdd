package codesquad.web;

import java.util.Arrays;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

public class HtmlFormDataBuilder {
	private HttpHeaders headers;
	private MultiValueMap<String, Object> params;

	private HtmlFormDataBuilder(HttpHeaders headers) {
		this.headers = headers;
		this.params = new LinkedMultiValueMap<>();
	}

	public HtmlFormDataBuilder addParameter(String key, Object value) {
		this.params.add(key, value);
		return this;
	}

	public HttpEntity<MultiValueMap<String, Object>> build() {
		return new HttpEntity<MultiValueMap<String, Object>>(params, headers);
	}
	
	public HtmlFormDataBuilder addSampleQuestion() {
		addParameter("title", "I have a questions");
		addParameter("contents", "Coding is too hard to learn..");

		return this;
	}

	public HtmlFormDataBuilder addSampleUser(String userId) {
		addParameter("userId", userId);
		addParameter("password", "password");
		addParameter("name", "자바지기");
		addParameter("email", "javajigi@slipp.net");

		return this;
	}

	public static HtmlFormDataBuilder urlEncodedForm() {
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Arrays.asList(MediaType.TEXT_HTML));
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
		return new HtmlFormDataBuilder(headers);
	}
}
