package com.devonfw.tools.solicitor.common.webcontent;

/*
 * Class that contains information about retrieved web content.
 */
public class WebContentObject {

	private String content = "";
	private String effectiveURL ="";
	private String trace = "";
	private String url ="";

	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getEffectiveURL() {
		return effectiveURL;
	}
	public void setEffectiveURL(String effectiveURL) {
		this.effectiveURL = effectiveURL;
	}
	public String getTrace() {
		return trace;
	}
	public void setTrace(String trace) {
		this.trace = trace;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
}
