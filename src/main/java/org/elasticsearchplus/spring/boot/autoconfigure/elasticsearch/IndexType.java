package org.elasticsearchplus.spring.boot.autoconfigure.elasticsearch;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum IndexType {
    /*  不良驾驶-realtime_userdriver
        工单-realtime_cartask
        订单-realtime_order
        车辆-realtime_carinfo
        网点车辆调入调出动态-realtime_parking_carstate
        网点app打开热力-sd_open_parking_show_info
        违规预警-身份识别、驾驶员-realtime_iov_face_identity
     */
    CARINFO("realtime_carinfo","realtime_carinfo_type"),
    ORDER("realtime_order","realtime_order_type"),
    CARTASK("realtime_cartask","realtime_cartask_type"),
    USER_DRIVER("realtime_userdriver","realtime_userdriver_type"),
    PARK_CARSTATE("realtime_parking_carstate","realtime_parking_carstate_type"),
    APP_OPEN_PARK("sd_open_parking_show_info","sd_open_parking_show_type"),
    IOV_FACE_IDENTITY("realtime_iov_face_identity","realtime_iov_face_identity_type");

    private @Getter String index;
    private @Getter String type;
}
