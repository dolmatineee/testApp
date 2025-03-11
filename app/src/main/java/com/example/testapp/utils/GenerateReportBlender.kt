package com.example.testapp.utils

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import com.example.testapp.domain.models.Customer
import com.example.testapp.domain.models.Field
import com.example.testapp.domain.models.Layer
import com.example.testapp.domain.models.Reagent
import com.example.testapp.domain.models.TestAttempt
import com.example.testapp.domain.models.Well
import com.example.testapp.domain.models.Photo
import org.apache.poi.util.Units
import org.apache.poi.xwpf.usermodel.*
import java.io.ByteArrayInputStream
import java.io.File
import java.io.FileOutputStream
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.core.content.FileProvider
import org.apache.commons.imaging.Imaging
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STMerge
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STVerticalJc
import java.io.ByteArrayOutputStream
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlin.math.roundToInt


fun shareReport(file: File, context: Context) {
    // Получаем URI для файла через FileProvider
    val fileUri = FileProvider.getUriForFile(
        context,
        "${context.packageName}.provider", // Убедитесь, что это совпадает с authority в манифесте
        file
    )

    // Создаем Intent для отправки файла
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "application/vnd.openxmlformats-officedocument.wordprocessingml.document" // MIME-тип для .docx
        putExtra(Intent.EXTRA_STREAM, fileUri) // Добавляем URI файла
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION) // Даем временное разрешение на чтение
    }

    // Запускаем Intent с выбором приложения
    context.startActivity(Intent.createChooser(intent, "Поделиться отчетом"))
}



