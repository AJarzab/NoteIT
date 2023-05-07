package converters

import androidx.room.TypeConverter
import androidx.room.TypeConverters

@TypeConverters
class Converters {
    @TypeConverter
    fun fromString(value: String): List<String> {
        return value.split(",")
    }

    @TypeConverter
    fun fromList(value: List<String>): String {
        return value.joinToString(",")
    }
}
