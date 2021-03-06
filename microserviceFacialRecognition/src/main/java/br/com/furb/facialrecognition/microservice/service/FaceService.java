package br.com.furb.facialrecognition.microservice.service;

//This sample uses Apache HttpComponents:
//http://hc.apache.org/httpcomponents-core-ga/httpcore/apidocs/
//https://hc.apache.org/httpcomponents-client-ga/httpclient/apidocs/

import java.net.URI;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.springframework.boot.configurationprocessor.json.JSONArray;
import org.springframework.boot.configurationprocessor.json.JSONObject;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

public class FaceService {
	// Replace <Subscription Key> with your valid subscription key.
	private static final String subscriptionKey = "5bbada03d26241dc8042170633212970";

	// NOTE: You must use the same region in your REST call as you used to
	// obtain your subscription keys. For example, if you obtained your
	// subscription keys from westus, replace "westcentralus" in the URL
	// below with "westus".
	//
	// Free trial subscription keys are generated in the westcentralus region. If
	// you
	// use a free trial subscription key, you shouldn't need to change this region.
	private static final String uriBase = "https://westcentralus.api.cognitive.microsoft.com/face/v1.0/detect";

//	private static final String imageWithFaces = "{\"url\":\"https://play.minio.io:9000/furb/dilma2.jpg?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Credential=Q3AM3UQ867SPQQA43P2F%2F20181112%2Fus-east-1%2Fs3%2Faws4_request&X-Amz-Date=20181112T190243Z&X-Amz-Expires=432000&X-Amz-SignedHeaders=host&X-Amz-Signature=7c70bf60b88a15b94c1f9cb825e4b21ce1a331418872ebfcfb0d4b47f0b32a77\"}";

	private static final String faceAttributes = "age,gender,headPose,smile,facialHair,glasses,emotion,hair,makeup,occlusion,accessories,blur,exposure,noise";

	public static String uploadImage(String urlImage) throws Exception {
		HttpClient httpclient = HttpClientBuilder.create().build();

		try {
			URIBuilder builder = new URIBuilder(uriBase);

			// Request parameters. All of them are optional.
			builder.setParameter("returnFaceId", "true");
			builder.setParameter("returnFaceLandmarks", "false");
			builder.setParameter("returnFaceAttributes", faceAttributes);

			// Prepare the URI for the REST API call.
			URI uri = builder.build();
			HttpPost request = new HttpPost(uri);

			// Request headers.
			request.setHeader("Content-Type", "application/json");
			request.setHeader("Ocp-Apim-Subscription-Key", subscriptionKey);
			
			// Request body.
			urlImage = "\"" + urlImage + "\"";
			String imageWithFaces = "{\"url\":" + urlImage + "}";
			StringEntity reqEntity = new StringEntity(imageWithFaces);
			request.setEntity(reqEntity);

			// Execute the REST API call and get the response entity.
			HttpResponse response = httpclient.execute(request);
			HttpEntity entity = response.getEntity();

			if (entity != null) {
				// Format and display the JSON response.
				System.out.println("REST Response:\n");

				String jsonString = EntityUtils.toString(entity).trim();
				if (jsonString.charAt(0) == '[') {
					JSONArray jsonArray = new JSONArray(jsonString);
					System.out.println(jsonArray.toString(2));
					JsonElement root = new JsonParser().parse(jsonString);
					return root.getAsJsonArray().get(0).getAsJsonObject().get("faceId").toString();
				} else if (jsonString.charAt(0) == '{') {
					JSONObject jsonObject = new JSONObject(jsonString);
					System.out.println(jsonObject.toString(2));
					return jsonObject.get("faceId").toString();
				} else {
					throw new Exception("Fail uploading image");
				}
			}
			throw new Exception("Fail uploading image");
		} catch (Exception e) {
			// Display error message.
			System.out.println(e.getMessage());
			throw new Exception("Fail uploading image", e);
		}		
	}
}