package com.yt.hz.financial.argame.bean;

import java.util.List;

public class LocationMsgBean {


    /**
     * status : 0
     * message : ok
     * results : [{"name":"全聚德(天安门店)","location":{"lat":39.90782,"lng":116.406386},"address":"北京市东城区东交民巷44号","province":"北京市","city":"北京市","area":"东城区","street_id":"0ce65c2df9ca8b402d473321","telephone":"(010)65122265,(010)65132855","detail":1,"uid":"0ce65c2df9ca8b402d473321","detail_info":{"distance":823,"tag":"美食;中餐厅","navi_location":{"lng":116.40607538878,"lat":39.907824573622},"type":"cater","detail_url":"http://api.map.baidu.com/place/detail?uid=0ce65c2df9ca8b402d473321&output=html&source=placeapi_v2","price":"177.0","overall_rating":"4.2","comment_num":"200","children":[]}},{"name":"老北京炸酱面老店","location":{"lat":39.919028,"lng":116.398143},"address":"南长街甲30","province":"北京市","city":"北京市","area":"西城区","street_id":"f432e53d73bc146dc7f02528","detail":1,"uid":"f432e53d73bc146dc7f02528","detail_info":{"distance":670,"tag":"美食;小吃快餐店","navi_location":{"lng":116.39808370827,"lat":39.919012821482},"type":"cater","detail_url":"http://api.map.baidu.com/place/detail?uid=f432e53d73bc146dc7f02528&output=html&source=placeapi_v2","overall_rating":"4.2","comment_num":"16","children":[]}},{"name":"东来顺(天安门店)","location":{"lat":39.907367,"lng":116.406529},"address":"北京市东城区东交民巷44号院(天安门广场,毛主席纪念堂东南角,国家博物馆南300米)","province":"北京市","city":"北京市","area":"东城区","street_id":"22e11407b5aad1add4b74eb7","telephone":"(010)65241042","detail":1,"uid":"22e11407b5aad1add4b74eb7","detail_info":{"distance":875,"tag":"美食;中餐厅","navi_location":{"lng":116.40612440406,"lat":39.907260935985},"type":"cater","detail_url":"http://api.map.baidu.com/place/detail?uid=22e11407b5aad1add4b74eb7&output=html&source=placeapi_v2","price":"151.0","overall_rating":"3.7","comment_num":"77","children":[]}},{"name":"北京贵宾楼饭店","location":{"lat":39.915153,"lng":116.413558},"address":"北京市东城区东长安街35号","province":"北京市","city":"北京市","area":"东城区","street_id":"4ce10ce66096f0b8441a323d","telephone":"(010)65137788","detail":1,"uid":"4ce10ce66096f0b8441a323d","detail_info":{"distance":815,"tag":"酒店;星级酒店","navi_location":{"lng":116.41393966918,"lat":39.914500615103},"type":"cater","detail_url":"http://api.map.baidu.com/place/detail?uid=4ce10ce66096f0b8441a323d&output=html&source=placeapi_v2","price":"2254.0","overall_rating":"4.7","groupon_num":"1","children":[]}},{"name":"客家小镇菜馆(南河沿店)","location":{"lat":39.91603,"lng":116.412859},"address":"北京东城区南河沿大街103-1号(贵宾楼向北80米路西)","province":"北京市","city":"北京市","area":"东城区","street_id":"78bd50f7c56cac4230870a92","telephone":"(010)65228993","detail":1,"uid":"78bd50f7c56cac4230870a92","detail_info":{"distance":764,"tag":"美食;中餐厅","navi_location":{"lng":116.41285828933,"lat":39.916094797375},"type":"cater","detail_url":"http://api.map.baidu.com/place/detail?uid=78bd50f7c56cac4230870a92&output=html&source=placeapi_v2","price":"64.0","overall_rating":"4.0","comment_num":"20","children":[]}},{"name":"鸿兴餐厅(西交民巷店)","location":{"lat":39.908271,"lng":116.39768},"address":"北京市西城区西交民巷25号","province":"北京市","city":"北京市","area":"西城区","street_id":"46e86fddbca91876d03ce3a4","telephone":"(010)66057163","detail":1,"uid":"46e86fddbca91876d03ce3a4","detail_info":{"distance":922,"tag":"美食;中餐厅","navi_location":{"lng":116.39768327469,"lat":39.9081615406},"type":"cater","detail_url":"http://api.map.baidu.com/place/detail?uid=46e86fddbca91876d03ce3a4&output=html&source=placeapi_v2","price":"40.0","overall_rating":"4.2","comment_num":"9","children":[]}},{"name":"TRB Forbidden City","location":{"lat":39.921587,"lng":116.408869},"address":"东城区东华门大街95号","province":"北京市","city":"北京市","area":"东城区","street_id":"e4d9e6b1b68261c96fa3cccf","telephone":"(010)64016676","detail":1,"uid":"e4d9e6b1b68261c96fa3cccf","detail_info":{"distance":841,"tag":"美食;外国餐厅","type":"cater","detail_url":"http://api.map.baidu.com/place/detail?uid=e4d9e6b1b68261c96fa3cccf&output=html&source=placeapi_v2","price":"818.0","overall_rating":"4.4","comment_num":"150","children":[]}},{"name":"程府宴(1店)","location":{"lat":39.918844,"lng":116.39818},"address":"南长街38号","province":"北京市","city":"北京市","area":"西城区","street_id":"0a46dc0f16e0d147316212bc","telephone":"(010)66069936","detail":1,"uid":"0a46dc0f16e0d147316212bc","detail_info":{"distance":654,"tag":"美食;中餐厅","navi_location":{"lng":116.39809484816,"lat":39.918796964088},"type":"cater","detail_url":"http://api.map.baidu.com/place/detail?uid=0a46dc0f16e0d147316212bc&output=html&source=placeapi_v2","price":"1412.0","overall_rating":"4.6","comment_num":"40","children":[]}},{"name":"温鼎府(潮汕牛肉火锅)","location":{"lat":39.907504,"lng":116.407434},"address":"前门东大街前门23号院内(正阳门东100米)","province":"北京市","city":"北京市","area":"东城区","street_id":"6fbebf98e8b558f3c5effb91","telephone":"(010)65270128,18511277666","detail":1,"uid":"6fbebf98e8b558f3c5effb91","detail_info":{"distance":883,"tag":"美食;中餐厅","type":"cater","detail_url":"http://api.map.baidu.com/place/detail?uid=6fbebf98e8b558f3c5effb91&output=html&source=placeapi_v2","price":"454.0","overall_rating":"4.7","comment_num":"200","children":[]}},{"name":"仙客林北京餐厅","location":{"lat":39.91745,"lng":116.413467},"address":"南河沿大街华龙街D座都季商务快捷酒店2层","province":"北京市","city":"北京市","area":"东城区","street_id":"34f68ab0a551a4138f55ec67","telephone":"010-65133901","detail":1,"uid":"34f68ab0a551a4138f55ec67","detail_info":{"distance":852,"tag":"美食;中餐厅","type":"cater","detail_url":"http://api.map.baidu.com/place/detail?uid=34f68ab0a551a4138f55ec67&output=html&source=placeapi_v2","price":"70.0","overall_rating":"4.2","comment_num":"5","children":[]}}]
     */

