package com.cx.springboot02.common.E;


public enum DeliveryWay {

    INTELLIGENT_SORTING(1,"智能排序"),
    NEAREST(2,"距离最近"),
    HIGHEST_SALES(3,"销量最高"),
    LOWEST_STARTING_PRICE(4,"最低价格"),
    THE_FASTEST_PACE(5,"速度最快"),
    HIGHEST_SCORE(6,"评分最高"),

    ;
    private Integer code;
    private String message;


    DeliveryWay(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
    /**
     * 根据code获取message
     *
     * @param code
     * @return
     */
    public static String getMessageByCode(Integer code) {
        for (DeliveryWay ele : values()) {
            if (ele.getCode().equals(code)) {
                return ele.getMessage();
            }
        }
        return null;
    }


}
