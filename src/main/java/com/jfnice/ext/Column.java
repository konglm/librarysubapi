package com.jfnice.ext;

import lombok.Data;

@Data
public class Column {

    private String key;
    private String title;
    private Integer sort;
    private Boolean sorter;
    private Width width;
    private Fix fix;
    private String align;
    private Boolean field;
    private Boolean required;
    private Boolean exportable;
    private Integer display;

    public class Width {
        private Integer value;
        private String type;

        public Integer getValue() {
            return value;
        }

        public void setValue(Integer value) {
            this.value = value;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }
    }

    public class Fix {
        private Boolean flag;
        private String direction;

        public Boolean getFlag() {
            return flag;
        }

        public void setFlag(Boolean flag) {
            this.flag = flag;
        }

        public String getDirection() {
            return direction;
        }

        public void setDirection(String direction) {
            this.direction = direction;
        }
    }

}