    private int status;
    private String message;
    private List<ResultsBean> results;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<ResultsBean> getResults() {
        return results;
    }

    public void setResults(List<ResultsBean> results) {
        this.results = results;
    }

    public static class ResultsBean {
        /**
         * name : 全聚德(天安门店)
         * location : {"lat":39.90782,"lng":116.406386}
         * address : 北京市东城区东交民巷44号
         * province : 北京市
         * city : 北京市
         * area : 东城区
         * street_id : 0ce65c2df9ca8b402d473321
         * telephone : (010)65122265,(010)65132855
         * detail : 1
         * uid : 0ce65c2df9ca8b402d473321
         * detail_info : {"distance":823,"tag":"美食;中餐厅","navi_location":{"lng":116.40607538878,"lat":39.907824573622},"type":"cater","detail_url":"http://api.map.baidu.com/place/detail?uid=0ce65c2df9ca8b402d473321&output=html&source=placeapi_v2","price":"177.0","overall_rating":"4.2","comment_num":"200","children":[]}
         */

        private String name;
        private LocationBean location;
        private String address;
        private String province;
        private String city;
        private String area;
        private String street_id;
        private String telephone;
        private int detail;
        private String uid;
        private DetailInfoBean detail_info;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public LocationBean getLocation() {
            return location;
        }

