package com.kobo.mobile_map_core.mobile_map_core.data.models.mqttmessage

import com.google.gson.annotations.SerializedName


data class KoboBusinessUnitTag(

        @SerializedName("tag") val tag: String?,
        @SerializedName("businessUnitName") val businessUnitName: String?,
        @SerializedName("businessUnitId") val businessUnitId: Int?,
        @SerializedName("parameters") val parameters: Parameters?,
        @SerializedName("businessHead") val businessHead: BusinessHead?,
        @SerializedName("waybillCollector") val waybillCollector: List<String?>?,
        @SerializedName("accountManager") val accountManager: List<AccountManager?>?,
        @SerializedName("fieldOfficer") val fieldOfficer: List<FieldOfficer?>?,
        @SerializedName("supplyCoordinator") val supplyCoordinator: List<String?>?,
        @SerializedName("visibilityAgent") val visibilityAgent: List<String?>?,
        @SerializedName("operationsCoordinator") val operationsCoordinator: List<String?>?,
        @SerializedName("supplyAgent") val supplyAgent: List<String?>?,
//        @SerializedName(" yyyy") val  yyyy : List<String>,
//        @SerializedName("yyyyy") val yyyyy : List<String>,
//        @SerializedName("yyyyy1") val yyyyy1 : List<String>,
//        @SerializedName("yyyyy2") val yyyyy2 : List<String>,
//        @SerializedName("yyyyy3") val yyyyy3 : List<String>,
        @SerializedName("financeOfficer") val financeOfficer: List<String?>?,
        @SerializedName("financeManager") val financeManager: List<String?>?
)