fun generateReportBlender(
    customer: Customer,
    field: Field,
    layer: Layer,
    well: Well,
    testAttemptsMap: Map<String, List<TestAttempt>>,
    photos: List<Photo>,
    signatureBitmap: ImageBitmap, // Все фотографии
    context: Context
): File {
    // Создаем новый документ
    val document = XWPFDocument()


    val separatorParagraphForTitle = document.createParagraph()
    val separatorRunForTitle = separatorParagraphForTitle.createRun()
    repeat(6) {
        separatorRunForTitle.addBreak()
    }


    // Добавляем титульный лист
    val titlePage = document.createParagraph()
    titlePage.alignment = ParagraphAlignment.CENTER
    val titleRun = titlePage.createRun()
    titleRun.setText("Отчёт о тестировании блендера во время проведения ГРП")
    titleRun.isBold = true
    titleRun.fontSize = 20
    titleRun.fontFamily = "Times New Roman"
    repeat(6) {
        titleRun.addBreak()
    } // Добавляем отступ после заголовка



    // Добавляем информацию о месторождении, скважине, пласте и дате проведения
    val infoParagraph = document.createParagraph()
    infoParagraph.alignment = ParagraphAlignment.LEFT
    val infoRun = infoParagraph.createRun()
    infoRun.setText("Месторождение: ${field.name}")
    infoRun.fontSize = 16
    infoRun.isBold = true
    infoRun.fontFamily = "Times New Roman"
    infoRun.addBreak()

    infoRun.setText("Скважина: ${well.wellNumber}")
    infoRun.fontSize = 16
    infoRun.isBold = true
    infoRun.fontFamily = "Times New Roman"
    infoRun.addBreak()

    infoRun.setText("Пласт: ${layer.layerName}")
    infoRun.fontSize = 16
    infoRun.isBold = true
    infoRun.fontFamily = "Times New Roman"
    infoRun.addBreak()

    // Дата проведения (текущая дата)
    val currentDate = LocalDate.now()
    val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy г.")
    val formattedDate = currentDate.format(formatter)
    infoRun.setText("Дата проведения: $formattedDate")
    infoRun.fontSize = 16
    infoRun.isBold = true
    infoRun.fontFamily = "Times New Roman"
    repeat(10) {
        infoRun.addBreak()
    }



    // Добавляем информацию о заказчике внизу титульного листа
    val customerParagraph = document.createParagraph()
    customerParagraph.alignment = ParagraphAlignment.CENTER
    val customerRun = customerParagraph.createRun()
    customerRun.setText("Для ${customer.companyName}.")
    customerRun.fontSize = 16
    customerRun.isBold = true
    customerRun.fontFamily = "Times New Roman"
    customerRun.addBreak() // Переносим год на новую строку
    customerRun.setText("${currentDate.year} г.") // Добавляем год
    customerRun.fontSize = 16
    customerRun.fontFamily = "Times New Roman"



    // Добавляем разрыв страницы после титульного листа
    val breakParagraph = document.createParagraph()
    val breakRun = breakParagraph.createRun()
    breakRun.addBreak(BreakType.PAGE)

    // Добавляем таблицу для фотографий
    val photoTable = document.createTable()
    photoTable.setWidth("100%")

    // Максимальная ширина изображения (например, 500 пикселей)
    val maxImageWidth = 500.0

    // Добавляем фотографии в таблицу
    photos.forEach { photo ->
        // Создаем строку для фотографии
        val photoRow = photoTable.createRow()
        val photoCell = photoRow.getCell(0)

        // Добавляем изображение в ячейку
        val imageParagraph = photoCell.addParagraph()
        imageParagraph.alignment = ParagraphAlignment.CENTER
        val imageRun = imageParagraph.createRun()
        val inputStream = context.contentResolver.openInputStream(photo.uri)
        val imageBytes = inputStream?.readBytes()
        if (imageBytes != null) {
            // Получаем размеры изображения с помощью Apache Commons Imaging
            val (width, height) = getImageDimensions(imageBytes)
            val aspectRatio = width.toDouble() / height.toDouble()

            // Устанавливаем высоту изображения (400 пикселей), ширина рассчитывается автоматически
            var imageHeight = 400.0
            var imageWidth = imageHeight * aspectRatio

            // Если ширина превышает максимальную, уменьшаем её
            if (imageWidth > maxImageWidth) {
                imageWidth = maxImageWidth
                imageHeight = imageWidth / aspectRatio
            }

            imageRun.addPicture(
                ByteArrayInputStream(imageBytes),
                XWPFDocument.PICTURE_TYPE_JPEG,
                "image.jpg",
                Units.toEMU(imageWidth),
                Units.toEMU(imageHeight)
            )
        }


        // Добавляем описание фотографии в следующую строку
        val descriptionRow = photoTable.createRow()
        val descriptionCell = descriptionRow.getCell(0)
        descriptionCell.text = "Фотография тестирования основного насоса подачи ${photo.reagentName} (попытка №${photo.attemptNumber})"
        descriptionCell.paragraphs[0].alignment = ParagraphAlignment.CENTER
    }


    val separatorParagraph = document.createParagraph()
    val separatorRun = separatorParagraph.createRun()
    separatorRun.addBreak()



    val dataTable = document.createTable()
    dataTable.setWidth("100%")


    val headerRow = dataTable.getRow(0)
    headerRow.getCell(0).setText("№")
    headerRow.addNewTableCell().setText("Хим. реагент")
    headerRow.addNewTableCell().setText("Насос")
    headerRow.addNewTableCell().setText("Номер теста")
    headerRow.addNewTableCell().setText("Расход, м3/мин")
    headerRow.addNewTableCell().setText("Концентрация, л(кг)/м3")
    headerRow.addNewTableCell().setText("Время теста, мин")
    headerRow.addNewTableCell().setText("План, л (кг)")
    headerRow.addNewTableCell().setText("Факт, л (кг)")
    headerRow.addNewTableCell().setText("Отклонение, %")


    for (i in 0 until headerRow.tableCells.size) {
        val cell = headerRow.getCell(i)
        cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER) // Центрируем текст по вертикали
        cell.setColor("FFFF99")


        for (paragraph in cell.paragraphs) {
            for (run in paragraph.runs) {
                run.fontFamily = "Times New Roman"
                run.fontSize = 12
                run.isBold = true
            }
        }
    }

    var rowCounter = 1
    var mainRowCounter = 1
    var subRowCounter = 1


    testAttemptsMap.forEach { (reagentName, attempts) ->
        val startRowIndex = rowCounter

        attempts.forEachIndexed { testIndex, attempt ->
            val row = dataTable.createRow()
            row.getCell(0).setText(mainRowCounter.toString())
            row.getCell(1).setText(reagentName)
            row.getCell(2).setText("Осн.") //
            row.getCell(3).setText((subRowCounter).toString())
            row.getCell(4).setText(attempt.flowRate.toString())
            row.getCell(5).setText(attempt.concentration.toString())
            row.getCell(6).setText(attempt.testTime.toString())
            row.getCell(7).setText((attempt.flowRate * attempt.concentration * attempt.testTime).toString())
            row.getCell(8).setText(attempt.actualAmount.toString())
            row.getCell(9).setText(
                ((attempt.actualAmount - (attempt.flowRate * attempt.concentration * attempt.testTime)) /
                        (attempt.flowRate * attempt.concentration * attempt.testTime) * 100
                        ).toString() // Отклонение
            )

            // Настраиваем стиль для текста в ячейках
            for (i in 0 until row.tableCells.size) {
                val cell = row.getCell(i)
                cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER) // Центрируем текст по вертикали

                for (paragraph in cell.paragraphs) {
                    for (run in paragraph.runs) {
                        run.fontFamily = "Times New Roman"
                        run.fontSize = 12
                        if (i == 0 || i == 1 || i == 2) { // Жирный шрифт для номеров, реагентов и насосов
                            run.isBold = true
                        }
                    }
                }
            }

            rowCounter++
            subRowCounter++
        }

        // Объединяем ячейки для столбцов "№", "Хим. реагент" и "Насос"
        val endRowIndex = rowCounter - 1 // Конечная строка для текущего реагента
        for (rowIndex in startRowIndex until endRowIndex) {
            for (colIndex in 0..2) { // Объединяем первые три столбца
                val cell = dataTable.getRow(startRowIndex).getCell(colIndex)
                val cellToMerge = dataTable.getRow(rowIndex + 1).getCell(colIndex)

                // Устанавливаем вертикальное объединение
                cell.ctTc.addNewTcPr().addNewVMerge().setVal(STMerge.RESTART)
                cellToMerge.ctTc.addNewTcPr().addNewVMerge().setVal(STMerge.CONTINUE)
            }
        }
        mainRowCounter++
        subRowCounter = 1
    }

    val byteArrayOutputStream = ByteArrayOutputStream()
    signatureBitmap.asAndroidBitmap().compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
    val signatureBytes = byteArrayOutputStream.toByteArray()

    val sharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
    val footerParagraph = document.createParagraph()
    footerParagraph.alignment = ParagraphAlignment.BOTH

