package com.timeandspacehub.formtopdf.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class InputDto {
	public BuyerInfoDto buyerInfoDto;
	public BrokerInfoDto brokerInfoDto;
	public ReceiptInfoDto receiptInfoDto;
}
