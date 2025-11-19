package com.timeandspacehub.formtopdf.dto;


public class InputDto {
	public BuyerInfoDto buyerInfoDto;
	public BrokerInfoDto brokerInfoDto;
	public ReceiptInfoDto receiptInfoDto;

	public InputDto() {
	}

	public InputDto(BuyerInfoDto buyerInfoDto, BrokerInfoDto brokerInfoDto, ReceiptInfoDto receiptInfoDto) {
		super();
		this.buyerInfoDto = buyerInfoDto;
		this.brokerInfoDto = brokerInfoDto;
		this.receiptInfoDto = receiptInfoDto;
	}

	public BuyerInfoDto getBuyerInfoDto() {
		return buyerInfoDto;
	}

	public void setBuyerInfoDto(BuyerInfoDto buyerInfoDto) {
		this.buyerInfoDto = buyerInfoDto;
	}

	public BrokerInfoDto getBrokerInfoDto() {
		return brokerInfoDto;
	}

	public void setBrokerInfoDto(BrokerInfoDto brokerInfoDto) {
		this.brokerInfoDto = brokerInfoDto;
	}

	public ReceiptInfoDto getReceiptInfoDto() {
		return receiptInfoDto;
	}

	public void setReceiptInfoDto(ReceiptInfoDto receiptInfoDto) {
		this.receiptInfoDto = receiptInfoDto;
	}

}