        public void setLocation(LocationBean location) {
            this.location = location;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getProvince() {
            return province;
        }

        public void setProvince(String province) {
            this.province = province;
        }

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public String getArea() {
            return area;
        }

        public void setArea(String area) {
            this.area = area;
        }

        public String getStreet_id() {
            return street_id;
        }

        public void setStreet_id(String street_id) {
            this.street_id = street_id;
        }

        public String getTelephone() {
            return telephone;
        }

        public void setTelephone(String telephone) {
            this.telephone = telephone;
        }

        public int getDetail() {
            return detail;
        }

        public void setDetail(int detail) {
            this.detail = detail;
        }

        public String getUid() {
            return uid;
        }

        public void setUid(String uid) {
            this.uid = uid;
        }

        public DetailInfoBean getDetail_info() {
            return detail_info;
        }

        public void setDetail_info(DetailInfoBean detail_info) {
            this.detail_info = detail_info;
        }

        public static class LocationBean {
            /**
             * lat : 39.90782
             * lng : 116.406386
             */

            private double lat;
            private double lng;

            public double getLat() {
                return lat;
            }

            public void setLat(double lat) {
                this.lat = lat;
            }

            public double getLng() {
                return lng;
            }

            public void setLng(double lng) {
                this.lng = lng;
            }
        }

        public static class DetailInfoBean {
            /**
             * distance : 823
             * tag : 美食;中餐厅
             * navi_location : {"lng":116.40607538878,"lat":39.907824573622}
             * type : cater
             * detail_url : http://api.map.baidu.com/place/detail?uid=0ce65c2df9ca8b402d473321&output=html&source=placeapi_v2
             * price : 177.0
             * overall_rating : 4.2
             * comment_num : 200
             * children : []
             */

            private int distance;
            private String tag;
            private NaviLocationBean navi_location;
            private String type;
            private String detail_url;
            private String price;
            private String overall_rating;
            private String comment_num;
            private List<?> children;

            public int getDistance() {
                return distance;
            }

            public void setDistance(int distance) {
                this.distance = distance;
            }

            public String getTag() {
                return tag;
            }

            public void setTag(String tag) {
                this.tag = tag;
            }

            public NaviLocationBean getNavi_location() {
                return navi_location;
            }

            public void setNavi_location(NaviLocationBean navi_location) {
                this.navi_location = navi_location;
            }

            public String getType() {
                return type;
            }

            public void setType(String type) {
                this.type = type;
            }

            public String getDetail_url() {
                return detail_url;
            }

            public void setDetail_url(String detail_url) {
                this.detail_url = detail_url;
            }

            public String getPrice() {
                return price;
            }

            public void setPrice(String price) {
                this.price = price;
            }

            public String getOverall_rating() {
                return overall_rating;
            }

            public void setOverall_rating(String overall_rating) {
                this.overall_rating = overall_rating;
            }

            public String getComment_num() {
                return comment_num;
            }

            public void setComment_num(String comment_num) {
                this.comment_num = comment_num;
            }

            public List<?> getChildren() {
                return children;
            }

            public void setChildren(List<?> children) {
                this.children = children;
            }

            public static class NaviLocationBean {
                /**
                 * lng : 116.40607538878
                 * lat : 39.907824573622
                 */

                private double lng;
                private double lat;

                public double getLng() {
                    return lng;
                }

                public void setLng(double lng) {
                    this.lng = lng;
                }

                public double getLat() {
                    return lat;
                }

                public void setLat(double lat) {
                    this.lat = lat;
                }
            }
        }
    }
}
