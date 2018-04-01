package com.gtc.tradinggateway.service.huobi.dto;

import lombok.Data;

import java.util.List;

/**
 * Created by mikro on 01.04.2018.
 */
@Data
public class HuobiGetOpenResponseDto {

    private List<HuobiOrderDto> orders;
}
