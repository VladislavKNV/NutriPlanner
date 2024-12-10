package com.example.nutriPlanner


import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import co.yml.charts.axis.AxisData
import co.yml.charts.common.model.Point
import co.yml.charts.ui.linechart.LineChart
import co.yml.charts.ui.linechart.model.GridLines
import co.yml.charts.ui.linechart.model.IntersectionPoint
import co.yml.charts.ui.linechart.model.Line
import co.yml.charts.ui.linechart.model.LineChartData
import co.yml.charts.ui.linechart.model.LinePlotData
import co.yml.charts.ui.linechart.model.LineStyle
import co.yml.charts.ui.linechart.model.SelectionHighlightPoint
import co.yml.charts.ui.linechart.model.SelectionHighlightPopUp
import co.yml.charts.ui.linechart.model.ShadowUnderLine
import com.example.nutriPlanner.Helpers.BMIUpdater
import com.example.nutriPlanner.View.AccountActivity
import com.example.nutriPlanner.View.CartActivity
import com.example.nutriPlanner.View.MainActivity
import com.example.nutriPlanner.ui.theme.NutriPlannerTheme
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationBarView

const val steps = 10

class ProgressActivity : ComponentActivity() {
    val mainColor = Color(0xFF2BAB0A)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val context = LocalContext.current
            val bmiUpdater = BMIUpdater(context)
            val arrayListWeight: List<Double> = bmiUpdater.getListBmiWeight()
            val arrayListDate: List<String> = bmiUpdater.getListBmiDate()

            NutriPlannerTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.background)
                            .padding(24.dp)
                            .verticalScroll(rememberScrollState()),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Мониторинг BMI",
                            style = MaterialTheme.typography.headlineSmall,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        Text(
                            text = "Отслеживайте изменения вашего веса",
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(bottom = 0.dp)
                        )

                        val pointsList = getPointsList(arrayListWeight)
                        DrawChart(
                            pointsList = pointsList,
                            arrayListDate = arrayListDate,
                            steps = steps,
                            mainColor = mainColor
                        )

                        var heightText by remember { mutableStateOf(TextFieldValue(bmiUpdater.lastgetHeight.toString() ?: "")) }
                        var weightText by remember { mutableStateOf(TextFieldValue(arrayListWeight.lastOrNull()?.toString() ?: "")) }
                        val height = heightText.text.toFloatOrNull()
                        val weight = weightText.text.toFloatOrNull()

                        if (height != null && weight != null && height > 0) {
                            BMIScale(currentBMI = weight / (height / 100 * height / 100))
                        } else {
                            BMIScale(currentBMI = 0f)
                        }

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            // поле для ввода роста
                            OutlinedTextField(
                                value = heightText,
                                onValueChange = { heightText = it },
                                label = { Text("Рост (см)") },
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth(0.9f)
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            // поле для ввода веса
                            OutlinedTextField(
                                value = weightText,
                                onValueChange = { weightText = it },
                                label = { Text("Вес (кг)") },
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth(0.9f)
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            Button(
                                onClick = {
                                    val height = heightText.text.toIntOrNull()
                                    val weight = weightText.text.toDoubleOrNull()

                                    if (height != null && weight != null) {
                                        bmiUpdater.addOrUpdateBMIOnServer(height, weight, context,
                                            {
                                                Toast.makeText(context,"Данные сохранены",Toast.LENGTH_SHORT).show()
                                                context.startActivity(Intent(context, ProgressActivity::class.java))
                                                overridePendingTransition(0, 0)
                                            },
                                            {
                                                Toast.makeText(context,"Ошибка при сохранении данных",Toast.LENGTH_SHORT).show()
                                            }
                                        )
                                    } else {
                                        Toast.makeText(context, "Введите корректные данные", Toast.LENGTH_SHORT).show()
                                    }
                                },
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier
                                    .fillMaxWidth(0.9f)
                                    .height(52.dp)
                            ) {
                                Text(
                                    text = "Сохранить",
                                    style = TextStyle(
                                        fontSize = 20.sp
                                    )
                                )
                            }
                            Spacer(modifier = Modifier.height(32.dp))
                        }
                    }
                    Column(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Spacer(modifier = Modifier.weight(1f))
                        AndroidView(
                            modifier = Modifier.fillMaxWidth(),
                            factory = { context ->
                                BottomNavigationView(context).apply {
                                    inflateMenu(R.menu.bottom_navigation_menu)
                                    setSelectedItemId(R.id.menu_progress)
                                    itemTextAppearanceActive = R.style.BottomNavigationText1
                                    itemTextAppearanceInactive = R.style.BottomNavigationTextActive1
                                    labelVisibilityMode = NavigationBarView.LABEL_VISIBILITY_LABELED
                                    setOnNavigationItemSelectedListener { item ->
                                        when (item.itemId) {
                                            R.id.menu_home -> {
                                                context.startActivity(Intent(context, MainActivity::class.java))
                                                overridePendingTransition(0, 0)
                                                true
                                            }
                                            R.id.menu_progress -> true
                                            R.id.menu_cart -> {
                                                context.startActivity(Intent(context, CartActivity::class.java))
                                                overridePendingTransition(0, 0)
                                                true
                                            }
                                            R.id.menu_profile -> {
                                                context.startActivity(Intent(context, AccountActivity::class.java))
                                                overridePendingTransition(0, 0)
                                                true
                                            }
                                            else -> false
                                        }
                                    }
                                }
                            }
                        )
                    }
                }
            }
        }

    }

    private fun getPointsList(arrayList: List<Double>): List<Point> {
        val list = ArrayList<Point>()
        for (i in arrayList.indices) {
            list.add(Point(i.toFloat(), arrayList[i].toFloat()))
        }
        return list
    }

    private fun getMax(list: List<Point>): Float {
        var max = 0F
        list.forEach { point ->
            if (max < point.y) max = point.y
        }
        return max
    }

    private fun getMin(list: List<Point>): Float {
        var min = 200F
        list.forEach { point ->
            if (min > point.y) min = point.y
        }
        return min
    }


    // отрисовка графика
    @Composable
    fun DrawChart(
        pointsList: List<Point>,
        arrayListDate: List<String>,
        steps: Int,
        mainColor: Color
    ) {
        val max = getMax(pointsList)
        val min = getMin(pointsList)

        val xAxisData = AxisData.Builder()
            .axisStepSize(45.dp)
            .backgroundColor(Color(0xFFF2EDE9))
            .steps(pointsList.size - 1)
            .labelData { i -> arrayListDate[i] }
            .labelAndAxisLinePadding(20.dp)
            .build()

        val yAxisData = AxisData.Builder()
            .steps(steps)
            .backgroundColor(Color(0xFFF2EDE9))
            .labelAndAxisLinePadding(20.dp)
            .labelData { i ->
                val yScale = (max - min) / steps.toFloat()
                String.format("%.1f", ((i * yScale) + min))
            }
            .build()

        LineChart(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
                .padding(0.dp),
            lineChartData = LineChartData(
                linePlotData = LinePlotData(
                    lines = listOf(
                        Line(
                            dataPoints = pointsList,
                            lineStyle = LineStyle(color = mainColor),
                            intersectionPoint = IntersectionPoint(
                                draw = { center ->
                                    drawCircle(
                                        color = mainColor,
                                        radius = 5.dp.toPx(),
                                        center = center,
                                        style = Fill
                                    )
                                    drawCircle(
                                        color = Color(0xFFF2EDE9),
                                        radius = 3.dp.toPx(),
                                        center = center,
                                        style = Fill
                                    )
                                }
                            ),
                            selectionHighlightPoint = SelectionHighlightPoint(color = mainColor),
                            shadowUnderLine = ShadowUnderLine(),
                            selectionHighlightPopUp = SelectionHighlightPopUp(popUpLabel = { x, y -> "вес: ${y}" }, backgroundAlpha = 0f)
                        )
                    ),
                ),
                backgroundColor = MaterialTheme.colorScheme.background,
                yAxisData = yAxisData,
                containerPaddingEnd = 0.dp,
                paddingRight = 0.dp,
                xAxisData = xAxisData,
                gridLines = GridLines(),
            ),
        )
    }



    //отрисовка шкалы
    @Composable
    fun BMIScale(currentBMI: Float) {
        // Диапазоны и цвета для градиента
        val bmiRanges = listOf(15f, 18.5f, 25f, 30f, 40f)
        val gradientColors = listOf(
            Color(0xFF64B5F6), // Синий
            Color(0xFF81C784), // Зеленый
            Color(0xFFFFF176), // Желтый
            Color(0xFFE57373)  // Красный
        )
        val bmiCategories = listOf(
            "Недостаточный вес", "Норма", "Избыточный вес", "Ожирение"
        )

        var scaleWidth by remember { mutableStateOf(0f) }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // шкала с градиентом
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(35.dp)
                    .padding(horizontal = 8.dp)
                    .clip(RoundedCornerShape(100.dp)) // Добавляет скругление углов
                    .onGloballyPositioned { coordinates ->
                        scaleWidth = coordinates.size.width.toFloat()
                    }
            ) {
                // градиентная шкала
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val width = size.width
                    val height = size.height

                    drawRect(
                        brush = Brush.horizontalGradient(colors = gradientColors),
                        size = Size(width = width, height = height / 3),
                        topLeft = Offset(0f, height / 3)
                    )
                }

                // Маркер текущего значения ИМТ
                if (scaleWidth > 0f) {
                    val markerPosition = calculateMarkerPosition(currentBMI, bmiRanges, scaleWidth)

                    // определение цветового индекса с использованием нормализованной позиции
                    val normalizedPosition = markerPosition / scaleWidth
                    val colorIndex = (normalizedPosition * (gradientColors.size - 1)).toInt().coerceIn(0, gradientColors.size - 1)

                    // Интерполяция для более точного цвета
                    val fraction = normalizedPosition * (gradientColors.size - 1) - colorIndex
                    val startColor = gradientColors[colorIndex]
                    val endColor = gradientColors.getOrElse(colorIndex + 1) { startColor } // Используем startColor, если endColor выходит за границы

                    val borderColor = Color(
                        red = (startColor.red * (1 - fraction) + endColor.red * fraction).coerceIn(0f, 1f),
                        green = (startColor.green * (1 - fraction) + endColor.green * fraction).coerceIn(0f, 1f),
                        blue = (startColor.blue * (1 - fraction) + endColor.blue * fraction).coerceIn(0f, 1f)
                    )

                    Canvas(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(40.dp)
                    ) {
                        // маркер
                        drawCircle(
                            color = Color.White,
                            radius = 7.dp.toPx(),
                            center = Offset(markerPosition, size.height / 2)
                        )
                        // обводка маркера с интерполированным цветом
                        drawCircle(
                            color = borderColor,
                            radius = 8.dp.toPx(), //
                            style = Stroke(width = 2.5.dp.toPx()),
                            center = Offset(markerPosition, size.height / 2)
                        )
                    }
                }

            }
            // текущее значение ИМТ
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = String.format("%.1f", currentBMI),
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                    ),
                    color = when {
                        currentBMI < 18.5 -> gradientColors[0]
                        currentBMI < 25 -> mainColor
                        currentBMI < 30 -> Color(0xFF8D9B3E)
                        currentBMI < 35 -> Color(0xFFFFC107)
                        else -> gradientColors[3]
                    },
                    modifier = Modifier.padding(end = 4.dp)
                )
                Text(
                    text = " — ",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                    ),
                    color = when {
                        currentBMI < 18.5 -> gradientColors[0]
                        currentBMI < 25 -> mainColor
                        currentBMI < 30 -> Color(0xFF8D9B3E)
                        currentBMI < 35 -> Color(0xFFFFC107)
                        else -> gradientColors[3]
                    },
                    modifier = Modifier.padding(horizontal = 4.dp)
                )
                Text(
                    text = when {
                        currentBMI < 18.5 -> bmiCategories[0]
                        currentBMI < 25 -> bmiCategories[1]
                        currentBMI < 30 -> bmiCategories[2]
                        else -> bmiCategories[3]
                    },
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                    ),
                    color = when {
                        currentBMI < 18.5 -> gradientColors[0]
                        currentBMI < 25 -> mainColor
                        currentBMI < 30 -> Color(0xFF8D9B3E)
                        currentBMI < 35 -> Color(0xFFFFC107)
                        else -> gradientColors[3]
                    }
                )
            }

        }
    }

    // Расчет позиции маркера
    fun calculateMarkerPosition(bmi: Float, ranges: List<Float>, width: Float): Float {
        val rangeWidth = width / (ranges.size - 1)

        for (i in 0 until ranges.size - 1) {
            if (bmi in ranges[i]..ranges[i + 1]) {
                val normalizedBMI = (bmi - ranges[i]) / (ranges[i + 1] - ranges[i])
                return rangeWidth * i + rangeWidth * normalizedBMI
            }
        }

        return when {
            bmi < ranges.first() -> 0f
            bmi > ranges.last() -> width
            else -> 0f
        }
    }

}