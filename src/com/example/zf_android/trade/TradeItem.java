package com.example.zf_android.trade;

/**
 * Created by Leo on 2015/2/7.
 */
public class TradeItem {

    /**
     * ����ʱ��
     */
    private String tradeTime;
    /**
     * �����˺�
     */
    private String tradeAccount;
    /**
     * �տ��˺�
     */
    private String tradeReceiveAccount;
    /**
     * �ն˺�
     */
    private String tradeClientNumber;
    /**
     * ���׽��
     */
    private Float tradeAmount;

    public String getTradeTime() {
        return tradeTime;
    }

    public void setTradeTime(String tradeTime) {
        this.tradeTime = tradeTime;
    }

    public String getTradeAccount() {
        return tradeAccount;
    }

    public void setTradeAccount(String tradeAccount) {
        this.tradeAccount = tradeAccount;
    }

    public String getTradeReceiveAccount() {
        return tradeReceiveAccount;
    }

    public void setTradeReceiveAccount(String tradeReceiveAccount) {
        this.tradeReceiveAccount = tradeReceiveAccount;
    }

    public String getTradeClientNumber() {
        return tradeClientNumber;
    }

    public void setTradeClientNumber(String tradeClientNumber) {
        this.tradeClientNumber = tradeClientNumber;
    }

    public Float getTradeAmount() {
        return tradeAmount;
    }

    public void setTradeAmount(Float tradeAmount) {
        this.tradeAmount = tradeAmount;
    }
}
