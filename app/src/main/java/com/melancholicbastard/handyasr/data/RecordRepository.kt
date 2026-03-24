package com.melancholicbastard.handyasr.data

import com.melancholicbastard.handyasr.domain.RecordData
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class RecordRepository {
    suspend fun fetchRecordsData(): List<RecordData> {
        delay(2000)
        return listOf(
            RecordData(
                1,
                SimpleDateFormat("dd/M/yyyy hh:mm:ss", Locale.getDefault()).format(Date()),
                "Первая запись"
            ),
            RecordData(
                2,
                SimpleDateFormat("dd/M/yyyy hh:mm:ss", Locale.getDefault()).format(Date()),
                "Вторая запись"
            )
        )
    }
}