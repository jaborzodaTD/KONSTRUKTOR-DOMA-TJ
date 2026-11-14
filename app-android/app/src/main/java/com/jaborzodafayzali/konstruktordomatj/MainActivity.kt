package com.jaborzodafayzali.konstruktordomatj

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.max
import kotlin.math.min

private val Bg = Color(0xFF101612)
private val Panel = Color(0xFF1C2720)
private val Panel2 = Color(0xFF29372D)
private val Lime = Color(0xFFB9F36A)
private val Ink = Color(0xFFF1F5EF)
private val Muted = Color(0xFFA2B0A4)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val prefs = getSharedPreferences("house_project", MODE_PRIVATE)
        setContent {
            var length by remember { mutableFloatStateOf(prefs.getFloat("length", 10f)) }
            var width by remember { mutableFloatStateOf(prefs.getFloat("width", 8f)) }
            var height by remember { mutableFloatStateOf(prefs.getFloat("height", 3f)) }
            var floors by remember { mutableIntStateOf(prefs.getInt("floors", 1)) }
            var rooms by remember { mutableIntStateOf(prefs.getInt("rooms", 4)) }
            var windows by remember { mutableIntStateOf(prefs.getInt("windows", 8)) }
            var doors by remember { mutableIntStateOf(prefs.getInt("doors", 3)) }
            var wall by remember { mutableStateOf("Кирпич") }
            var roof by remember { mutableStateOf("Двускатная") }
            var foundation by remember { mutableStateOf("Ленточный бетон") }
            var tab by remember { mutableStateOf("3D") }
            var currency by remember { mutableStateOf("TJS") }
            var furniture by remember { mutableIntStateOf(0) }
            var saved by remember { mutableStateOf(false) }

            MaterialTheme(colorScheme = darkColorScheme(primary = Lime, background = Bg, surface = Panel, onPrimary = Bg, onBackground = Ink, onSurface = Ink)) {
                Column(Modifier.fillMaxSize().background(Bg)) {
                    Row(Modifier.fillMaxWidth().padding(horizontal = 18.dp, vertical = 12.dp), verticalAlignment = Alignment.CenterVertically) {
                        Box(Modifier.size(42.dp).background(Lime, RoundedCornerShape(13.dp)), contentAlignment = Alignment.Center) {
                            Text("TJ", color = Bg, fontWeight = FontWeight.Black, fontSize = 17.sp)
                        }
                        Column(Modifier.weight(1f).padding(start = 11.dp)) {
                            Text("КОНСТРУКТОР ДОМА", color = Ink, fontWeight = FontWeight.ExtraBold, fontSize = 15.sp)
                            Text("ПЛАНИРОВАНИЕ · 3D · СМЕТА", color = Muted, fontSize = 9.sp, letterSpacing = 1.sp)
                        }
                        TextButton(onClick = {
                            prefs.edit().putFloat("length", length).putFloat("width", width).putFloat("height", height)
                                .putInt("floors", floors).putInt("rooms", rooms).putInt("windows", windows).putInt("doors", doors).apply()
                            saved = true
                        }) { Text(if (saved) "Сохранено ✓" else "Сохранить", color = Lime, fontSize = 12.sp) }
                    }
                    Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()).padding(horizontal = 12.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf("2D-план", "3D", "Интерьер", "Смета").forEach { label ->
                            val selected = tab == label
                            Surface(onClick = { tab = label; saved = false }, color = if (selected) Lime else Panel2, shape = RoundedCornerShape(22.dp)) {
                                Text(label, color = if (selected) Bg else Ink, fontWeight = FontWeight.SemiBold, fontSize = 12.sp, modifier = Modifier.padding(horizontal = 17.dp, vertical = 11.dp))
                            }
                        }
                    }
                    Spacer(Modifier.height(10.dp))
                    when (tab) {
                        "2D-план" -> PlanView(length, width, rooms, windows, Modifier.weight(1f).fillMaxWidth().padding(horizontal = 12.dp))
                        "3D" -> ModelView(length, width, height, floors, windows, roof, furniture, Modifier.weight(1f).fillMaxWidth().padding(horizontal = 12.dp))
                        "Интерьер" -> InteriorView(furniture, { furniture = it }, Modifier.weight(1f))
                        else -> EstimateView(length, width, height, floors, windows, doors, wall, roof, foundation, currency, { currency = it }, Modifier.weight(1f))
                    }
                    if (tab != "Смета") {
                        Column(Modifier.fillMaxWidth().heightIn(max = 390.dp).background(Panel, RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)).verticalScroll(rememberScrollState()).padding(16.dp)) {
                            Text("Параметры дома", color = Ink, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                            Text("Изменяйте параметры — план и модель обновятся", color = Muted, fontSize = 11.sp)
                            Spacer(Modifier.height(12.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Stepper("Длина", length, "м", Modifier.weight(1f), { length = it.coerceIn(4f, 30f) })
                                Stepper("Ширина", width, "м", Modifier.weight(1f), { width = it.coerceIn(4f, 25f) })
                                Stepper("Высота", height, "м", Modifier.weight(1f), { height = it.coerceIn(2.4f, 5f) })
                            }
                            Spacer(Modifier.height(8.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Stepper("Этажи", floors.toFloat(), "", Modifier.weight(1f), { floors = it.toInt().coerceIn(1, 2) }, 1f)
                                Stepper("Комнаты", rooms.toFloat(), "", Modifier.weight(1f), { rooms = it.toInt().coerceIn(1, 8) }, 1f)
                                Stepper("Окна", windows.toFloat(), "", Modifier.weight(1f), { windows = it.toInt().coerceIn(0, 20) }, 1f)
                                Stepper("Двери", doors.toFloat(), "", Modifier.weight(1f), { doors = it.toInt().coerceIn(1, 10) }, 1f)
                            }
                            Choice("Материал стен", listOf("Кирпич", "Саман", "Газоблок", "Шлакоблок", "Пахса"), wall) { wall = it }
                            Choice("Тип крыши", listOf("Плоская", "Двускатная", "Вальмовая"), roof) { roof = it }
                            Choice("Фундамент", listOf("Ленточный бетон", "Бутовый", "Плита"), foundation) { foundation = it }
                            Text("Смета предварительная. Для строительства нужен рабочий проект и проверка инженера.", color = Muted, fontSize = 10.sp, modifier = Modifier.padding(top = 10.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable private fun Stepper(title: String, value: Float, unit: String, modifier: Modifier, change: (Float) -> Unit, step: Float = .5f) {
    Column(modifier.background(Panel2, RoundedCornerShape(13.dp)).padding(8.dp)) {
        Text(title, color = Muted, fontSize = 10.sp, maxLines = 1)
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
            TextButton(onClick = { change(value - step) }, contentPadding = PaddingValues(0.dp)) { Text("−", color = Lime, fontSize = 19.sp) }
            Text("${if (value % 1f == 0f) value.toInt() else "%.1f".format(value)}$unit", color = Ink, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            TextButton(onClick = { change(value + step) }, contentPadding = PaddingValues(0.dp)) { Text("+", color = Lime, fontSize = 19.sp) }
        }
    }
}

@Composable private fun Choice(title: String, options: List<String>, selected: String, onSelect: (String) -> Unit) {
    Column(Modifier.padding(top = 10.dp)) {
        Text(title, color = Muted, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
        Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()).padding(top = 6.dp), horizontalArrangement = Arrangement.spacedBy(7.dp)) {
            options.forEach { option ->
                Surface(onClick = { onSelect(option) }, color = if (option == selected) Lime else Panel2, shape = RoundedCornerShape(20.dp)) {
                    Text(option, color = if (option == selected) Bg else Ink, fontSize = 11.sp, modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp))
                }
            }
        }
    }
}

@Composable private fun PlanView(length: Float, width: Float, rooms: Int, windows: Int, modifier: Modifier = Modifier) {
    Column(modifier.background(Color(0xFF202B23), RoundedCornerShape(22.dp)).padding(12.dp)) {
        Text("ПЛАНИРОВКА · ВИД СВЕРХУ", color = Lime, fontWeight = FontWeight.Bold, fontSize = 12.sp)
        Canvas(Modifier.fillMaxSize().padding(4.dp)) {
            val unit = min((size.width - 70f) / length, (size.height - 80f) / width)
            val w = length * unit; val h = width * unit
            val l = (size.width - w) / 2; val t = (size.height - h) / 2
            for (x in 0..(size.width / 28).toInt()) drawLine(Color(0xFF344238), Offset(x * 28f, 0f), Offset(x * 28f, size.height), 1f)
            for (y in 0..(size.height / 28).toInt()) drawLine(Color(0xFF344238), Offset(0f, y * 28f), Offset(size.width, y * 28f), 1f)
            drawRect(Color(0xFFB4C1B1), Offset(l, t), Size(w, h), style = Stroke(9f))
            for (i in 1 until rooms.coerceAtMost(6)) {
                val x = l + w * i / rooms.coerceAtMost(6)
                drawLine(Color(0xFF879A89), Offset(x, t + 5), Offset(x, t + h - 5), 5f)
            }
            repeat(windows.coerceAtMost(12)) { i ->
                val x = l + w * (i + 1) / (windows.coerceAtMost(12) + 1)
                drawLine(Color(0xFF79D5E4), Offset(x - 10, t), Offset(x + 10, t), 8f)
            }
            drawContext.canvas.nativeCanvas.drawText("${length} м", l + w / 2 - 20, t - 15, android.graphics.Paint().apply { color = android.graphics.Color.WHITE; textSize = 25f })
            drawContext.canvas.nativeCanvas.drawText("${width} м", l - 15, t + h / 2, android.graphics.Paint().apply { color = android.graphics.Color.WHITE; textSize = 23f })
        }
    }
}

@Composable private fun ModelView(length: Float, width: Float, height: Float, floors: Int, windows: Int, roof: String, furniture: Int, modifier: Modifier = Modifier) {
    Column(modifier.background(Color(0xFF202B23), RoundedCornerShape(22.dp)).padding(12.dp)) {
        Text("3D-ПРОСМОТР · ИЗОМЕТРИЯ", color = Lime, fontWeight = FontWeight.Bold, fontSize = 12.sp)
        Canvas(Modifier.fillMaxSize()) {
            val scale = min(size.width / (length + width + 4f), size.height / (height * floors + width * .5f + 3f)) * .82f
            val ox = size.width * .48f; val oy = size.height * .70f
            val dx = Offset(length * scale * .62f, -length * scale * .34f)
            val dy = Offset(-width * scale * .62f, -width * scale * .34f)
            fun p(x: Float, y: Float, z: Float) = Offset(ox + x * dx.x / length + y * dy.x / width, oy + x * dx.y / length + y * dy.y / width - z * scale)
            val a=p(0f,0f,0f); val b=p(length,0f,0f); val c=p(length,width,0f); val d=p(0f,width,0f)
            val at=p(0f,0f,height*floors); val bt=p(length,0f,height*floors); val ct=p(length,width,height*floors); val dt=p(0f,width,height*floors)
            fun face(points: List<Offset>, color: Color) {
                val path = Path().apply { moveTo(points.first().x, points.first().y); points.drop(1).forEach { lineTo(it.x,it.y) }; close() }
                drawPath(path,color)
            }
            face(listOf(d,c,ct,dt), Color(0xFF9B8068))
            face(listOf(b,c,ct,bt), Color(0xFFC6A88A))
            face(listOf(at,bt,ct,dt), Color(0xFF596B56))
            if (roof == "Плоская") face(listOf(at,bt,ct,dt), Color(0xFF46594A))
            else {
                val r1=Offset((at.x+dt.x)/2,(at.y+dt.y)/2-scale*.6f)
                val r2=Offset((bt.x+ct.x)/2,(bt.y+ct.y)/2-scale*.6f)
                face(listOf(at,bt,r2,r1),Color(0xFF46594A))
                face(listOf(dt,ct,r2,r1),Color(0xFF60755D))
            }
            repeat(windows.coerceAtMost(6)) { i ->
                val t=(i+1f)/(windows.coerceAtMost(6)+1f)
                val base=Offset(at.x+(bt.x-at.x)*t,at.y+(bt.y-at.y)*t)
                drawRect(Color(0xFF8CD7E5),Offset(base.x-9,base.y+32),Size(18f,27f))
                drawRect(Color(0xFF25312A),Offset(base.x-9,base.y+32),Size(18f,27f),style=Stroke(2f))
            }
            val door=p(length*.72f,0f,0f)
            drawRect(Color(0xFF493426),Offset(door.x-10,door.y-48),Size(20f,48f))
            if (furniture > 0) drawRect(Color(0xFFB88C57),Offset(ox+12,oy-38),Size(65f,13f))
            drawLine(Color(0xFF151C17),a,b,3f); drawLine(Color(0xFF151C17),b,c,3f); drawLine(Color(0xFF151C17),c,d,3f)
            drawContext.canvas.nativeCanvas.drawText("${length} × ${width} м · $floors этаж(а)",18f,size.height-15f,android.graphics.Paint().apply { color=android.graphics.Color.WHITE;textSize=24f })
        }
    }
}

@Composable private fun InteriorView(count: Int, onCount: (Int) -> Unit, modifier: Modifier = Modifier) {
    Column(modifier.fillMaxWidth().verticalScroll(rememberScrollState()).padding(16.dp)) {
        Text("ИНТЕРЬЕР И МЕБЕЛЬ", color = Ink, fontSize = 19.sp, fontWeight = FontWeight.Bold)
        Text("Добавляйте предметы в концепцию проекта", color = Muted, fontSize = 12.sp)
        listOf("Диван", "Обеденный стол", "Кровать", "Шкаф", "Кухонный гарнитур", "Кресло", "Тумба", "Светильник").forEach { item ->
            Row(Modifier.fillMaxWidth().padding(top = 8.dp).background(Panel, RoundedCornerShape(14.dp)).padding(13.dp), verticalAlignment = Alignment.CenterVertically) {
                Text("▰", color = Lime, fontSize = 22.sp)
                Text(item, color = Ink, modifier = Modifier.weight(1f).padding(start = 12.dp))
                TextButton(onClick = { onCount(count + 1) }) { Text("Добавить", color = Lime) }
            }
        }
        Text("Добавлено предметов: $count. Пока это каталог-концепция; точное размещение и редактирование объектов будут расширены в следующих версиях.", color = Muted, fontSize = 11.sp, modifier = Modifier.padding(top = 12.dp))
    }
}

@Composable private fun EstimateView(length: Float, width: Float, height: Float, floors: Int, windows: Int, doors: Int, wall: String, roof: String, foundation: String, currency: String, setCurrency: (String) -> Unit, modifier: Modifier = Modifier) {
    val perimeter=2*(length+width)
    val wallArea=max(0f,perimeter*height*floors-windows*1.5f-doors*1.9f)
    val roofArea=length*width*1.12f
    val foundation=perimeter*.5f*.65f
    val wallRate=when(wall){"Кирпич"->420f;"Саман"->180f;"Газоблок"->390f;"Шлакоблок"->300f;else->250f}
    val foundationRate=when(foundation){"Ленточный бетон"->900f;"Бутовый"->750f;else->1100f}
    val roofRate=when(roof){"Плоская"->280f;"Вальмовая"->420f;else->350f}
    val total=wallArea*wallRate+foundation*foundationRate+roofArea*roofRate+windows*650f+doors*900f
    Column(modifier.fillMaxWidth().verticalScroll(rememberScrollState()).padding(16.dp)) {
        Text("ПРЕДВАРИТЕЛЬНАЯ СМЕТА", color=Ink, fontSize=19.sp, fontWeight=FontWeight.Bold)
        Text("Ориентировочные ставки — не коммерческое предложение", color=Muted, fontSize=11.sp)
        Row(horizontalArrangement=Arrangement.spacedBy(8.dp), modifier=Modifier.padding(vertical=12.dp)) {
            listOf("TJS","KZT").forEach { c -> Surface(onClick={setCurrency(c)},color=if(currency==c)Lime else Panel2,shape=RoundedCornerShape(20.dp)){Text(c,color=if(currency==c)Bg else Ink,modifier=Modifier.padding(horizontal=18.dp,vertical=9.dp))} }
        }
        EstimateLine("Площадь стен (нетто)", "${"%.1f".format(wallArea)} м²")
        EstimateLine("Кровля", "${"%.1f".format(roofArea)} м² · $roof")
        EstimateLine("Фундамент", "${"%.1f".format(foundation)} м³ · $foundation")
        EstimateLine("Стены", wall)
        EstimateLine("Окна / двери", "$windows / $doors шт.")
        val converted=if(currency=="KZT") total*55f else total
        val amount=converted*1.08f
        Spacer(Modifier.height(12.dp))
        Text("ИТОГО С ЗАПАСОМ 8%", color=Muted, fontSize=11.sp, fontWeight=FontWeight.Bold)
        Text("${"%,.0f".format(amount)} $currency", color=Lime, fontSize=28.sp, fontWeight=FontWeight.ExtraBold)
        Text("Включены ориентировочные материалы стен, фундамент, кровля, окна и двери. Не включены работа, инженерные сети, отделка, доставка и разрешения. Курс для KZT условный.", color=Muted, fontSize=11.sp, modifier=Modifier.padding(top=12.dp))
    }
}

@Composable private fun EstimateLine(label: String, value: String) {
    Row(Modifier.fillMaxWidth().padding(vertical=8.dp), horizontalArrangement=Arrangement.SpaceBetween) {
        Text(label,color=Muted,fontSize=12.sp)
        Text(value,color=Ink,fontSize=12.sp,fontWeight=FontWeight.SemiBold,textAlign=TextAlign.End)
    }
    HorizontalDivider(color=Panel2)
}
