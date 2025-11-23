package com.example.uneodinary

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ReportTable")
data class Report(
    var total: Int = 0,
    var date: String = ""
) {
    @PrimaryKey(autoGenerate = true) var id: Int = 0
}
