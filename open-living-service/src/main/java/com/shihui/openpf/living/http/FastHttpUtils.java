package com.shihui.openpf.living.http;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.Args;
import org.apache.http.util.EntityUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Simple Safe Fast
 *
 * @author SongJian
 */
public final class FastHttpUtils {

	public static final String FASTHTTP_DEFAULT_ENCODE = "utf-8";

	static int CONNECT_TIMEOUT = 3000;

	/**
	 * 推荐使用
	 *
	 * @return
	 */
	public static CloseableHttpAsyncClient defaultHttpAsyncClient() {
		return getClient(CONNECT_TIMEOUT);
	}

	public static CloseableHttpAsyncClient getClient(int connectTimeout) {
		RequestConfig requestConfig = 
				RequestConfig
				.custom()
				.setConnectTimeout(connectTimeout)// 请求超时
				.build();
		return HttpAsyncClients.custom().setMaxConnPerRoute(1000).setDefaultRequestConfig(requestConfig).build();
	}

	/**
	 * 复用HTTP客户端,并发高 或 任何时候都推荐使用,频繁调用 [不会]在客户端产生大量TIME_WAIT,
	 * 但是要记得在外面自己调用CloseableHttpAsyncClient的close()
	 *
	 * @param client
	 * @param httpGet
	 * @return
	 */
	public static String executeReturnString(CloseableHttpAsyncClient client, HttpGet httpGet) {
		// 不要自己关闭
		HttpCallbackHandler<String> callbackHandler = new HttpCallbackHandler<String>();
		try {
			executeHttpGetReturnString(client, httpGet, FastHttpUtils.FASTHTTP_DEFAULT_ENCODE, callbackHandler, false);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return callbackHandler.get();// 异步回调得到结果
	}

	public static HttpCallbackHandler<String> executeReturnStringHandler(CloseableHttpAsyncClient client,
			HttpGet httpGet) {
		// 不要自己关闭
		HttpCallbackHandler<String> callbackHandler = new HttpCallbackHandler<String>();
		try {
			executeHttpGetReturnString(client, httpGet, FastHttpUtils.FASTHTTP_DEFAULT_ENCODE, callbackHandler, false);
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
		return callbackHandler;// 异步回调得到结果
	}

	/**
	 * Get请求得到String
	 *
	 * @param client
	 * @param httpGet
	 * @param encoding
	 * @param handler
	 * @param closeClient
	 *            是否关闭HttpClient.不推荐关闭
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public static String executeHttpGetReturnString(final CloseableHttpAsyncClient client, HttpGet httpGet,
			final String encoding, final IHttpCallbackHandler<String> handler, boolean closeClient)
					throws UnsupportedEncodingException {
		Args.notNull(client, "CloseableHttpAsyncClient not be null!");
		Args.notNull(handler, "callback handler must be set!");
		// 自动关闭HTTP的TCP设置,对于自己的服务器一定要加,否则服务端会产生大量TCP_CLOSEWAIT
		httpGet.setHeader("Connection", "close");// 即使加了这个参数,大量并发下客户端也会产生大量TIME_WAIT,需要调一些*inux系统参数
		httpGet.setHeader("Content-type", "application/x-www-form-urlencoded");// 看需要是否需要注释掉,一般没问题
		httpGet.setHeader("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");// 看需要是否需要注释掉,一般没问题

		try {
			handler.setHttpRequestConnection(httpGet);// !!

			client.start();
			client.execute(httpGet, new FutureCallback<HttpResponse>() {
				@Override
				public void failed(Exception ex) {
					handler.failed(ex);
				}

				@Override
				public void completed(HttpResponse resp) {
					HttpEntity entity = resp.getEntity();
					String body = "";
					int statusCode = resp.getStatusLine().getStatusCode();
					try {
						if (entity != null) {
							final InputStream instream = entity.getContent();
							try {
								final StringBuilder sb = new StringBuilder();
								final char[] tmp = new char[1024];
								final Reader reader = new InputStreamReader(instream, encoding);
								int l;
								while ((l = reader.read(tmp)) != -1) {
									sb.append(tmp, 0, l);
								}
								body = sb.toString();
							} finally {
								instream.close();
								EntityUtils.consume(entity);
							}
						}
						handler.completed(statusCode, body);
					} catch (ParseException | IOException e) {
						handler.failed(e);
					}
				}

				@Override
				public void cancelled() {
					handler.cancelled();
				}
			});
		} finally {
			if (closeClient) {
				close(client);
			}
		}
		return handler.get();
	}

	/**
	 * get
	 * ----------------------------------------------------------------------
	 * post
	 */

	public static String executePostReturnString(CloseableHttpAsyncClient client, HttpPost httpPost) {
		HttpCallbackHandler<String> callbackHandler = new HttpCallbackHandler<String>();
		try {
			executeHttpPostReturnString(client, httpPost, null, FastHttpUtils.FASTHTTP_DEFAULT_ENCODE, callbackHandler,
					false);
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
		return callbackHandler.get();
	}

	/**
	 * 外部关闭client,post
	 *
	 * @param client
	 * @param httpPost
	 * @return
	 */
	public static HttpCallbackHandler<String> executeReturnStringHandler(CloseableHttpAsyncClient client,
			HttpPost httpPost) {
		HttpCallbackHandler<String> callbackHandler = new HttpCallbackHandler<String>();
		try {
			executeHttpPostReturnString(client, httpPost, null, FastHttpUtils.FASTHTTP_DEFAULT_ENCODE, callbackHandler,
					false);
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
		return callbackHandler;
	}

	/**
	 * 外部关闭client,post
	 *
	 * @param client
	 * @param httpPost
	 * @return
	 */
	public static HttpCallbackHandler<String> executeReturnStringHandler(CloseableHttpAsyncClient client,
			HttpPost httpPost, Map<String, String> params) {
		HttpCallbackHandler<String> callbackHandler = new HttpCallbackHandler<String>();
		try {
			executeHttpPostReturnString(client, httpPost, params, FastHttpUtils.FASTHTTP_DEFAULT_ENCODE,
					callbackHandler, false);
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
		return callbackHandler;
	}

	/**
	 * 复用HttpAsyncClient,并发时使用,外部关闭HttpAsyncClient
	 *
	 * @param client
	 * @param httpPost
	 * @param params
	 *            参数,会覆盖post中添加的参数
	 * @return
	 */
	public static String executeReturnString(CloseableHttpAsyncClient client, HttpPost httpPost,
			Map<String, String> params) {
		HttpCallbackHandler<String> callbackHandler = new HttpCallbackHandler<String>();
		try {
			executeHttpPostReturnString(client, httpPost, params, FastHttpUtils.FASTHTTP_DEFAULT_ENCODE,
					callbackHandler, false);
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
		return callbackHandler.get();
	}

	/**
	 * 不传递HttpGet对象执行Get操作.1
	 *
	 * @param client
	 * @param url
	 * @param params
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public static String executeGetReturnString(CloseableHttpAsyncClient client, String url, Map<String, String> params)
			throws UnsupportedEncodingException {
		HttpGet post2 = new FastHttpGet(url).addParam(params).build();
		return FastHttpUtils.executeReturnStringHandler(client, post2).get();
	}

	/**
	 * 不传递HttpGet对象执行Get操作.2
	 *
	 * @param client
	 * @param url
	 * @param params
	 *            字符串数组,长度%2 必须==0
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public static String executeGetReturnString(CloseableHttpAsyncClient client, String url, String... params)
			throws UnsupportedEncodingException {
		HttpGet post2 = new FastHttpGet(url).addStringParameters(params).build();
		return FastHttpUtils.executeReturnStringHandler(client, post2).get();
	}

	/**
	 * 不传递HttpPost对象执行Post操作
	 *
	 * @param client
	 *            执行请求客户端
	 * @param url
	 *            post 提交地址
	 * @param params
	 *            post 的参数
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public static String executePostReturnString(CloseableHttpAsyncClient client, String url,
			Map<String, String> params) throws UnsupportedEncodingException {
		HttpPost post2 = new FastHttpPost(url).build();
		return FastHttpUtils.executeReturnStringHandler(client, post2, params).get();
	}

	/**
	 * POST请求得到String的结果
	 *
	 * @param client
	 *            请求客户端
	 * @param httpPost
	 *            请求对象
	 * @param param
	 *            参数
	 * @param encoding
	 *            请求结果转为字符串时的编码
	 * @param handler
	 *            回调
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public static String executeHttpPostReturnString(final CloseableHttpAsyncClient client, HttpPost httpPost,
			Map<String, String> param, final String encoding, final IHttpCallbackHandler<String> handler,
			boolean closeClient) throws UnsupportedEncodingException {
		Args.notNull(client, "CloseableHttpAsyncClient not be null!");
		Args.notNull(httpPost, "httpPost not be null!");
		Args.notBlank(encoding, "encoding must be set!");
		Args.notNull(handler, "callback handler must be set!");
		// 通知服务器自动关闭HTTP的TCP设置,对于自己的服务器一定要加,否则服务端会产生大量TCP_CLOSEWAIT
		httpPost.setHeader("Connection", "close");// 即使加了这个参数,大量并发下客户端也会产生大量TIME_WAIT,需要调一些*inux系统参数
		httpPost.setHeader("Content-type", "application/x-www-form-urlencoded");
		httpPost.setHeader("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");

		if (param != null && param.size() > 0) {
			List<NameValuePair> nvps = new ArrayList<NameValuePair>();
			for (Entry<String, String> entry : param.entrySet()) {
				nvps.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
			}
			// 设置参数到请求对象中
			httpPost.setEntity(new UrlEncodedFormEntity(nvps, encoding));
		}
		try {
			// 重要
			handler.setHttpRequestConnection(httpPost);

			client.start();
			// 执行请求操作并设置结果
			client.execute(httpPost, new FutureCallback<HttpResponse>() {
				@Override
				public void failed(Exception ex) {
					handler.failed(ex);
				}

				//
				@Override
				public void completed(HttpResponse resp) {
					HttpEntity responseEntity = resp.getEntity();
					int statusCode = resp.getStatusLine().getStatusCode();
					String body = "";
					try {
						if (responseEntity != null) {

							final InputStream instream = responseEntity.getContent();
							try {
								final StringBuilder sb = new StringBuilder();
								final char[] tmp = new char[1024];
								final Reader reader = new InputStreamReader(instream, encoding);
								int l;
								while ((l = reader.read(tmp)) != -1) {
									sb.append(tmp, 0, l);
								}
								body = sb.toString();
							} finally {
								instream.close();
								EntityUtils.consume(responseEntity);
							}
						}
						handler.completed(statusCode, body);
					} catch (ParseException | IOException e) {
						handler.failed(e);
					}
				}

				//
				@Override
				public void cancelled() {
					handler.cancelled();
				}
			});
		} finally {
			if (closeClient) {
				close(client);
			}
		}

		return handler.get();
	}

	/**
	 * 提交POST请求得到String的结果,要在外面自己调用CloseableHttpAsyncClient的close()
	 *
	 * @param client
	 *            请求客户端
	 * @param httpPost
	 *            请求对象
	 * @param encoding
	 *            请求结果转为字符串时的编码
	 * @param handler
	 *            回调
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public static String executeAsString(final CloseableHttpAsyncClient client, HttpPost httpPost,
			final String encoding, final IHttpCallbackHandler<String> handler) throws UnsupportedEncodingException {
		return executeHttpPostReturnString(client, httpPost, null, encoding, handler, false);
	}

	/**
	 * 关闭client对象
	 *
	 * @param client
	 */
	public static void close(CloseableHttpAsyncClient client) {
		try {
			client.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

}
