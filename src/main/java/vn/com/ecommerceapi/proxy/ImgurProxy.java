package vn.com.ecommerceapi.proxy;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;
import vn.com.ecommerceapi.model.proxy.response.ImgurUploadResponse;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class ImgurProxy extends BaseProxy {

    private static final Logger LOGGER = LoggerFactory.getLogger(ImgurProxy.class);

    private static final String CLIENT_ID = "Client-ID %s";

    @Value("${imgur.client-id}")
    private String clientId;

    @Value("${imgur.upload-url}")
    private String urlUpload;

    public ImgurUploadResponse upload(Map<String, Object> payload) {
        try {
            String url = UriComponentsBuilder.fromHttpUrl(urlUpload).toUriString();
            LOGGER.info("[PROXY][IMGUR][UPLOAD] Url: {}", url);

            String client = String.format(CLIENT_ID, clientId);

            ImgurUploadResponse response = this.post(url, integer -> initHeaders(client), payload, ImgurUploadResponse.class);
            LOGGER.info("[PROXY][IMGUR][UPLOAD] Response: {}", response);

            return response;
        } catch (Exception e) {
            LOGGER.info("[PROXY][IMGUR][UPLOAD] Exception: {}", e.getMessage());
            return null;
        }
    }
}
