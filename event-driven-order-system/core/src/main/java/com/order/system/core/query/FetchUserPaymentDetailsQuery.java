package com.order.system.core.query;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

//Scatter-Gather Queries
@Data
@Builder
@AllArgsConstructor
public class FetchUserPaymentDetailsQuery {
    private String userId;
}
