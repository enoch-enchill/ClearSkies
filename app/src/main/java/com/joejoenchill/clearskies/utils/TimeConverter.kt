package com.joejoenchill.clearskies.utils

class TimeConverter {

    companion object {

        fun toTime(timestamp: Long) : String{
            val sdf = java.text.SimpleDateFormat("hh:mm a")
            val date = java.util.Date(timestamp * 1000)

            return sdf.format(date);
        }

        fun toDate(timestamp: Long) : String{
            val sdf = java.text.SimpleDateFormat("MMM d, YYYY")
            val date = java.util.Date(timestamp * 1000)

            return sdf.format(date);
        }

        fun toDay(timestamp: Long) : String{
            val sdf = java.text.SimpleDateFormat("EEEE")
            val date = java.util.Date(timestamp * 1000)
            return sdf.format(date);
        }
    }
}