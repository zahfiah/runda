package com.ruoyi.runda.utils;

public enum NoiseFactorCodingEnum {
    LA("LA", "A权声级"),
    L5("L5", "累积百分声级L5"),
    L10("L10", "累积百分声级L10"),
    L50("L50", "累积百分声级L50"),
    L90("L90", "累积百分声级L90"),
    L95("L95", "累积百分声级L95"),
    LEQ("Leq", "等效声级"),
    LDN("Ldn", "昼夜等效声级"),
    LD("Ld", "烟气温度"),
    LN("Ln", "烟气压力"),
    LMX("LMx", "烟气湿度"),
    LMN("LMn", "制冷温度");
    private String code;
    private String description;

    NoiseFactorCodingEnum(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * 通过编码获取中文描述
     *
     * @param code
     * @return
     */
    public static String getValue(String code) {
        for (NoiseFactorCodingEnum attributeType : NoiseFactorCodingEnum.values()) {
            if (attributeType.getCode().equals(code)) {
                return attributeType.getDescription();
            }
        }
        return null;
    }
}
