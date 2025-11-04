package com.tareasPracticas.client_microservice.feign;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.time.Instant;

@FeignClient(
        name = "merchant-ms",
        url = "${merchant.base-url}",
        path = "/api/merchant",
        configuration = com.tareasPracticas.client_microservice.config.FeingAuthConfig.class
)
public interface MerchantClient {
    @GetMapping("/{id}")
    MerchantOut findById(@PathVariable("id") String id);

    @Setter
    @Getter
    @JsonIgnoreProperties(ignoreUnknown = true)
    class MerchantOut {
        private String id;
        private String nombre;
        private String dir;

        @JsonProperty("merchantType")
        private MerchantType merchantType;

        private String status;
        private Instant createDate;

        public enum MerchantType {
            MERCHANT_TYPE_PERSONAL_SERVICES,
            MERCHANT_TYPE_FINANCIAL_SERVICES
        }
    }
}