// Добавляем должность (прижата к левому краю)
    val positionRun = footerParagraph.createRun()
    positionRun.setText(sharedPreferences.getString("position", null))
    positionRun.fontSize = 16
    positionRun.isBold = true
    positionRun.fontFamily = "Times New Roman"
    positionRun.addTab()

    // Получаем размеры изображения с помощью Apache Commons Imaging
    val (width, height) = getImageDimensions(signatureBytes)
    val aspectRatio = width.toDouble() / height.toDouble()

    // Устанавливаем высоту изображения (400 пикселей), ширина рассчитывается автоматически
    var signatureHeight = 70.0
    var signatureWidth = signatureHeight * aspectRatio

    // Если ширина превышает максимальную, уменьшаем её
    if (signatureWidth > 120.0) {
        signatureWidth = 120.0
        signatureHeight = signatureWidth / aspectRatio
    }

// Добавляем фотографию подписи (маленькая, рядом с ФИО)
    val signatureRun = footerParagraph.createRun()
    signatureRun.addPicture(
        ByteArrayInputStream(signatureBytes),
        XWPFDocument.PICTURE_TYPE_PNG,
        "signature.png",
        Units.toEMU(signatureHeight), // Ширина подписи
        Units.toEMU(signatureWidth)  // Высота подписи
    )
    signatureRun.addTab()

// Добавляем ФИО (прижато к правому краю)
    val fullNameRun = footerParagraph.createRun()
    fullNameRun.setText(sharedPreferences.getString("fullName", null))
    fullNameRun.fontSize = 16
    fullNameRun.isBold = true
    fullNameRun.fontFamily = "Times New Roman"


// Сохраняем документ во временный файл
    val file = File.createTempFile("report", ".docx", context.cacheDir)
    document.write(FileOutputStream(file))
    return file
}


fun getImageDimensions(imageBytes: ByteArray): Pair<Int, Int> {
    val inputStream = ByteArrayInputStream(imageBytes)
    val imageInfo = Imaging.getImageInfo(inputStream, null)
    return Pair(imageInfo.width, imageInfo.height)
}

// Расширение для применения стиля к ячейке
fun XWPFTableCell.setText(text: String, style: XWPFStyle? = null): XWPFTableCell {
    this.removeParagraph(0) // Удаляем существующий параграф
    val paragraph = this.addParagraph()
    val run = paragraph.createRun()
    run.setText(text)
    style?.let { run.setStyle(it.styleId) }
    return this
}

// Расширение для применения стиля к ячейке
fun XWPFTableCell.applyStyle(style: XWPFStyle): XWPFTableCell {
    this.paragraphs.forEach { paragraph ->
        paragraph.runs.forEach { run ->
            run.setStyle(style.styleId)
        }
    }
    return this
}

// Расширение для центрирования текста в ячейке
fun XWPFTableCell.centerText(): XWPFTableCell {
    this.paragraphs.forEach { paragraph ->
        paragraph.alignment = ParagraphAlignment.CENTER // Горизонтальное выравнивание по центру
    }
    this.ctTc.addNewTcPr().addNewVAlign().setVal(STVerticalJc.CENTER) // Вертикальное выравнивание по центру
    return this
}