package com.kotlinblog.ibeacon

class Constants {
    companion object {
        const val I_BEACON_LAYOUT = "m:0-3=4c000215,i:4-19,i:20-21,i:22-23,p:24-24"
        const val IBEACON_LAYOUT_BY_GEOFF = "m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24"

        const val PAPER_KEY_UUID_A = "uuidA"
        const val PAPER_KEY_UUID_B = "uuidB"
        const val PAPER_KEY_EVENTS = "events"

        const val TAG = "QA_LOG"

        const val DEFAULT_UUID_A = "a0d756db-06cd-4da1-b925-7e57831ccf09"
        const val DEFAULT_UUID_B = "72b03d75-999b-4d2e-a632-1355b314ed78"
    }
}
