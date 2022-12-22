import com.opencsv.CSVWriter
import java.io.File
import java.io.FileWriter

class Logger {
    val log = mutableListOf<Pair<Long, Double>>()

    fun writeToFile(name: String){
        val file = File(name)
        if (!file.exists()) file.createNewFile()

        val writer = CSVWriter(FileWriter(file))
        for (lg in log){
            writer.writeNext(arrayOf(lg.first.toString(), lg.second.toString()))
        }

        writer.close()
    }
}